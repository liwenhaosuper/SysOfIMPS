package com.imps.basetypes;

public class SystemMsgType {
	public static final byte FROM = 0;
	public static final byte TO = 1;
	public static final byte NOTICE = 2;
	
	public static final byte ACCEPTED = 0;
	public static final byte ACCEPT = 1;
	public static final byte DENIED = 2;
	public static final byte DENY = 3;
	public static final byte NONE = 4;
	
    public String name;
    public String time;
    public String text;
    public byte status;
    public byte type;
    public int layoutID;
}
