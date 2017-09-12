package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public class ModelSiloPakan extends ArduinoUnoModel {

	transient byte pinBufferLevelSilo[] = {2,3};
	transient byte pinRelayBucketSilo = 4;
	transient byte pinLEDsilo = 5;
	
	byte currentFillLevelSilo = 0;
	byte fillStateSilo = 2; //0=off,1=fill,2=auto
	
	//when to start filling in auto mode
	public byte fillLevelSilo = 0;
	
	//status json
	boolean isSiloStartFilling = false;
	boolean manualMode = false;
	
	public ModelSiloPakan(I2CBus bus, int address) {
		// TODO Auto-generated constructor stub
		super(bus, address);
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		pinMode(pinRelayBucketSilo, OUTPUT);
		pinMode(pinBufferLevelSilo[0], INPUT);
		pinMode(pinBufferLevelSilo[1], INPUT);
		pinMode(pinLEDsilo, INPUT);
		pinMode(pinLEDsilo, OUTPUT);
		
	}

	@Override
	protected void mainLoop() {
		// TODO Auto-generated method stub
		if (digitalRead(pinBufferLevelSilo[0]) == LOW) this.currentFillLevelSilo = 0;
		if (digitalRead(pinBufferLevelSilo[1]) == LOW) this.currentFillLevelSilo = 1;
		
		if (manualMode == false) {
			if (fillStateSilo == 2) {
				if (isSiloFull()) {
					digitalWrite(pinRelayBucketSilo, LOW);
				} else if (isSiloToFill()) {
					digitalWrite(pinRelayBucketSilo, HIGH);
				}
			} else if (fillStateSilo == 0) {
				digitalWrite(pinRelayBucketSilo, LOW);
			} else if (fillStateSilo == 1) {
				digitalWrite(pinRelayBucketSilo, HIGH);
			}
		}
		
	}
	
	void digitalWrite(byte pinNumber, byte highLow) {
		super.digitalWrite(pinNumber, highLow);
		
		if (highLow == LOW) {
			if (pinNumber == pinRelayBucketSilo) {
				isSiloStartFilling = false;
			}
		} else if (highLow == HIGH) {
			if (pinNumber == pinRelayBucketSilo) {
				isSiloStartFilling = true;
			}
		}
	}
	
	private boolean isSiloFull() {
		return (digitalRead(pinBufferLevelSilo[1]) == LOW);
	}
	
	private boolean isSiloToFill() {
		return (digitalRead(pinBufferLevelSilo[fillLevelSilo]) == HIGH);
	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		// TODO Auto-generated method stub
		
	}
	
	public void manualSilo() {
		if (manualMode == true) {
			manualMode = false;
		} else {
			manualMode = true;
		}
	}
	
	public String printStateDetails() {
		StringBuffer sb = new StringBuffer(super.printStateDetails());
		sb.append("\n");
		String tempString = "STOP";
		if (digitalRead(pinRelayBucketSilo)==HIGH) {
			tempString = "FILL";
		}
		sb.append("\nSILO Fill State .:\t " + tempString + " (Conf: " + fillLevelSilo + ")");
		sb.append("\nSILO Level . . . :\t " + digitalRead(this.pinBufferLevelSilo[0]) +""+ digitalRead(this.pinBufferLevelSilo[1]));
		
		sb.append("\n");
		sb.append("\nOutput Close/Open:\t " + digitalRead(pinRelayBucketSilo));
		
		return sb.toString();
		
	}
	
	public String processCommand(String stringCommand) {
		String[] cmds = stringCommand.trim().split(" ");
		if (cmds.length > 0) {
			if ("manualMode".equals(cmds[0])) {
				this.manualSilo();
				return "OK";
			}
		}
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": manualMode\n";
		}
		return parentResponse;
	}

}
