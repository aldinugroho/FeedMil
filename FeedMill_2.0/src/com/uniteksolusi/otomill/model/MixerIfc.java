package com.uniteksolusi.otomill.model;

public interface MixerIfc {

	long getMixingDuration();

	boolean isReadyForMixing();

	void startMixing();

	void stopMixing();

	void startEjecting();

	void stopEjecting();

}