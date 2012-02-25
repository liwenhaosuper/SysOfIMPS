package com.imps.events;

public interface IAudioEventDispacher {
	void addAudioEventHandler(IAudioEvent event);
	void removeAudioHandler(IAudioEvent event);
}
