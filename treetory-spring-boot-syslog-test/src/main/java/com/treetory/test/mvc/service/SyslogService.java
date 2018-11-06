package com.treetory.test.mvc.service;

import java.util.Map;

public interface SyslogService {
	
	boolean createSyslogServer();

	boolean destorySyslogServer();
	
	boolean createSyslogServer(Map<String, Object> config);

	boolean destorySyslogServer(Map<String, Object> config);
	
	boolean destroyWholeSyslogServer();
}
