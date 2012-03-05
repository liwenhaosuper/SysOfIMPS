package com.imps.server.net.impl;

import com.imps.server.net.Configure;
import com.imps.server.net.ConfigureBuilder;
import com.imps.server.net.impl.XmlConfigureBuilder;

/**
 * <p>
 * </p>
 * <br>
 */
public class XmlConfigureBuilder implements ConfigureBuilder {

	private static ConfigureBuilder instance;
	
	public static ConfigureBuilder getInstance() {
		if(instance == null) {
			instance = new XmlConfigureBuilder();
		}
		
		return instance;
	}
	
	@Override
	public Configure buildConfigure(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

}
