package com.uniteksolusi.otomill.stub;

public abstract class StubArduinoUno extends StubArduino {


//	/*
	//##---- JAVA ALTERNATIVE ONLY ----##//
	
	public StubArduinoUno(int address) {
		super(address);
		initializeConstants();
	}
	
	void initializeAppliedDigitalPinState() {
		appliedDigitalPinState = new byte[14];
	}

	abstract void stateSpecificToResponse(byte[] responseByte);
	
	abstract void readSpecificData();
	
	abstract void loopSpecific();
	
	abstract void setupSpecific();
	
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

	void initResponseByte() {
		responseByte = new byte[responseByteSize]; //JAVA
	}
	
	void initializeConstants() {
		
		digitalPinSize = 14;
		exceptionPinSize = 1;
		requestByteSize = 4;
		responseByteSize = 4;
		
		digitalPinMode = new byte[digitalPinSize]; //0 = INPUT, 1 = OUTPUT
		digitalPinStateDefault = new byte[digitalPinSize]; //not really filled yet
		digitalPinState = new byte[digitalPinSize];
		responseByte = new byte[responseByteSize];
		
	}
	
	
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
	
	byte digitalPinMode[14];  //0 = INPUT, 1 = OUTPUT
	byte digitalPinStateDefault[14]; //not really filled yet
	byte digitalPinState[14]; 
	byte responseByte[10];

	void initResponseByte() {
		responseByte[responseByteSize];
	}
	
	void initializeConstants() {
		
		digitalPinSize = 14;
		exceptionPinSize = 1;
		requestByteSize = 4;
		responseByteSize = 4;
		
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

		setupSpecific();
		
		debug("Done Setup");
		debugFlush();
	}

	void initializeLocal() {	

		pinMode(pinOperatingLED, OUTPUT);

	}


	void loop() {

		if(heartbeatCounter >= heartbeatTimeoutCycle) {
			suspendOperation();
			initI2C(); //reinit I2C
			heartbeatCounter = 0;
		}

		if(!isInitializedFromMaster) {

			//turn on LED
			errorLED();

		} else {

			//normal run, toggle
			toggleOperatingLED();

		}

		loopSpecific();
//		readAllPins();
//		readSpecificData();

		heartbeatCounter++;

		//loop delay
		delay(cycleDelay);

	}


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
		byte pin813 = -1; //= Wire.read();
		byte crc = -1; // = Wire.read();
		
		messageType = Wire.read();
		pin07 = Wire.read();
		pin813 = Wire.read();
		crc = Wire.read();
		
		debug(messageType);
		debug(" ");
		debug(pin07);
		debug(" ");
		debug(pin813);
		debug(" ");
		debug(crc);
		debugFlush();
		
		if(crc != -1) {

			//everytime we receive valid data, reset heartbeatCounter
			heartbeatCounter = 0;
			
			initResponseByte(); //prepare empty response

			if(messageType == 0) {


			} else if(messageType == 1) { //initialize from master

				digitalPinMode[0]  = (byte) ((pin07 >> 0) & 1);
				digitalPinMode[1]  = (byte) ((pin07 >> 1) & 1);
				digitalPinMode[2]  = (byte) ((pin07 >> 2) & 1);
				digitalPinMode[3]  = (byte) ((pin07 >> 3) & 1);
				digitalPinMode[4]  = (byte) ((pin07 >> 4) & 1);
				digitalPinMode[5]  = (byte) ((pin07 >> 5) & 1);
				digitalPinMode[6]  = (byte) ((pin07 >> 6) & 1);
				digitalPinMode[7]  = (byte) ((pin07 >> 7) & 1);

				digitalPinMode[8]   = (byte) ((pin813 >> 0) & 1);
				digitalPinMode[9]   = (byte) ((pin813 >> 1) & 1);
				digitalPinMode[10]  = (byte) ((pin813 >> 2) & 1);
				digitalPinMode[11]  = (byte) ((pin813 >> 3) & 1);
				digitalPinMode[12]  = (byte) ((pin813 >> 4) & 1);
				digitalPinMode[13]  = (byte) ((pin813 >> 5) & 1);
				
				debug("Apply PIN MODE ");
				debug(getBinaryString(digitalPinMode));
				debugFlush();

				applyPinMode();

			} else if(messageType == 2) { //apply pin state

				digitalPinState[0]  = (byte) ((pin07 >> 0) & 1);
				digitalPinState[1]  = (byte) ((pin07 >> 1) & 1);
				digitalPinState[2]  = (byte) ((pin07 >> 2) & 1);
				digitalPinState[3]  = (byte) ((pin07 >> 3) & 1);
				digitalPinState[4]  = (byte) ((pin07 >> 4) & 1);
				digitalPinState[5]  = (byte) ((pin07 >> 5) & 1);
				digitalPinState[6]  = (byte) ((pin07 >> 6) & 1);
				digitalPinState[7]  = (byte) ((pin07 >> 7) & 1);

				digitalPinState[8]   = (byte) ((pin813 >> 0) & 1);
				digitalPinState[9]   = (byte) ((pin813 >> 1) & 1);
				digitalPinState[10]  = (byte) ((pin813 >> 2) & 1);
				digitalPinState[11]  = (byte) ((pin813 >> 3) & 1);
				digitalPinState[12]  = (byte) ((pin813 >> 4) & 1);
				digitalPinState[13]  = (byte) ((pin813 >> 5) & 1);
				
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
				);

	}


	void readAllPins() {

		for(byte i=0; i<digitalPinSize; i++) {

			if(digitalPinMode[i] == 0) {
				digitalPinState[i] = digitalRead(i);
			} else {
//				digitalPinState[i] = LOW;
			}

		}

	}


	void suspendOperation() {

		//isInitializedFromMaster = false;
		errorLED();

		for(byte i=0; i<digitalPinSize; i++) {

			if(digitalPinMode[i] == 1) {
				digitalWrite(i, digitalPinStateDefault[i]);
			}

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
			if(i==8) {
				tempString += " ";
			}
			tempString += byteArray[i];
		}
		
		return tempString;
	}

	
	//---- end of copy Arduino Ino ----//
	
}
