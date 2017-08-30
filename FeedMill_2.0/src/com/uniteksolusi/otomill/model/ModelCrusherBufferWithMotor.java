package com.uniteksolusi.otomill.model;

import com.pi4j.io.i2c.I2CBus;

public class ModelCrusherBufferWithMotor extends ArduinoMegaModel {

	//### CONFIGURE ###//
	//JAGUNG 1, JAGUNG 2, AND SBM 1
	byte pinRelayCleanerBlower = 24; 
	byte pinRelayBucketCleanerOut = 25;

	byte pinRelayCrusher = 26;
	byte pinRelayBucketCrusherOut = 27;

	byte pinIsCrusherEmpty = 22;

	byte pinCrusherOutMostLeft = 30; //this is a microswitch to stop moving the router at the end 
	byte pinCrusherOutMostRight = 31;

	byte pinCrusherOutSelectorJagung1 = 32; //this is a microswitch to stop moving the router at the right output
	byte pinCrusherOutSelectorJagung2 = 33;
	byte pinCrusherOutSelectorSBM1 = 34;

	byte pinRelayMoveCrusherOutLeft = 36;
	byte pinRelayMoveCrusherOutRight = 37;


	//JAGUNG 1
	byte pinBufferLevelJagung1[] = {2,3,4};  //3 pins of IR OBSTACLE = low level, half, full

	//JAGUNG 2
	byte pinBufferLevelJagung2[] = {6,7,8};

	//SBM 1
	byte pinBufferLevelSBM1[] = {10,11,12};

	//SBM 2
	byte pinBufferLevelSBM2[] = {14,15,16}; 
	byte pinRelayBucketSBM2 = 17;

	///### END of CONFIGURATION ###///


	//### running variables ###//
	public byte sharedBucketJagungOrSbm = 0;  //0 = jagung, 1 = sbm
	public byte sbmCleanerOnOff = 0; //when SBM, clean or not
	public byte sbmCrusherOnOff = 1; //when SBM, crush or not 

	byte curPos = -1;  //-1,0,1,2 = undefined, jagung1, jagung2, sbm1
	byte targetPos = -1; //-1,0,1,2 = undefined, jagung1, jagung2, sbm1
	byte movingState = 0; // 0,1,2 = stop, moving left, moving right

	//fill state for each silo. 0=off, 1=fill, 2=auto
	byte fillStateJagung1 = 2;
	byte fillStateJagung2 = 2;
	byte fillStateSBM1 = 2;
	byte fillStateSBM2 = 2;

	//when to start filling in auto mode. 0 = low level, 1 = half, 2 = full
	public byte fillLevelJagung1 = 0;
	public byte fillLevelJagung2 = 0;
	public byte fillLevelSBM1 = 0;
	public byte fillLevelSBM2 = 0;



	public ModelCrusherBufferWithMotor(I2CBus bus, int address) {
		super(bus, address);
	}

	@Override
	protected void initialize() {

		//JAGUNG 1, JAGUNG 2, AND SBM 1
		pinMode(pinRelayCleanerBlower, OUTPUT); 
		pinMode(pinRelayBucketCleanerOut, OUTPUT);

		pinMode(pinRelayCrusher, OUTPUT);
		pinMode(pinRelayBucketCrusherOut, OUTPUT);

		pinMode(pinIsCrusherEmpty, INPUT);

		pinMode(pinCrusherOutMostLeft, INPUT); //this is a microswitch to stop moving the router at the end 
		pinMode(pinCrusherOutMostRight, INPUT);

		pinMode(pinCrusherOutSelectorJagung1, INPUT); //this is a microswitch to stop moving the router at the right output
		pinMode(pinCrusherOutSelectorJagung2, INPUT);
		pinMode(pinCrusherOutSelectorSBM1, INPUT);

		pinMode(pinRelayMoveCrusherOutLeft, OUTPUT);
		pinMode(pinRelayMoveCrusherOutRight, OUTPUT);


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

	}

	@Override
	protected void mainLoop() {

		//check and fill each silos

		if(movingState == 0) {

			if(sharedBucketJagungOrSbm == 0) { //jagung

				if(targetPos == 0 && digitalRead(pinCrusherOutSelectorJagung1) == HIGH) {  

					if(fillStateJagung1 == 2) {

						if(digitalRead(pinBufferLevelJagung1[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle

							turnOffCleanerCrusherLine();
							if(fillStateJagung2 == 2) {  //if full, and jagung2 is auto, move the crusher out to jagung2
								moveCrusherOutToJagung2(); 
							}

						} else if(digitalRead(pinBufferLevelJagung1[fillLevelJagung1]) == HIGH) { //IR read 1 = no obstacle at the fillLevel

							turnOnCleanerCrusherLine(); //lower than fill level, start the bucket 

						} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue

					} else if(fillStateJagung1 == 1) {

						turnOnCleanerCrusherLine(); //lower than fill level, start the bucket

					} else if(fillStateJagung1 == 0) { //off

						turnOffCleanerCrusherLine();

					}

				} if(targetPos == 1 && digitalRead(pinCrusherOutSelectorJagung2) == HIGH) {  

					if(fillStateJagung2 == 2) {

						if(digitalRead(pinBufferLevelJagung2[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle

							turnOffCleanerCrusherLine();
							if(fillStateJagung1 == 2) {  //if full, and jagung1 is auto, move the crusher out to jagung2
								moveCrusherOutToJagung1(); //TO DO create the function
							}

						} else if(digitalRead(pinBufferLevelJagung2[fillLevelJagung2]) == HIGH) { //IR read 1 = no obstacle at the fillLevel

							turnOnCleanerCrusherLine(); //lower than fill level, start the bucket 

						} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue

					} else if(fillStateJagung2 == 1) {

						turnOnCleanerCrusherLine(); //lower than fill level, start the bucket

					} else if(fillStateJagung2 == 0) { //off

						turnOffCleanerCrusherLine();

					}

				} else { //setting is set for jagung, but crusher out not yet set for jagung

					//only move if pos not defined yet (only happens at start up)
					if(targetPos == -1) {
						if(fillStateJagung1 > 0) { //ON or AUTO
							moveCrusherOutToJagung1();
						} else if(fillStateJagung2 > 0) { 
							moveCrusherOutToJagung2();
						}
					} else if(targetPos == 0) {
						moveCrusherOutToJagung1();
					} else if(targetPos == 1) {
						moveCrusherOutToJagung2();
					}

				}


			} else if(sharedBucketJagungOrSbm == 1) { //sbm1

				//sbm 1
				if(targetPos == 2 && digitalRead(pinCrusherOutSelectorSBM1) == HIGH) { 
					if(fillStateSBM1 == 2) {

						if(digitalRead(pinBufferLevelSBM1[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle

							turnOffCleanerCrusherLine();

						} else if(digitalRead(pinBufferLevelSBM1[fillLevelSBM1]) == HIGH) { //IR read 1 = no obstacle at the fillLevel

							turnOnCleanerCrusherLine();

						} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue

					} else if(fillStateSBM1 == 0) { //stop filling

						turnOffCleanerCrusherLine();					

					} else if(fillStateSBM1 == 1) { //start filling

						turnOnCleanerCrusherLine();

					}

				} else { //setting is set for SBM1, but crusher out not yet set for SBM1

					//TODO wait first until EMPTY!?!?
					moveCrusherOutToSBM1();

				}

			}

		} else if(movingState > 0) { //moving

			if(targetPos == 0) {
				moveCrusherOutToJagung1();
			} else if(targetPos == 1) {
				moveCrusherOutToJagung2();
			} else if(targetPos == 2) {
				moveCrusherOutToSBM1();
			}

		}


		//sbm 2
//		System.out.println( fillStateSBM2 + 
//				" reading: " + digitalRead(pinBufferLevelSBM2[0]) 
//				+ "" + digitalRead(pinBufferLevelSBM2[1]) + "" + digitalRead(pinBufferLevelSBM2[2]) );
		if(fillStateSBM2 == 2) { //0=off, 1=on, 2=auto
			if(digitalRead(pinBufferLevelSBM2[2]) == LOW) { //level 2 = full, IR obstacle read 0 = there is obstacle
				digitalWrite(pinRelayBucketSBM2, LOW); //full, stop the bucket
			} else if(digitalRead(pinBufferLevelSBM2[fillLevelSBM2]) == HIGH) { //IR read 1 = no obstacle at the fillLevel
				digitalWrite(pinRelayBucketSBM2, HIGH); //lower than fill level, start the bucket
			} //else, it means, it is filling already passed fill level, but not yet full, just do nothing and continue
		} else if(fillStateSBM2 == 0) { //off
			digitalWrite(pinRelayBucketSBM2, LOW); //stop the bucket
		} else if(fillStateSBM2 == 1) {
			digitalWrite(pinRelayBucketSBM2, HIGH); //start the bucket
		}

	}


	void turnOnCleanerCrusherLine(){

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

	void stopMoveCrusherOut() {
		digitalWrite(pinRelayMoveCrusherOutLeft, LOW);
		digitalWrite(pinRelayMoveCrusherOutRight, LOW);
		movingState = 0;
	}

	void moveCrusherOutLeft() {
		digitalWrite(pinRelayMoveCrusherOutRight, LOW);
		digitalWrite(pinRelayMoveCrusherOutLeft, HIGH);
		movingState = 1;
	}

	void moveCrusherOutRight() {
		digitalWrite(pinRelayMoveCrusherOutLeft, LOW);
		digitalWrite(pinRelayMoveCrusherOutRight, HIGH);
		movingState = 2;
	}

	void moveCrusherOutToJagung1() {

		targetPos = 0;

		if(movingState == 0) { //first time moving
			moveCrusherOutLeft();
		}

		if(digitalRead(pinCrusherOutSelectorJagung1) == HIGH) {
			stopMoveCrusherOut();
		} else if(digitalRead(pinCrusherOutMostLeft) == HIGH) { //reaches most left, move right
			moveCrusherOutRight();
		} else if(digitalRead(pinCrusherOutMostRight) == HIGH) { //reaches most right, move left
			moveCrusherOutLeft();
		}

	}


	void moveCrusherOutToJagung2() {

		targetPos = 1;

		if(movingState == 0) { //first time moving
			moveCrusherOutRight();
		}

		if(digitalRead(pinCrusherOutSelectorJagung2) == HIGH) {
			stopMoveCrusherOut();
		} else if(digitalRead(pinCrusherOutMostRight) == HIGH) { //reaches most right, move left
			moveCrusherOutLeft();
		} else if(digitalRead(pinCrusherOutMostLeft) == HIGH) { //reaches most left, move right
			moveCrusherOutRight();
		} 

	}


	void moveCrusherOutToSBM1() {
		
//		System.out.println("moving crusher to SBM1");

		targetPos = 2;

		if(movingState == 0) { //first time moving
			moveCrusherOutRight();
		}

		if(digitalRead(pinCrusherOutSelectorSBM1) == HIGH) {
			stopMoveCrusherOut();
		} else if(digitalRead(pinCrusherOutMostRight) == HIGH) { //reaches most right, move left
			moveCrusherOutLeft();
		} else if(digitalRead(pinCrusherOutMostLeft) == HIGH) { //reaches most left, move right
			moveCrusherOutRight();
		} 

	}



	void turnOffCleanerCrusherLine() {	
		digitalWrite(pinRelayCleanerBlower, LOW); 
		digitalWrite(pinRelayBucketCleanerOut, LOW); 
		digitalWrite(pinRelayCrusher, LOW); 
		digitalWrite(pinRelayBucketCrusherOut, LOW);
	}

	@Override
	protected void writeRequestByteSpecific(byte[] requestByte) {
		//nothing special
	}

	@Override
	protected void readResponseByteSpecific(byte[] responseByte) {
		//nothing special
	}


/*	public String printStateDetails() {
		
		StringBuffer sb  = new StringBuffer(super.printStateDetails());

		sb.append("\n");
		sb.append("\nJagung/SBM:\t " + this.sharedBucketJagungOrSbm);
		sb.append(" . . . . . ||| TargetPos Jagung1/Jagung2/SBM1 :\t. " + targetPos);
		
		sb.append("\nMoving Left/Right:\t " + digitalRead(pinRelayMoveCrusherOutLeft) + "/" + digitalRead(pinRelayMoveCrusherOutRight));
		sb.append(" .||| CurrentPos Jagung1/Jagung2/SBM1:\t " + digitalRead(pinCrusherOutSelectorJagung1) +""+ digitalRead(pinCrusherOutSelectorJagung2) + "" + digitalRead(pinCrusherOutSelectorSBM1));
		
		sb.append("\n");
		sb.append("\nSBM Cleaner/Crusher Conf :\t " + this.sbmCleanerOnOff + " / " + this.sbmCrusherOnOff);
		sb.append("\nCleaner/OutBucket Status :\t " + digitalRead(pinRelayCleanerBlower) + " / " + digitalRead(pinRelayBucketCleanerOut));
		sb.append(" || Crusher/OutBucket Status :\t " + digitalRead(pinRelayCrusher) + " / " + digitalRead(pinRelayBucketCrusherOut));
		
		sb.append("\n");
		String tempString = "STOP";
		if(targetPos==0 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
		}
		sb.append("\nJagung1 State:\t " + tempString);
		
		tempString = "STOP";
		if(targetPos==1 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
		}
		sb.append(" | Jagung1 State:\t " + tempString);
		
		tempString = "STOP";
		if(targetPos==2 && digitalRead(pinRelayCrusher)==HIGH){
			tempString = "FILL";
		}
		sb.append(" | SBM1 State:\t " + tempString);
		
		
		sb.append("\nJagung1 Level:\t " + digitalRead(this.pinBufferLevelJagung1[0]) +""+ digitalRead(this.pinBufferLevelJagung1[1]) +""+ digitalRead(this.pinBufferLevelJagung1[2])); 
		sb.append(" || Jagung2 Level:\t " + digitalRead(this.pinBufferLevelJagung2[0]) +""+ digitalRead(this.pinBufferLevelJagung2[1]) +""+ digitalRead(this.pinBufferLevelJagung2[2])); 
		sb.append(" || SBM1 Level:\t " + digitalRead(this.pinBufferLevelSBM1[0]) +""+ digitalRead(this.pinBufferLevelSBM1[1]) +""+ digitalRead(this.pinBufferLevelSBM1[2])); 
		
		sb.append("\n");
		tempString = "STOP";
		if(digitalRead(pinRelayBucketSBM2)==HIGH){
			tempString = "FILL";
		}
		sb.append("\nSBM2 Fill State:\t " + tempString + " (Conf: " + fillLevelSBM2 + ")");
		sb.append("\nSBM2 Level . . :\t " + digitalRead(this.pinBufferLevelSBM2[0]) +""+ digitalRead(this.pinBufferLevelSBM2[1]) +""+ digitalRead(this.pinBufferLevelSBM2[2]));
		
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
			
		}
		
		String parentResponse = super.processCommand(stringCommand);
		if(parentResponse.startsWith("NOK")) {
			parentResponse = parentResponse 
								+ "Available commands "+this.getClass().getSimpleName()
								+": setJagung, setSBM, startSBMCrusher, stopSBMCrusher\n";
		}
		return parentResponse;
	}

}
