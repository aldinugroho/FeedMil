package com.uniteksolusi.otomill.model;

import java.io.IOException;

public interface ArduinoModelIfc {

	String getId();

	void pinMode(int pinNumber, byte mode);

	byte digitalRead(int pinNumber);

	void digitalWrite(int pinNumber, byte newState);

	boolean isStarted();

	void start();

	void stop();

	boolean pushToDevice() throws IOException, InterruptedException;

	boolean pullFromDevice() throws IOException, InterruptedException;

	String processCommand(String stringCommand);
	
	public String printStateDetails();
	
	public String printStateDetails2();

}