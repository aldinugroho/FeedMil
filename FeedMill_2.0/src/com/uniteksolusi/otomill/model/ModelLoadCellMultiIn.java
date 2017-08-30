package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public class ModelLoadCellMultiIn extends ModelLoadCell {
	
	//#### CONFIGURATION ###//
	transient byte pinRelayPneumaticInput 	= 2;
	
	boolean isInputB = false;
	
	/*
	 * bellow is defined in the super class, overwrite in constructor if needed.
	 * 	int pinRelayScrewConveyor = 3;
	 * 	int pinRelayPneumaticOutput = 4;
	 */
	
	public ModelLoadCellMultiIn(I2CBus bus, int address) {
		super(bus, address);
	}
	
	public ModelLoadCellMultiIn(I2CBus bus, int address, int targetWeight) {
		super(bus, address, targetWeight);
	}
	

	@Override
	protected void initialize() {
		super.initialize();
		pinMode(pinRelayPneumaticInput, OUTPUT);
	}
	
	
	public void switchInput() {
//		digitalWrite(pinRelayPneumaticInput, (byte) (digitalRead(pinRelayPneumaticInput)^1));
		if(digitalRead(pinRelayPneumaticInput) == LOW) {
			digitalWrite(pinRelayPneumaticInput, HIGH);
			isInputB = true;
		} else {
			digitalWrite(pinRelayPneumaticInput, LOW);
			isInputB = false;
		}
	}
	
	public String printStateDetails() {
		
		StringBuffer sb  = new StringBuffer(super.printStateDetails());
		sb.append("\nInput Route:\t " + digitalRead(pinRelayPneumaticInput));
		
		return sb.toString();
		
	}
	
	
	public String processCommand(String stringCommand) {
		
		String[] cmds = stringCommand.trim().split(" ");
		
		if(cmds.length > 0) {
		
			if("switchInput".equals(cmds[0])) {
				this.switchInput();
				return "OK";
			}
			
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": switchInput\n";
		}
		return parentResponse;
	}

}
