package com.treetory.test.common.config;

import javax.servlet.Filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import com.treetory.test.mvc.service.SyslogServiceImpl;

@ComponentScan(basePackages = {"com.treetory.test.mvc", "com.treetory.test.common.util"}, 
useDefaultFilters = false,
includeFilters = {
            @ComponentScan.Filter(value = Controller.class),
            @ComponentScan.Filter(value = Service.class),
            @ComponentScan.Filter(value = Repository.class),
            @ComponentScan.Filter(value = Component.class)
            }
)
@Configuration
@EnableAsync
@EnableWebMvc
@Import(value={})
public class ApplicationConfiguration implements InitializingBean, ApplicationListener<ApplicationEvent>, WebMvcConfigurer {

	@Autowired
	private WebApplicationContext appContext;
	
    /**
     * REST 요청 시, 한글로 된 body 를 받을 때 한글 깨짐 방지
     */
    @Bean
    @Description("Prevent the broken euckr character set.")
    public Filter charactertEncodingFilter() {
    	CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    	characterEncodingFilter.setEncoding("UTF-8");
    	characterEncodingFilter.setForceEncoding(true);
    	return characterEncodingFilter;
    }
    
    @Override
    @Description("Every resources for requesting from view is registerd in here.")
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	registry.addResourceHandler("/resources/**", "/css/**", "/images/**", "/js/**", "/lib/**", "/fonts/**")
    	.addResourceLocations(
    			"classpath:/resources/",
    			"classpath:/static/css/",
    			"classpath:/static/images/",
    			"classpath:/static/js/",
    			"classpath:/static/lib/",
    			"classpath:/static/fonts/"
    			)
    	.setCachePeriod(600).resourceChain(true).addResolver(new PathResourceResolver());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
    	
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
           	
    	switch (event.getClass().getSimpleName()) {
    	case "ContextRefreshedEvent" :
    		break;
    	case "ServletWebServerInitializedEvent" :
    		break;
    	case "ApplicationStartedEvent" :
    		break;
    	case "ApplicationReadyEvent":
    		break;
    	case "ContextClosedEvent" :
    		
    		//appContext.getBean(SyslogServiceImpl.class).destorySyslogServer();
    		appContext.getBean(SyslogServiceImpl.class).destroyWholeSyslogServer();
    		
    		break;
    	}
    	
    }
    
}
