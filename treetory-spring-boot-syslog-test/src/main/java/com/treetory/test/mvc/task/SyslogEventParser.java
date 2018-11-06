package com.treetory.test.mvc.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyslogEventParser implements SyslogEventParserInterface, Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(SyslogEventParser.class);
	
	private String logMessage;
	
	protected String[] patterns = 
		{
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)).",
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)).",
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]* [\\d]*) ([\\S]*: [\\d]*\\/[\\S]*), ([\\S\\s]*: [\\S]*), ([\\S]*: [\\S\\s]*)"
		};
	
	public static SyslogEventParser withLogMessage(String logMessage) {
		SyslogEventParser parser = new SyslogEventParser();
		parser.logMessage = logMessage;
		return parser;
	}
	
	@Override
	public void run() {
		
		try {
			
			for (String pattern : patterns) {
				Pattern _pattern = Pattern.compile(pattern, Pattern.MULTILINE);
				Matcher matcher = _pattern.matcher(this.logMessage);
				if (matcher.find()) {
					LOG.debug("{}", matcher.group());
				}
			}
			
			//LOG.debug("{}>>> Syslog message came: {}", System.lineSeparator(), this.logMessage);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//LOG.debug("QUEUE SIZE = {}", UDPSyslogServer.getQueueSize());
		}
		
	}

}
