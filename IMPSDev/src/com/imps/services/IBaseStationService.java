package com.imps.services;

import com.imps.basetypes.GeoLocation;
/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface IBaseStationService extends IService{
	public boolean isAvailable();
	public GeoLocation getGeoLocation();
}
