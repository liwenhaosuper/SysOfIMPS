package com.imps.services.impl;

import java.util.LinkedList;

/**
 * A screen service is used to manage the active activities.An activity becomes active
 * when onResume() is called and inactive when onStop() is called.
 * @author liwenhaosuper
 *
 */
public class ScreenService {
	private LinkedList<Class<?>> mClsList = new LinkedList<Class<?>>();
	public void addScreen(Class<?> cls){
		mClsList.addFirst(cls);
	}
	public void removeScreen(Class<?> cls){
		mClsList.remove(cls);
	}
	public int getScreenStackSize(){
		return mClsList.size();
	}
}
