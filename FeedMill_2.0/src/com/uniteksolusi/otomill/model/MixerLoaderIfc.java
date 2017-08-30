package com.uniteksolusi.otomill.model;

public interface MixerLoaderIfc extends ArduinoModelIfc {

	
	public void startFilling();
	public void stopFilling();
	public void startEjecting();
	public void stopEjecting();
	
	public boolean isReadyForFilling();
	public boolean isFilling();
	public boolean isReadyForEject();
	public boolean isEjecting();
	
}
