package com.uniteksolusi.otomill.stub;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.uniteksolusi.otomill.util.FileAccessManager;

/**
 * @author nanda
 * 
 * Abstract class for DummyArduino in Java.
 * This is a Java only helper class 
 *
 */
public abstract class StubArduino implements Runnable {
	
	protected static final byte INPUT = 0;
	protected static final byte OUTPUT = 1;

	protected static final byte LOW = 0;
	protected static final byte HIGH = 1;
	
	protected static final byte A0 = 0;
	
	
	private static final Gson gson = new Gson();
	public static final String STUB_STATE_FOLDER = "./stub/"; 
	
	public final StubSerial Serial = new StubSerial(this);
	public final StubWire Wire = new StubWire();
	
	//used for dummy call back reference
	Object receiveI2CData = null;
	Object sendI2CData = null;	
	
	byte[] appliedDigitalPinState;
	
	int SLAVE_ADDRESS = 0x00; //to be overwriten in arduino subclass
	
	
	private File file;

	
	public StubArduino(int address) {
		
		this.SLAVE_ADDRESS = address;
		this.file = new File(STUB_STATE_FOLDER + "stub-0x" + Integer.toHexString(address) + ".state");
		
		initializeAppliedDigitalPinState();
		
	}
	
	abstract void initializeAppliedDigitalPinState();
	
	public void pinMode(int i, int set) {
		//do nothing
	}
	
	public void delay(long milis) {
		try { Thread.sleep(milis); } 
		catch (InterruptedException e) {}
	}
	
	public void digitalWrite(int pin, int state) {
		appliedDigitalPinState[pin] = (byte) state;
		
		
		try {
			serialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
	public byte digitalRead(byte pin) {
		
		try {
			deserialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(appliedDigitalPinState == null) {
			System.err.println( "appliedDigitalPinState is NULL !!!!");
		}
		return appliedDigitalPinState[pin];
	}

	abstract void receiveI2CData(int size);

	abstract void sendI2CData();
	
	abstract void setup();
	
	abstract void loop();
	
	public int getAddress() {
		return SLAVE_ADDRESS;
	}

	
	public boolean serialize() throws IOException {
		FileAccessManager.getInstance(file.getAbsolutePath())
				.synchronizedWrite(gson.toJson(appliedDigitalPinState), false);
		return true;
		
	}
	
	public boolean deserialize() throws IOException {
		String fileString = new String( 
										FileAccessManager.getInstance(file
												.getAbsolutePath()).synchronizedRead() 
									);
		byte[] newData = gson.fromJson(fileString, byte[].class);
		if(newData != null) {
			appliedDigitalPinState = newData;
		}
		return true;
		
	}
	
	
	private boolean started = false;
	
	public void start() {
		
		if(!started) {
			started = true;
			new Thread(this).start();
		}
		
	}
	
	public void stop() {
		started = false;
	}
	
	public void run() {
		
		setup();
		
		while(started) {
			
			loop();
			
		}
		
		
	}
	
	
}
