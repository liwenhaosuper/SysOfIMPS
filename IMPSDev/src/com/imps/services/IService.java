package com.imps.services;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */

public interface IService {
	//check if the service is already started
	boolean isStarted();
	//start the service
	boolean start();
	//stop the service
	boolean stop();
}
