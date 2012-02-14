package com.imps.events;

public interface IContactEventDispacher {
	void addContactEventHandler(IContactEvent event);
	void removeContactEventHandler(IContactEvent event);
}
