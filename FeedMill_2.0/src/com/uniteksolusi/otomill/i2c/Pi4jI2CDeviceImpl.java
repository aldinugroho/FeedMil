package com.uniteksolusi.otomill.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.impl.I2CBusImpl;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;

public class Pi4jI2CDeviceImpl implements UnitekI2CDevice, I2CDevice {
	
	public static int READ_RETRY = 3;
	public static int WRITE_RETRY = 3;
	
	private I2CDeviceImpl i2cDevice;
	I2CBus bus;
	int address;
	

	public Pi4jI2CDeviceImpl(I2CBusImpl bus, int address) {
		this.bus = bus;
		this.address = address;
		i2cDevice = new I2CDeviceImpl(bus, address);
	}

	@Override
	public void write(byte[] data, int offset, int size) throws IOException {
//		System.out.println( "Pi4jI2CDeviceImpl -- write() ");
		
		int retry = 1;
		while(retry<=WRITE_RETRY) {
			try {
				i2cDevice.write(data, offset, size);
				retry = WRITE_RETRY+1;
			} catch (IOException ioe) {
				if(retry >= WRITE_RETRY) {
					throw ioe;
				} else {
					retry++;
				}
			}
		}
		
	}

	@Override
	public int read(byte[] data, int offset, int size) throws IOException {
		return i2cDevice.read(data, offset, size);
	}

	@Override
	public void write(byte b) throws IOException {
		i2cDevice.write(b);
	}

	@Override
	public void write(int address, byte b) throws IOException {
		i2cDevice.write(address, b);
	}

	@Override
	public void write(int address, byte[] buffer, int offset, int size) throws IOException {
		i2cDevice.write(address, buffer, offset, size);
	}

	@Override
	public int read() throws IOException {
		return i2cDevice.read();
	}

	@Override
	public int read(int address) throws IOException {
		return i2cDevice.read(address);
	}

	@Override
	public int read(int address, byte[] buffer, int offset, int size) throws IOException {
		return i2cDevice.read(address, buffer, offset, size);
	}

	@Override
	public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize)
			throws IOException {
		return i2cDevice.read(writeBuffer, writeOffset, writeSize, readBuffer, readOffset, readSize);
	}

	@Override
	public int getAddress() {
		return this.address;
	}

}
