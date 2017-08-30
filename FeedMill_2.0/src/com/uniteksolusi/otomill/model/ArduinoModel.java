package com.uniteksolusi.otomill.model;

import java.io.IOException;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.uniteksolusi.otomill.command.CommandExecutor;
import com.uniteksolusi.otomill.i2c.ArduinoI2CSlave;
import com.uniteksolusi.otomill.i2c.UnitekI2CDevice;
import com.uniteksolusi.otomill.i2c.UnitekI2CFactory;
import com.uniteksolusi.otomill.util.BinaryFormatter;

public abstract class ArduinoModel implements ArduinoI2CSlave, CommandExecutor, Runnable, ArduinoModelIfc {
	
	public static final int DEFAULT_CYCLE_DELAY = 500; //milis
	
	public static final byte LOW = 0;
	public static final byte HIGH = 1;
	
	public static final byte INPUT = 0;
	public static final byte OUTPUT = 1;
	
	transient protected UnitekI2CDevice i2cDevice;
	transient public int cycleDelay = DEFAULT_CYCLE_DELAY; //milis

	transient public Logger logger;

	
	//#### I2C pin ####//
	//Uno SDA pin : A4, SCL : A5
	//Mega SDA pin : 20, SCL : 21

	protected byte[] digitalPinState = new byte[14]; // arduino uno is 14 pins
	protected byte[] digitalPinMode = new byte[14]; // arduino uno is 14 pins

	protected int i2cRequestByteLength = 4;
	protected int i2cResponseByteLength = 4;
	
	protected String id;
	
	protected boolean isInitialized = false;
	
	
	public ArduinoModel(I2CBus bus, int address, int amountDigitalPin, int i2cRequestByteLength, int i2cResponseByteLength) {

		this.id = "0x" + Integer.toHexString(address);
		this.logger = Logger.getLogger("Model-" + getId());
		
		this.i2cDevice = UnitekI2CFactory.getI2CDevice(bus, address);
		
		this.i2cRequestByteLength = i2cRequestByteLength;
		this.i2cResponseByteLength = i2cResponseByteLength;
		
		this.digitalPinState = new byte[amountDigitalPin];
		this.digitalPinMode = new byte[amountDigitalPin];
		
		initPinModeDefault(); //make sure it is started with unused pin mode.
	
	}

	private void initPinModeDefault() {
		for(int i=0; i<digitalPinMode.length; i++) {
			digitalPinMode[i] = INPUT; //default is input
		}
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#getId()
	 */
	@Override
	public String getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#pinMode(int, byte)
	 */
	@Override
	public void pinMode(int pinNumber, byte mode) {
		digitalPinMode[pinNumber] = mode;
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#digitalRead(int)
	 */
	@Override
	public byte digitalRead(int pinNumber) {
		return digitalPinState[pinNumber];
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#digitalWrite(int, byte)
	 */
	@Override
	public void digitalWrite(int pinNumber, byte newState) {
		digitalPinState[pinNumber] = newState;
	}

	/**
	 * To be implemented in sub-classes. It is to initialize the pin mode.
	 * 
	 * example implementation:
	 * 
	  protected void initialize() {
	  	pinMode(pinRelayReadyForFilling, OUTPUT);
		pinMode(pinRelayReadyForEjectLight, OUTPUT);
		pinMode(pinRelayPneumaticOutput, OUTPUT);

		pinMode(pinButtonDoneLoading, INPUT);
		pinMode(pinSensorTubeLoaded, INPUT);
	  }
	 *
	 */
	protected abstract void initialize();
	
	protected abstract void mainLoop();
	
	private boolean started = false;
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#isStarted()
	 */
	@Override
	public boolean isStarted() {
		return started;
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#start()
	 */
	@Override
	public final void start() {
		if(!started) {
			started = true;
			new Thread(this).start();
		} else {
			logger.warning("Already Started. Do nothing.");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#stop()
	 */
	@Override
	public final void stop() {
		started = false;
	}
	
	@Override
	public final void run() {
		
		while(started) {
			
			try {
								
				pullFromDevice();
			
				if(isInitialized) {
					
					mainLoop();
					pushToDevice();
				
				} else {
				
					logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> Not Initialized Yet" );
					
					initialize();
					initializeToDevice();
				
				}
				
				Thread.sleep(cycleDelay);
				
			} catch (InterruptedException e) {
				e.printStackTrace(); //TODO what todo
			} catch (IOException e) {
				e.printStackTrace(); //TODO what todo
				logger.warning(this.getClass().getSimpleName() + "("+this.getId()+")" + "> IOException: "+ e.getMessage());
			} catch (Exception e) {
				e.printStackTrace(); //TODO what todo
				logger.severe(this.getClass().getSimpleName() + "("+this.getId()+")" + "> Exception: "+ e.getMessage());
			}
			
			logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> end-of-loop()\n");
		
		}
		
	}
	
		
	protected boolean initializeToDevice() throws IOException, InterruptedException {

		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> initializeToDevice()");
		
		byte crc = (byte) System.currentTimeMillis();

		/*
		 * 4 bytes message:
		 * 	0 : message type
		 * 			0 : Request Status
		 * 			1 : Set Pin Mode
		 * 			2 : Set Pin State
		 *  1 : pin mode / pin state 0-7
		 *  2 : pin mode / pin state 8-13
		 *  3 : CRC
		 *  
		 */
		byte[] requestByte = new byte[i2cRequestByteLength];
		requestByte[0] = 1;
		
		pushDigitalPinMode(requestByte);
				
		requestByte[i2cRequestByteLength-1] = crc;
		
		byte[] responseByte = new byte[i2cResponseByteLength];
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> Pin MDOE: " + BinaryFormatter.getBinaryString(digitalPinMode));
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> " + BinaryFormatter.getUnsignedByteString(requestByte));
		
		//make sure the communication is 1 device at a time
		synchronized (UnitekI2CFactory.getI2CBus()) {
				
			i2cDevice.write(requestByte, 0, requestByte.length);
			
			Thread.sleep(i2cResponseTime);
	
			i2cDevice.read(responseByte, 0, responseByte.length);
			
		}
		
		if(responseByte[0] == 2 && responseByte[responseByte.length-1] == crc) {
			
//			pushToDevice(); //push the initial pin state
			return true;
			
		} else {
			return false;
		}
		
	}
	
	protected void pushDigitalPinMode(byte[] requestByte) {
		
//		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
//		requestByte[1] = (byte) ( 
//									(digitalPinMode[0] * (1 << 0))    //rx
//									+ (digitalPinMode[1] * (1 << 1))  //tx
//									+ (digitalPinMode[2] * (1 << 2))
//									+ (digitalPinMode[3] * (1 << 3))
//									+ (digitalPinMode[4] * (1 << 4))
//									+ (digitalPinMode[5] * (1 << 5))
//									+ (digitalPinMode[6] * (1 << 6))
//									+ (digitalPinMode[7] * (1 << 7))  
//									);
//		
//		requestByte[2] = (byte) ( (digitalPinMode[8] * (1 << 0))   
//									+ (digitalPinMode[9] * (1 << 1))
//									+ (digitalPinMode[10] * (1 << 2))  
//									+ (digitalPinMode[11] * (1 << 3))
//									+ (digitalPinMode[12] * (1 << 4))
//									+ (digitalPinMode[13] * (1 << 5)) 
//									);
		
		
		int indexRequest = 1;
		int indexPin = 0;
		while(indexPin < digitalPinMode.length) {
			requestByte[indexRequest] = 0;
			for(int indexPos=0; indexPos < 8; indexPos++, indexPin++) {
				//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
				if(indexPin< digitalPinMode.length) {
					requestByte[indexRequest] += (byte) ( (digitalPinMode[indexPin] * (1 << indexPos)) );  
				}
			}
			indexRequest++;
		}
				
	}
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#pushToDevice()
	 */
	@Override
	public boolean pushToDevice() throws IOException, InterruptedException {
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> pushToDevice()");

		byte crc = (byte) System.currentTimeMillis();

		/*
		 * 4 bytes message:
		 * 	0 : message type
		 * 			0 : Request Status
		 * 			1 : Set Pin Mode
		 * 			2 : Set Pin State
		 *  1 : pin mode / pin state 0-7
		 *  2 : pin mode / pin state 8-13
		 *  3 : CRC
		 *  
		 */
		byte[] requestByte = new byte[i2cRequestByteLength];		
		requestByte[0] = 2;
		
		pushDigitalPinState(requestByte);
		
		writeRequestByteSpecific(requestByte);
				
		requestByte[requestByte.length-1] = crc;
		
		/*
		 * Response byte
		 */
		byte[] responseByte = new byte[i2cResponseByteLength];
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + ">   pinState: " + BinaryFormatter.getBinaryString(digitalPinState));
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + ">   requestByte: " + BinaryFormatter.getUnsignedByteString(requestByte));
		
		
		//make sure the communication is 1 device at a time
		synchronized (UnitekI2CFactory.getI2CBus()) {
				
			i2cDevice.write(requestByte, 0, requestByte.length);
			
			Thread.sleep(i2cResponseTime);
	
			i2cDevice.read(responseByte, 0, responseByte.length);
			
		}
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + ">   responseByte: " + BinaryFormatter.getUnsignedByteString(responseByte));
		
		if(responseByte[0] == 2 && responseByte[responseByte.length-1] == crc) {
			
			//checkPinOutputState(responseByte);
			return true;
		
		} else {
			return false;
		}
		
	}
	
	protected void pushDigitalPinState(byte[] requestByte) {
		
//		//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
//		requestByte[1] = (byte) ( 
//									(digitalPinState[0] * (1 << 0))    //rx
//									+ (digitalPinState[1] * (1 << 1))  //tx
//									+ (digitalPinState[2] * (1 << 2))
//									+ (digitalPinState[3] * (1 << 3))
//									+ (digitalPinState[4] * (1 << 4))
//									+ (digitalPinState[5] * (1 << 5))
//									+ (digitalPinState[6] * (1 << 6))
//									+ (digitalPinState[7] * (1 << 7))  
//									);
//		
//		requestByte[2] = (byte) ( (digitalPinState[8] * (1 << 0))   
//									+ (digitalPinState[9] * (1 << 1))
//									+ (digitalPinState[10] * (1 << 2))  
//									+ (digitalPinState[11] * (1 << 3))
//									+ (digitalPinState[12] * (1 << 4))
//									+ (digitalPinState[13] * (1 << 5)) 
//									);
				
		int indexRequest = 1;
		int indexPin = 0;
		while(indexPin < digitalPinState.length) {
			requestByte[indexRequest] = 0;
			for(int indexPos=0; indexPos < 8; indexPos++, indexPin++) {
				//bit shifting operation (1 << 7) = 128, (1<<6) = 64, etc.
				if(indexPin< digitalPinMode.length) {
					requestByte[indexRequest] += (byte) ( (digitalPinState[indexPin] * (1 << indexPos)) );   
				}
			}
			indexRequest++;
		}
		
	}
		
	protected abstract void writeRequestByteSpecific(byte[] requestByte);

	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#pullFromDevice()
	 */
	@Override
	public boolean pullFromDevice() throws IOException, InterruptedException {
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + "> pullFromDevice()");
		
		byte crc = (byte) System.currentTimeMillis();

		/* request :
		 * 0 : 0
		 */
		byte[] requestByte = new byte[i2cRequestByteLength];
		requestByte[0] = 0;
		requestByte[requestByte.length-1] = crc;
		
		/* 
		 * 4 bytes response:
		 * 	0   : response code
		 * 			0 : NOT INITIALIZED
		 * 			1 : NOT OK
		 * 			2 : OK
		 * 	1   : pin state 0-7
		 * 	2   : pin state 8-13
		 *  4-N : response byte specific
		 * 	N   : CRC 
		 */
		byte[] responseByte = new byte[i2cResponseByteLength];
	
		
		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + ">   requestByte: " + BinaryFormatter.getUnsignedByteString(requestByte) );

		
		//make sure the communication is 1 device at a time
		synchronized (UnitekI2CFactory.getI2CBus()) {
			
			i2cDevice.write(requestByte, 0, requestByte.length);
			
			Thread.sleep(i2cResponseTime);
	
			i2cDevice.read(responseByte, 0, responseByte.length);
			
		}

		logger.fine(this.getClass().getSimpleName() + "("+this.getId()+")" + ">   responseByte: " + BinaryFormatter.getUnsignedByteString(responseByte) );
		
		if(responseByte[responseByte.length-1] == crc) {
			
			if(responseByte[0] == 0) {
				
				isInitialized = false;
				
			} else if(responseByte[0] == 2) {
				
				isInitialized = true;
				pullDigitalPinState(responseByte);
				readResponseByteSpecific(responseByte);

			}
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
	
	protected void checkPinOutputState(byte[] responseByte) {
		
		for(int i=0; i<digitalPinMode.length; i++) {
			
			byte state = (byte) ((responseByte[(i/8) + 1] >> i%8) & 1);
			
			if(digitalPinMode[i] == OUTPUT) {
				
				//if OUTPUT but state is not the same as prev state
				if(isInitialized && digitalPinState[i]!=state) {
					logger.warning("State Output ["+i+"] from device ("+state+") is NOT the same as model ("+digitalPinState[i]+") ");
				}
				
			}
			
			
		}
		
			
	}
	
	protected void pullDigitalPinState(byte[] responseByte) {
		
		for(int i=0; i<digitalPinMode.length; i++) {
			
			byte state = (byte) ((responseByte[(i/8) + 1] >> i%8) & 1);
			
			
			
			if(digitalPinMode[i] == INPUT) {
				digitalPinState[i] = state; 
			} 
			
			else {
				
				//if OUTPUT but state is not the same as prev state
//				if(isInitialized && digitalPinState[i]!=state) {
//					logger.warning(this + "State Output ["+i+"] from device ("+state+") is NOT the same as model ("+digitalPinState[i]+") ");
//				}
				
			}
			
			
		}
		
//		digitalPinState[0]  = (byte) ((responseByte[1] >> 0) & 1);
//		digitalPinState[1]  = (byte) ((responseByte[1] >> 1) & 1);
//		digitalPinState[2]  = (byte) ((responseByte[1] >> 2) & 1);
//		digitalPinState[3]  = (byte) ((responseByte[1] >> 3) & 1);
//		digitalPinState[4]  = (byte) ((responseByte[1] >> 4) & 1);
//		digitalPinState[5]  = (byte) ((responseByte[1] >> 5) & 1);
//		digitalPinState[6]  = (byte) ((responseByte[1] >> 6) & 1);
//		digitalPinState[7]  = (byte) ((responseByte[1] >> 7) & 1);
//
//		digitalPinState[8]   = (byte) ((responseByte[2] >> 0) & 1);
//		digitalPinState[9]   = (byte) ((responseByte[2] >> 1) & 1);
//		digitalPinState[10]  = (byte) ((responseByte[2] >> 2) & 1);
//		digitalPinState[11]  = (byte) ((responseByte[2] >> 3) & 1);
//		digitalPinState[12]  = (byte) ((responseByte[2] >> 4) & 1);
//		digitalPinState[13]  = (byte) ((responseByte[2] >> 5) & 1);
			
	}

	
	
	protected abstract void readResponseByteSpecific(byte[] responseByte);
	
	
	public String toString() {

		return this.getClass().getSimpleName() + "(" + this.getId() + ")";
	
	}
	
	public String printStateDetails() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(  "Type . . :\t " + this.getClass().getSimpleName()); 
		sb.append(" (Id: " + this.getId() + ")");
//		sb.append("\n");
		sb.append("\nPin Mode :\t " + BinaryFormatter.getBinaryString(this.digitalPinMode));
		sb.append("\nPin State:\t " + BinaryFormatter.getBinaryString(this.digitalPinState));
		
		return sb.toString();
		
	}
	
	public String printStateDetails2() {
		StringBuffer sb = new StringBuffer();
		sb.append(  "Type. . :\t " + this.getClass().getSimpleName());
		sb.append(" (Id: " + this.getId() + ")");
//		sb.append("\n");
		sb.append("\nPin Mode :\t " + BinaryFormatter.getBinaryString(this.digitalPinMode));
		sb.append("\nPin State:\t " + BinaryFormatter.getBinaryString(this.digitalPinState));
		
		return sb.toString();
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.uniteksolusi.otomill.model.ArduinoModelIfc#processCommand(java.lang.String)
	 */
	@Override
	public String processCommand(String stringCommand) {
		
		String[] cmds = stringCommand.trim().split(" ");
		
		if(cmds.length > 0) {
		
			if("status".equals(cmds[0])) {
				return this.printStateDetails();
			}
			
			if("start".equals(cmds[0])) {
				start();
				return "OK"; 
			}
			
			if("stop".equals(cmds[0])) {
				stop();
				return "OK"; 
			}
			
			if(cmds[0].startsWith("digitalwrite")) {
				
				if(cmds.length >= 3) {
					digitalWrite(Integer.valueOf(cmds[1]), Byte.valueOf(cmds[2]));
					return "OK";
				}
			}
			
			if(cmds[0].startsWith("digitalread")) {
				return String.valueOf( digitalRead(Integer.valueOf(cmds[1])) );
			}
			
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("NOK\n");
		sb.append("Invalid command: " + stringCommand + "\n");
		sb.append("Available commands "+this.getClass().getSimpleName()+": start, stop, status, digitalwrite x y, digitalread x\n");

		return sb.toString();
		
	}
	
	
	
	
}
