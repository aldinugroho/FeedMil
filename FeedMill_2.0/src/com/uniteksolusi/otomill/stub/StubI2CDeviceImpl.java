package com.uniteksolusi.otomill.stub;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.uniteksolusi.otomill.i2c.UnitekI2CDevice;

public class StubI2CDeviceImpl implements UnitekI2CDevice, I2CDevice {

	StubArduino ard; 
	I2CBus bus;
	int address;
	
	public StubI2CDeviceImpl(I2CBus bus, int address) {
		this.bus = bus;
		this.address = address;
		this.ard = StubI2CBus.getInstance().getStub(address);
	}

	@Override
	public void write(byte[] data, int offset, int size) throws IOException {
		for(int i=offset; i < offset+size; i++) {
			ard.Wire.write(data[i]);
		}
		ard.receiveI2CData(size);
	}

	@Override
	public int read(byte[] data, int offset, int size) throws IOException {

		ard.sendI2CData();
		
		for(int i=0; i<offset; i++) {
			ard.Wire.read(); //first throw away the offset
		}
		
		for(int i=0; i<size; i++) {
			data[i] = ard.Wire.read(); //read the data
		}
		
		return 0;
	}

	@Override
	public void write(byte b) throws IOException {
		byte[] byteArray = {b};
		this.write(byteArray, 0, 1);
	}

	@Override
	public void write(int address, byte b) throws IOException {
		byte[] byteArray = {b};
		this.write(byteArray, 0, 1);
	}

	@Override
	public void write(int address, byte[] buffer, int offset, int size) throws IOException {
		this.write(buffer, offset, size);
	}

	@Override
	public int read() throws IOException {
		ard.sendI2CData();
		return ard.Wire.read();
	}

	@Override
	public int read(int address) throws IOException {
		ard.sendI2CData();
		return ard.Wire.read();
	}

	@Override
	public int read(int address, byte[] buffer, int offset, int size) throws IOException {
		return this.read(buffer, offset, size);
	}

	@Override
	public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize)
			throws IOException {
		return this.read(readBuffer, readOffset, readSize);
	}

	@Override
	public int getAddress() {
		return this.address;
	}

}
