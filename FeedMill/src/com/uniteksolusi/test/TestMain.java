package com.uniteksolusi.test;

public class TestMain {

	public static void main(String[] args) {
		
		int requestInt = ( (2 * 64) 
							+ (1 * 16) 
							+ (2 * 4) 
							+ (1 * 1) );
		
		byte requestByte = (byte) requestInt;
		
		System.out.println("Int=" + requestInt + " || Byte=" + requestByte + " || Binary=" + Integer.toString(Byte.toUnsignedInt(requestByte),2));
		
		byte theByte = (byte) requestByte;
		System.out.print( (theByte >> 7) & 1);
		System.out.print( (theByte >> 6) & 1);
		System.out.print( (theByte >> 5) & 1);
		System.out.print( (theByte >> 4) & 1);
		System.out.print( (theByte >> 3) & 1);
		System.out.print( (theByte >> 2) & 1);
		System.out.print( (theByte >> 1) & 1);
		System.out.print( (theByte >> 0) & 1);
		System.out.println();
		
		System.out.println( "print byte: " + requestByte);
		System.out.println( "print byte as unsignedInt: " + Byte.toUnsignedInt(requestByte));
		
		System.out.println( "(1 << 7) = " + (1 << 7));
		
		
		
		int weight = 4527;
		
		System.out.println( weight + " / 100 = "  + weight/100);
		System.out.println( "calc = "  + (weight / 1 - weight / 10 * 10)  );
		
		requestByte = (byte) ( ( (weight / 1000) * (1 << 4))  
									+ ( (weight / 100 - weight / 1000 * 10) * (1 << 0)) );
		
		System.out.println("Weight Int = " + weight +  " || RequestByte = " + requestByte + " || Binary = " + Integer.toString(Byte.toUnsignedInt(requestByte),2));
		
		
		requestByte =  (byte) ( ( (weight / 10 - weight / 100 * 10) * (1 << 4))  
							+ ( (weight / 1 - weight / 10 * 10) * (1 << 0)) );
		
		System.out.println("Weight Int = " + weight +  " || RequestByte = " + requestByte + " || Binary = " + Integer.toString(Byte.toUnsignedInt(requestByte),2));
		

		byte byte0 = 0;
		byte byte1 = 1;
		
		System.out.println("byte0 : " + byte0 + " || byte1 : " + byte1);
		System.out.println("byte0^1: " + (byte0 ^ 1) + " || byte1^1 : " + (byte1 ^ 1) );
		
	}
	
	
}
