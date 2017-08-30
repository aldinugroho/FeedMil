package com.uniteksolusi.otomill.model;

public interface LoadCellIfc {

	int getCurrentWeight();

	int getTargetWeight();

	int getEmptyTolerance();

	int getFullTolerance();

}