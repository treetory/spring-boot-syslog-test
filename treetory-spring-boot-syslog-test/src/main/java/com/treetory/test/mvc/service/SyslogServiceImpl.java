package com.treetory.test.mvc.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
//import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.treetory.test.syslog.CustomSyslogServer;
import com.treetory.test.syslog.UDPSyslogServer;
import com.treetory.test.syslog.UDPSyslogServerConfig;

@Service
public class SyslogServiceImpl implements SyslogService{

	private static final Logger LOG = LoggerFactory.getLogger(SyslogServiceImpl.class);
	
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
		
		return ((UDPSyslogServer) syslogServer).getSocketStatus();
	}
	
	private static Map<Integer, SyslogServerIF> servers = new HashMap<Integer, SyslogServerIF>();
	
	@Override
	public boolean createSyslogServer(Map<String, Object> param) {
		
		String host = (String) param.get("host");
		int port = (int) param.get("port");
		
		if (servers.containsKey(port)) {
			servers.get(port).shutdown();
		}
		
		SyslogServerConfigIF config = new UDPSyslogServerConfig();
		config.setUseStructuredData(true);
		config.setHost(host);
		config.setPort(port);
		
		LOG.debug("CONFIG = > {}, {}", config.getHost(), config.getPort());
		
		SyslogServerIF syslogServer = CustomSyslogServer.createThreadedInstance(SyslogConstants.UDP, config);
		servers.put(config.getPort(), syslogServer);
		
		return servers.get(config.getPort()).getThread().isAlive();
	}

	@Override
	public boolean destorySyslogServer(Map<String, Object> param) {
		
		//String host = (String) param.get("host");
		int port = (int) param.get("port");
		
		boolean isDestroyed = false;
		
		if (servers.containsKey(port)) {
			servers.get(port).shutdown();
		}
		
		LOG.debug("SOCKET STATUS = {}", ((UDPSyslogServer) servers.get(port)).getSocketStatus());
		
		if (((UDPSyslogServer) servers.get(port)).getSocketStatus()) {
			servers.remove(port);
			if (!servers.containsKey(port)) {
				isDestroyed = true;
			}
		}
		
		LOG.debug("isDestroyed = {} / REMAIN KEYS = {}", isDestroyed, servers.keySet());
		
		return isDestroyed;
	}

	@Override
	public boolean destroyWholeSyslogServer() {
		
		boolean isDestroyed = false;
		
		servers.values().stream().forEach(s -> s.shutdown());
		
		servers.clear();
		
		isDestroyed = servers.isEmpty();
		
		LOG.debug("isDestroyed = {} / REMAIN KEYS = {}", isDestroyed, servers.keySet());
		
		return isDestroyed;
	}
	
}
