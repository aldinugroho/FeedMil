package com.uniteksolusi.feedmill.model;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;
import com.uniteksolusi.feedmill.i2c.ArduinoI2CSlave;





/**
 * @author nanda
 * 
 *
 */
public class SimpleBufferArduino extends I2CDeviceImpl implements ArduinoI2CSlave {
	
	private static int i2cDataSize = 5; //data size in bytes
	
	int mbmAutoFillConfig = 2;
	int gritAutoFillConfig = 2;
	int katulAutoFillConfig = 2;
	
	int mbmStartFillLevelConfig = 0;
	int gritStartFillLevelConfig = 0;
	int katulStartFillLevelConfig = 0;
	
	int mbmBucketStatus = 0;
	int gritBucketStatus = 0;
	int katulBucketStatus = 0;
	
	int mbmLevelStatus = 0;
	int gritLevelStatus = 0;
	int katulLevelStatus = 0;
	
	public SimpleBufferArduino(I2CBus theBus, int theAddress) {
		super(theBus, theAddress);	
	}

	@Override
	public boolean pushToDevice() throws IOException, InterruptedException {

		byte crc = (byte) System.currentTimeMillis();
		
		byte[] requestByte = new byte[i2cDataSize];
		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
		requestByte[0] = (byte) ( (mbmAutoFillConfig * (1 << 6))   
									+ (gritAutoFillConfig * (1 << 4))
									+ (katulAutoFillConfig * (1 << 2))  
									);
		
		requestByte[1] = (byte) ( (mbmStartFillLevelConfig * (1 << 6))  
									+ (gritStartFillLevelConfig * (1 << 4))  
									+ (katulStartFillLevelConfig * (1 << 2))  
									);
		
		requestByte[2] = 0;
		requestByte[3] = 0;
		requestByte[4] = crc;
		
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
		
		if(responseByte[4] == crc) {
		
//			mbmFillStateConfig 		= ((responseByte[0] >> 7) & 1) * 2 + ((responseByte[0] >> 6) & 1) * 1;
//			gritFillStateConfig 	= ((responseByte[0] >> 5) & 1) * 2 + ((responseByte[0] >> 4) & 1) * 1;
//			katulFillStateConfig 	= ((responseByte[0] >> 3) & 1) * 2 + ((responseByte[0] >> 2) & 1) * 1;
//			
//			mbmStartFillLevelConfig 	= ((responseByte[1] >> 7) & 1) * 2 + ((responseByte[1] >> 6) & 1) * 1;
//			gritStartFillLevelConfig 	= ((responseByte[1] >> 5) & 1) * 2 + ((responseByte[1] >> 4) & 1) * 1;
//			katulStartFillLevelConfig 	= ((responseByte[1] >> 3) & 1) * 2 + ((responseByte[1] >> 2) & 1) * 1;
			
			mbmBucketStatus 	= ((responseByte[2] >> 7) & 1) * 2 + ((responseByte[2] >> 6) & 1) * 1;
			gritBucketStatus 	= ((responseByte[2] >> 5) & 1) * 2 + ((responseByte[2] >> 4) & 1) * 1;
			katulBucketStatus 	= ((responseByte[2] >> 3) & 1) * 2 + ((responseByte[2] >> 2) & 1) * 1;
			
			mbmLevelStatus 		= ((responseByte[3] >> 7) & 1) * 2 + ((responseByte[3] >> 6) & 1) * 1;
			gritLevelStatus 	= ((responseByte[3] >> 5) & 1) * 2 + ((responseByte[3] >> 4) & 1) * 1;
			katulLevelStatus 	= ((responseByte[3] >> 3) & 1) * 2 + ((responseByte[3] >> 2) & 1) * 1;
			
			return true;
			
		} else {
			return false;
		}
		
	}

}
