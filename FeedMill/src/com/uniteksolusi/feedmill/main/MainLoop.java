package com.uniteksolusi.feedmill.main;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.uniteksolusi.feedmill.model.CrusherBufferArduino;
import com.uniteksolusi.feedmill.model.LoadCellArduino;
import com.uniteksolusi.feedmill.model.LoadCellMultiInArduino;
import com.uniteksolusi.feedmill.model.ManualLoadArduino;
import com.uniteksolusi.feedmill.model.MixerArduino;
import com.uniteksolusi.feedmill.model.SimpleBufferArduino;

/**
 * @author nanda
 *
 * This class contains the main loop of the program.
 *
 */
public class MainLoop {
	
	static long sleepCycle = 100;
	
	static I2CBus theBus;
	
	static CrusherBufferArduino jagungSbmBuffer;
	static SimpleBufferArduino mbmKatulGritBuffer;
	
	static LoadCellMultiInArduino jagungLoadCell;
	static LoadCellMultiInArduino sbmLoadCell;
	
	static LoadCellArduino mbmLoadCell;
	static LoadCellArduino gritLoadCell;
	static LoadCellArduino katulLoadCell;
	
	static ManualLoadArduino manualLoad;
	
	static MixerArduino mixer;
	
	
	
	
	public static I2CBus getBus() throws IOException {
		if(theBus == null) {
			theBus = I2CFactory.getInstance(I2CBus.BUS_1); //check this
		}
		
		return theBus;
	}
	
	public static void initI2CArduinos() throws IOException {
		
		jagungSbmBuffer = new CrusherBufferArduino(getBus(), 0x01);
		mbmKatulGritBuffer = new SimpleBufferArduino(getBus(), 0x02);
		
		////
		
		jagungLoadCell = new LoadCellMultiInArduino(getBus(), 0x11);
		sbmLoadCell = new LoadCellMultiInArduino(getBus(), 0x12);
		
		mbmLoadCell = new LoadCellArduino(getBus(), 0x13);
		gritLoadCell = new LoadCellArduino(getBus(), 0x14);
		katulLoadCell = new LoadCellArduino(getBus(), 0x15);
		
		manualLoad = new ManualLoadArduino(getBus(), 0x16);
		
		////
		
		mixer = new MixerArduino(getBus(), 0x21);
		
	}
	
	public static void refreshAll() {
		
		try {
			
			jagungSbmBuffer.pullFromDevice();
			mbmKatulGritBuffer.pullFromDevice();
			jagungLoadCell.pullFromDevice();
			sbmLoadCell.pullFromDevice();
			mbmLoadCell.pullFromDevice();
			gritLoadCell.pullFromDevice();
			katulLoadCell.pullFromDevice();
			manualLoad.pullFromDevice();
			mixer.pullFromDevice();
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static boolean isAllLoadCellReadyForEject() {
		if(	jagungLoadCell.isReadyForEject()
			&& sbmLoadCell.isReadyForEject()
			&& mbmLoadCell.isReadyForEject()
			&& gritLoadCell.isReadyForEject()
			&& katulLoadCell.isReadyForEject()
			&& manualLoad.isReadyForEject()
			) {
			return true;
		}
		return false;
	}
	
	
	public static void main(String[] args) {

		try {
			
			initI2CArduinos();
			
		} catch (IOException e) {
			System.err.println("ERROR on Inits. Exiting.");
			e.printStackTrace();
			System.exit(0);
		}
		
		while(true) {
			
			//1. refresh all, assume all default running at auto
			refreshAll();
			
			//2. check if ejecting state
			if(jagungLoadCell.isEjecting()) {
				
				if(jagungLoadCell.getCurrentWeight() <= (0.5 * jagungLoadCell.getTargetWeight()) ) {
					//if half way, start ejecting others
					sbmLoadCell.eject();
					mbmLoadCell.eject();
					gritLoadCell.eject();
					katulLoadCell.eject();
				}
				
			} else {
				
				//when all load cell full and mixer ready, start ejecting (1st is jagung)
				if( isAllLoadCellReadyForEject() && mixer.isReadyForMixing() ) {
					mixer.startMixing();
					jagungLoadCell.eject();
				}
				
			}
			
			
			try {
				Thread.sleep(sleepCycle);
			} catch (InterruptedException e) {
				System.out.println("Sleep cycle interrupted");
			}
			
		}
		
		
	}

}
