package com.uniteksolusi.otomill.stub;

import java.util.LinkedList;
import java.util.Queue;

public class StubWire {
	
	Queue<Byte> theQueue = new LinkedList<Byte>();
	
	public boolean available() {
		return !theQueue.isEmpty();
	}
	
	public byte read() {
		return theQueue.poll();
	}
	
	public void write(byte byteToWrite) {
		theQueue.add(byteToWrite);
	}
	
	public void write(byte[] byteToWrite, int byteSize) {
		for(int i=0; i< byteSize; i++) {
			theQueue.add(byteToWrite[i]);
		}
	}
	
	public void begin(int i) {
		//dummy method
	}
	
	public void onReceive(Object obj) {
		//dummy method
	}
	
	public void onRequest(Object obj) {
		//dummy method
	}


}

