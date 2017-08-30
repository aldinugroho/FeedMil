package com.uniteksolusi.test;

public class TestMain {

	public static void main(String[] args) {
		
//		int requestInt = ( (2 * 64) 
//							+ (1 * 16) 
//							+ (2 * 4) 
//							+ (1 * 1) );
//		
//		byte requestByte = (byte) requestInt;
//		
//		System.out.println("Int=" + requestInt + " || Byte=" + requestByte + " || Binary=" + Integer.toString(Byte.toUnsignedInt(requestByte),2));
//		
//		byte theByte = (byte) requestByte;
//		System.out.print( (theByte >> 7) & 1);
//		System.out.print( (theByte >> 6) & 1);
//		System.out.print( (theByte >> 5) & 1);
//		System.out.print( (theByte >> 4) & 1);
//		System.out.print( (theByte >> 3) & 1);
//		System.out.print( (theByte >> 2) & 1);
//		System.out.print( (theByte >> 1) & 1);
//		System.out.print( (theByte >> 0) & 1);
//		System.out.println();
//		
//		System.out.println( "print byte: " + requestByte);
//		System.out.println( "print byte as unsignedInt: " + Byte.toUnsignedInt(requestByte));
//		
//		System.out.println( "(1 << 7) = " + (1 << 7));
//		
//		
//		
//		int weight = 4527;
//		
//		System.out.println( weight + " / 100 = "  + weight/100);
//		System.out.println( "calc = "  + (weight / 1 - weight / 10 * 10)  );
//		
//		requestByte = (byte) ( ( (weight / 1000) * (1 << 4))  
//									+ ( (weight / 100 - weight / 1000 * 10) * (1 << 0)) );
//		
//		System.out.println("Weight Int = " + weight +  " || RequestByte = " + requestByte + " || Binary = " + Integer.toString(Byte.toUnsignedInt(requestByte),2));
//		
//		
//		requestByte =  (byte) ( ( (weight / 10 - weight / 100 * 10) * (1 << 4))  
//							+ ( (weight / 1 - weight / 10 * 10) * (1 << 0)) );
//		
//		System.out.println("Weight Int = " + weight +  " || RequestByte = " + requestByte + " || Binary = " + Integer.toString(Byte.toUnsignedInt(requestByte),2));
//		
//		
//		long tempTime = System.currentTimeMillis();
//		System.out.println(tempTime);
//		System.out.println((byte) tempTime);
//		System.out.println();
//		
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		tempTime = System.currentTimeMillis();
//		System.out.println(tempTime);
//		System.out.println((byte) tempTime);
//		System.out.println();
//		
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		tempTime = System.currentTimeMillis();
//		System.out.println(tempTime);
//		System.out.println((byte) tempTime);
//		System.out.println();
//		
//		
//		String tempStr = "";
//		
//		tempStr += " abc ";
//		tempStr += 123;
//		System.out.println(tempStr);
//		
//		 
//		byte tempByte = 89;  //1011001
//		
//		System.out.println("89 -> 01011001");
//		
//		int first4B = (tempByte >> 4);
//		System.out.println( first4B + " -> " + (byte) first4B + " -> " + getBinaryString((byte)first4B));
//
//		int second4B = (tempByte >> 0) - first4B*16;
//		System.out.println( second4B + " -> " + (byte) second4B + " -> " + getBinaryString((byte)second4B));
//
//		int total = first4B*10 + second4B;
//		System.out.println( "total: " + total);
//		
//		
//		System.out.println( "modulus test: ");
//		System.out.println( "0%8 = " + 0%8);
//		System.out.println( "1%8 = " + 1%8);
//		System.out.println( "2%8 = " + 2%8);
//		System.out.println( "8%8 = " + 8%8);
//		System.out.println( "9%8 = " + 9%8);
//		System.out.println( "16%8 = " + 16%8);
//		
//		int id = 0x26;
//		System.out.println( "id = " + Integer.toHexString(id));
		
		
		validateIntToBin();
	}
	
	static String validateIntToBin() {
		
		byte[] requestByte = new byte[4];
		byte[] digitalPinMode = new byte[14];
		byte[] digitalPinState = new byte[14]; 
		
		//01100011 01100000
		digitalPinState[0] = 0;
		digitalPinState[1] = 1;
		digitalPinState[2] = 1;
		digitalPinState[3] = 0;
		digitalPinState[4] = 0;
		digitalPinState[5] = 0;
		digitalPinState[6] = 1;
		digitalPinState[7] = 1;
		
		digitalPinState[8] = 0;
		digitalPinState[9] = 1;
		digitalPinState[10] = 1;
		digitalPinState[11] = 0;
		digitalPinState[12] = 0;
		digitalPinState[13] = 0;
		
		
		
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
		
		
		System.out.print(requestByte[1]);
		System.out.print(" ");
		System.out.print(getBinaryString(requestByte[1]));
		System.out.print("   ");
		System.out.print(requestByte[2]);
		System.out.print(" ");
		System.out.print(getBinaryString(requestByte[2]));
		System.out.println();
		
		
		
		
		byte INPUT = 0;
		byte[] responseByte = new byte[4];
		responseByte[0] = 2;
		responseByte[1] = requestByte[1];
		responseByte[2] = requestByte[2];
		responseByte[3] = 0;
		
		for(int i=0; i<digitalPinMode.length; i++) {
			if(digitalPinMode[i] == INPUT) {
				digitalPinState[i]  = (byte) ((responseByte[(i/8) + 1] >> i%8) & 1);
			}
		}
		
		
		System.out.print(responseByte[1]);
		System.out.print(" ");
		System.out.print(getBinaryString(responseByte[1]));
		System.out.print("   ");
		System.out.print(responseByte[2]);
		System.out.print(" ");
		System.out.print(getBinaryString(responseByte[2]));
		System.out.println();
		
		
		return null;
		
	}
	
	
	static String getBinaryString(byte b) {
		String tempString = "";
		tempString += (byte) ((b >> 7) & 1);
		tempString += (byte) ((b >> 6) & 1);
		tempString += (byte) ((b >> 5) & 1);
		tempString += (byte) ((b >> 4) & 1);
		tempString += (byte) ((b >> 3) & 1);
		tempString += (byte) ((b >> 2) & 1);
		tempString += (byte) ((b >> 1) & 1);
		tempString += (byte) ((b >> 0) & 1);
		return tempString;
	}
	
	
}
