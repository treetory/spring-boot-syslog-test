package com.treetory.test.mvc.service;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.springframework.stereotype.Service;

import com.treetory.test.syslog.UDPSyslogServerConfig;

@Service
public class SyslogServiceImpl implements SyslogService{

	public static final int SYSLOG_PORT = 9898;
	
	private static SyslogServerIF syslogServer = null;
	
	@Override
	public boolean createSyslogServer() {
		
		if (SyslogServer.exists(SyslogConstants.UDP)) {
			SyslogServer.shutdown();
		}
		
		SyslogServerConfigIF config = new UDPSyslogServerConfig();
		config.setUseStructuredData(true);
		config.setHost("0.0.0.0");
		config.setPort(SYSLOG_PORT);
		
		syslogServer = SyslogServer.createThreadedInstance(SyslogConstants.UDP, config);
		
		return syslogServer.getThread().isAlive();
	}

	@Override
	public boolean destorySyslogServer() {
		if (syslogServer != null) {
			syslogServer.shutdown();
		}
		
		return syslogServer.getThread() == null ? true : false;
	}

}
