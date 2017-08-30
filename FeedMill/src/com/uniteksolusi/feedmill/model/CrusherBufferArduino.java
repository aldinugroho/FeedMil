package com.uniteksolusi.feedmill.model;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;
import com.uniteksolusi.feedmill.i2c.ArduinoI2CSlave;

public class CrusherBufferArduino extends I2CDeviceImpl implements ArduinoI2CSlave {

	private static int i2cDataSize = 8; //data size in bytes
	
	int jagungSBMConfig = 1;
	int blowerOffOnConfig = 1;
	int crusherOffOnConfig = 1;
	
	int outputJagung1Config = 1;
	int outputJagung2Config = 0;
	int outputSBM1Config = 0;
	
	int autoFillJagung1Config = 2;
	int autoFillJagung2Config = 2;
	int autoFillSBM1Config = 2;
	int autoFillSBM2Config = 2;
	
	int startFillLevelJagung1Config = 0;
	int startFillLevelJagung2Config = 0;
	int startFillLevelSBM1Config = 0;
	int startFillLevelSBM2Config = 0;
	
	int jagungSBMStatus = 0;
	int blowerOffOnStatus = 0;
	int blowerOutOffOnStatus = 0;
	int crusherOffOnStatus = 0;
	int crusherOutOffOnStatus = 0;
	int crusherFillEmptyStatus = 0;
	int movingOutLeftStatus = 0;
	int movingOutRightStatus = 0;
	
	int outputJagung1Status = 0;
	int outputJagung2Status = 0;
	int outputSBM1Status = 0;
	
	int fillLevelJagung1Status = 0;
	int fillLevelJagung2Status = 0;
	int fillLevelSBM1Status = 0;
	int fillLevelSBM2Status = 0;
	
	
	
	
	public CrusherBufferArduino(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	public boolean pushToDevice() throws IOException, InterruptedException {

		byte crc = (byte) System.currentTimeMillis();
		
		byte[] requestByte = new byte[i2cDataSize];
		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
		requestByte[0] = (byte) ( (jagungSBMConfig * (1 << 7))   
									+ (blowerOffOnConfig * (1 << 7))
									+ (crusherOffOnConfig * (1 << 4))  
									);
		
		requestByte[1] = (byte) ( (outputJagung1Config * (1 << 6))  
									+ (outputJagung2Config * (1 << 4))  
									+ (outputSBM1Config * (1 << 2))  
									);
		
		requestByte[2] = (byte) ( (autoFillJagung1Config * (1 << 6))  
									+ (autoFillJagung2Config * (1 << 4))  
									+ (autoFillSBM1Config * (1 << 2))  
									+ (autoFillSBM2Config * (1 << 0))
									);

		requestByte[3] = (byte) ( (startFillLevelJagung1Config * (1 << 6))  
									+ (startFillLevelJagung2Config * (1 << 4))  
									+ (startFillLevelSBM1Config * (1 << 2))  
									+ (startFillLevelSBM2Config * (1 << 0))
									);
		
		requestByte[4] = 0;
		requestByte[5] = 0;
		requestByte[6] = 0;
		
		requestByte[7] = crc;
		
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
		
		if(responseByte[7] == crc) {
			
			jagungSBMStatus 		= ((responseByte[4] >> 7) & 1) ;
			blowerOffOnStatus 		= ((responseByte[4] >> 6) & 1) ;
			blowerOutOffOnStatus 	= ((responseByte[4] >> 5) & 1) ;
			crusherOffOnStatus 		= ((responseByte[4] >> 4) & 1) ;
			crusherOutOffOnStatus 	= ((responseByte[4] >> 3) & 1) ;
			crusherFillEmptyStatus 	= ((responseByte[4] >> 2) & 1) ;
			movingOutLeftStatus 	= ((responseByte[4] >> 1) & 1) ;
			movingOutRightStatus	= ((responseByte[4] >> 0) & 1) ;
					
			outputJagung1Status = ((responseByte[5] >> 6) & 1) ;
			outputJagung2Status = ((responseByte[5] >> 4) & 1) ;
			outputSBM1Status 	= ((responseByte[5] >> 2) & 1) ;
			
			fillLevelJagung1Status 	= ((responseByte[6] >> 7) & 1) * 2 + ((responseByte[6] >> 6) & 1) * 1;
			fillLevelJagung2Status 	= ((responseByte[6] >> 5) & 1) * 2 + ((responseByte[6] >> 4) & 1) * 1;
			fillLevelSBM1Status 	= ((responseByte[6] >> 3) & 1) * 2 + ((responseByte[6] >> 2) & 1) * 1;
			fillLevelSBM2Status 	= ((responseByte[6] >> 1) & 1) * 2 + ((responseByte[6] >> 0) & 1) * 1;
			
			return true;
			
		} else {
			return false;
		}
		
	}

}
