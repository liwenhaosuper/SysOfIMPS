package com.imps.services.impl;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.services.ISoundService;

public class SoundService implements ISoundService{


	private MediaPlayer ringTonePlayer;
	private MediaPlayer eventPlayer;
	private MediaPlayer connPlayer;
	
	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void playNewSms() {
		// TODO Auto-generated method stub
		if(eventPlayer==null){
			eventPlayer = MediaPlayer.create(IMPSDev.getContext(),R.raw.event);
		}
		else{
			eventPlayer.reset();
			AssetFileDescriptor afd = IMPSDev.getContext().getResources().openRawResourceFd(R.raw.event);
			try {
				this.eventPlayer.setDataSource(afd.getFileDescriptor(),
				        afd.getStartOffset(),
				        afd.getLength());
				afd.close();
				this.eventPlayer.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try{
			this.eventPlayer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopNewSms() {
		// TODO Auto-generated method stub
		if(this.eventPlayer == null){
			return;
		}
		try{
			this.eventPlayer.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playRingTone() {
		// TODO Auto-generated method stub
		if(this.ringTonePlayer == null){
			this.ringTonePlayer  = MediaPlayer.create(IMPSDev.getContext(), R.raw.ringbacktone);
		}
		else{
			this.ringTonePlayer.reset();
			try {
				AssetFileDescriptor afd = IMPSDev.getContext().getResources().openRawResourceFd(R.raw.ringbacktone);
				this.ringTonePlayer.setDataSource(afd.getFileDescriptor(),
	                    afd.getStartOffset(),
	                    afd.getLength());
				afd.close();
				this.ringTonePlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try{
			this.ringTonePlayer.setLooping(true);
			this.ringTonePlayer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopRingTone() {
		// TODO Auto-generated method stub
		if(this.ringTonePlayer == null){
			return;
		}
		try{
			this.ringTonePlayer.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playConnectionChanged(boolean connected) {
		// TODO Auto-generated method stub
		if(this.connPlayer == null){
			this.connPlayer  = MediaPlayer.create(IMPSDev.getContext(), R.raw.conn);
		}
		else{
			this.connPlayer.reset();
			try {
				AssetFileDescriptor afd = IMPSDev.getContext().getResources().openRawResourceFd(R.raw.conn);
				this.connPlayer.setDataSource(afd.getFileDescriptor(),
	                    afd.getStartOffset(),
	                    afd.getLength());
				afd.close();
				this.connPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			this.connPlayer.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopConnectionChanged(boolean connected) {
		// TODO Auto-generated method stub
		if(this.connPlayer == null){
			return;
		}
		try{
			this.connPlayer.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playCommonTone() {
		// TODO Auto-generated method stub
		playNewSms();
	}

	@Override
	public void stopCommonTone() {
		// TODO Auto-generated method stub
		stopNewSms();
	}

}
