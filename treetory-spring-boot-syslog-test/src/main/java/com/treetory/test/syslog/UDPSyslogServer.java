/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.treetory.test.syslog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.impl.AbstractSyslogServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UDP syslog server implementation for syslog4j.
 *
 * @author Josef Cacek
 * @author treetory
 */
public class UDPSyslogServer extends AbstractSyslogServer {

	private static final Logger LOG = LoggerFactory.getLogger(UDPSyslogServer.class);

	protected DatagramSocket ds = null;
	
	@Override
	public void shutdown() {
		super.shutdown();
		super.thread = null;
	}
	
	@Override
	public void run() {
		
		this.shutdown = false;
		
		try {
			this.createDatagramSocket();
		} catch (Exception e) {
			System.err.println("Creating DatagramSocket failed");
			e.printStackTrace();
			throw new SyslogRuntimeException(e);
		}

		byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];

		while (!this.shutdown) {
			try {
				final DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
				this.ds.receive(dp);
				//final SyslogServerEventIF event = new Rfc5424SyslogEvent(receiveData, dp.getOffset(), dp.getLength());
				CounterACTSyslogEvent event = new CounterACTSyslogEvent(receiveData, dp.getOffset(), dp.getLength());
				System.out.println(">>> Syslog message came: " + event.getLogMessage());
			} catch (SocketException se) {
				se.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void createDatagramSocket() throws SocketException, UnknownHostException {
		
		if (this.ds == null) {

			if (this.syslogServerConfig.getHost() != null) {
				InetAddress inetAddress = InetAddress.getByName(this.syslogServerConfig.getHost());
				this.ds = new DatagramSocket(this.syslogServerConfig.getPort(),inetAddress);
			} else {
				this.ds = new DatagramSocket(this.syslogServerConfig.getPort());
			}
			
		}
		
	}

	@Override
	protected void initialize() throws SyslogRuntimeException {
		// TODO Auto-generated method stub
		
	}
	
}
