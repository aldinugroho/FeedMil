package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public abstract class ArduinoUnoModel extends ArduinoModel {
	
	public static final int DEFAULT_AMOUNT_DIGITAL_PIN = 14;
	public static final int DEFAULT_REQUEST_BYTE_LENGTH = 4;
	public static final int DEFAULT_RESPONSE_BYTE_LENGTH = 4;
		
	public ArduinoUnoModel(I2CBus bus, int address, int amountDigitalPin, int i2cRequestByteLength, int i2cResponseByteLength) {
		super(bus, address, amountDigitalPin, i2cRequestByteLength, i2cResponseByteLength);
		
	}
		
	public ArduinoUnoModel(I2CBus bus, int address) {
		this(bus, address, DEFAULT_AMOUNT_DIGITAL_PIN, DEFAULT_REQUEST_BYTE_LENGTH, DEFAULT_RESPONSE_BYTE_LENGTH);
	}
	


}
