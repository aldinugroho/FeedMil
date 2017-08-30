package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public class ModelMixer extends ArduinoUnoModel implements MixerIfc {
	
	public static final byte outputOpen = HIGH;  //TODO high or low? 
	public static final byte outputClose = LOW;  //TODO high or low?

	
	///### CONFIGURATION ###///
	transient byte pinRelayMixer = 3;
	transient byte pinRelayPneumaticOutput = 4;
	transient byte pinRelayScrewOut = 5;
	transient byte pinBucketOut = 10;
	transient byte pinBufferLevelSiloPakan = 11; // 2 Pins of IR obstacle, Low level, High Level
	transient byte pinRelaySiloPakan = 13;

	transient byte pinSensorMixerEmpty = 9;
	
	//when to start filling in auto mode (Silo Pakan) 0 = low, 1 = high;
	public byte fillLevelSiloPakan = 1;
	
	//fill state for each silo. 0=off, 1=fill, 2=auto
	byte fillStateSiloPakan = 2;
	
	transient public int mixerEmptyReadingRetry = 30; //how many times to retry

	/**
	 * The Mixing duration in milis
	 */
	public int mixingDuration = 60000;
	
	/**
	 * The expected delay time from start mixing instruction until it is started by the Arduino 
	 */
	transient public long mixingTimeCalibration = 500; 
	
	public boolean mixerAlwaysOn = false;
	public boolean screwOutAlwaysOn = false;
	
	
	///### END of CONFIGURATION ###///

	//### running variables ###//
	byte mixingState = 0;  //0/1/2/3/4 = ready / mixing / done mixing / ejecting / stop(suspended)
	
	transient long startMixingTime = -1;

	transient boolean isReadingMixingEmpty = false;
	transient byte readingMixingEmptyCount = 0;

	
	//status for json 
	boolean isMixerOn = false;
	boolean isOutputOpen = false;
	boolean isScrewOutOn = false;
	boolean isBucketOutOn = false;
	boolean isFillSiloPakan = false;
	
	//boolean isBucketOutOn = false;

	public ModelMixer(I2CBus bus, int address) {
		super(bus, address);
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#getMixingDuration()
	 */
	public long getMixingDuration() {
		return mixingDuration;
	}
	
	public void setMixingDuration(int milisDuration) {
		this.mixingDuration = milisDuration;
	}
	

	@Override
	protected void initialize() {
		pinMode(pinRelayMixer, OUTPUT);   
		pinMode(pinRelayPneumaticOutput, OUTPUT);
		pinMode(pinRelayScrewOut, OUTPUT);
		pinMode(pinBufferLevelSiloPakan, INPUT);
		pinMode(pinRelaySiloPakan, OUTPUT);
		
		pinMode(pinSensorMixerEmpty, INPUT);
	}
	
	public void SwitchInputMixerOn() {
		if(digitalRead(pinRelayMixer) == LOW){
			digitalWrite(pinRelayMixer, HIGH);
			isMixerOn = true;
		} else {
			digitalWrite(pinRelayMixer, LOW);
			isMixerOn = false;
		}
	}
	
	public void SwitchOutputOpen() {
		if(digitalRead(pinRelayPneumaticOutput) == LOW) {
			digitalWrite(pinRelayPneumaticOutput, HIGH);
			isOutputOpen = true;
		} else {
			digitalWrite(pinRelayPneumaticOutput, LOW);
			isOutputOpen = false;
		}
	}
	
	public void SwitchInputScrewOutOn() {
		if(digitalRead(pinRelayScrewOut) == LOW) {
			digitalWrite(pinRelayScrewOut, HIGH);
			isScrewOutOn = true;
		} else {
			digitalWrite(pinRelayScrewOut, LOW);
			isScrewOutOn = false;
		}
	}
	
	public void SwitchInputBucketOutOn() {
		if(digitalRead(pinBucketOut) == LOW) {
			digitalWrite(pinBucketOut, HIGH);
			isBucketOutOn = true;
		} else {
			digitalWrite(pinBucketOut, LOW);
			isBucketOutOn = false;
		}
	}
	

	@Override
	protected void mainLoop() {
		
		//0/1/2/3 = ready / mixing / done mixing / ejecting
		
		if(mixingState == 0) { //ready, do nothing, wait for instruction
			
			logger.finer(this.getId() + " > Mixing State = 0 -> Ready, WAIT for input");
			
		} else if(mixingState == 1) { //mixing process
			
			logger.finer(this.getId() + " > Mixing State = 1 -> MIXING");
			
			if( (System.currentTimeMillis() - startMixingTime + mixingTimeCalibration) > (mixingDuration) ) {
				//it's time, stop
				stopMixing();
			} 
			
		} else if(mixingState == 2) { //done mixing
			
			logger.finer(this.getId() + " > Mixing State = 2 -> Mixing DONE");
			
			startEjecting(); //TODO eject directly?
		
		} else if(mixingState == 3) { //ejecting state
			
			logger.finer(this.getId() + " > Mixing State = 3 -> EJECTING");
			
			//if empty, go back to ready
			if(digitalRead(pinSensorMixerEmpty) == HIGH) { //high means no obstacle (empty), repeat the reading x times
				
				if(!isReadingMixingEmpty) { //first time reading
					
					isReadingMixingEmpty = true;
					readingMixingEmptyCount = 1;
					
				} 
					
				if(readingMixingEmptyCount < mixerEmptyReadingRetry) { //not enough retry yet

					readingMixingEmptyCount++;
//					try {
//						Thread.sleep(mixerEmptyReadingCycle);
//					} catch (InterruptedException e) {
//						System.err.println(this + " > sleep interrupted.");
//					}
				
				} else { //enough retry, stop ejecting
				
					isReadingMixingEmpty = false; 
					readingMixingEmptyCount = 0;
					
					stopEjecting();
					
				}
				
				
			} else if(digitalRead(pinSensorMixerEmpty) == LOW) { //some obstacle, not empty yet
				
				isReadingMixingEmpty = false; 
				readingMixingEmptyCount = 0;
				
			}
		
		}
		
		if(fillStateSiloPakan == 2) {
			if(digitalRead(pinBufferLevelSiloPakan) == LOW) {
				digitalWrite(pinRelaySiloPakan, LOW); 
				isFillSiloPakan = true;
			} else {
				digitalWrite(pinRelaySiloPakan, HIGH);
				isFillSiloPakan = false;
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateSiloPakan == 0) {
			digitalWrite(pinRelaySiloPakan, LOW); //stop the bucket
			isFillSiloPakan = false;
		} else if(fillStateSiloPakan == 1) {
			if(digitalRead(pinBufferLevelSiloPakan) == LOW){
				digitalWrite(pinRelaySiloPakan, LOW); 
				isFillSiloPakan = true;
			} else {
				digitalWrite(pinRelaySiloPakan, HIGH); //start the bucket
				isFillSiloPakan = false;
			}
		}
		
	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		
	}

	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#isReadyForMixing()
	 */
	public boolean isReadyForMixing() {
		if(mixingState == 0) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#startMixing()
	 */
	public void startMixing() {

		if(mixingState != 1) {
			mixingState = 1;
			startMixingTime = System.currentTimeMillis();

			digitalWrite(pinRelayPneumaticOutput, outputClose);
			isOutputOpen = false;

			digitalWrite(pinRelayMixer, HIGH);
			isMixerOn = true;
		} 
		//else mixingState == 1 -> already mixing, or not ready yet, do nothing
		

	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#stopMixing()
	 */
	public void stopMixing() {

		mixingState = 2;
		if(!mixerAlwaysOn) {
			digitalWrite(pinRelayMixer, LOW);
			isMixerOn = false;
		}
		
	}


	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#startEjecting()
	 */
	public void startEjecting() {

		mixingState = 3;
		digitalWrite(pinRelayScrewOut, HIGH); 
		isScrewOutOn = true;
		isBucketOutOn = true;
		

		digitalWrite(pinRelayPneumaticOutput, outputOpen);
		isOutputOpen = true;
		
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.MixerIfc#stopEjecting()
	 */
	public void stopEjecting() {

		mixingState = 0;

		digitalWrite(pinRelayPneumaticOutput, outputClose);
		isOutputOpen = false;

		if(!screwOutAlwaysOn) {
			digitalWrite(pinRelayScrewOut, LOW); 
			isScrewOutOn = false;
		}
		
	}

	

	public String getStateString() {
		
		switch(mixingState) {

		case 0: 
			return "READY";
		case 1: 
			return "MIXING";
		case 2: 
			return "DONE MIXING";
		case 3: 
			return "EJECTING";
		default: 
			return "SUSPENDED";

		}
	
	}
	
	public String printStateDetails() {
	
		StringBuffer sb  = new StringBuffer(super.printStateDetails());
		sb.append("\n");
		sb.append("\nState:\t " + mixingState + " / " + this.getStateString()); 
		
		sb.append("\n");
		sb.append("\nMixer Off/On:\t " + digitalRead(pinRelayMixer));
		sb.append("\nOutput Close/Open:\t " + digitalRead(pinRelayPneumaticOutput));
		sb.append("\nScrew Out Off/On:\t " + digitalRead(pinRelayScrewOut));

		sb.append("\n");
		sb.append("\nWayout Not/Empty:\t " + digitalRead(pinSensorMixerEmpty));
		return sb.toString();
	
	}
	
	public String processCommand(String stringCommand) {
		
		String[] cmds = stringCommand.trim().split(" ");
		
		if(cmds.length > 0) {
		
			if("startMixing".equals(cmds[0])) {
				this.startMixing();
				return "OK";
			}
			
			if("stopMixing".equals(cmds[0])) {
				this.stopMixing();
				return "OK"; 
			}
			
			if("startEjecting".equals(cmds[0])) {
				this.startEjecting();
				return "OK"; 
			}
			
			if("stopEjecting".equals(cmds[0])) {
				this.stopEjecting();
				return "OK"; 
			}
			
			if("SwitchInputMixerOn".equals(cmds[0])) {
				SwitchInputMixerOn();
				return "OK"; 
			}
			
			if("SwitchOutputOpen".equals(cmds[0])) {
				SwitchOutputOpen();
				return "OK"; 
			}
			
			if("SwitchInputScrewOutOn".equals(cmds[0])) {
				SwitchInputScrewOutOn();
				return "OK"; 
			}
			
			if("SwitchInputBucketOutOn".equals(cmds[0])) {
				SwitchInputBucketOutOn();
				return "OK"; 
			}
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": startMixing, stopMixing, startEjecting, stopEjecting, SwitchInputMixerOn, SwitchOutputOpen, SwitchInputScrewOutOn, SwitchInputBucketOutOn \n";
		}
		return parentResponse;
	}

}
