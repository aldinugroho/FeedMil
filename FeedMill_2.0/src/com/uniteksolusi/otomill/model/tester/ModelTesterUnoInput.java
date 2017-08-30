package com.uniteksolusi.otomill.model.tester;

import com.pi4j.io.i2c.I2CBus;
import com.uniteksolusi.otomill.model.ArduinoUnoModel;
import com.uniteksolusi.otomill.util.BinaryFormatter;

/**
 * @author nanda
 *
 * This tester model class turn on all output sequentially, then turning them back off
 * 
 * 
 */
public class ModelTesterUnoInput extends ArduinoUnoModel {

	int currentIndex = 0;
	byte currentOperation = HIGH;
	
	public ModelTesterUnoInput(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	protected void initialize() {

		for(int i=0; i<digitalPinMode.length; i++) {
			pinMode(i, INPUT);
		}

	}

	@Override
	protected void mainLoop() {
		
		System.out.println(this + " > " + BinaryFormatter.getBinaryString(digitalPinState));

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
