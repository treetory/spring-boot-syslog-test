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

@RequestMapping("/syslog")
@RestController
public class SyslogController {

	@Autowired
	private SyslogService sService;
	
	@RequestMapping(
			value = "/create",
			method = { RequestMethod.POST },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public boolean createSyslogServer(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		return sService.createSyslogServer();
		
	}
	
	@RequestMapping(
			value = "/destroy",
			method = { RequestMethod.DELETE },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public boolean destorySyslogServer(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		return sService.destorySyslogServer();
		
	}
	
}
