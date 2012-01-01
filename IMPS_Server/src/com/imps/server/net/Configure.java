package com.imps.server.net;

import java.net.SocketAddress;

/**
 * <p>
 */
public class Configure {
	private long overTimeIntervalCheckTime = 5 * 1000;
	
	
	private long readOverTime = Long.MAX_VALUE;
	
	private long writeOverTime = Long.MAX_VALUE;
	
	private long bothOverTime = Long.MAX_VALUE;

	private SocketAddress address;
	
	private IoHandler ioHandler;
	
	private ProtocolHandler protocolHandler;
	
	private OverTimeHandler overTimeHandler;
	
	
	public Configure() {
		overTimeIntervalCheckTime = 5 * 1000;
		bothOverTime =  2 * 60 * 1000;
		 
		overTimeHandler = new DefaultOverTimeHandler();
	}
	
	public long getOverTimeIntervalCheckTime() {
		return overTimeIntervalCheckTime;
	}




	public void setOverTimeIntervalCheckTime(long overTimeIntervalCheckTime) {
		this.overTimeIntervalCheckTime = overTimeIntervalCheckTime;
	}




	public long getReadOverTime() {
		return readOverTime;
	}




	public void setReadOverTime(long readOverTime) {
		this.readOverTime = readOverTime;
	}




	public long getWriteOverTime() {
		return writeOverTime;
	}




	public void setWriteOverTime(long writeOverTime) {
		this.writeOverTime = writeOverTime;
	}




	public long getBothOverTime() {
		return bothOverTime;
	}




	public void setBothOverTime(long bothOverTime) {
		this.bothOverTime = bothOverTime;
	}




	public SocketAddress getAddress() {
		return address;
	}




	public void setAddress(SocketAddress address) {
		this.address = address;
	}




	public IoHandler getIoHandler() {
		return ioHandler;
	}




	public void setIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}




	public ProtocolHandler getProtocolHandler() {
		return protocolHandler;
	}




	public void setProtocolHandler(ProtocolHandler protocolHandler) {
		this.protocolHandler = protocolHandler;
	}




	public OverTimeHandler getOverTimeHandler() {
		return overTimeHandler;
	}




	public void setOverTimeHandler(OverTimeHandler overTimeHandler) {
		this.overTimeHandler = overTimeHandler;
	}

	
	public void start(IoService service) throws Exception {
		service.setConfigure(this);
		service.start();
	}
	
	
	class DefaultOverTimeHandler implements OverTimeHandler {

		@Override
		public void onBothOverTime(IoSession session) {
			session.close();    
		}

		@Override
		public void onReadOverTime(IoSession session) {
			
		}

		@Override
		public void onWriterOverTime(IoSession session) {
			
		}
	}
}
