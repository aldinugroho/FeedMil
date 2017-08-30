package com.uniteksolusi.otomill.stub;

import java.util.logging.Logger;

public class StubSerial {
	
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
	Object ownerObject = null;
	int baudrate = -1;

	public StubSerial(Object anObj) {
		ownerObject = anObj;
	}
	
	public void begin(int i) {
		baudrate = i;
	}

	public void println(char str) {
		logger.fine(ownerObject.getClass().getSimpleName() + "/" + baudrate + "> " + str);
//		System.out.println(ownerObject.getClass().getSimpleName() + "/" + baudrate + "> " + str);
	}
		
	public void println(String str) {
		logger.fine(ownerObject.getClass().getSimpleName() + "/" + baudrate + "> " + str);
//		System.out.println(ownerObject.getClass().getSimpleName() + "/" + baudrate + "> " + str);
	}

}
