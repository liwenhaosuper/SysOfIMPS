package com.imps.model;


/**
 * Header:
 * 	    Command:   xxx
 * 		UserName:  xxx
 * 		*Password: xxx
 * 		Time: xxx
 * 		*Receiver: xxx
 * Body:
 * 		body: description
 * @author liwenhaosuper
 *
 */
public class CommandType extends IMPSType{
	
	public CommandType(String name){
		super(IMPSType.COMMAND);
		this.getmHeader().put("UserName", name);
	}
	public CommandType(String name,String command){
		super(IMPSType.COMMAND);
		this.getmHeader().put("UserName", name);
		this.getmHeader().put("Command", command);
	}
	public CommandType(){
		super(IMPSType.COMMAND);
	}
}
