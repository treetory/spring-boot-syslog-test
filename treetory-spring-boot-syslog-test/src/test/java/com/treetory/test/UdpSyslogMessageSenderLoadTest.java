package com.treetory.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.sender.UdpSyslogMessageSender;

@RunWith(SpringJUnit4ClassRunner.class)
public class UdpSyslogMessageSenderLoadTest {

	//private static final Logger LOG = LoggerFactory.getLogger(UdpSyslogMessageSenderLoadTest.class);
	
	static int THREADS_COUNT;
	static int ITERATION_COUNT;
	static int SEND_COUNT;
	
	public static synchronized void increaseSendCount() {
		SEND_COUNT++;
	}
	
	static ExecutorService executorService = null;
	
	static UdpSyslogMessageSender messageSender = null;
	
	static List<String> patterns = new ArrayList<String>();
	
	public static void setExecutor(int threadCnt, int iterationCnt) {
		
		ITERATION_COUNT = iterationCnt;
		THREADS_COUNT = threadCnt;
		executorService = Executors.newFixedThreadPool(threadCnt);
		
	}
	
	public static UdpSyslogMessageSender createMessageSender(
			String messageHostname,
			String defaultAppName,
			Facility defaultFacility,
			Severity defaultSeverity,
			String syslogServerHostname,
			int syslogServerPort
			) {
		
		UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
		messageSender.setDefaultMessageHostname(messageHostname);
        messageSender.setDefaultAppName(defaultAppName);
        messageSender.setDefaultFacility(defaultFacility);
        messageSender.setDefaultSeverity(defaultSeverity);
        messageSender.setSyslogServerHostname(syslogServerHostname);
        messageSender.setSyslogServerPort(syslogServerPort);
		
		return messageSender;
	}
	
	public static void setPatterns(String hostIp) {
		patterns.add(
				String.format("Manual event. Source: %s.", hostIp)
				);
		patterns.add(
        		String.format("Scan event. Source: %s.", hostIp)
        		);
        patterns.add(
        		String.format("Port bite. Source: %s. Destination: 10.10.10.20:8080", hostIp)
        		);
        patterns.add(
        		String.format("Block Event: Host: %s, Target: 10.20.3.234, Time %d, Service: 23/TCP, Is Virtual Firewall blocking rule: false, Reason: Port block", hostIp, System.currentTimeMillis())
        		);
        /*patterns.add(
        		String.format("Block Event: Host: 10.20.3.234, Target: %s, Time %d, Service: 23/TCP, Is Virtual Firewall blocking rule: false, Reason: Port block", hostIp, System.currentTimeMillis())
        		);
        		*/
	}
	
	@Test
	public void loadTest() {
		
		try {
			
			UdpSyslogMessageSenderLoadTest.messageSender = UdpSyslogMessageSenderLoadTest.createMessageSender(
					"50.100.100.191", 
					"NAC[27681]", 
					Facility.USER, 
					Severity.CRITICAL, 
					"50.100.100.172"/*"50.100.100.189"*/,
					9898/*514*/);
			
			UdpSyslogMessageSenderLoadTest.setPatterns("172.16.0.2");
			
			UdpSyslogMessageSenderLoadTest.setExecutor(patterns.size(), 1000);
			
			for (int i = 0; i < THREADS_COUNT; i++) {
	            final String pattern = patterns.get(i);

	            Runnable command = new Runnable() {
	                @Override
	                public void run() {
	                    for (int j = 0; j < ITERATION_COUNT; j++) {
	                        try {
	                            messageSender.sendMessage(pattern);
	                            UdpSyslogMessageSenderLoadTest.increaseSendCount();
	                        } catch (IOException e) {
	                            System.err.println("ERROR in " + pattern);
	                            e.printStackTrace();
	                            break;
	                        }
	                    }
	                }
	            };

	            executorService.execute(command);
	        }

	        executorService.shutdown();
	        executorService.awaitTermination(1, TimeUnit.MINUTES);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(SEND_COUNT);
			assertEquals(SEND_COUNT, THREADS_COUNT * ITERATION_COUNT);
		}
		
	}

}
