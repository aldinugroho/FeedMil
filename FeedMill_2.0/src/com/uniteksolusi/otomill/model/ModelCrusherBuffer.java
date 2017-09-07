package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public class ModelCrusherBuffer extends ArduinoMegaModel {

	//### CONFIGURE ###//
	//JAGUNG 1, JAGUNG 2, AND SBM 1
	transient byte pinRelayCleanerBlower = 24; 
	transient byte pinRelayBucketCleanerOut = 25;

	transient byte pinRelayCrusher = 26;
	transient byte pinRelayBucketCrusherOut = 27;
	
	transient byte pinRelayScrewCrusherOut = 28;


	transient byte pinRelayPneumaticOutJagung1 = 32; //this is a microswitch to stop moving the router at the right output
	transient byte pinRelayPneumaticOutJagung2 = 33;
	transient byte pinRelayPneumaticOutSBM1 = 34;
	transient byte pinRelayScrewManual = 35;


	//JAGUNG 1
	transient byte pinBufferLevelJagung1[] = {2,3,4};  //3 pins of IR OBSTACLE = low level, half, full

	//JAGUNG 2
	transient byte pinBufferLevelJagung2[] = {6,7,8};

	//SBM 1
	transient byte pinBufferLevelSBM1[] = {10,11,12};

	//SBM 2
	transient byte pinBufferLevelSBM2[] = {14,15,16}; 
	transient byte pinRelayBucketSBM2 = 17;

	//MBM
	transient byte pinBufferLevelMBM[] = {18,19,20};
	transient byte pinRelayBucketMBM = 21;
	
	//GRIT
	transient byte pinBufferLevelGrit[] = {36,37,38};
	transient byte pinRelayBucketGrit = 39;
	
	//KATUL
	transient byte pinBufferLevelKatul[] = {41,42,43};
	transient byte pinRelayBucketKatul = 44;
	
	//Pin LED Notification
	transient byte pinLEDJagungSBM = 46;
	transient byte pinLEDSBMHalus = 47;
	transient byte pinLEDMBM = 48;
	transient byte pinLEDKatul = 49;
	transient byte pinLEDGrit = 50;
	
	//current fill level of each silo
	byte currentFillLevelJagung1 = 0;
	byte currentFillLevelJagung2 = 0;
	byte currentFillLevelSBM1 = 0;
	byte currentFillLevelSBM2 = 0;
	byte currentFillLevelMBM = 0;
	byte currentFillLevelGrit = 0;
	byte currentFillLevelKatul = 0;

	///### END of CONFIGURATION ###///


	//### running variables ###//
	public byte sharedBucketJagungOrSbm = 0;  //0 = jagung, 1 = sbm
	public byte sbmCleanerOnOff = 0; //when SBM, clean or not
	public byte sbmCrusherOnOff = 1; //when SBM, crush or not 
	public byte screwManual = 0; //when Screw, manual or not. 0 = off, 1 = on

	byte curOutput = -1;  //-1,0,1,2 = undefined, jagung1, jagung2, sbm1

	//fill state for each silo. 0=off, 1=fill, 2=auto
	byte fillStateJagung1 = 2;
	byte fillStateJagung2 = 2;
	byte fillStateSBM1 = 2;
	byte fillStateSBM2 = 2;
	
	//fill state for LED. 0=off, 1=on
	byte LedON = 1;
	
	//fill state added from ArduinoUno
	byte fillStateMBM = 2;
	byte fillStateGrit = 2;
	byte fillStateKatul = 2;

	//when to start filling in auto mode. 0 = low level, 1 = half, 2 = full
	public byte fillLevelJagung1 = 1;
	public byte fillLevelJagung2 = 1;
	public byte fillLevelSBM1 = 1;
	public byte fillLevelSBM2 = 1;

	//when to start filling in auto mode (MBM,Grit,Katul)
	public byte fillLevelMBM = 1;
	public byte fillLevelGrit = 1;
	public byte fillLevelKatul = 1;
	
	//status json
	boolean isCleanerOn = false;
	boolean isBucketCleanerOutOn = false;
	boolean isCrusherOn = false;
	boolean isBucketCrusherOutOn = false;
	boolean isScrewCrusherOutOn = false;
	boolean isScrewManual = false;
	
	boolean isInputJagung1Open = false;
	boolean isInputJagung2Open = false;
	boolean isInputSBM1Open = false;
	
	boolean isBucketSBM2On = false;
	boolean isLedOn = true;
	boolean isVibroOn = false;
	
	
	//status json (MBM,Grit,Katul)
	boolean isMBMBucketOn = false;
	boolean isGritBucketOn = false;
	boolean isKatulBucketOn = false;

	public ModelCrusherBuffer(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	protected void initialize() {

		//JAGUNG 1, JAGUNG 2, AND SBM 1
		pinMode(pinRelayCleanerBlower, OUTPUT); 
		pinMode(pinRelayBucketCleanerOut, OUTPUT);

		pinMode(pinRelayCrusher, OUTPUT);
		pinMode(pinRelayBucketCrusherOut, OUTPUT);
		pinMode(pinRelayScrewCrusherOut, OUTPUT);
		pinMode(pinRelayScrewManual, OUTPUT);
		
		pinMode(pinRelayPneumaticOutJagung1, OUTPUT); 
		pinMode(pinRelayPneumaticOutJagung2, OUTPUT);
		pinMode(pinRelayPneumaticOutSBM1, OUTPUT);


		//JAGUNG 1
		pinMode(pinBufferLevelJagung1[0], INPUT); 	//3 pins of IR OBSTACLE = low level, half, full
		pinMode(pinBufferLevelJagung1[1], INPUT);
		pinMode(pinBufferLevelJagung1[2], INPUT);

		//JAGUNG 2
		pinMode(pinBufferLevelJagung2[0], INPUT); 
		pinMode(pinBufferLevelJagung2[1], INPUT);
		pinMode(pinBufferLevelJagung2[2], INPUT);

		//SBM 1
		pinMode(pinBufferLevelSBM1[0], INPUT);
		pinMode(pinBufferLevelSBM1[1], INPUT);
		pinMode(pinBufferLevelSBM1[2], INPUT); 

		//SBM 2
		pinMode(pinBufferLevelSBM2[0], INPUT);
		pinMode(pinBufferLevelSBM2[1], INPUT);
		pinMode(pinBufferLevelSBM2[2], INPUT);	
		
		
		pinMode(pinRelayBucketSBM2, OUTPUT);
		
		//MBM
		pinMode(pinBufferLevelMBM[0], INPUT);   //3 pins of IR OBSTACLE = low level, half, full
		pinMode(pinBufferLevelMBM[1], INPUT);
		pinMode(pinBufferLevelMBM[2], INPUT);
				
		//Grit
		pinMode(pinBufferLevelGrit[0], INPUT);
		pinMode(pinBufferLevelGrit[1], INPUT);
		pinMode(pinBufferLevelGrit[2], INPUT);
				
		//Katul
		pinMode(pinBufferLevelKatul[0], INPUT);
		pinMode(pinBufferLevelKatul[1], INPUT);
		pinMode(pinBufferLevelKatul[2], INPUT);

		pinMode(pinRelayBucketMBM, OUTPUT);
		pinMode(pinRelayBucketGrit, OUTPUT);
		pinMode(pinRelayBucketKatul, OUTPUT);
		
		
		//LED
		pinMode(pinLEDJagungSBM, INPUT);
		pinMode(pinLEDSBMHalus, INPUT);
		pinMode(pinLEDMBM, INPUT);
		pinMode(pinLEDGrit, INPUT);
		pinMode(pinLEDKatul, INPUT);
		//LED
		pinMode(pinLEDJagungSBM, OUTPUT);
		pinMode(pinLEDSBMHalus, OUTPUT);
		pinMode(pinLEDMBM, OUTPUT);
		pinMode(pinLEDGrit, OUTPUT);
		pinMode(pinLEDKatul, OUTPUT);

		
	}

	@Override
	protected void mainLoop() {
	
		//jagung1
		if(digitalRead(pinBufferLevelJagung1[0]) == LOW) this.currentFillLevelJagung1 = 0;
		if(digitalRead(pinBufferLevelJagung1[1]) == LOW) this.currentFillLevelJagung1 = 1;
		if(digitalRead(pinBufferLevelJagung1[2]) == LOW) this.currentFillLevelJagung1 = 2;
		
		//jagung1
		if(digitalRead(pinBufferLevelJagung2[0]) == LOW) this.currentFillLevelJagung2 = 0;
		if(digitalRead(pinBufferLevelJagung2[1]) == LOW) this.currentFillLevelJagung2 = 1;
		if(digitalRead(pinBufferLevelJagung2[2]) == LOW) this.currentFillLevelJagung2 = 2;
				
		//sbm1
		if(digitalRead(pinBufferLevelSBM1[0]) == LOW) this.currentFillLevelSBM1 = 0;
		if(digitalRead(pinBufferLevelSBM1[1]) == LOW) this.currentFillLevelSBM1 = 1;
		if(digitalRead(pinBufferLevelSBM1[2]) == LOW) this.currentFillLevelSBM1 = 2;

		//sbm2
		if(digitalRead(pinBufferLevelSBM2[0]) == LOW) this.currentFillLevelSBM2 = 0;
		if(digitalRead(pinBufferLevelSBM2[1]) == LOW) this.currentFillLevelSBM2 = 1;
		if(digitalRead(pinBufferLevelSBM2[2]) == LOW) this.currentFillLevelSBM2 = 2;
		//check and fill each silos
		//Fill condition
		if(digitalRead(pinBufferLevelGrit[0]) == LOW) this.currentFillLevelGrit = 0;
		if(digitalRead(pinBufferLevelGrit[1]) == LOW) this.currentFillLevelGrit = 1;
		if(digitalRead(pinBufferLevelGrit[2]) == LOW) this.currentFillLevelGrit = 2;
		
		if(digitalRead(pinBufferLevelMBM[0]) == LOW) this.currentFillLevelMBM = 0;
		if(digitalRead(pinBufferLevelMBM[1]) == LOW) this.currentFillLevelMBM = 1;
		if(digitalRead(pinBufferLevelMBM[2]) == LOW) this.currentFillLevelMBM = 2;

		if(digitalRead(pinBufferLevelKatul[0]) == LOW) this.currentFillLevelKatul = 0;
		if(digitalRead(pinBufferLevelKatul[1]) == LOW) this.currentFillLevelKatul = 1;
		if(digitalRead(pinBufferLevelKatul[2]) == LOW) this.currentFillLevelKatul = 2;
		
		
		if(sharedBucketJagungOrSbm == 0) { //jagung
			
			if(curOutput == 0) { //jagung1  
				
				moveCrusherOutToJagung1();

				if(fillStateJagung1 == 2) {
					
					if(isJagung1Full()) {
						
						turnOffCleanerCrusherLine(); 
						if(fillStateJagung2==2 && !isJagung2Full()) {
							
							moveCrusherOutToJagung2();
						
						} else { //both are full
							
							turnOffCleanerCrusherLine();
						
						}
						
					} else if(isJagung1ToFill()) {
						
						turnOnCleanerCrusherLine();
					}
					
				} else if(fillStateJagung1 == 1) {
					
					turnOnCleanerCrusherLine(); //lower than fill level, start the bucket

				} else if(fillStateJagung1 == 0) { //off
					
					turnOffCleanerCrusherLine();

				}

			} if(curOutput == 1) { //jagung2  
				
				moveCrusherOutToJagung2();

				if(fillStateJagung2 == 2) {
					
					if(isJagung2Full()) {
						turnOffCleanerCrusherLine(); 
						
						
						if(fillStateJagung1==2 && !isJagung1Full()) {
							
							moveCrusherOutToJagung1();
					
						} else { //both are full
							
							turnOffCleanerCrusherLine();
						
						}
						
					} else if(isJagung2ToFill()) {
						
						turnOnCleanerCrusherLine();
					}

				} else if(fillStateJagung2 == 1) {
					
					turnOnCleanerCrusherLine(); //lower than fill level, start the bucket

				} else if(fillStateJagung2 == 0) { //off
					
					turnOffCleanerCrusherLine();

				}

			} else { //setting is set for jagung, but crusher out not yet set for jagung

				//let's start with jagung1
				
				moveCrusherOutToJagung1();

			}


		} else if(sharedBucketJagungOrSbm == 1) { //sbm1

			//sbm 1
			if(curOutput == 2) { //sbm1
				
				moveCrusherOutToSBM1();
				
				if(fillStateSBM1 == 2) {
					
					if(isSBM1Full()) {
						
						turnOffCleanerCrusherLine();
					} else if(isSBM1ToFill()) {
						
						turnOnCleanerCrusherLine();
					}
					
				} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
				
				else if(fillStateSBM1 == 0) { //stop filling
					
					turnOffCleanerCrusherLine();					

				} else if(fillStateSBM1 == 1) { //start filling
					
					turnOnCleanerCrusherLine();

				}

			} else { //setting is set for SBM1, but crusher out not yet set for SBM1
				
				moveCrusherOutToSBM1();

			}

		}

		//sbm 2
		if(fillStateSBM2 == 2) { //0=off, 1=on, 2=auto
//			digitalWrite(pinLEDSBMHalus, HIGH);
			if(isSBM2Full()) { //level 2 = full, IR obstacle read 0 = there is obstacle
//				digitalWrite(pinLEDSBMHalus, LOW);
				digitalWrite(pinRelayBucketSBM2, LOW); //full, stop the bucket
			} else if(isSBM2ToFill()) { //IR read 1 = no obstacle at the fillLevel
//				digitalWrite(pinLEDSBMHalus, HIGH);
				digitalWrite(pinRelayBucketSBM2, HIGH); //lower than fill level, start the bucket
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateSBM2 == 0) { //off
//			digitalWrite(pinLEDSBMHalus, LOW);
			digitalWrite(pinRelayBucketSBM2, LOW); //stop the bucket
		} else if(fillStateSBM2 == 1) {
//			digitalWrite(pinLEDSBMHalus, HIGH);
			digitalWrite(pinRelayBucketSBM2, HIGH); //start the bucket
		}
		
		
		//New Configuration from ArduinoUno
		//mbm
		if(fillStateMBM == 2) {
//			digitalWrite(pinLEDMBM, HIGH);
			if(digitalRead(pinBufferLevelMBM[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle
//				digitalWrite(pinLEDMBM, LOW);
				digitalWrite(pinRelayBucketMBM, LOW); //full, stop the bucket
				isMBMBucketOn = false;
			} else if(digitalRead(pinBufferLevelMBM[fillLevelMBM]) == HIGH) { //IR read 1 = no obstacle at the fillLevel
//				digitalWrite(pinLEDMBM, HIGH);
				digitalWrite(pinRelayBucketMBM, HIGH); //lower than fill level, start the bucket
				isMBMBucketOn = true;
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateMBM == 0) {
//			digitalWrite(pinLEDMBM, LOW);
			digitalWrite(pinRelayBucketMBM, LOW); //stop the bucket
			isMBMBucketOn = false;
		} else if(fillStateMBM == 1) {
//			digitalWrite(pinLEDMBM, HIGH);
			digitalWrite(pinRelayBucketMBM, HIGH); //start the bucket
			isMBMBucketOn = true;
		}
		
		
		//Grit
//		digitalWrite(pinLEDGrit, HIGH);
		if(fillStateGrit == 2) {
//			digitalWrite(pinLEDGrit, HIGH);
			if(digitalRead(pinBufferLevelGrit[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle
//				digitalWrite(pinLEDGrit, LOW);
				digitalWrite(pinRelayBucketGrit, LOW); //full, stop the bucket
				isGritBucketOn = false;
			} else if(digitalRead(pinBufferLevelGrit[fillLevelGrit]) == HIGH) { //IR read 1 = no obstacle at the fillLevel
//				digitalWrite(pinLEDGrit, HIGH);
				digitalWrite(pinRelayBucketGrit, HIGH); //lower than fill level, start the bucket
				isGritBucketOn = true;
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateGrit == 0) {
//			digitalWrite(pinLEDGrit, LOW);
			digitalWrite(pinRelayBucketGrit, LOW); //stop the bucket
			isGritBucketOn = false;
		} else if(fillStateGrit == 1) {
//			digitalWrite(pinLEDGrit, HIGH);
			digitalWrite(pinRelayBucketGrit, HIGH); //start the bucket
			isGritBucketOn = true;
		}
		
		
		//Katul
	//	digitalWrite(pinLEDKatul, HIGH);
		if(fillStateKatul == 2) {
			digitalWrite(pinLEDKatul, LOW);
			if(digitalRead(pinBufferLevelKatul[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle
//				digitalWrite(pinLEDKatul, LOW);
				digitalWrite(pinRelayBucketKatul, LOW); //full, stop the bucket
				isKatulBucketOn = false;
			} else if(digitalRead(pinBufferLevelKatul[fillLevelKatul]) == HIGH) { //IR read 1 = no obstacle at the fillLevel
//				digitalWrite(pinLEDKatul, HIGH);
				digitalWrite(pinRelayBucketKatul, HIGH); //lower than fill level, start the bucket
				isKatulBucketOn = true;
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateKatul == 0) {
//			digitalWrite(pinLEDKatul, LOW);
			digitalWrite(pinRelayBucketKatul, LOW); //stop the bucket
			isKatulBucketOn = false;
		} else if(fillStateKatul == 1) {
			digitalWrite(pinRelayBucketKatul, HIGH); //start the bucket
			isKatulBucketOn = true;
		}
	}
		// ====== Screw Manual Mode =======
		
	
	
	private boolean isJagung1Full() {
		return (digitalRead(pinBufferLevelJagung1[2]) == LOW);
	}
	
	private boolean isJagung2Full() {
		return (digitalRead(pinBufferLevelJagung2[2]) == LOW);
	}
	
	private boolean isSBM1Full() {
		return (digitalRead(pinBufferLevelSBM1[2]) == LOW);
	}
	
	private boolean isSBM2Full() {
		return (digitalRead(pinBufferLevelSBM2[2]) == LOW);
	}
	
	
	private boolean isJagung1ToFill() {
		return (digitalRead(pinBufferLevelJagung1[fillLevelJagung1]) == HIGH);
	}
	
	private boolean isJagung2ToFill() {
		return (digitalRead(pinBufferLevelJagung2[fillLevelJagung2]) == HIGH);
	}
	
	private boolean isSBM1ToFill() {
		return (digitalRead(pinBufferLevelSBM1[fillLevelSBM1]) == HIGH);
	}
	
	private boolean isSBM2ToFill() {
		return (digitalRead(pinBufferLevelSBM2[fillLevelSBM2]) == HIGH);
	}
	


	void turnOnCleanerCrusherLine(){

		digitalWrite(pinRelayScrewCrusherOut, HIGH);
		
		digitalWrite(pinRelayBucketCrusherOut, HIGH); 
		
		if(sharedBucketJagungOrSbm == 1 && sbmCrusherOnOff == 0) { //this is SBM with crusher setting is off
			digitalWrite(pinRelayCrusher, LOW); 
		} else { //this is jagung (always with crusher) OR SBM with crusher setting on 
			digitalWrite(pinRelayCrusher, HIGH); 
		}

		digitalWrite(pinRelayBucketCleanerOut, HIGH);

		if(sharedBucketJagungOrSbm == 1 && sbmCleanerOnOff == 0) { //this is SBM with cleaner setting is off
			digitalWrite(pinRelayCleanerBlower, LOW); 
		} else { //this is jagung (always with cleaner) OR SBM with cleaner setting on 
			digitalWrite(pinRelayCleanerBlower, HIGH); 
		}

	}


	void moveCrusherOutToJagung1() {

		curOutput = 0;
		digitalWrite(pinRelayPneumaticOutJagung1, HIGH);
		digitalWrite(pinRelayPneumaticOutJagung2, LOW);
		digitalWrite(pinRelayPneumaticOutSBM1, LOW);
		

	}
	


	void moveCrusherOutToJagung2() {

		curOutput = 1;
		digitalWrite(pinRelayPneumaticOutJagung1, LOW);
		digitalWrite(pinRelayPneumaticOutJagung2, HIGH);
		digitalWrite(pinRelayPneumaticOutSBM1, LOW);
		

	}


	void moveCrusherOutToSBM1() {

		curOutput = 2;
		digitalWrite(pinRelayPneumaticOutJagung1, LOW);
		digitalWrite(pinRelayPneumaticOutJagung2, LOW);
		digitalWrite(pinRelayPneumaticOutSBM1, HIGH);
		
		
	}



	void turnOffCleanerCrusherLine() {	
		digitalWrite(pinRelayCleanerBlower, LOW); 
		digitalWrite(pinRelayBucketCleanerOut, LOW); 
		digitalWrite(pinRelayCrusher, LOW); 
		digitalWrite(pinRelayBucketCrusherOut, LOW);
		
	}


	protected void writeRequestByteSpecific(byte[] requestByte) {
		//nothing special
	}

	protected void readResponseByteSpecific(byte[] responseByte) {
		//nothing special
	}
	
	
	void digitalWrite(byte pinNumber, byte highLow) {
		
		super.digitalWrite(pinNumber, highLow);
		
		if(highLow == LOW) {
			
			if(pinNumber == pinRelayCleanerBlower) {
				isCleanerOn = false;
			} else if(pinNumber == pinRelayBucketCleanerOut) {
				isBucketCleanerOutOn = false;
			} else if(pinNumber == pinRelayCrusher) {
				isCrusherOn = false;
			} else if(pinNumber == pinRelayBucketCrusherOut) {
				this.isBucketCrusherOutOn = false;
			} else if(pinNumber == pinRelayScrewCrusherOut) {
				isScrewCrusherOutOn = false;
			} else if(pinNumber == pinRelayPneumaticOutJagung1) {
				isInputJagung1Open = false;
			} else if(pinNumber == pinRelayPneumaticOutJagung2) {
				isInputJagung2Open = false;
			} else if(pinNumber == pinRelayPneumaticOutSBM1) {
				isInputSBM1Open = false;
			} else if(pinNumber == pinRelayBucketSBM2) {
				isBucketSBM2On = false;
			
			}
			
			
		} else if(highLow == HIGH) {
			
			if(pinNumber == pinRelayCleanerBlower) {
				isCleanerOn = true;
			} else if(pinNumber == pinRelayBucketCleanerOut) {
				isBucketCleanerOutOn = true;
			} else if(pinNumber == pinRelayCrusher) {
				isCrusherOn = true;
			} else if(pinNumber == pinRelayBucketCrusherOut) {
				this.isBucketCrusherOutOn = true;
			} else if(pinNumber == pinRelayScrewCrusherOut) {
				isScrewCrusherOutOn = true;
			} else if(pinNumber == pinRelayPneumaticOutJagung1) {
				isInputJagung1Open = true;
			} else if(pinNumber == pinRelayPneumaticOutJagung2) {
				isInputJagung2Open = true;
			} else if(pinNumber == pinRelayPneumaticOutSBM1) {
				isInputSBM1Open = true;
			} else if(pinNumber == pinRelayBucketSBM2) {
				isBucketSBM2On = true;
			
			}
			
		} 

	}
	
	public void switchInputIsCleanerOn() {
		if(digitalRead(pinRelayCleanerBlower) == LOW) {
			digitalWrite(pinRelayCleanerBlower, HIGH);
			isCleanerOn = true;
		} else {
			digitalWrite(pinRelayCleanerBlower, LOW);
			isCleanerOn = false;
		}
	}
	
	public void switchInputIsScrewManual() {
		if(digitalRead(pinRelayScrewManual) == LOW) {
			digitalWrite(pinRelayScrewManual, HIGH);
			isScrewManual = true;
			screwManual = 1;
		} else if(digitalRead(pinRelayScrewManual) == HIGH){
			digitalWrite(pinRelayScrewManual, LOW);
			isScrewManual = false;
			screwManual = 0;
		}
	}
	
	public void switchInputIsBucketCleanerOutOn() {
		if(digitalRead(pinRelayBucketCleanerOut) == LOW) {
			digitalWrite(pinRelayBucketCleanerOut, HIGH);
			isBucketCleanerOutOn = true;
		} else {
			digitalWrite(pinRelayBucketCleanerOut, LOW);
			isBucketCleanerOutOn = false;
		}
	}
	
	public void switchInputIsCrusherOn() {
		if(digitalRead(pinRelayCrusher) == LOW) {
			digitalWrite(pinRelayCrusher, HIGH);
			isCrusherOn = true;
		} else {
			digitalWrite(pinRelayCrusher, LOW);
			isCrusherOn = false;
		}
	}
	
	public void switchInputIsBucketCrusherOutOn() {
		if(digitalRead(pinRelayBucketCrusherOut) == LOW) {
			digitalWrite(pinRelayBucketCrusherOut, HIGH);
			isBucketCrusherOutOn = true;
		} else {
			digitalWrite(pinRelayBucketCrusherOut, LOW);
			isBucketCrusherOutOn = false;
		}
	}
	
	public void switchInputIsScrewCrusherOutOn() {
		if(digitalRead(pinRelayScrewCrusherOut) == LOW) {
			digitalWrite(pinRelayScrewCrusherOut, HIGH);
			isScrewCrusherOutOn = true;
		} else {
			digitalWrite(pinRelayScrewCrusherOut, LOW);
			isScrewCrusherOutOn = false;
		}
	}
	
	public void switchInputJagungSBM() {
		if(digitalRead(pinRelayPneumaticOutJagung1) == LOW || digitalRead(pinRelayPneumaticOutJagung2) == LOW) {
			digitalWrite(pinRelayPneumaticOutSBM1, HIGH);
			isInputJagung1Open = false;
			isInputJagung2Open = false;
			isInputSBM1Open = true;
		} else if (digitalRead(pinRelayPneumaticOutJagung1) == HIGH || digitalRead(pinRelayPneumaticOutJagung2) == HIGH) {
			digitalWrite(pinRelayPneumaticOutSBM1, LOW);
			isInputJagung1Open = true;
			isInputJagung2Open = true;
			isInputSBM1Open = false;
		}
	}
	
	public void switchInputIsMBMBucketOn() {
		if(digitalRead(pinRelayBucketMBM) == LOW) {
			digitalWrite(pinRelayBucketMBM, HIGH);
			isMBMBucketOn = true;
		} else {
			digitalWrite(pinRelayBucketMBM, LOW);
			isMBMBucketOn = false;
		}
	}
	
	public void switchInputIsGritBucketOn() {
		if(digitalRead(pinRelayBucketGrit) == LOW) {
			digitalWrite(pinRelayBucketGrit, HIGH);
			isGritBucketOn = true;
		} else {
			digitalWrite(pinRelayBucketGrit, LOW);
			isGritBucketOn = false;
		}
	}
	
	public void switchInputIsKatulBucketOn() {
		if(digitalRead(pinRelayBucketKatul) == LOW) {
			digitalWrite(pinRelayBucketKatul, HIGH);
			isKatulBucketOn = true;
		} else {
			digitalWrite(pinRelayBucketKatul, LOW);
			isKatulBucketOn = false;
		}
	}
	
	public void switchInputIsBucketSBM2On() {
		if(digitalRead(pinRelayBucketSBM2) == LOW) {
			digitalWrite(pinRelayBucketSBM2, HIGH);
			isBucketSBM2On = true;
		} else {
			digitalWrite(pinRelayBucketSBM2, LOW);
			isBucketSBM2On = false;
		}
	}
	

	public String printStateDetails() {
		
		StringBuffer sb  = new StringBuffer(super.printStateDetails());

		sb.append("\n");
		sb.append("\nJagung/SBM:\t " + this.sharedBucketJagungOrSbm);
		sb.append(" . . . . . ||| Cur Output Jagung1/Jagung2/SBM1 :\t. " + curOutput);

		
		sb.append("\n");
		sb.append("\nSBM Cleaner/Crusher Conf :\t " + this.sbmCleanerOnOff + " / " + this.sbmCrusherOnOff);
		sb.append("\nCleaner/OutBucket Status :\t " + digitalRead(pinRelayCleanerBlower) + " / " + digitalRead(pinRelayBucketCleanerOut));
		sb.append(" || Crusher/OutBucket/Screw Status :\t " + digitalRead(pinRelayCrusher) + " / " + digitalRead(pinRelayBucketCrusherOut));
		
		sb.append("\n");
		String tempString = "STOP";
		isInputJagung1Open = false;
		if(curOutput==0 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
			isInputJagung1Open = true;
			
		}
		sb.append("\nJagung1 State:\t " + tempString);
		
		tempString = "STOP";
		isInputJagung2Open = false;
		if(curOutput==1 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
			isInputJagung2Open = true;
			
		}
		sb.append(" | Jagung2 State:\t " + tempString);
		
		// check 
		if (isInputJagung1Open == false && isInputJagung2Open == false) {
			digitalWrite(pinLEDJagungSBM, LOW);
		} else {
			digitalWrite(pinLEDJagungSBM, HIGH);
		}
		
		
		tempString = "STOP";
		
		if(curOutput==2 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
			
		}
		sb.append(" | SBM1 State:\t " + tempString);
		
		
		sb.append("\nJagung1 Level:\t " + digitalRead(this.pinBufferLevelJagung1[0]) +""+ digitalRead(this.pinBufferLevelJagung1[1]) +""+ digitalRead(this.pinBufferLevelJagung1[2])); 
		sb.append(" || Jagung2 Level:\t " + digitalRead(this.pinBufferLevelJagung2[0]) +""+ digitalRead(this.pinBufferLevelJagung2[1]) +""+ digitalRead(this.pinBufferLevelJagung2[2])); 
		sb.append(" || SBM1 Level:\t " + digitalRead(this.pinBufferLevelSBM1[0]) +""+ digitalRead(this.pinBufferLevelSBM1[1]) +""+ digitalRead(this.pinBufferLevelSBM1[2])); 
		sb.append("\n || LED STATUS. . . :\t " + digitalRead(this.pinLEDJagungSBM));
		
		sb.append("\n");
		tempString = "STOP";
		digitalWrite(pinLEDSBMHalus, LOW);
		if(digitalRead(pinRelayBucketSBM2)==HIGH){
			tempString = "FILL";
			digitalWrite(pinLEDSBMHalus, HIGH);
		}
		sb.append("\nSBM2 Fill State:\t " + tempString + " (Conf: " + fillLevelSBM2 + ")");
		sb.append("\nSBM2 Level . . :\t " + digitalRead(this.pinBufferLevelSBM2[0]) +""+ digitalRead(this.pinBufferLevelSBM2[1]) +""+ digitalRead(this.pinBufferLevelSBM2[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDSBMHalus));
		
		sb.append("\n");
		tempString = "STOP";
		digitalWrite(pinLEDMBM, LOW);
		if(digitalRead(pinRelayBucketMBM)==HIGH){
			tempString = "FILL";
			digitalWrite(pinLEDMBM, HIGH);
		}
		sb.append("\nMBM Fill State .:\t " + tempString + " (Conf: " + fillLevelMBM + ")");
		sb.append("\nMBM Level . . . :\t " + digitalRead(this.pinBufferLevelMBM[0]) +""+ digitalRead(this.pinBufferLevelMBM[1]) +""+ digitalRead(this.pinBufferLevelMBM[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDMBM));

		
		sb.append("\n");
		tempString = "STOP";
		digitalWrite(pinLEDGrit, LOW);
		if(digitalRead(pinRelayBucketGrit)==HIGH){
			tempString = "FILL";
			digitalWrite(pinLEDGrit, HIGH);
		}
		sb.append("\nGrit Fill State :\t " + tempString + " (Conf: " + fillLevelGrit + ")");
		sb.append("\nGrit Level . . .:\t " + digitalRead(this.pinBufferLevelGrit[0]) +""+ digitalRead(this.pinBufferLevelGrit[1]) +""+ digitalRead(this.pinBufferLevelGrit[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDGrit));

		
		sb.append("\n");
		tempString = "STOP";
		digitalWrite(pinLEDKatul, LOW);
		if(digitalRead(pinRelayBucketKatul)==HIGH){
			tempString = "FILL";
			digitalWrite(pinLEDKatul, HIGH);
		}
		sb.append("\nKatul Fill State:\t " + tempString + " (Conf: " + fillLevelKatul + ")");
		sb.append("\nKatul Level . . :\t " + digitalRead(this.pinBufferLevelKatul[0]) +""+ digitalRead(this.pinBufferLevelKatul[1]) +""+ digitalRead(this.pinBufferLevelKatul[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDKatul));
		return sb.toString();
	
	}	
	
/*	public String printStateDetails2(){
		StringBuffer sb  = new StringBuffer(super.printStateDetails());
		
		sb.append("\n");
		String tempString = "STOP";
		if(digitalRead(pinRelayBucketMBM)==HIGH){
			tempString = "FILL";
		}
		sb.append("\nMBM Fill State .:\t " + tempString + " (Conf: " + fillLevelMBM + ")");
		sb.append("\nMBM Level . . . :\t " + digitalRead(this.pinBufferLevelMBM[0]) +""+ digitalRead(this.pinBufferLevelMBM[1]) +""+ digitalRead(this.pinBufferLevelMBM[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDMBM));

		
		sb.append("\n");
		tempString = "STOP";
		if(digitalRead(pinRelayBucketGrit)==HIGH){
			tempString = "FILL";
		}
		sb.append("\nGrit Fill State :\t " + tempString + " (Conf: " + fillLevelGrit + ")");
		sb.append("\nGrit Level . . .:\t " + digitalRead(this.pinBufferLevelGrit[0]) +""+ digitalRead(this.pinBufferLevelGrit[1]) +""+ digitalRead(this.pinBufferLevelGrit[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDGrit));

		
		sb.append("\n");
		tempString = "STOP";
		if(digitalRead(pinRelayBucketKatul)==HIGH){
			tempString = "FILL";
		}
		sb.append("\nKatul Fill State:\t " + tempString + " (Conf: " + fillLevelKatul + ")");
		sb.append("\nKatul Level . . :\t " + digitalRead(this.pinBufferLevelKatul[0]) +""+ digitalRead(this.pinBufferLevelKatul[1]) +""+ digitalRead(this.pinBufferLevelKatul[2]));
		sb.append("\nLED STATUS . .  :\t " + digitalRead(this.pinLEDKatul));
		return sb.toString();
	}
*/	
	public String processCommand(String stringCommand) {
		
		String[] cmds = stringCommand.trim().split(" ");
		
		if(cmds.length > 0) {
		
			if("setJagung".equals(cmds[0])) {
				sharedBucketJagungOrSbm=0;
				moveCrusherOutToJagung1();
				return "OK";
			}
			
			if("setSBM".equals(cmds[0])) {
				sharedBucketJagungOrSbm=1;
				moveCrusherOutToSBM1();
				return "OK"; 
			}
			
			if("startSBMCrusher".equals(cmds[0])) {
				this.sbmCrusherOnOff=1;
				return "OK"; 
			}
			
			if("stopSBMCrusher".equals(cmds[0])) {
				this.sbmCrusherOnOff=0;
				return "OK"; 
			}
			if("switchInputIsScrewManual".equals(cmds[0])){
				switchInputIsScrewManual();
				return "OK";
			}
			if("switchInputIsCleanerOn".equals(cmds[0])){
				switchInputIsCleanerOn();
				return "OK";
			}
			if("switchInputIsBucketCleanerOutOn".equals(cmds[0])){
				switchInputIsBucketCleanerOutOn();
				return "OK";
			}
			if("switchInputIsCrusherOn".equals(cmds[0])){
				switchInputIsCrusherOn();
				return "OK";
			}
			if("switchInputIsBucketCrusherOutOn".equals(cmds[0])){
				switchInputIsBucketCrusherOutOn();
				return "OK";
			}
			if("switchInputIsScrewCrusherOutOn".equals(cmds[0])){
				switchInputIsScrewCrusherOutOn();
				return "OK";
			}
			if("switchInputJagungSBM".equals(cmds[0])){
				switchInputJagungSBM();
				return "OK";
			}
			if("switchInputIsMBMBucketOn".equals(cmds[0])){
				switchInputIsMBMBucketOn();
				return "OK";
			}
			if("switchInputIsGritBucketOn".equals(cmds[0])){
				switchInputIsGritBucketOn();
				return "OK";
			}
			if("switchInputIsKatulBucketOn".equals(cmds[0])){
				switchInputIsKatulBucketOn();
				return "OK";
			}
			if("switchInputIsBucketSBM2On".equals(cmds[0])){
				switchInputIsBucketSBM2On();
				return "OK";
			}
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+ ": setJagung, setSBM, startSBMCrusher, stopSBMCrusher, switchInputIsScrewManual  \n"
								+ "\n"
								+ "Available commands "+this.getClass().getSimpleName()
								+ ": switchInputIsCleanerOn, switchInputIsBucketCleanerOutOn, switchInputIsCrusherOn, switchInputIsBucketCrusherOutOn, "
								+ "switchInputIsScrewCrusherOutOn, switchInputJagungSBM, switchInputIsMBMBucketOn, switchInputIsGritBucketOn, switchInputIsKatulBucketOn, switchInputIsBucketSBM2On \n";
								
		}
		return parentResponse;
	}

}
