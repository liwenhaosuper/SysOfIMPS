/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * Copyright (C) 2005 Luca Veltri - University of Parma - Italy
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.imps.audioEngine.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.IOException;



/**
 * RtpSocket implements a RTP socket for receiving and sending RTP packets.
 * <p>
 * RtpSocket is associated to a DatagramSocket that is used to send and/or
 * receive RtpPackets.
 */
public class RtpSocket {
	/** UDP socket */
	ImpsSocket socket;
	DatagramPacket datagram;
	
	/** Remote address */
	InetAddress r_addr;

	/** Remote port */
	int r_port;
	
	
	DatagramSocket udpSocket ;

	/** Creates a new RTP socket (only receiver) */
	public RtpSocket(ImpsSocket datagram_socket) {
		socket = datagram_socket;
		r_addr = socket.getInetAddress();
		r_port = socket.getPort();
		datagram = new DatagramPacket(new byte[1],1);
		
		System.out.println("create a new RTP socket only reciever");
	}

	/** Creates a new RTP socket (sender and receiver) */
	public RtpSocket(ImpsSocket datagram_socket,
			InetAddress remote_address, int remote_port) {
		socket = datagram_socket;
		r_addr = remote_address;
		r_port = remote_port;
		datagram = new DatagramPacket(new byte[1],1);
		
		System.out.println("Creates a new RTP socket (sender and receiver):"+r_addr+"|"+r_port);
	}

	/** Returns the RTP ImpsSocket */
	public ImpsSocket getDatagramSocket() {
		return socket;
	}

	/** Receives a RTP packet from this socket */
	public void receive(RtpPacket rtpp) throws IOException {
		datagram.setData(rtpp.packet);
		datagram.setLength(rtpp.packet.length);
		socket.receive(datagram);
		
//		if (!socket.isConnected())
//			socket.connect(datagram.getAddress(),datagram.getPort());
		rtpp.packet_len = datagram.getLength();
//		System.out.println("rece rtp:"+rtpp.packet_len+"|"+"|"+r_addr+"|"+r_port+"|"+"--content:"+new String(datagram.getData()));
	}
	
	
	
	/** Receives a RTP packet from this socket */
	public void receive(RtpPacket rtpp,int i) throws IOException {
		datagram.setData(rtpp.packet);
		datagram.setLength(rtpp.packet.length);
		socket.receive(datagram);
		
		if (!socket.isConnected())
			socket.connect(datagram.getAddress(),datagram.getPort());
		rtpp.packet_len = datagram.getLength();
//		System.out.println("do not want:"+rtpp.packet_len+"|"+"|"+r_addr+"|"+r_port+"|"+"--content:"+new String(datagram.getData()));
	}

	/** Sends a RTP packet from this socket */
	public void send(RtpPacket rtpp) throws IOException {
		datagram.setData(rtpp.packet);
		System.out.println("packet_len:"+rtpp.packet_len);
		datagram.setLength(rtpp.packet_len);
		datagram.setAddress(r_addr);
		datagram.setPort(r_port);
		socket.send(datagram);
	}
	
	
	public void send(RtpPacket rtpp,int i) throws IOException{
		datagram.setData(rtpp.packet);
		datagram.setLength(rtpp.packet_len);
		datagram.setAddress(r_addr);
		datagram.setPort(r_port);
		System.out.println("send r_addr:"+r_addr);
		System.out.println("send r_port:"+r_port);
		socket.send(datagram);
	}
	
	
	/** Closes this socket */
	public void close() { // socket.close();
	}

}
