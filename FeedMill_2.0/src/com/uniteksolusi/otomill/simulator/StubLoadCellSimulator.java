package com.uniteksolusi.otomill.simulator;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.uniteksolusi.otomill.util.FileAccessManager;

public class StubLoadCellSimulator implements Runnable {
	
	private static final Gson gson = new Gson(); //gson helper class
	
	private static final int cycleTime = 500; //milis
	private static final int interval = 1;
	
	File stubStateFile; // = new File(STUB_STATE_FOLDER + "stub-" + "0x21" + ".state");
	File stubSoftwareSerialFile; // = new File(STUB_STATE_FOLDER + "stub-" + "0x21" + ".softwareserial");
	byte pinRelayInput; // = 3;
	byte pinRelayEject; // = 4;
	
	
	boolean shouldRun = false;

	
	public StubLoadCellSimulator(File stubStateFile, File stubSoftwareSerialFile, byte pinRelayInput, byte pinRelayEject ) {
		
		this.stubStateFile = stubStateFile;
		this.stubSoftwareSerialFile = stubSoftwareSerialFile;
		this.pinRelayInput = pinRelayInput;
		this.pinRelayEject = pinRelayEject;
		
	}

	public void start() {
		
		if(!shouldRun) {
			shouldRun = true;
			new Thread(this).start();
		}
		
	}
	
	public void stop() {
		shouldRun = false;
	}
	
	
	@Override
	public void run() {
		
		while(shouldRun) {

			try {

				//read file, get current state
				String fileString = new String(FileAccessManager.getInstance(stubStateFile.getAbsolutePath())
											.synchronizedRead());
				byte[] curState = gson.fromJson(fileString, byte[].class);

				if(curState[pinRelayInput]==1 || curState[pinRelayEject]==1) { //if filling or ejecting

//					System.out.println(stubStateFile + " : Filling or ejecting");

					String weightStr = new String(FileAccessManager.getInstance(stubSoftwareSerialFile.getAbsolutePath())
										.synchronizedRead());
					weightStr = weightStr.trim() + (char) 10;
					int weightInt = (int) (double) Double.valueOf(weightStr.substring(7,14));

					if(curState[pinRelayInput]==1) { 
						weightInt = weightInt + interval;
					} else {
						weightInt = weightInt - interval;
						if (weightInt < 0) {
							weightInt = 0;
						}
					}

					String newWeightStr = "ST,NT,+" + String.format("%05d", weightInt) + ".0kg" + (char) 10;

					FileAccessManager.getInstance(stubSoftwareSerialFile.getAbsolutePath())
										.synchronizedWrite(newWeightStr, false);

				} 					

				try {
					Thread.sleep(cycleTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}


			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		
	}

}
