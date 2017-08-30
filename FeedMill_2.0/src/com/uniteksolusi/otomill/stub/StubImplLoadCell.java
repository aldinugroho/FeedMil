package com.uniteksolusi.otomill.stub;

public class StubImplLoadCell extends StubArduinoUno {



//	/*
	//##---- JAVA ALTERNATIVE ----##//
	public StubImplLoadCell(int address) {
		super(address);
		serialLoadCell = new SoftwareSerial(address);
	}
	
	SoftwareSerial serialLoadCell; 
	int stringToInt(String str) {
		return (int) (double) Double.valueOf(str);
	}
	
	void overwriteDefaultConstants() {
		responseByteSize = 6;
		responseByte = new byte[responseByteSize];
	}
	
	//##---- END OF JAVA ALTERNATIVE ----##//
//	 */


	
	/*
	//----- From here below can be copied to Arduino Ino file ----//
	
	//##---- ARDUINO ALTERNATIVE ----##//
	
	#include <Wire.h>
	#include <EEPROM.h>
	#include <SoftwareSerial.h>
	
	int SLAVE_ADDRESS = 0x21; //21-7
	
	SoftwareSerial serialLoadCell(10, 11);
	int stringToInt(String str) {
		return str.toFloat();
	}
	
	void overwriteDefaultConstants() {
		responseByteSize = 6;
		//responseByte[responseByteSize];
	}
	//##---- END OF ARDUINO ----##//
//	 */
	

	
	// This is the arduino to control the filling of loadcell silos
	// It reads the loadcell weight indicator:
	// 1. when it reads 0, 
	//	    1.1. It CLOSES the output pneumatic
	//	    1.2. It STARTS the input screw conveyor 
	// 2. when it reaches the TARGET, it STOPS the screw conveyor
	// 3. it OPENS the output pneumatic when Requested

	
	// ##################
	
	

	int currentWeight = -1;
	
	void readSpecificData() {
		//do nothing, it is done in loopSpecific();
	}
	
	void stateSpecificToResponse(byte responseByte[]) {  //note the [] at the end of variable name (not normal), it's for arduino compatibility
		
		responseByte[3] = (byte) ( ( (currentWeight / 1000) * (1 << 4))  
							+ ( (currentWeight / 100 - currentWeight / 1000 * 10) * (1 << 0)) );

		responseByte[4] = (byte) ( ( (currentWeight / 10 - currentWeight / 100 * 10) * (1 << 4))  
							+ ( (currentWeight / 1 - currentWeight / 10 * 10) * (1 << 0)) );
		
	}
	
	
	int getWeightInt() {
		
	    String foundStr = getWeightString();
		
		if(foundStr.substring(6,7).equals("-")) {
			//load cell calibration error! how come negative weight
			debug("ERROR. Found negative weight: ");
			debug(foundStr);
			debugFlush();
			return -1;
			
		}		
		
		
		if(!(foundStr.length()==0)) {
			
			//found the correct string ST,NT,+00000.0kg
			//let's parse
			String weightStr = foundStr.substring(7,14);
			return stringToInt(weightStr);
		
		} else {
			//weight not found, error
			debug("ERROR. Weight not found");
			return -1;
		}

	}
	
	String getWeightString() {

		//incoming from serial is in this format: ST,NT,+00000.0kg
		
		int retryCount = 3;
		String foundStr = "";
		
		while( foundStr.length()==0 && retryCount > 0 ) {
		
	        String theStr = "";
	        char theCh = (char) -1;
	    
	        int max = 0; //to avoid infinite loop;
	        
	        do {
	        
	            theCh = serialLoadCell.read();
	            if(theCh == 'S') { //the correct start, if found again, "restart" the reading 
	            	max = 0;
	                theStr = "";
	                theStr += theCh;
	            } else if(theCh != 10) {
	                theStr += theCh;
	            } else {
	                //LINEFEED
	                //theCh = -1;
	            	//debug("linefeed");
	            }
	        
	            max++;
	            
	        } while(theCh != 10 && max<17);

	        //we get one line, now validate
	        theStr.trim();
	        //Serial.println(theStr.length());
	        //length 16
	        if(theStr.length() == 16
	            && theStr.substring(0,6).equals("ST,NT,") 
	            && theStr.substring(14,16).equals("kg")) {
	    
	        	debug("data str: ");
	    		debug(theStr);
	    		debugFlush();
	    		
	            //readSuccess = true;
	            //delay(500);
				foundStr = theStr;
	      
	        } else {
	            debug("--------------- ignoring: ");
	            debug(theStr);
	            debugFlush();
				retryCount--;
	        }

	    }
		
		return foundStr;

	}


	void loopSpecific() {
		int tempWeight = getWeightInt();
		if(tempWeight!=-1) { //only store if success reading
			currentWeight = tempWeight;
		}
	}	
	
	void setupSpecific() {
		serialLoadCell.begin(9600);
	}


	//---- end of copy Arduino Ino ----//
	
}
