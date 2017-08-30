package com.uniteksolusi.otomill.stub;

/**
 * @author nanda
 * 
 * it has nothing special / specific function, other than normal pin state changes
 *
 */
public class StubImplSimpleBuffer extends StubArduinoUno {

	public StubImplSimpleBuffer(int address) {
		super(address);
	}

	/*

	//----- From here below can be copied to Arduino Ino file ----//
 
 
	// This is the arduino to control the filling of 3 simple buffer silos 
	// It reads IR obstacle sensors from each of the buffer silos to know whether it's time to fill 
	// when it's time to fill, 
	// -> it starts the bucket conveyor
	// 


	//this arduino I2C slave address
	int SLAVE_ADDRESS = 0x12;
	
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
