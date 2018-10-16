package com.treetory.test.syslog;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class CounterACTSyslogEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5215623864748459915L;
	private static final String CHARSET = "UTF-8";
	
	private byte[] raw;
	private String logMessage;
	
	public CounterACTSyslogEvent(byte[] data, int offset, int length) {
		raw = new byte[length - offset];
		System.arraycopy(data, offset, raw, 0, length);
		logMessage = this.getString(raw, 0, raw.length);
	}

	private String getString(byte[] data, int startPos, int endPos) {
		try {
			return new String(data, startPos, endPos - startPos, CHARSET);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported encoding");
		}
		return "";
	}

	public String getLogMessage() {
		return logMessage;
	}
	
}
