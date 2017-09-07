package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class ModelSiloPakan extends ArduinoUnoModel implements MixerLoaderIfc, LoadCellIfc {

	public static final byte outputOpen = HIGH;   
	public static final byte outputClose = LOW; 
	
	transient byte pinRelayPneumaticOutput = 4;
	
	boolean isInputSilo = false;
	boolean isOutputSilo = false;
	
	byte fillingState = 0;
	int currentWeight = 0;
	
	public int targetWeight = 10;
	public int emptyTolerance = 0;
	public int fullTolerance = 0;
	
	public ModelSiloPakan(I2CBus bus, int address) {
		// TODO Auto-generated constructor stub
		super(bus, address, 14, 4, 6); //2 bytes for weight
	}
	
	public ModelSiloPakan(I2CBus bus, int address, int targetWeight) {
		super(bus, address, 14, 4, 6);
		this.targetWeight = targetWeight;
	}



	@Override
	public int getCurrentWeight() {
		// TODO Auto-generated method stub
		return currentWeight;
	}

	@Override
	public int getTargetWeight() {
		// TODO Auto-generated method stub
		return targetWeight;
	}
	
	public void setTargetWeight(int target) {
		this.targetWeight = target;
	}

	@Override
	public int getEmptyTolerance() {
		// TODO Auto-generated method stub
		return emptyTolerance;
	}
	
	public void setEmptyTolerance(int toleranceKg) {
		this.emptyTolerance = toleranceKg;
	}

	@Override
	public int getFullTolerance() {
		// TODO Auto-generated method stub
		return fullTolerance;
	}

	@Override
	public void startFilling() {
		// TODO Auto-generated method stub
		isInputSilo = true;
		fillingState = 1;
		
	}

	@Override
	public void stopFilling() {
		// TODO Auto-generated method stub
		isInputSilo = false;
		fillingState = 2;
		
	}

	@Override
	public void startEjecting() {
		// TODO Auto-generated method stub
		digitalWrite(pinRelayPneumaticOutput, outputOpen);
		isOutputSilo = true;
		fillingState = 3;
		
	}

	@Override
	public void stopEjecting() {
		// TODO Auto-generated method stub
		digitalWrite(pinRelayPneumaticOutput, outputClose);
		isOutputSilo = false;
		fillingState = 0;
	}

	@Override
	public boolean isReadyForFilling() {
		// TODO Auto-generated method stub
		if (fillingState == 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean isFilling() {
		// TODO Auto-generated method stub
		if (fillingState == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isReadyForEject() {
		// TODO Auto-generated method stub
		if (fillingState == 2) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEjecting() {
		// TODO Auto-generated method stub
		if (fillingState == 3) {
			return true;
		}
		return false;
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		pinMode(pinRelayPneumaticOutput, OUTPUT);
	}

	@Override
	protected void mainLoop() {
		// TODO Auto-generated method stub
		int weightRead = -1;
		weightRead = getCurrentWeight();
		
		if(weightRead == -1) { 	
			//do nothing, retry next loop
		} else { //if(weightRead != -1) {
		
			int tempCurrentWeight = weightRead;
			
			if(fillingState == 0) { //when it's ready to start, just start immediately   
				
				startFilling(); 
				
			} else if(fillingState == 1) {
				
				if(tempCurrentWeight >= (targetWeight - fullTolerance) )  {
					stopFilling();
				}
				
			} else if(fillingState == 2) {
				
				//do nothing, wait for eject command
			
			} else if(fillingState == 3) {
			
				if(tempCurrentWeight <= (0 + emptyTolerance) ) {
					stopEjecting();
				}
				
			} 
		
		} 
	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		// TODO Auto-generated method stub
		
	}

	private int lastWeight = -1;
	
	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		// TODO Auto-generated method stub
		
		logger.fine(this.getClass().getSimpleName() + "("+this.i2cDevice.getAddress()+")" + " > READ RESPONSE SPECIFIC: ");

		lastWeight = currentWeight;

		//read the current weight from byte[3] and [4]
		currentWeight =     (  Math.abs((responseByte[3] >> 4)                       * 1000   )) +

							(  Math.abs(( responseByte[3] - ((responseByte[3] >> 4)*16) ) * 100    )) +

							(  Math.abs((responseByte[4] >> 4)                       * 10     )) +

							(  Math.abs(( responseByte[4] - ((responseByte[4] >> 4)*16) ) * 1      )) ;

		if(currentWeight < 0) {

			logger.warning(this.getClass().getSimpleName() + "("+this.i2cDevice.getAddress()+")" + " Current Weight is NEGATIVE!! : " + currentWeight );
		}

		logger.fine(this.getClass().getSimpleName() + "("+this.i2cDevice.getAddress()+")" + " Cur/Tar Weight: " + currentWeight + "  /  " + targetWeight);
		
		//check if lastWeight have same weight with currentWeight
		
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
		sb.append("\nWeight:\t " + this.getCurrentWeight() + " / " + this.getTargetWeight() );
		
		sb.append("\n");
		sb.append("\nOutput Close/Open:\t " + digitalRead(pinRelayPneumaticOutput));
		
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
			
			if("setTarget".equals(cmds[0])) {
				int target = Integer.valueOf(cmds[1]);
				this.setTargetWeight(target);
				return "OK";
			}
			
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": startFilling, stopFilling, startEjecting, stopEjecting, setTarget(spasi)(new Target Weight)\n";
		}
		return parentResponse;
	}
	
}
