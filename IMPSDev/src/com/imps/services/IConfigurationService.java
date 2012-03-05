package com.imps.services;

import android.content.SharedPreferences;


/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface IConfigurationService extends IService {
	void setupDefault();
	void logIn(String username,String password);
	void logOut();
	SharedPreferences getPreferences();
}
