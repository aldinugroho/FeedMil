package com.uniteksolusi.otomill.simulator;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.uniteksolusi.otomill.util.FileAccessManager;

public class StubBufferSiloSimulatorBaru implements Runnable {
	
	private static final Gson gson = new Gson(); //gson helper class
	private static final int cycleTime = 3000;
	
	File stubStateFile; // = new File(STUB_STATE_FOLDER + "stub-" + "0x41" + ".state");
	byte pinLevelSensor[]; // = {2,3};
	byte pinRelayInput; // = 4;
	
	boolean shouldRun = false;

	public StubBufferSiloSimulatorBaru(File stubStateFile, byte pinLevelSensor[], byte pinRelayInput ) {
		// TODO Auto-generated constructor stub
		this.stubStateFile = stubStateFile;
		this.pinLevelSensor = pinLevelSensor;
		this.pinRelayInput = pinRelayInput;
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
		// TODO Auto-generated method stub
		while(shouldRun) {
			
			try {

				//read file, get current state
				String fileString = new String( 
						FileAccessManager.getInstance(stubStateFile.getAbsolutePath()).synchronizedRead() 
					);
				byte[] curState = gson.fromJson(fileString, byte[].class);

				if(curState[pinRelayInput] == 1) { //state is filling

					if(curState[pinLevelSensor[0]] == 0) { //level 0 was filled, let's filled level 1
						curState[pinLevelSensor[1]] = 0;
					}

					curState[pinLevelSensor[0]] = 0; //let's fill level 0

				} else if(curState[pinRelayInput] == 0) { //state is ejecting

					if(curState[pinLevelSensor[1]] == 1) { //level 1 was empty, let's empty level 0
						curState[pinLevelSensor[0]] = 1;
					}
					curState[pinLevelSensor[1]] = 1;
	
				}


				//push to file
				FileAccessManager.getInstance(stubStateFile.getAbsolutePath())
								.synchronizedWrite(gson.toJson(curState), false);

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
