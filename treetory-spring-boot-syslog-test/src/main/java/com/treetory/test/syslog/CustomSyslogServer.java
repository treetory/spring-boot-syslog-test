package com.treetory.test.syslog;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.productivity.java.syslog4j.Syslog4jVersion;
import org.productivity.java.syslog4j.SyslogConstants;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.productivity.java.syslog4j.server.SyslogServerIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a Singleton-based interface for Syslog4j
 * server implementations.
 * 
 * <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
 * of the LGPL license is available in the META-INF folder in all
 * distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 * 
 * Modify org.productivity.java.syslog4j.server.SyslogServer' codes.
 * Can create multiple SyslogServer to modify key of hashtable from protocol to port.
 * 
 * @author &lt;syslog4j@productivity.org&gt;
 * @author treetory
 * @version $Id: SyslogServer.java,v 1.11 2009/04/17 02:37:04 cvs Exp $
 */
public class CustomSyslogServer implements SyslogConstants {

	private static final Logger LOG = LoggerFactory.getLogger(CustomSyslogServer.class);
	
	private static final long serialVersionUID = 5189725830841029906L;

	protected static final Map<Integer, SyslogServerIF> instances = new Hashtable<Integer, SyslogServerIF>();
	
	static {
		initialize();
	}
	
	/**
	 * @return Returns the current version identifier for Syslog4j.
	 */
	public static final String getVersion() {
		return Syslog4jVersion.VERSION;
	}
	
	private static void initialize() {
		LOG.info("CustomSyslogServer is initialized now.");
	}

	public static final SyslogServerIF getInstance(Integer port) throws SyslogRuntimeException {
		
		if (instances.containsKey(port)) {
			return (SyslogServerIF) instances.get(port);
			
		} else {
			throw new SyslogRuntimeException("SyslogServer instance using the \"[" + port + "]\" is not defined; call SyslogServer.createInstance(protocol,config) first");
		}
	}
	
	public static final SyslogServerIF getThreadedInstance(Integer port) throws SyslogRuntimeException {
		
		SyslogServerIF server = getInstance(port);

		if (server.getThread() == null) {
			Thread thread = new Thread(server);
			thread.setName(String.format("%s:[%d]", "SyslogServer", port));
			
			server.setThread(thread);
			thread.start();
		}
		
		return server;
	}
	
	public static final boolean exists(Integer port) {
		if (port == null) {
			return false;
		}
		
		return instances.containsKey(port);
	}
	
	public static final SyslogServerIF createInstance(String protocol, SyslogServerConfigIF config) throws SyslogRuntimeException {
		
		if (protocol == null || "".equals(protocol.trim())) {
			throw new SyslogRuntimeException("Instance protocol cannot be null or empty.");
		}
		
		if (config == null) {
			throw new SyslogRuntimeException("SyslogServerConfig cannot be null.");
		}
		
		String syslogProtocol = protocol.toLowerCase();
		
		SyslogServerIF syslogServer = null;
		
		synchronized(instances) {
			if (instances.containsKey(config.getPort())) {
				throw new SyslogRuntimeException("SyslogServer instance using the \"[" + config.getPort() + "]\" is already defined.");
			}
			
			try {
				
				Class<?> syslogClass = config.getSyslogServerClass();
				
				syslogServer = (SyslogServerIF) syslogClass.newInstance();
				
			} catch (ClassCastException  | IllegalAccessException | InstantiationException e) {
				throw new SyslogRuntimeException(e);
			} 
	
			syslogServer.initialize(syslogProtocol,config);
			
			instances.put(config.getPort(), syslogServer);
		}

		return syslogServer;
	}
	
	public static final SyslogServerIF createThreadedInstance(String protocol, SyslogServerConfigIF config) throws SyslogRuntimeException {
		
		createInstance(protocol,config);
		
		SyslogServerIF server = getThreadedInstance(config.getPort()); 
		
		return server;
	}
	
	public synchronized static final void shutdown() throws SyslogRuntimeException {
		
		Set<Integer> ports = instances.keySet();
		
		Iterator<Integer> ir = ports.iterator();
		
		while(ir.hasNext()) {
			
			Integer port = ir.next();
			
			SyslogServerIF syslogServer = (SyslogServerIF) instances.get(port);

			syslogServer.shutdown();
		}

		instances.clear();
	}
	
	public synchronized static final boolean shutdown(Integer port) throws SyslogRuntimeException {
		
		SyslogServerIF syslogServer = (SyslogServerIF) instances.get(port);

		syslogServer.shutdown();
		
		boolean isAlive = true;
		
		while(isAlive) {
			isAlive = syslogServer.getThread().isAlive();
		}
		
		return instances.remove(port, syslogServer);
	}
}
