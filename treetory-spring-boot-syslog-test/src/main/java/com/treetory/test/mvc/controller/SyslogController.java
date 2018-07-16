package com.treetory.test.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.treetory.test.mvc.service.SyslogService;

@RestController
public class SyslogController {

	@Autowired
	private SyslogService sService;
	
	@RequestMapping(
			value = "/create/syslog",
			method = { RequestMethod.POST },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public void createSyslogServer(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		sService.createSyslogServer();
		
	}
	
	@RequestMapping(
			value = "/destroy/syslog",
			method = { RequestMethod.POST },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public void destorySyslogServer(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		sService.destorySyslogServer();
		
	}
	
}
