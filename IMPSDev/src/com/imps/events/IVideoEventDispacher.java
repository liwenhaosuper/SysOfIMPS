package com.imps.events;

public interface IVideoEventDispacher {
	void addVideoEventHandler(IVideoEvent event);
	void removeVideoEventHandler(IVideoEvent event);
}
