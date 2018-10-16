package com.treetory.test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.treetory.test.common.config.ApplicationConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
    @ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
    })
public abstract class AbstractMockMvcTests {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractMockMvcTests.class);
	
	@Autowired WebApplicationContext wac; 
    @Autowired MockHttpSession session;
    @Autowired MockHttpServletRequest request;

    protected MockMvc mockMvc;
    protected ObjectMapper mapper;
    protected ObjectWriter ow;
	
	@Before
	public void setUp() throws Exception
	{
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.mapper = new ObjectMapper();
		this.mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		this.ow = mapper.writer().withDefaultPrettyPrinter();
		
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}

		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		
	}
	
	@Test
	public void test() {
		try {
			testMethod();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	abstract void testMethod() throws Exception;
	
	/**
	 * 
	 * @param 	REST API URL
	 * @param 	The parameter that being shaped by JSON String
	 * @param 	RequestMethod {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE}
	 * @return	ResultActions -> REST call 수행 결과
	 * @throws 	Exception
	 * 			Exception 이 발생하는 경우 
	 * 			-> mockMvc 를 이용해서 REST API call 을 수행하고 ResultActions 을 받아오는 과정에서 HandlerExceptionResolver 에 의해
	 * 			   Controller Exception Handling 전략을 별도로 지정한다면, org.springframework.web.util.NestedServletException
	 * 			   이 발생한다. 그 이유는 mockMvc 는 서블릿 컨테이너를 가지지 않는 container-less testing 이기 때문에, Controller 레이어에서
	 * 			   발생한 예외를 그대로 받아버린다(simply bubbles up).
	 * 			   이는 Spring Boot 를 통해 생성하는 에러 응답을 테스트하지 않는 구조를 가지고 있기 때문이기도 하다.
	 * 			   그러나 다른 REST Client 로 해당 에러가 발생하는 REST call 을 수행하면, HandlerExceptionResolver 에 의해 생성된
	 * 			   에러 페이지 혹은 에러 메시지가 설정된 HttpStatus 와 함께 수령되는 것을 볼 수 있다.
	 * 			   즉, 이 상황은 에러가 에러가 아닌 것이다...
	 * 			
	 */
	protected ResultActions sendRequest(String urlTemplate, String jsonParam, RequestMethod rm) throws Exception {
		
		RequestBuilder rb = null;
		ResultActions ra = null;
		
		if (jsonParam == null || jsonParam.length() == 0) {
			jsonParam = "{}";
		}
		
		switch(rm){
		case DELETE:
			
			rb = MockMvcRequestBuilders
					.delete(urlTemplate)
					.session(session)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.content(jsonParam);
			
			break;
		case GET:
			
			rb = MockMvcRequestBuilders
					.get(urlTemplate)
					.session(session)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.content(jsonParam);
			break;
		case POST:
			
			rb = MockMvcRequestBuilders
					.post(urlTemplate)
					.session(session)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.content(jsonParam);
			
			break;
		case PUT:
			
			rb = MockMvcRequestBuilders
					.put(urlTemplate)
					.session(session)
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.content(jsonParam);
			
			break;
		case HEAD:
			break;
		case OPTIONS:
			break;
		case PATCH:
			break;
		case TRACE:
			break;
		default:
			break;
		
		}
		
		ra = this.mockMvc
				.perform(rb)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk());

		return ra;
	}

}
