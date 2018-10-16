package com.treetory.test.syslog;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyslogEvent implements Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(SyslogEvent.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5215623864748459915L;
	private static final String CHARSET = "UTF-8";
	
	private byte[] raw;
	private String logMessage;
	
	public SyslogEvent(byte[] data, int offset, int length) {
		raw = new byte[length - offset];
		System.arraycopy(data, offset, raw, 0, length);
		logMessage = this.getString(raw, 0, raw.length);
	}

	private String getString(byte[] data, int startPos, int endPos) {
		try {
			return new String(data, startPos, endPos - startPos, CHARSET);
		} catch (UnsupportedEncodingException e) {
			LOG.error("{}", "Unsupported encoding");
		}
		return "";
	}

	public String getLogMessage() {
		return logMessage;
	}
	
}
