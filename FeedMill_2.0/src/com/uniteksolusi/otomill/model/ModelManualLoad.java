package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;

public class ModelManualLoad extends ArduinoUnoModel implements MixerLoaderIfc {
	
	static final byte outputOpen = HIGH;  //TODO high or low? 
	static final byte outputClose = LOW;  //TODO high or low?
	

	//#### CONFIGURATION ###//
	
	transient byte pinRelayReadyForFilling 	= 2;
	transient byte pinRelayReadyForEjectLight 	= 3;
	transient byte pinRelayPneumaticOutput 	= 4;

	//input pins
	transient byte pinButtonDoneLoading 	= 8;
	transient byte pinSensorTubeLoaded 	= 9;
	
	transient public int buttonDoneReadingRetry = 4; //how many times to retry
	
	transient public int tubeLoadedReadingRetry = 4; //how many times to retry
	
	transient public int tubeEmptyReadingRetry = 4; //how many times to retry
	
	//waiting for 5 seconds, reading every 0.5 sec. So total 10 readings

	
	///### END of CONFIGURATION ###///


	//### running variables ###//
	int fillingState = 0; //0 = ready, 1 = filling, 2 = full, 3 = ejecting, 4 = stop/suspend
	
	transient boolean isReadingButtonDone = false;
	transient byte readingButtonDoneCount = 0;
	
	transient boolean isReadingTubeLoaded = false;
	transient byte readingTubeLoadedCount = 0;
	
	transient boolean isReadingTubeEmpty = false;
	transient byte readingTubeEmptyCount = 0;

	
	//status json
	boolean isReadyToFill = true;
	boolean isFilled = false;
	boolean isReadyToEject = false;
	boolean isOutputOpen = false;
	
	
	public ModelManualLoad(I2CBus bus, int address) {
		super(bus, address);
	}
	

	public boolean isReadyForFilling() {
		if(fillingState == 0) {
			return true;
		}
		return false;
	}

	public boolean isFilling() {
		if(fillingState == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isReadyForEject() {
		if(fillingState == 2) {
			return true;
		}
		return false;
	}
	
	public boolean isEjecting() {
		if(fillingState == 3) {
			return true;
		}
		return false;
	}
	

	@Override
	protected void initialize() {
		pinMode(pinRelayReadyForFilling, OUTPUT);
		pinMode(pinRelayReadyForEjectLight, OUTPUT);
		pinMode(pinRelayPneumaticOutput, OUTPUT);

		pinMode(pinButtonDoneLoading, INPUT);
		pinMode(pinSensorTubeLoaded, INPUT);
		
		
		
		stopEjecting(); //default state
	}

	@Override
	protected void mainLoop() {
		
		//fill is manual, it checks if it's already filled in, and then the button is pressed
		if(fillingState == 0) {
			
//			System.out.println
			logger.finer(this.getId() + " > Filling State = 0 -> Ready");
			
			if(digitalRead(pinSensorTubeLoaded) == LOW) { //high means no obstacle (empty), repeat the reading x times

				logger.finer(this.getId() + " > LOADED!");
				
				if(!isReadingTubeLoaded) { //first time reading
					
					isReadingTubeLoaded = true;
					readingTubeLoadedCount = 1;
					
				} 
					
				if(readingTubeLoadedCount < tubeLoadedReadingRetry) { //not enough retry yet

					readingTubeLoadedCount++;
//					try {
//						Thread.sleep(tubeLoadedReadingCycle);
//					} catch (InterruptedException e) {
//						System.err.println(this + " > sleep interrupted.");
//					}
				
				} else { //enough retry, stop ejecting
					
					isReadingTubeLoaded = false; 
					readingTubeLoadedCount = 0;
					
					startFilling();
					
				}
				
				
			} else if(digitalRead(pinSensorTubeLoaded) == HIGH) { 
				
				isReadingTubeLoaded = false; 
				readingTubeLoadedCount = 0;
				
			}
			
		} else if(fillingState == 1) {
			
			logger.finer(this.getId() + " > Filling State = 1 -> FILLING");
			
			if(digitalRead(pinButtonDoneLoading) == HIGH) { //high means pressed, repeat the reading x times
				
				if(!isReadingButtonDone) { //first time reading
					
					isReadingButtonDone = true;
					readingButtonDoneCount = 1;
					
				} 
					
				if(readingButtonDoneCount < buttonDoneReadingRetry) { //not enough retry yet

					readingButtonDoneCount++;
//					try {
//						Thread.sleep(buttonDoneReadingCycle);
//					} catch (InterruptedException e) {
//						System.err.println(this + " > sleep interrupted.");
//					}
				
				} else { //enough retry, stop ejecting
					
					isReadingButtonDone = false; 
					readingButtonDoneCount = 0;
					
					stopFilling();
					
				}
				
				
			} else if(digitalRead(pinButtonDoneLoading) == LOW) { 
				
				isReadingButtonDone = false; 
				readingButtonDoneCount = 0;
				
			}
			
			
		} else if(fillingState == 2) {

			logger.finer(this.getId() + " > Filling State = 2 -> WAITING for Eject");
			
			//do nothing, waiting for eject command
			
		} else if(fillingState == 3) {
			
//			System.out.println
			logger.finer(this.getId() + " > Filling State = 3 -> EJECTING");

			toggleEjectLED();
			if(digitalRead(pinSensorTubeLoaded) == HIGH) { //high means no obstacle (empty), repeat the reading x times
				
				if(!isReadingTubeEmpty) { //first time reading
					
					isReadingTubeEmpty = true;
					readingTubeEmptyCount = 1;
					
				} 
					
				if(readingTubeEmptyCount < tubeEmptyReadingRetry) { //not enough retry yet

					readingTubeEmptyCount++;
//					try {
//						Thread.sleep(tubeEmptyReadingCycle);
//					} catch (InterruptedException e) {
//						System.err.println(this + " > sleep interrupted.");
//					}
				
				} else { //enough retry, stop ejecting
					
					isReadingTubeEmpty = false; 
					readingTubeEmptyCount = 0;
					
					stopEjecting();
					
				}
				
				
			} else if(digitalRead(pinSensorTubeLoaded) == LOW) { //some obstacle, not empty yet
				
				isReadingTubeEmpty = false; 
				readingTubeEmptyCount = 0;
				
			}
				
		} 
		
	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		//do nothing, no special request message
	}

	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		//do nothing, no special response message
	}
	
	
	public void startFilling() { //filled
		fillingState = 1;
		digitalWrite(pinRelayReadyForFilling, LOW); 
		
		isReadyToFill = false;
		isFilled = true;
		isReadyToEject = false;
		isOutputOpen = false;
	}
	
	public void stopFilling() { //full
		digitalWrite(pinRelayReadyForEjectLight, HIGH);
		fillingState = 2;
		
		isReadyToFill = false;
		isFilled = true;
		isReadyToEject = true;
		isOutputOpen = false;

	}
	
	public void startEjecting() { //ejecting
		digitalWrite(pinRelayPneumaticOutput, outputOpen); 
		fillingState = 3;
		
		isReadyToFill = false;
		isFilled = false;
		isReadyToEject = false;
		isOutputOpen = true;

	}
	
	public void stopEjecting() { //ready for next filling
		digitalWrite(pinRelayPneumaticOutput, outputClose); 
		digitalWrite(pinRelayReadyForEjectLight, LOW);
		digitalWrite(pinRelayReadyForFilling, HIGH);
		fillingState = 0;
	}
	

	protected void toggleEjectLED() {
		digitalWrite( pinRelayReadyForEjectLight, (byte) (digitalRead(pinRelayReadyForEjectLight)^1) );
	}
	
	public void SwitchInputReadyToFill() {
		if(digitalRead(pinRelayReadyForFilling) == LOW){
			digitalWrite(pinRelayReadyForFilling, HIGH);
			isReadyToFill = true;
		} else {
			digitalWrite(pinRelayReadyForFilling, LOW);
			isReadyToFill = false;
		}
	}
	
	public void SwitchInputReadyToEject() {
		if(digitalRead(pinRelayReadyForEjectLight) == LOW){
			digitalWrite(pinRelayReadyForEjectLight, HIGH);
			isReadyToEject = true;
		} else {
			digitalWrite(pinRelayReadyForEjectLight, LOW);
			isReadyToEject = false;
		}
	}
	
	public void SwitchOutputOpen() {
		if(digitalRead(pinRelayPneumaticOutput) == LOW){
			digitalWrite(pinRelayPneumaticOutput, HIGH);
			isOutputOpen = true;
		} else {
			digitalWrite(pinRelayPneumaticOutput, HIGH);
			isOutputOpen = false;
		}
	}
	
	
	public String getStateString() {
		
		switch(fillingState) {

		case 0: 
			return "READY";
		case 1: 
			return "FILLING";
		case 2: 
			return "WAITING FOR EJECT (FULL)";
		case 3: 
			return "EJECTING";
		default: 
			return "STOPPED";

		}
	
	}
	
	public String printStateDetails() {
	
		StringBuffer sb  = new StringBuffer(super.printStateDetails());
		sb.append("\n");
		sb.append("\nState:\t " + fillingState + " / " + this.getStateString()); 
		sb.append("\n");
		sb.append("\nOutput Close/Open:\t " + digitalRead(pinRelayPneumaticOutput));
		sb.append("\nSensor Loaded/Not:\t " + digitalRead(pinSensorTubeLoaded));
		
		return sb.toString();
	
	}
	
	
	public String processCommand(String stringCommand) {
		
		String[] cmds = stringCommand.trim().split(" ");
		
		if(cmds.length > 0) {
		
			if("startFilling".equals(cmds[0])) {
				this.startFilling();
				return "OK";
			}
			
			if("stopFilling".equals(cmds[0])) {
				this.stopFilling();
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
			
			if("SwitchInputReadyToFill".equals(cmds[0])) {
				SwitchInputReadyToFill();
				return "OK";
			}
			
			if("SwitchInputReadyToEject".equals(cmds[0])) {
				SwitchInputReadyToEject();
				return "OK";
			}
			
			if("SwitchOutputOpen".equals(cmds[0])) {
				SwitchOutputOpen();
				return "OK";
			}
			
			
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": startFilling, stopFilling, startEjecting, stopEjecting, startEjectingAll, stopEjectingAll, SwitchInputReadyToFill, SwitchInputReadyToEject, SwitchOutputOpen \n";
		}
		return parentResponse;
	}
	
	
}
