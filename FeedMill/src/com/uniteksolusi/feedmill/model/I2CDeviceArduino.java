package com.uniteksolusi.feedmill.model;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

/**
 * @author nanda
 * 
 * not used for now, use the library I2CDeviceImpl directly
 * 
 */
public abstract class I2CDeviceArduino {

	int i2cDeviceAddress;
	I2CBus i2cBus;
	
	I2CDevice i2cDevice;
	
	long i2cPushTimeInterval = 500;
	long i2cPullTimeInterval = 500;
	
	public I2CDeviceArduino(I2CBus theBus, int theAddress) {
		this.i2cDeviceAddress = theAddress;
		this.i2cBus = theBus;
		
		try {
			this.i2cDevice = i2cBus.getDevice(i2cDeviceAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
//	public I2CDeviceArduino(I2CDevice theDevice) {
//	
//		this.i2cDevice = theDevice;
//	
//	}

	public abstract boolean pushToDevice();
	
	public abstract boolean pullFromDevice();
	
}
