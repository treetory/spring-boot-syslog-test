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
import java.util.LinkedList;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.impl.AbstractSyslogServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.treetory.test.mvc.task.SyslogEventParser;

/**
 * UDP syslog server implementation for syslog4j.
 *
 * @author Josef Cacek
 * @author treetory
 */
public class UDPSyslogServer extends AbstractSyslogServer {

	private static final Logger LOG = LoggerFactory.getLogger(UDPSyslogServer.class);

	protected DatagramSocket ds = null;
	
	protected ThreadPoolTaskExecutor te = null;
	
	protected LinkedList<String> queue = new LinkedList<String>();
	
	@Override
	public void shutdown() {

		if (!this.ds.isClosed()) {
			this.ds.close();
		}
		
		super.shutdown();
		
	}
	
	public boolean getSocketStatus() {
		return this.ds.isClosed();
	}
	
	@Override
	public void run() {
		
		super.shutdown = false;
		
		try {
			
			this.createDatagramSocket();
			this.createThreadPoolTaskExecutor();
			
		} catch (Exception e) {
			LOG.error("Creating DatagramSocket failed");
			e.printStackTrace();
			throw new SyslogRuntimeException(e);
		}

		byte[] receiveData = new byte[SyslogConstants.SYSLOG_BUFFER_SIZE];
		
		while (!super.shutdown) {
						
			try {
				
				final DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
				//LOG.debug("[BOUND : {} / CLOSED : {} / CONNECTED : {}]", this.ds.isBound(), this.ds.isClosed(), this.ds.isConnected());
				this.ds.receive(dp);
				
				SyslogEvent event = new SyslogEvent(receiveData, dp.getOffset(), dp.getLength());
				
				SyslogEventParser parser = SyslogEventParser.withLogMessage(event.getLogMessage());
				te.execute(parser);
								
			} catch (SocketException se) {
				/**
				 * close() 명령을 수행하여도, 현재 Thread 는 여전히 대기 중이기 때문에 while 구문 receive() 에서 계속 대기 중인 상태다.
				 * 그러므로 외부에서 소켓을 close() 하여 상태가 변화해도, receive() 는 진행된다. 
				 * 그래서 소켓이 close() 된 상태에선 receive() 수행 중에 SocketException 이 발생되며, 해당 내용은 Socket Closed 가 되는 것이다.
				 * 
				 * 소켓이 close() 된 상태에서의 receive() 수행 중 발생한 Exception 은 console / logger 에 찍지 않고 지나간다. -> 일단 의도대로 되는 것이므로...
				 */
				if (!this.ds.isClosed()) {
					se.printStackTrace();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				this.ds.disconnect();
			}
		}
		/**
		 * 보통 shutdown flag 을 이용해서 socket 이 자연적으로 닫히게 만들 땐, 이 위치(while 블럭을 빠져나간 지점)에서 소켓을 닫아준다.
		 * 근데 나의 의도와는 다르게 이 지점에서 소켓을 close 해주면 SocketException 은 발생하진 않지만, port 가 여전히 bind 되어 있는 문제가 남아있다.
		 * 
		 * 이는 소켓을 close() 했으나, 이 때 사용한 주소를 다시 사용할 수 없는 문제가 발생한다.
		 * 대부분의 article 에선 소켓을 생성 후, 주소를 bind() 하기전에 setReuseAddress(true) 옵션을 주어서 해당 주소를 다시 사용할 수 있다고 하는데...
		 * 
		 * 테스트를 해보면 이는 하드웨어 장치의 PORT 를 다시 사용하게 하는 것은 아닌 것 같다.
		 * 아마 multicast 와 같이 한 PORT 에 여러 주소가 물리게 할 때, 필요한 것으로 보이며 이는 내가 사용하려는 의도에서는 벗어난다.
		 * 
		 * 내 의도는 해당 PORT 를 사용하는 소켓을 열었다, 닫았다 제어할 수 있게 하는 것이 목적이다.
		 */
		//this.ds.close();
		
		LOG.debug("DATAGRAM SOCKET isClosed = {} / isBound = {}", this.ds.isClosed(), this.ds.isBound());
	}

	private void createThreadPoolTaskExecutor() {
		
		te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(10);
		te.setMaxPoolSize(20);
		te.setQueueCapacity(10000);
		te.setKeepAliveSeconds(60);
		te.setThreadGroupName("SYSLOG PARSER");
		te.setThreadNamePrefix("SYSLOG");
		te.setWaitForTasksToCompleteOnShutdown(true);
		
		te.initialize();
	}

	private void createDatagramSocket() throws SocketException, UnknownHostException {
		
		if (this.ds == null) {

			this.ds = new DatagramSocket();
			//this.ds.setReuseAddress(true);
			
			if (this.syslogServerConfig.getHost() != null) {
				InetAddress inetAddress = InetAddress.getByName(this.syslogServerConfig.getHost());
				this.ds = new DatagramSocket(this.syslogServerConfig.getPort(),inetAddress);
				//this.ds.bind(new InetSocketAddress(inetAddress, this.syslogServerConfig.getPort()));
			} else {
				throw new UnknownHostException("The UDPSyslogServerConfig hasn't the value of Host.");
			}
			
			LOG.debug("HOST ADDRESS = {} / PORT = {} / isBound = {} / isConnected = {} / isClosed = {}", 
					this.ds.getLocalAddress().getHostAddress(), 
					this.ds.getLocalPort(), 
					this.ds.isBound(), 
					this.ds.isConnected(), 
					this.ds.isClosed());
			
		}
		
	}

	@Override
	protected void initialize() throws SyslogRuntimeException {}

}
