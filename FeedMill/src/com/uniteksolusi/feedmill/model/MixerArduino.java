package com.uniteksolusi.feedmill.model;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;
import com.uniteksolusi.feedmill.i2c.ArduinoI2CSlave;

public class MixerArduino extends I2CDeviceImpl implements ArduinoI2CSlave {
	
	private static int i2cDataSize = 3; //data size in bytes 
	
	int mixerOffOnConfig = 0;
	int outputCloseOpenConfig = 0;
	int outputScrewOffOnConfig = 0;
	
	int mixerOffOnStatus = 0;
	int outputCloseOpenStatus = 0;
	int outputScrewOffOnStatus = 0;
	
	int readyForMixingStatus = 0;
	

	public MixerArduino(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	public boolean pushToDevice() throws IOException, InterruptedException {

		byte crc = (byte) System.currentTimeMillis();
		
		byte[] requestByte = new byte[i2cDataSize];
		
		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
		requestByte[0] = (byte) ( (mixerOffOnConfig * (1 << 7))  
									+ (outputCloseOpenConfig * (1 << 6))  
									+ (outputScrewOffOnConfig * (1 << 5))  );
		
		requestByte[1] = 0;
		
		requestByte[2] = crc;
		
		write(requestByte, 0, requestByte.length);
		
		Thread.sleep(i2cResponseTime);

		byte[] responseByte = new byte[2];
		this.read(responseByte, 0, responseByte.length);
		
		if(responseByte[0] == 1 && responseByte[1] == crc) {
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public boolean pullFromDevice() throws IOException, InterruptedException {

		byte crc = (byte) System.currentTimeMillis();
		
		byte[] requestByte = new byte[2];
		requestByte[0] = 0;
		requestByte[1] = crc;
		
		write(requestByte, 0, requestByte.length);
		
		Thread.sleep(i2cResponseTime);

		byte[] responseByte = new byte[i2cDataSize];
		this.read(responseByte, 0, responseByte.length);
		
		if(responseByte[2] == crc) {
					
			mixerOffOnStatus 		= ((responseByte[1] >> 7) & 1);
			outputCloseOpenStatus 	= ((responseByte[1] >> 6) & 1);
			outputScrewOffOnStatus 	= ((responseByte[1] >> 5) & 1);
											
			return true;
			
		} else {
			return false;
		}

	}
	
	public boolean isReadyForMixing() {
		if(readyForMixingStatus == 1) {
			return true;
		}
		return false;
	}
	
	public void startMixing() {
		
	}

}
