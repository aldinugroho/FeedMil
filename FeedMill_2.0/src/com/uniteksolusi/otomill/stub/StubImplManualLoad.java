package com.uniteksolusi.otomill.stub;

/**
 * @author nanda
 * 
 * it has nothing special / specific function, other than normal pin state changes
 *
 */
public class StubImplManualLoad extends StubArduinoUno {

	public StubImplManualLoad(int address) {
		super(address);
	}

	/*

	//----- From here below can be copied to Arduino Ino file ----//
 
 
	// This is the arduino to control the manual load
	// 


	//this arduino I2C slave address
	int SLAVE_ADDRESS = 0x26;
	
//	*/
	
	void overwriteDefaultConstants() {
		//nothing special
	}
	
	void stateSpecificToResponse(byte responseByte[]) {
		
	}

	void readSpecificData() {
		
	}

	void loopSpecific() {
		// TODO Auto-generated method stub
		
	}

	void setupSpecific() {
		// TODO Auto-generated method stub
		
	}


	//---- end of copy Arduino Ino ----//
	
}
