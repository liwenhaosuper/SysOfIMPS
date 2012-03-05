package com.imps.services;


/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface INetworkService extends IService{
	String getLocalIP(boolean ipv6);
	boolean acquire();
	boolean release();
	String getDnsServer(int type);
	boolean isAvailable();
}
