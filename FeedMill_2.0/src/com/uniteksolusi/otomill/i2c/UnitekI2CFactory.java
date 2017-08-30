package com.uniteksolusi.otomill.i2c;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CBusImpl;
import com.uniteksolusi.otomill.stub.StubI2CBus;
import com.uniteksolusi.otomill.stub.StubI2CDeviceImpl;

public class UnitekI2CFactory {
	
	private static I2CBus i2cBus = null; //make sure there is only 1 bus
	
	public static I2CBus getI2CBus() throws IOException {
		
		if(i2cBus == null) {
			if("true".equals(System.getProperty("stub.enabled"))) {
				i2cBus = new StubI2CBus();
			} else {
				i2cBus = I2CFactory.getInstance(I2CBus.BUS_1);
			}
		}
		
		return i2cBus;
		
	}

	public static UnitekI2CDevice getI2CDevice(I2CBus bus, int address) {
		if(bus instanceof StubI2CBus) {
			return new StubI2CDeviceImpl(bus, address);
		} else if(bus instanceof I2CBusImpl) {
			return new Pi4jI2CDeviceImpl((I2CBusImpl) bus, address);
		} else {
			return null;
		}
	}

}
