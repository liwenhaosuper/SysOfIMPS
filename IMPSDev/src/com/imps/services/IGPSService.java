package com.imps.services;

import com.imps.basetypes.GeoLocation;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface IGPSService extends IService{
	public GeoLocation getGeoLocation();
	public boolean isAvailable();
}
