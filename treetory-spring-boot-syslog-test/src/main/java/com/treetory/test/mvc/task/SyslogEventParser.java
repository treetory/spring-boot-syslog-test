package com.treetory.test.mvc.task;

import org.jooq.DSLContext;
import org.jooq.TableField;
import org.jooq.exception.DataAccessException;
import org.productivity.java.syslog4j.server.SyslogServerConfigIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.treetory.test.mvc.model.test.Test.TEST;

public class SyslogEventParser implements SyslogEventParserInterface, Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(SyslogEventParser.class);

	private SyslogServerConfigIF config;
	private String logMessage;
	private DSLContext dsl;

	/*
	protected String[] patterns = 
		{
				//"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)).",
				//"([\\S\\s\\S]*): ([\\S\\s\\S]*). ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]*: (?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)), ([\\S]* [\\d]*) ([\\S]*: [\\d]*\\/[\\S]*), ([\\S\\s]*: [\\S]*), ([\\S]*: [\\S\\s]*)"
				//Port Bite
				"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Port bite. Source: (\\d*.\\d*.\\d*.\\d*). Destination: (\\d*.\\d*.\\d*.\\d*):(\\d*)",
				//Manual Event
				"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Manual event. Source: (\\d*.\\d*.\\d*.\\d*).",
				//Scan Event
				"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Scan event. Source: (\\d*.\\d*.\\d*.\\d*).",
				//Block Event
				"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Block Event[\\S\\s]* Host: (\\d*.\\d*.\\d*.\\d*), Target: (\\d*.\\d*.\\d*.\\d*), Time (\\d*), Service: (\\d*[\\S\\s]*), Is Virtual Firewall blocking rule: ([\\S\\s]*), Reason: ([\\S\\s]*)"
		};
	*/

	protected Map<String, String> logPatterns;

	private Map<String, String> getLogPatterns() {

		Map<String, String> logPatterns = new HashMap<>();
		logPatterns.put("Port bite", 	"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Port bite. Source: (\\d*.\\d*.\\d*.\\d*). Destination: (\\d*.\\d*.\\d*.\\d*):(\\d*)");
		logPatterns.put("Manual event", "([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Manual event. Source: (\\d*.\\d*.\\d*.\\d*).");
		logPatterns.put("Scan event", 	"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Scan event. Source: (\\d*.\\d*.\\d*.\\d*).");
		logPatterns.put("Block Event", 	"([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Block Event[\\S\\s]* Host: (\\d*.\\d*.\\d*.\\d*), Target: (\\d*.\\d*.\\d*.\\d*), Time (\\d*), Service: (\\d*[\\S\\s]*), Is Virtual Firewall blocking rule: ([\\S\\s]*), Reason: ([\\S\\s]*)");

		return logPatterns;
	}

	protected Map<String, Map<String, Object>> extendedLogPatterns;

	private Map<String, Map<String, Object>> getExtendedLogPatterns() {

		Map<String, Map<String, Object>> logPatterns = new HashMap<>();

		Map<String, Object> port = new HashMap<>();
		port.put("regex", "([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Port bite. Source: (\\d*.\\d*.\\d*.\\d*). Destination: (\\d*.\\d*.\\d*.\\d*):(\\d*)");

		Map<TableField, String> port_mapping = new HashMap<>();
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_ID , 		"value(1)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_CLASS , 	"value(NAC)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_CLASS , 	"value(NAC)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT , 		"value(Port bite)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.ORG_ID , 		"value(9991)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.PROXY_ID , 	"value(9991)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.ACTION , 		"value(Warning)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_COUNT , 	"value(1)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.ORIGIN_IP , 	"group(2)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.S_IP , 		"group(4)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.D_IP , 		"group(5)");
		port_mapping.put(TEST.NORMALIZEDLOG_TEST.D_PORT , 		"group(6)");

		port.put("mapping", port_mapping);
		logPatterns.put("Port bite", port);

		Map<String, Object> scan = new HashMap<>();
		scan.put("regex", "([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Scan event. Source: (\\d*.\\d*.\\d*.\\d*).");

		Map<TableField, String> scan_mapping = new HashMap<>();
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_ID , 		"value(1)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_CLASS , 	"value(NAC)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_CLASS , 	"value(NAC)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT , 		"value(Scan event)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.ORG_ID , 		"value(9991)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.PROXY_ID , 	"value(9991)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.ACTION , 		"value(Warning)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_COUNT , 	"value(1)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.ORIGIN_IP , 	"group(2)");
		scan_mapping.put(TEST.NORMALIZEDLOG_TEST.S_IP , 		"group(4)");

		scan.put("mapping", scan_mapping);
		logPatterns.put("Scan event", scan);

		Map<String, Object> manual = new HashMap<>();
		manual.put("regex", "([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Manual event. Source: (\\d*.\\d*.\\d*.\\d*).");

		Map<TableField, String> manual_mapping = new HashMap<>();
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_ID , 		"value(1)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_CLASS , 		"value(NAC)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_CLASS , 	"value(NAC)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT , 			"value(Manual event)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.ORG_ID , 		"value(9991)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.PROXY_ID , 		"value(9991)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.ACTION , 		"value(Warning)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_COUNT , 	"value(1)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.ORIGIN_IP , 		"group(2)");
		manual_mapping.put(TEST.NORMALIZEDLOG_TEST.S_IP , 			"group(4)");

		manual.put("mapping", manual_mapping);
		logPatterns.put("Manual event", manual);

		Map<String, Object> block = new HashMap<>();
		block.put("regex", "([\\S\\s]*) (\\d*.\\d*.\\d*.\\d*) ([\\S\\s]*): Block Event[\\S\\s]* Host: (\\d*.\\d*.\\d*.\\d*), Target: (\\d*.\\d*.\\d*.\\d*), Time (\\d*), Service: (\\d*)[\\S\\s]*, Is Virtual Firewall blocking rule: ([\\S\\s]*), Reason: ([\\S\\s]*)");

		Map<TableField, String> block_mapping = new HashMap<>();
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_ID , 			"value(1)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.DEV_CLASS , 		"value(NAC)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_CLASS , 	"value(NAC)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT , 			"value(Block Event)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.ORG_ID , 			"value(9991)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.PROXY_ID , 		"value(9991)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.ACTION , 			"value(Warning)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.EVENT_COUNT , 	"value(1)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.ORIGIN_IP , 		"group(2)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.S_IP , 			"group(4)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.D_IP , 			"group(5)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.START_TIME , 		"group(6)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.D_PORT , 			"group(7)");
		block_mapping.put(TEST.NORMALIZEDLOG_TEST.ETC , 			"group(9)");

		block.put("mapping", block_mapping);
		logPatterns.put("Block Event", block);

		return logPatterns;
	}

	public static SyslogEventParser create(SyslogServerConfigIF config, DSLContext dsl) {
        SyslogEventParser parser = new SyslogEventParser();
        parser.config = config;
        parser.dsl = dsl;
        parser.logPatterns = parser.getLogPatterns();
        parser.extendedLogPatterns = parser.getExtendedLogPatterns();
        return  parser;
    }

	public SyslogEventParser withLogMessage(String logMessage) {
		this.logMessage = logMessage;
		return this;
	}

	@Override
	public void run() {
		
		try {
			/*
			for (String pattern : patterns) {
				Pattern _pattern = Pattern.compile(pattern, Pattern.MULTILINE);
				Matcher matcher = _pattern.matcher(this.logMessage);
				if (matcher.find()) {
					dsl.insertInto(TEST.RAWLOG_TEST, TEST.RAWLOG_TEST.LOG, TEST.RAWLOG_TEST.SYSTEM_TIMESTAMP)
                            .values(this.logMessage, new Timestamp(System.currentTimeMillis()))
                            .execute();
					this.normalizeLog(matcher);
				}
			}
			*/
			//LOG.debug("{}", this.logMessage);
			//Iterator<Map.Entry<String, String>> ir = logPatterns.entrySet().iterator();
			Iterator<Map.Entry<String, Map<String, Object>>> ir = extendedLogPatterns.entrySet().iterator();

			SEARCH_PATTERN : while(ir.hasNext()) {
				//Map.Entry<String, String> _entry = ir.next();
				Map.Entry<String, Map<String, Object>> _entry = ir.next();
				//if (this.logMessage.contains(_entry.getKey())) {
				//if (this.logMessage.indexOf(_entry.getKey()) != -1) {
				Pattern _p = Pattern.compile(_entry.getKey());
				Matcher _m = _p.matcher(this.logMessage);
				if (_m.find()) {

					//LOG.debug("ENTRY KEY = {}", _entry.getKey());

					//Pattern _pattern = Pattern.compile(_entry.getValue(), Pattern.MULTILINE);
					Pattern _pattern = Pattern.compile((String)_entry.getValue().get("regex"), Pattern.MULTILINE);
					Matcher matcher = _pattern.matcher(this.logMessage);
					if (matcher.find()) {
						//this.normalizeLog(_entry.getKey(), matcher);
						this.extendedNormalizedLog(_entry.getKey(), (Map<TableField, String>)_entry.getValue().get("mapping"), matcher);
						break SEARCH_PATTERN;
					}
				}
			}

			//LOG.debug("{}>>> Syslog message came: {}", System.lineSeparator(), this.logMessage);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dsl.insertInto(TEST.RAWLOG_TEST, TEST.RAWLOG_TEST.LOG, TEST.RAWLOG_TEST.SYSTEM_TIMESTAMP)
					.values(this.logMessage, new Timestamp(System.currentTimeMillis()))
					.execute();
					//.executeAsync();
		}
		
	}

	private void extendedNormalizedLog(String logPatternName, Map<TableField, String> mapping, Matcher matcher) throws DataAccessException {

		TableField[] tfs = new TableField[mapping.entrySet().size()+1];
		Object[] values = new Object[mapping.entrySet().size()+1];
		int i = 0;
		try {
			Iterator<Map.Entry<TableField, String>> ir = mapping.entrySet().iterator();
			while(ir.hasNext()) {
				Map.Entry<TableField, String> entry = ir.next();
				tfs[i] = entry.getKey();
				values[i] = this.createNormalizedLogValue(entry.getKey().getName(), entry.getValue(), matcher);
				i++;
			}
			tfs[i] = TEST.NORMALIZEDLOG_TEST.CREATE_TIME;
			values[i] = new Date(System.currentTimeMillis());
		} finally {
			//LOG.debug("{}", mapping);
			dsl.insertInto(TEST.NORMALIZEDLOG_TEST, tfs)
					.values(values)
					.execute();
					//.executeAsync();
		}

	}

	private Object createNormalizedLogValue(String name, String value, Matcher matcher) {

		int beginIndex = value.lastIndexOf("(")+1;
		int endIndex = value.lastIndexOf(")");

		String mapped = value.contains("value") ? value.substring(beginIndex, endIndex) : matcher.group(Integer.parseInt(value.substring(beginIndex, endIndex)));

		Object result = null;

		switch(name) {
			case "create_time" :
			case "start_time" :
			case "end_time" :
				result = new Date(Long.parseLong(mapped));
				break;
			case "dev_id" :
				result = Long.parseLong(mapped);
				break;
			case "duration":
			case "event_count":
			case "org_id":
			case "proxy_id":
			case "direction":
			case "tx_bytes":
			case "rx_bytes":
			case "s_port":
			case "d_port":
			case "severity":
			case "nat_s_port":
			case "nat_d_port":
			case "protocol_number":
				result = Integer.parseInt(mapped);
				break;
			case "origin_ip":
			case "s_ip":
			case "d_ip":
			case "nat_s_ip":
			case "nat_d_ip":

				String[] ipAddressInArray = mapped.split("\\.");

				long ipLong = 0;
				for (int i = 0; i < ipAddressInArray.length; i++) {

					int power = 3 - i;
					int ip = Integer.parseInt(ipAddressInArray[i]);
					ipLong += ip * Math.pow(256, power);

				}
				result = ipLong;

				break;
			default :
				result = mapped;
				break;
		}

		return result;
	}

	private void normalizeLog(String logPatternName, Matcher matcher) {
		StringBuffer sb = new StringBuffer();
		sb.append(System.lineSeparator());
		sb.append(String.format("[[[ %s ]]]", logPatternName));
		sb.append(System.lineSeparator());
		sb.append(String.format("GROUP COUNT = %d", matcher.groupCount()));
    	for (int i=0; i<=matcher.groupCount(); i++) {
			sb.append(System.lineSeparator());
			sb.append(String.format("GROUP[%d] ==> %s", i, matcher.group(i)));
		}
    	LOG.debug("{}", sb.toString());
	}

}
