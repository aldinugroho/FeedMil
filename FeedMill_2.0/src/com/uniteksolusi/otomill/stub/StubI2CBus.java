package com.uniteksolusi.otomill.stub;

import java.io.IOException;
import java.util.HashMap;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

public class StubI2CBus implements I2CBus {
	
	private static StubI2CBus instance = null;
	public static StubI2CBus getInstance() {
		if(instance == null) {
			synchronized (StubI2CBus.class) {
				if(instance == null) {
					instance = new StubI2CBus();
				}
			}
		}
		return instance;
	}
	
	
	private HashMap<Integer, StubArduino> deviceMap = new HashMap<Integer, StubArduino>(); 

	public StubI2CBus() {
		// TODO Auto-generated constructor stub
	}
	
	public StubArduino registerStub(StubArduino ard) {
		return this.registerStub(ard.getAddress(), ard);
	}
	
	public StubArduino registerStub(int address, StubArduino ard) {
		return deviceMap.put(address, ard);
	}
	
	public StubArduino getStub(int address) {
		return deviceMap.get(address);
	}

	@Override
	public I2CDevice getDevice(int address) throws IOException {
		return new StubI2CDeviceImpl(this, address);
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public int getFileDescriptor() {
		return 0;
	}

	@Override
	public String getFileName() {
		return "OnMemoryDummyBus";
	}

}
