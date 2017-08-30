package com.uniteksolusi.otomill.i2c;

import java.io.IOException;

public interface ArduinoI2CSlave {
	
	public static long i2cResponseTime = 100; 
	
	/**
	 * push configuration to arduino device
	 * 
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public abstract boolean pushToDevice() throws IOException, InterruptedException;
	
	/**
	 * pull status from arduino device
	 * 
	 * @return
	 */
	public abstract boolean pullFromDevice() throws IOException, InterruptedException;

}
