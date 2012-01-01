package com.imps.server.net.impl;

import com.imps.server.net.Configure;
import com.imps.server.net.ConfigureBuilder;
import com.imps.server.net.impl.PropertiesConfigureBuilder;

/**
 *
 */
public class PropertiesConfigureBuilder implements ConfigureBuilder {
	
	private static ConfigureBuilder instance;
	
	public static ConfigureBuilder getInstance() {
		if(instance == null) {
			instance = new PropertiesConfigureBuilder();
		}
		
		return instance;
	}
	
	
	@Override
	public Configure buildConfigure(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

}
