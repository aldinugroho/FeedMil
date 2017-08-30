package com.uniteksolusi.feedmill.model;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.uniteksolusi.feedmill.i2c.ArduinoI2CSlave;

public class LoadCellMultiInArduino extends LoadCellArduino implements ArduinoI2CSlave {

	int inputABConfig = 0;
//	int inputScrewOffOnConfig = 0;
//	int outputCloseOpenConfig = 0;
//	int weightTargetconfig = 0;
	
	int inputABStatus = 0;
//	int inputScrewOffOnStatus = 0;
//	int outputCloseOpenStatus = 0;
//	int weightCurrentStatus = 0;
	
	
	
	public LoadCellMultiInArduino(I2CBus bus, int address) {
		super(bus, address);
	}
	
	

	@Override
	public boolean pushToDevice() throws IOException, InterruptedException {

		byte crc = (byte) System.currentTimeMillis();
		
		byte[] requestByte = new byte[7];
		requestByte[0] = (byte) ( (inputABConfig * (1 << 7))  //bit shifting operation (1 << 7) = 128, (1 << 6) = 64, etc. 
									+ (inputScrewOffOnConfig * (1 << 6))  
									+ (outputCloseOpenConfig * (1 << 5))  );
		
		requestByte[1] = (byte) ( ( (weightTargetconfig / 1000) * (1 << 4))  
									+ ( (weightTargetconfig / 100 - weightTargetconfig / 1000 * 10) * (1 << 0)) );
		
		requestByte[2] = (byte) ( ( (weightTargetconfig / 10 - weightTargetconfig / 100 * 10) * (1 << 4))  
									+ ( (weightTargetconfig / 1 - weightTargetconfig / 10 * 10) * (1 << 0)) );
		
		requestByte[3] = 0;
		requestByte[4] = 0;
		requestByte[5] = 0;
		
		requestByte[6] = crc;
		
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

		byte[] responseByte = new byte[5];
		this.read(responseByte, 0, responseByte.length);
		
		if(responseByte[6] == crc) {
					
			inputABStatus 	= ((responseByte[3] >> 7) & 1);
			inputScrewOffOnStatus 	= ((responseByte[3] >> 6) & 1);
			outputCloseOpenStatus 	= ((responseByte[3] >> 5) & 1);
			
			weightCurrentStatus = ((responseByte[4] >> 4) & 1) * 1000 + ((responseByte[4] >> 0) & 1) * 100
										+ ((responseByte[5] >> 4) & 1) * 10 + ((responseByte[5] >> 0) & 1) * 1;
								
			return true;
			
		} else {
			return false;
		}
	
	}
	

}
