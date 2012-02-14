/***********************************************************************
 * Module:  IMediaEventListener.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class IMediaEventListener
 ***********************************************************************/

package com.imps.media.rtp;


/** ����ʵʱ������ʵʱ��Ƶ����Ƶ���?�������?����������������Ƶ��
 * 
 * @pdOid 2f7bcee3-20b2-4756-99f0-f2127375e2e1 */
public abstract interface IMediaEventListener {
	// Media is opened
	public abstract void onMediaOpened();
	
	// Media is closed
	public abstract void onMediaClosed();

	// Media is started
	public abstract void onMediaStarted();
	
	// Media is stopped
	public abstract void onMediaStopped();

	// Media has failed
	public abstract void onMediaError(String errorMsg);
	
	public abstract void onMediaAccept();
	
	public abstract void onMediaReject();
}