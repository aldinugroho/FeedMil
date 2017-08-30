package com.uniteksolusi.otomill.model.tester;

import com.pi4j.io.i2c.I2CBus;
import com.uniteksolusi.otomill.model.ArduinoUnoModel;

/**
 * @author nanda
 *
 * This tester model class turn on all output sequentially, then turning them back off
 * 
 * 
 */
public class ModelTesterUnoOutput extends ArduinoUnoModel {

	int currentIndex = 0;
	byte currentOperation = HIGH;
	
	public ModelTesterUnoOutput(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	protected void initialize() {

		for(int i=0; i<digitalPinMode.length; i++) {
			pinMode(i, OUTPUT);
		}

	}

	@Override
	protected void mainLoop() {
		
		digitalWrite(currentIndex, currentOperation);
		currentIndex++;
		
		if(currentIndex >= super.digitalPinState.length) {
			currentIndex = 0;
			currentOperation = (byte) (currentOperation^1);
		}

	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		// TODO Auto-generated method stub
		
	}

}
