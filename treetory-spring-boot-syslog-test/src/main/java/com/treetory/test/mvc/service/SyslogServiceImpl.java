package com.treetory.test.mvc.service;

import java.util.HashMap;
import java.util.Map;

import com.treetory.test.mvc.model.moca.Device;
import org.jooq.DSLContext;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.server.SyslogServer;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.treetory.test.syslog.CustomSyslogServer;
import com.treetory.test.syslog.CustomUDPSyslogServer;
import com.treetory.test.syslog.UDPSyslogServerConfig;

@Service
public class SyslogServiceImpl implements SyslogService{

	private static final Logger LOG = LoggerFactory.getLogger(SyslogServiceImpl.class);
	
	public static final int SYSLOG_PORT = 9898;
	
	private static SyslogServerIF syslogServer = null;

	@Autowired
	private DSLContext dsl;

	@Autowired
	private DSLContext h2Dsl;

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
		
		return ((CustomUDPSyslogServer) syslogServer).getSocketStatus();
	}
	
	private static Map<Integer, SyslogServerIF> servers = new HashMap<Integer, SyslogServerIF>();
	
	@Override
	public boolean createSyslogServer(/*Map<String, Object> param*/Device device) {
		
		String host = /*(String) param.get("host")*/device.getHost();
		int port = /*(int) param.get("port")*/device.getPort();
		
		if (servers.containsKey(port)) {
			servers.get(port).shutdown();
		}
		
		SyslogServerConfigIF config = new UDPSyslogServerConfig();
		config.setUseStructuredData(true);
		config.setHost(host);
		config.setPort(port);
		
		LOG.debug("CONFIG = > {}, {}", config.getHost(), config.getPort());
		
		SyslogServerIF syslogServer = CustomSyslogServer.createThreadedInstance(SyslogConstants.UDP, config, h2Dsl);
		servers.put(config.getPort(), syslogServer);
		
		return servers.get(config.getPort()).getThread().isAlive();
	}

	@Override
	public boolean destorySyslogServer(/*Map<String, Object> param*/Device device) {
		
		//String host = (String) param.get("host");
		int port = /*(int) param.get("port")*/device.getPort();
		
		boolean isDestroyed = false;
		
		if (servers.containsKey(port)) {
			servers.get(port).shutdown();
            CustomSyslogServer.shutdown(port);
		}
		
		LOG.debug("SOCKET STATUS = {}", ((CustomUDPSyslogServer) servers.get(port)).getSocketStatus());
		
		if (((CustomUDPSyslogServer) servers.get(port)).getSocketStatus()) {
			servers.remove(port);
			if (!servers.containsKey(port)) {
				isDestroyed = true;
			}
		}
		
		LOG.debug("SYSLOG SERVER [{}] isDestroyed = {} / REMAIN KEYS = {}", port, isDestroyed, servers.keySet());
		
		return isDestroyed;
	}

	@Override
	public boolean destroyWholeSyslogServer() {
		
		boolean isDestroyed = false;
		
		servers.values().stream().forEach(s -> s.shutdown());
		
		servers.clear();
		
		isDestroyed = servers.isEmpty();
		
		LOG.debug("WHOLE SYSLOG SERVER isDestroyed = {} / REMAIN KEYS = {}", isDestroyed, servers.keySet());
		
		return isDestroyed;
	}
	
}
