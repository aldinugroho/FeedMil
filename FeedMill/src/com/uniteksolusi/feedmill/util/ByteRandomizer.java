package com.uniteksolusi.feedmill.util;

import java.util.Random;

public class ByteRandomizer {

	private static Random randomObj = new Random();

	public static byte nextByte() {
		return (byte) randomObj.nextInt(255);
	}

}
