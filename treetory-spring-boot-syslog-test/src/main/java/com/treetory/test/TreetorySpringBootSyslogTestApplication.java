package com.treetory.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableRetry
@EnableTransactionManagement
public class TreetorySpringBootSyslogTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TreetorySpringBootSyslogTestApplication.class, args);
	}
}
