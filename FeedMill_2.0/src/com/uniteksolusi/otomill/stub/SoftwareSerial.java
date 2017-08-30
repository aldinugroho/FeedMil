package com.uniteksolusi.otomill.stub;

import java.io.File;
import java.io.IOException;

import com.uniteksolusi.otomill.util.FileAccessManager;

public class SoftwareSerial {
	
	public static final String STUB_STATE_FOLDER = "./stub/"; 
	
	int SLAVE_ADDRESS = 0x00; //to be overwriten in arduino subclass
	
	File file;
	private String weightStr = "ST,NT,+00002.0kg" + (char) 10;
	private int curPos = 0;
	
	public SoftwareSerial(int address) {
		this.SLAVE_ADDRESS = address;
		this.file = new File(STUB_STATE_FOLDER + "stub-0x" + Integer.toHexString(address) + ".softwareserial");
	}

	public char read() {
		
		if(curPos >= weightStr.length()) {
			curPos = 0;
			
			try {
				deserialize();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} 
		
		curPos++;
		return weightStr.charAt(curPos-1);
		
	}

	public void begin(int i) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean deserialize() throws IOException {
		weightStr = new String( FileAccessManager.getInstance(file.getAbsolutePath()).synchronizedRead() );
		weightStr = weightStr.trim() + (char) 10;
		return true;
	}

}
