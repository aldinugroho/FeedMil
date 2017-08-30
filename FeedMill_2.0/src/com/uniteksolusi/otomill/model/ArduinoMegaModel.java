package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

/**
 * @author nanda
 * 
 * This Model is representation of Arduino Mega.
 * It works the same as the super class, however it has more Pins
 * 
 */
public abstract class ArduinoMegaModel extends ArduinoModel {
	
	public static int DEFAULT_AMOUNT_DIGITAL_PIN = 54;
	public static int DEFAULT_REQUEST_BYTE_LENGTH = 9;
	public static int DEFAULT_RESPONSE_BYTE_LENGTH = 9;
	
	public ArduinoMegaModel(I2CBus bus, int address, int amountDigitalPin, int i2cRequestByteLength, int i2cResponseByteLength) {
		super(bus, address, amountDigitalPin, i2cRequestByteLength, i2cResponseByteLength);
		
	}
	public ArduinoMegaModel(I2CBus bus, int address) {
		this(bus, address, DEFAULT_AMOUNT_DIGITAL_PIN, DEFAULT_REQUEST_BYTE_LENGTH, DEFAULT_RESPONSE_BYTE_LENGTH);
	}
	
	
//	protected void pushDigitalPinMode(byte[] requestByte) {
//	
//		int indexRequest = 1;
//		int indexPin = 0;
//		while(indexPin < digitalPinMode.length) {
//			requestByte[indexRequest] = 0;
//			for(int indexPos=0; indexPos < 8; indexPos++, indexPin++) {
//				//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
//				if(indexPin< digitalPinMode.length) {
//					requestByte[indexRequest] += (byte) ( (digitalPinMode[indexPin] * (1 << indexPos)) );  
//				}
//			}
//			indexRequest++;
//		}
//		
//		
////		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
////		requestByte[1] = (byte) ( 
////									(digitalPinMode[0] * (1 << 0))    //rx
////									+ (digitalPinMode[1] * (1 << 1))  //tx
////									+ (digitalPinMode[2] * (1 << 2))
////									+ (digitalPinMode[3] * (1 << 3))
////									+ (digitalPinMode[4] * (1 << 4))
////									+ (digitalPinMode[5] * (1 << 5))
////									+ (digitalPinMode[6] * (1 << 6))
////									+ (digitalPinMode[7] * (1 << 7))  
////									);
////				
////		requestByte[2] = (byte) ( (digitalPinMode[8] * (1 << 0))   
////									+ (digitalPinMode[9] * (1 << 1))
////									+ (digitalPinMode[10] * (1 << 2))  
////									+ (digitalPinMode[11] * (1 << 3))
////									+ (digitalPinMode[12] * (1 << 4))
////									+ (digitalPinMode[13] * (1 << 5)) 
////									);
//						
//		
//	}
//
//	
//	protected void pushDigitalPinState(byte[] requestByte) {
//		
//		//TODO what to do with :
//		//		(digitalPinState[0] * (1 << 0))    //rx
//		//				+ (digitalPinState[1] * (1 << 1))  //tx
//		//ignore?? if INPUT/OUTPUT not set, then will be ignored by arduino??
//			
//		int indexRequest = 1;
//		int indexPin = 0;
//		while(indexPin < digitalPinState.length) {
//			requestByte[indexRequest] = 0;
//			for(int indexPos=0; indexPos < 8; indexPos++, indexPin++) {
//				//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
//				if(indexPin< digitalPinMode.length) {
//					requestByte[indexRequest] += (byte) ( (digitalPinState[indexPin] * (1 << indexPos)) );   
//				}
//			}
//			indexRequest++;
//		}
//		
//	}
//	
	
//	protected void pullDigitalPinState(byte[] responseByte) {
//
//		//TODO to test
//		//0 message type
//		//1 pin 0-7
//		//2 pin 8-15
//		//3 pin 16-23
//		//4 pin 24-31
//		//5 pin 32-39
//		//6 pin 40-57
//		//7 crc
//		int indexResponse = 1;
//		int indexPin = 0;
//		while(indexPin < digitalPinState.length) {
//			for(int indexPos=0; indexPos < 8; indexPos++, indexPin++) {
//				if(indexPin< digitalPinMode.length) {
//					digitalPinState[indexPin]  = (byte) ((responseByte[indexResponse] >> indexPos) & 1);
//				}
//			}
//			indexResponse++;
//		}
//					
//	}
	
}
