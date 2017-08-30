package com.uniteksolusi.otomill.util;

public class BinaryFormatter {
	
	public static String getBinaryString(byte byteArray[]) {
		
		String tempString = "";
		for(int i=0; i<byteArray.length; i++) {
			if(i%8 == 0) {
				tempString += " ";
			}
			tempString += byteArray[i];
		}
		
		return tempString;
	}
	
	public static String getBinaryString(byte b) {
		String tempString = "";
		tempString += (byte) ((b >> 0) & 1);
		tempString += (byte) ((b >> 1) & 1);
		tempString += (byte) ((b >> 2) & 1);
		tempString += (byte) ((b >> 3) & 1);
		tempString += (byte) ((b >> 4) & 1);
		tempString += (byte) ((b >> 5) & 1);
		tempString += (byte) ((b >> 6) & 1);
		tempString += (byte) ((b >> 7) & 1);
		return tempString;
	}
	
	public static String getUnsignedByteString(byte rrbyte[]) {
		
		String tempString = "";
		for(int i=0; i<rrbyte.length; i++) {
			tempString += Byte.toUnsignedInt(rrbyte[i]);
			tempString += " ";
		}
		
		return tempString;
	}

}
