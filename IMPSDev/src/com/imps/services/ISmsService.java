package com.imps.services;

import com.imps.basetypes.MediaType;

/**
 * Copyright (C) 2010-2020 IMPS Development Team
 * @author liwenhaosuper
 *
 */
public interface ISmsService extends IService{
	//send text message and return the message id,return -1 means an error send
	int sendSms(MediaType item);
	//check whether the message with a given id is successfully sent
	boolean isSent(int msgId);
	//send audio message and return the message id,return -1 means an error send
	int SendAudio(MediaType item);
	//send image message and return the message id,return -1 means an error send
	int SendImage(MediaType item);
	//determine whether the service is available
	boolean isSmsServiceAvailable();
}
