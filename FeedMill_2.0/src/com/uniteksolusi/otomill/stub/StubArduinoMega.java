package com.uniteksolusi.otomill.stub;

public abstract class StubArduinoMega extends StubArduino {

//	/*
	//##---- JAVA ALTERNATIVE ----##//
	
	public StubArduinoMega(int address) {
		super(address);
		initializeConstants();
	}
	
	//### java helper only ###//
	void initializeAppliedDigitalPinState() {
		appliedDigitalPinState = new byte[54];
	}

	abstract void stateSpecificToResponse(byte[] responseByte);
	
	abstract void readSpecificData();
	
	abstract void overwriteDefaultConstants();
	//### end of java helper ###//

	byte digitalPinSize;
	byte exceptionPinSize;
	byte requestByteSize;
	byte responseByteSize;
	
	byte[] digitalPinMode; //0 = INPUT, 1 = OUTPUT
	byte[] digitalPinStateDefault; //not really filled yet
	byte[] digitalPinState;
	byte[] responseByte;

	/**
	 * prepare the responseByte with empty array
	 */
	void initResponseByte() {
		responseByte = new byte[responseByteSize]; //JAVA
	}
	
	void initializeConstants() {
		
		digitalPinSize = 54;
		exceptionPinSize = 1;
		requestByteSize = 9;
		responseByteSize = 9;
		
		digitalPinMode = new byte[digitalPinSize]; //0 = INPUT, 1 = OUTPUT
		digitalPinStateDefault = new byte[digitalPinSize]; //not really filled yet
		digitalPinState = new byte[digitalPinSize];
		responseByte = new byte[responseByteSize];
		
	}
	
	int SLAVE_ADDRESS = 0x00; //to be overwriten in arduino subclass
	//##---- END OF JAVA ALTERNATIVE ----##//
//	 */


	
	//### UnitekFeedMillUno.ino ###//
	/*	
	
	//from here below can be copied to ino file
	
	//##---- ARDUINO ALTERNATIVE ----##//
	 
	#include <Wire.h>
	#include <EEPROM.h> 
	 
	byte digitalPinSize;
	byte exceptionPinSize;
	byte requestByteSize;
	byte responseByteSize;
	
	byte digitalPinMode[54];  //0 = INPUT, 1 = OUTPUT
	byte digitalPinStateDefault[54]; //not really filled yet
	byte digitalPinState[54]; 
	byte responseByte[20];

	void initResponseByte() {
		responseByte[responseByteSize];
	}
	
	void initializeConstants() {
		
		digitalPinSize = 54;
		exceptionPinSize = 1;
		requestByteSize = 9;
		responseByteSize = 9;
		
		digitalPinMode[digitalPinSize]; //0 = INPUT, 1 = OUTPUT
		digitalPinStateDefault[digitalPinSize]; //not really filled yet
		digitalPinState[digitalPinSize];
		responseByte[responseByteSize];
		
	}
	//##---- END OF ARDUINO ----##//
//	 */
	

	int cycleDelay = 500; //ms
	int heartbeatTimeoutCycle = 20; //this means 10 cycle, which means 10x 500ms

	//status pin
	byte pinOperatingLED = A0;


	//below pin should not be used by Master
	byte exceptionPin[] = {A0};


	//### running variables ###//
	boolean isInitializedFromMaster = false;
	int heartbeatCounter = 0;

	String tempStr = "";
	
	boolean requestRecieved = false;


	///////////////////////////////

	void overwriteDefaultValues() {
		
	}
	
	void initI2C() {
		Wire.begin(SLAVE_ADDRESS);
		Wire.onReceive(receiveI2CData);
		Wire.onRequest(sendI2CData);
		debug("I2C Initialized");
		debugFlush();
	}

	void setup()
	{
		initializeConstants();
		overwriteDefaultConstants();
		
		//serial monitor for debug on pc
		Serial.begin(9600);

		//I2C setup
		initI2C();
		
		//local initialization
		initializeLocal();

		debug("Done Setup");
		debugFlush();
	}

	void initializeLocal() {	

		pinMode(pinOperatingLED, OUTPUT);

	}


	void loop() {

		if(heartbeatCounter >= heartbeatTimeoutCycle) {
			suspendOperation();
		}

		if(!isInitializedFromMaster) {
			readAllPins();
			//turn on LED
			errorLED();

		} else {

			//normal run, toggle
			toggleOperatingLED();

		}

//		readAllPins();
//		readSpecificData();

		heartbeatCounter++;

		//loop delay
		delay(cycleDelay);

	}


	


	void applyPinMode() {

		debug("Pin Exception is: ");
		
		for(byte i=0; i<digitalPinSize; i++) {

			
			boolean notException = true;
			for(byte k=0; k<exceptionPinSize; k++) {
				if(i == exceptionPin[k]) {
					notException = false;
					
					debug(k);
					debug(" ");
					
					break;
				}
			}

			if(notException) {
				if(digitalPinMode[i] == 0) {
					pinMode(i, INPUT);
				} else if(digitalPinMode[i] == 1) {
					pinMode(i, OUTPUT);
				}

			}

		}
		
		debugFlush();
		
		debug("PIN Mode Initialized: ");
		debug(getBinaryString(digitalPinMode));
		debugFlush();
		
		isInitializedFromMaster = true;

	}

	void applyPinState() {

		for(byte i=0; i<digitalPinSize; i++) {

			if(digitalPinMode[i] == 1) {
				digitalWrite(i, digitalPinState[i]);
			}

		}

	}


	void readAllPins() {

		for(byte i=0; i<digitalPinSize; i++) {

			if(digitalPinMode[i] == 0) {
				digitalPinState[i] = digitalRead(i);
			} else {
				//do not read OUTPUT
//				digitalPinState[i] = LOW;
			}

		}

	}


	void suspendOperation() {

		isInitializedFromMaster = false;
		errorLED();

		for(byte i=0; i<digitalPinSize; i++) {

			if(digitalPinMode[i] == 1) {
				digitalWrite(i, digitalPinStateDefault[i]);
			}

		}



	}


	void toggleOperatingLED() {
		digitalWrite(pinOperatingLED, (digitalRead(pinOperatingLED)^1));
	}

	void errorLED() {
		digitalWrite(pinOperatingLED, HIGH);
	}


	// ######### debugging commands #######//

	void debug(char str){
		tempStr += str;
	}

	void debug(String str) {
		tempStr += str;		
	}

	void debug(int i) {
		tempStr += i;
	}

	void debugFlush() {
		Serial.println(tempStr);
//		Serial.println((char)10);
		tempStr = "";
	}
	
	String getBinaryString(byte byteArray[]) {
		
		String tempString = "";
		for(int i=0; i<digitalPinSize; i++) {
			if(i%8 == 0) {
				tempString += " ";
			}
			tempString += byteArray[i];
		}
		
		return tempString;
	}

	
	
	//// mega specific ////
	
	void receiveI2CData(int byteCount) {
		
		requestRecieved = true;

		debug("Receiving I2C Data ");
//		debug(byteCount);
//		debugFlush();
		
		/* 4 bytes message:
		 * 	0 : message type
		 * 		0 : Request Status
		 * 		1 : Set Pin Mode
		 * 		2 : Set Pin State
		 *  1 : pin mode / pin state 0-7
		 *  2 : pin mode / pin state 8-13
		 *  3 : CRC
		 */
		byte messageType = -1; //= Wire.read();
		byte pin07 = -1; //= Wire.read();
		byte pin815 = -1; //= Wire.read();
		byte pin1623 = -1; //= Wire.read();
		byte pin2431 = -1; //= Wire.read();
		byte pin3239 = -1; //= Wire.read();
		byte pin4047 = -1; //= Wire.read();
		byte pin4853 = -1; //= Wire.read();
		byte crc = -1; // = Wire.read();
		
		messageType = Wire.read();
		pin07 = Wire.read();
		pin815 = Wire.read();
		pin1623 = Wire.read();
		pin2431 = Wire.read();
		pin3239 = Wire.read();
		pin4047 = Wire.read();
		pin4853 = Wire.read();
		crc = Wire.read();
		
		debug(messageType);
		debug(" ");
		debug(pin07);
		debug(" ");
		debug(pin815);
		debug(" ");
		debug(pin1623);
		debug(" ");
		debug(pin2431);
		debug(" ");
		debug(pin3239);
		debug(" ");
		debug(pin4047);
		debug(" ");
		debug(pin4853);
		debug(" ");
		debug(crc);
		debugFlush();
		
		if(crc != -1) {

			//everytime we receive valid data, reset heartbeatCounter
			heartbeatCounter = 0;
			
			initResponseByte();

			if(messageType == 0) {


			} else if(messageType == 1) {

				digitalPinMode[0]  = (byte) ((pin07 >> 0) & 1);
				digitalPinMode[1]  = (byte) ((pin07 >> 1) & 1);
				digitalPinMode[2]  = (byte) ((pin07 >> 2) & 1);
				digitalPinMode[3]  = (byte) ((pin07 >> 3) & 1);
				digitalPinMode[4]  = (byte) ((pin07 >> 4) & 1);
				digitalPinMode[5]  = (byte) ((pin07 >> 5) & 1);
				digitalPinMode[6]  = (byte) ((pin07 >> 6) & 1);
				digitalPinMode[7]  = (byte) ((pin07 >> 7) & 1);

				digitalPinMode[8]   = (byte) ((pin815 >> 0) & 1);
				digitalPinMode[9]   = (byte) ((pin815 >> 1) & 1);
				digitalPinMode[10]  = (byte) ((pin815 >> 2) & 1);
				digitalPinMode[11]  = (byte) ((pin815 >> 3) & 1);
				digitalPinMode[12]  = (byte) ((pin815 >> 4) & 1);
				digitalPinMode[13]  = (byte) ((pin815 >> 5) & 1);
				digitalPinMode[14]  = (byte) ((pin815 >> 6) & 1);
				digitalPinMode[15]  = (byte) ((pin815 >> 7) & 1);

				digitalPinMode[16]   = (byte) ((pin1623 >> 0) & 1);
				digitalPinMode[17]   = (byte) ((pin1623 >> 1) & 1);
				digitalPinMode[18]  = (byte) ((pin1623 >> 2) & 1);
				digitalPinMode[19]  = (byte) ((pin1623 >> 3) & 1);
				digitalPinMode[20]  = (byte) ((pin1623 >> 4) & 1);
				digitalPinMode[21]  = (byte) ((pin1623 >> 5) & 1);
				digitalPinMode[22]  = (byte) ((pin1623 >> 6) & 1);
				digitalPinMode[23]  = (byte) ((pin1623 >> 7) & 1);

				digitalPinMode[24]   = (byte) ((pin2431 >> 0) & 1);
				digitalPinMode[25]   = (byte) ((pin2431 >> 1) & 1);
				digitalPinMode[26]  = (byte) ((pin2431 >> 2) & 1);
				digitalPinMode[27]  = (byte) ((pin2431 >> 3) & 1);
				digitalPinMode[28]  = (byte) ((pin2431 >> 4) & 1);
				digitalPinMode[29]  = (byte) ((pin2431 >> 5) & 1);
				digitalPinMode[30]  = (byte) ((pin2431 >> 6) & 1);
				digitalPinMode[31]  = (byte) ((pin2431 >> 7) & 1);

				digitalPinMode[32]   = (byte) ((pin3239 >> 0) & 1);
				digitalPinMode[33]   = (byte) ((pin3239 >> 1) & 1);
				digitalPinMode[34]  = (byte) ((pin3239 >> 2) & 1);
				digitalPinMode[35]  = (byte) ((pin3239 >> 3) & 1);
				digitalPinMode[36]  = (byte) ((pin3239 >> 4) & 1);
				digitalPinMode[37]  = (byte) ((pin3239 >> 5) & 1);
				digitalPinMode[38]  = (byte) ((pin3239 >> 6) & 1);
				digitalPinMode[39]  = (byte) ((pin3239 >> 7) & 1);

				digitalPinMode[40]   = (byte) ((pin4047 >> 0) & 1);
				digitalPinMode[41]   = (byte) ((pin4047 >> 1) & 1);
				digitalPinMode[42]  = (byte) ((pin4047 >> 2) & 1);
				digitalPinMode[43]  = (byte) ((pin4047 >> 3) & 1);
				digitalPinMode[44]  = (byte) ((pin4047 >> 4) & 1);
				digitalPinMode[45]  = (byte) ((pin4047 >> 5) & 1);
				digitalPinMode[46]  = (byte) ((pin4047 >> 6) & 1);
				digitalPinMode[47]  = (byte) ((pin4047 >> 7) & 1);

				digitalPinMode[48]   = (byte) ((pin4853 >> 0) & 1);
				digitalPinMode[49]   = (byte) ((pin4853 >> 1) & 1);
				digitalPinMode[50]  = (byte) ((pin4853 >> 2) & 1);
				digitalPinMode[51]  = (byte) ((pin4853 >> 3) & 1);
				digitalPinMode[52]  = (byte) ((pin4853 >> 4) & 1);
				digitalPinMode[53]  = (byte) ((pin4853 >> 5) & 1);

				debug("Apply PIN MODE ");
				debug(getBinaryString(digitalPinMode));
				debugFlush();

				applyPinMode();

			} else if(messageType == 2) {

				digitalPinState[0]  = (byte) ((pin07 >> 0) & 1);
				digitalPinState[1]  = (byte) ((pin07 >> 1) & 1);
				digitalPinState[2]  = (byte) ((pin07 >> 2) & 1);
				digitalPinState[3]  = (byte) ((pin07 >> 3) & 1);
				digitalPinState[4]  = (byte) ((pin07 >> 4) & 1);
				digitalPinState[5]  = (byte) ((pin07 >> 5) & 1);
				digitalPinState[6]  = (byte) ((pin07 >> 6) & 1);
				digitalPinState[7]  = (byte) ((pin07 >> 7) & 1);

				digitalPinState[8]   = (byte) ((pin815 >> 0) & 1);
				digitalPinState[9]   = (byte) ((pin815 >> 1) & 1);
				digitalPinState[10]  = (byte) ((pin815 >> 2) & 1);
				digitalPinState[11]  = (byte) ((pin815 >> 3) & 1);
				digitalPinState[12]  = (byte) ((pin815 >> 4) & 1);
				digitalPinState[13]  = (byte) ((pin815 >> 5) & 1);
				digitalPinState[14]  = (byte) ((pin815 >> 6) & 1);
				digitalPinState[15]  = (byte) ((pin815 >> 7) & 1);

				digitalPinState[16]   = (byte) ((pin1623 >> 0) & 1);
				digitalPinState[17]   = (byte) ((pin1623 >> 1) & 1);
				digitalPinState[18]  = (byte) ((pin1623 >> 2) & 1);
				digitalPinState[19]  = (byte) ((pin1623 >> 3) & 1);
				digitalPinState[20]  = (byte) ((pin1623 >> 4) & 1);
				digitalPinState[21]  = (byte) ((pin1623 >> 5) & 1);
				digitalPinState[22]  = (byte) ((pin1623 >> 6) & 1);
				digitalPinState[23]  = (byte) ((pin1623 >> 7) & 1);

				digitalPinState[24]   = (byte) ((pin2431 >> 0) & 1);
				digitalPinState[25]   = (byte) ((pin2431 >> 1) & 1);
				digitalPinState[26]  = (byte) ((pin2431 >> 2) & 1);
				digitalPinState[27]  = (byte) ((pin2431 >> 3) & 1);
				digitalPinState[28]  = (byte) ((pin2431 >> 4) & 1);
				digitalPinState[29]  = (byte) ((pin2431 >> 5) & 1);
				digitalPinState[30]  = (byte) ((pin2431 >> 6) & 1);
				digitalPinState[31]  = (byte) ((pin2431 >> 7) & 1);

				digitalPinState[32]   = (byte) ((pin3239 >> 0) & 1);
				digitalPinState[33]   = (byte) ((pin3239 >> 1) & 1);
				digitalPinState[34]  = (byte) ((pin3239 >> 2) & 1);
				digitalPinState[35]  = (byte) ((pin3239 >> 3) & 1);
				digitalPinState[36]  = (byte) ((pin3239 >> 4) & 1);
				digitalPinState[37]  = (byte) ((pin3239 >> 5) & 1);
				digitalPinState[38]  = (byte) ((pin3239 >> 6) & 1);
				digitalPinState[39]  = (byte) ((pin3239 >> 7) & 1);

				digitalPinState[40]   = (byte) ((pin4047 >> 0) & 1);
				digitalPinState[41]   = (byte) ((pin4047 >> 1) & 1);
				digitalPinState[42]  = (byte) ((pin4047 >> 2) & 1);
				digitalPinState[43]  = (byte) ((pin4047 >> 3) & 1);
				digitalPinState[44]  = (byte) ((pin4047 >> 4) & 1);
				digitalPinState[45]  = (byte) ((pin4047 >> 5) & 1);
				digitalPinState[46]  = (byte) ((pin4047 >> 6) & 1);
				digitalPinState[47]  = (byte) ((pin4047 >> 7) & 1);

				digitalPinState[48]   = (byte) ((pin4853 >> 0) & 1);
				digitalPinState[49]   = (byte) ((pin4853 >> 1) & 1);
				digitalPinState[50]  = (byte) ((pin4853 >> 2) & 1);
				digitalPinState[51]  = (byte) ((pin4853 >> 3) & 1);
				digitalPinState[52]  = (byte) ((pin4853 >> 4) & 1);
				digitalPinState[53]  = (byte) ((pin4853 >> 5) & 1);
				
				debug("Apply Pin State ");
				debug(getBinaryString(digitalPinState));
				debugFlush();

				applyPinState();

			} 

			stateToResponse();
			stateSpecificToResponse(responseByte);

			//reach here without error?
			if(isInitializedFromMaster) {
//				debug("Response OK");
//				debugFlush();
				responseByte[0] = 2;
			} else {
				responseByte[0] = 0;
				debug("Response NOT INITIALIZED");
				debugFlush();
			}
			responseByte[responseByteSize-1] = crc;
			

		} else {
			debug("Invalid incoming message. Byte Length:  ");
			debug(byteCount);
			debugFlush();
		}



	}
	
	void sendI2CData() {
		
		if(requestRecieved) {

			requestRecieved = false;

			debug("Sending I2C Data: ");
			debug(responseByte[0]);
			debug(" ");
			debug(responseByte[1]);
			debug(" ");
			debug(responseByte[2]);
			debug(" ");
			debug(responseByte[3]);
			debug(" ");
			debug(responseByte[4]);
			debug(" ");
			debug(responseByte[5]);
			debug(" ");
			debug(responseByte[6]);
			debug(" ");
			debug(responseByte[7]);
			debug(" ");
			debug(responseByte[8]);
			debug(" ");
			debugFlush();

			//if(responseByte != null) {

			Wire.write(responseByte, responseByteSize);

			//responseByte = null;
			//}

			debugFlush();

		} else {
			requestRecieved = false;
			//incorrect sequence procedure, reinit Wire
			initI2C();
		}
		
	}

	
	void stateToResponse() {
		
		readAllPins();
		readSpecificData();

		responseByte[1] = (byte) ( 
									(digitalPinState[0] * (1 << 0))    //rx
									+ (digitalPinState[1] * (1 << 1))  //tx
									+ (digitalPinState[2] * (1 << 2))
									+ (digitalPinState[3] * (1 << 3))
									+ (digitalPinState[4] * (1 << 4))
									+ (digitalPinState[5] * (1 << 5))
									+ (digitalPinState[6] * (1 << 6))
									+ (digitalPinState[7] * (1 << 7))  
									);

		responseByte[2] = (byte) ( (digitalPinState[8] * (1 << 0))   
									+ (digitalPinState[9] * (1 << 1))
									+ (digitalPinState[10] * (1 << 2))  
									+ (digitalPinState[11] * (1 << 3))
									+ (digitalPinState[12] * (1 << 4))
									+ (digitalPinState[13] * (1 << 5))
									+ (digitalPinState[14] * (1 << 6)) 
									+ (digitalPinState[15] * (1 << 7)) 
									);
		
		responseByte[3] = (byte) ( (digitalPinState[16] * (1 << 0))   
									+ (digitalPinState[17] * (1 << 1))
									+ (digitalPinState[18] * (1 << 2))  
									+ (digitalPinState[19] * (1 << 3))
									+ (digitalPinState[20] * (1 << 4))
									+ (digitalPinState[21] * (1 << 5))
									+ (digitalPinState[22] * (1 << 6)) 
									+ (digitalPinState[23] * (1 << 7)) 
									);
		
		responseByte[4] = (byte) ( (digitalPinState[24] * (1 << 0))   
									+ (digitalPinState[25] * (1 << 1))
									+ (digitalPinState[26] * (1 << 2))  
									+ (digitalPinState[27] * (1 << 3))
									+ (digitalPinState[28] * (1 << 4))
									+ (digitalPinState[29] * (1 << 5))
									+ (digitalPinState[30] * (1 << 6)) 
									+ (digitalPinState[31] * (1 << 7)) 
									);

		responseByte[5] = (byte) ( (digitalPinState[32] * (1 << 0))   
									+ (digitalPinState[33] * (1 << 1))
									+ (digitalPinState[34] * (1 << 2))  
									+ (digitalPinState[35] * (1 << 3))
									+ (digitalPinState[36] * (1 << 4))
									+ (digitalPinState[37] * (1 << 5))
									+ (digitalPinState[38] * (1 << 6)) 
									+ (digitalPinState[39] * (1 << 7)) 
									);
		
		responseByte[6] = (byte) ( (digitalPinState[40] * (1 << 0))   
									+ (digitalPinState[41] * (1 << 1))
									+ (digitalPinState[42] * (1 << 2))  
									+ (digitalPinState[43] * (1 << 3))
									+ (digitalPinState[44] * (1 << 4))
									+ (digitalPinState[45] * (1 << 5))
									+ (digitalPinState[46] * (1 << 6)) 
									+ (digitalPinState[47] * (1 << 7)) 
									);

		responseByte[7] = (byte) ( (digitalPinState[48] * (1 << 0))   
									+ (digitalPinState[49] * (1 << 1))
									+ (digitalPinState[50] * (1 << 2))  
									+ (digitalPinState[51] * (1 << 3))
									+ (digitalPinState[52] * (1 << 4))
									+ (digitalPinState[53] * (1 << 5))
									);

	}
	
	//---- end of copy Arduino Ino ----//
	
}
