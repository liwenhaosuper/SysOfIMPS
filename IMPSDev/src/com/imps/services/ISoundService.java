package com.imps.services;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface ISoundService extends IService{
	
	//play sound when new message received
	void playNewSms();
	void stopNewSms();
	
	//media call sound
	void playRingTone();
	void stopRingTone();
	
	//connection change sound
	void playConnectionChanged(boolean connected);
	void stopConnectionChanged(boolean connected);
	
	//other cases sound
	void playCommonTone();
	void stopCommonTone();
}
