package com.uniteksolusi.otomill.i2c;

import java.io.IOException;

/**
 * @author nanda
 *
 * This interface is created to decouple I2CDeviceImpl
 * and allow a Dummy can be created and used without changing the rest of the code using it.
 *
 */
public interface UnitekI2CDevice {
	
	public int getAddress();
	
	public void write(final byte[] data, final int offset, final int size) throws IOException;
	
	public int read(final byte[] data, final int offset, final int size) throws IOException;

}
