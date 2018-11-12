package com.treetory.test.mvc.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyslogEventParser implements SyslogEventParserInterface, Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(SyslogEventParser.class);

	private SyslogServerConfigIF config;
	private String logMessage;
	//private DSLContext dsl;

	protected String[] patterns = 
		{
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)).",
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)).",
				"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]* [\\d]*) ([\\S]*: [\\d]*\\/[\\S]*), ([\\S\\s]*: [\\S]*), ([\\S]*: [\\S\\s]*)"
		};

    public static SyslogEventParser create(SyslogServerConfigIF config/*, DefaultDSLContext dsl*/) {
        SyslogEventParser parser = new SyslogEventParser();
        parser.config = config;
        //parser.dsl = dsl;
        return  parser;
    }

	public SyslogEventParser withLogMessage(String logMessage) {
		this.logMessage = logMessage;
		return this;
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
