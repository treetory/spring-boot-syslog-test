package com.treetory.test.mvc.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.treetory.test.mvc.model.moca.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.treetory.test.mvc.service.SyslogService;
import com.treetory.test.syslog.UDPSyslogServerConfig;

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
			value = "/create/multi",
			method = { RequestMethod.POST },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public boolean createSyslogServer(HttpServletRequest req, HttpServletResponse res, @RequestBody /*Map<String, Object> param*/Device device) throws Exception {
		return sService.createSyslogServer(device);
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
	
	@RequestMapping(
			value = "/destroy/multi",
			method = { RequestMethod.DELETE },
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	@ResponseStatus(HttpStatus.OK)
	public boolean destorySyslogServer(HttpServletRequest req, HttpServletResponse res, @RequestBody /*Map<String, Object> param*/Device device) throws Exception {
		return sService.destorySyslogServer(device);
	}
	
}
