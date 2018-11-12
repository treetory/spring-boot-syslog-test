package com.treetory.test.mvc.service;

import com.treetory.test.mvc.model.moca.Device;

import java.util.Map;

public interface SyslogService {
	
	boolean createSyslogServer();

	boolean destorySyslogServer();
	
	boolean createSyslogServer(/*Map<String, Object> config*/Device device);

	boolean destorySyslogServer(/*Map<String, Object> config)*/Device device);
	
	boolean destroyWholeSyslogServer();
}
