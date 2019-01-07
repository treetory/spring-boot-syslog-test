package com.treetory.test.common.config;

import com.treetory.test.common.properties.H2DBDatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

@Configuration
public class jooqWithH2DBConfiguration implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(jooqWithH2DBConfiguration.class);

    @Autowired
    private WebApplicationContext appContext;

    @Autowired
    private H2DBDatasourceProperties h2DBDatasourceProperties;

    /**
     * 스프링 부트의 auto configuration 을 그대로 쓴다.
     * 즉, dataSource 의 필요 property 는 application.properties 에 설정된 기본값을 쓴다.
     */
    @Bean(name = "h2DataSource")
    public DataSource dataSource() {

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(h2DBDatasourceProperties.getDriverClassName());
        config.setJdbcUrl(h2DBDatasourceProperties.getUrl());
        config.setUsername(h2DBDatasourceProperties.getUsername());
        config.setPassword(h2DBDatasourceProperties.getPassword());
        config.setMaximumPoolSize(h2DBDatasourceProperties.getMaxActive());
        config.setMinimumIdle(h2DBDatasourceProperties.getMinIdle());
        //config.setSchema("test");
        config.setPoolName("[H2]");
        HikariDataSource dataSource = new HikariDataSource(config);
        LOG.debug("{}", h2DBDatasourceProperties);
        LOG.debug("{}", config);

        return dataSource;
    }

    /**
     * Configure jOOQ's ConnectionProvider to use Spring's TransactionAwareDataSourceProxy,
     * which can dynamically discover the transaction context
     *
     * 스프링 부트가 DataSourceTransactionManager 를 직접 물고 있기 때문에,
     * jOOQ 는 proxy 를 이용하여 transaction 관리를 하는 것 같다.
     *
     * @return  DataSourceConnectionProvider
     */
    @Bean(name = "h2ConnectionProvider")
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy((DataSource) appContext.getBean("h2DataSource")));
    }

    /**
     * Configure the DSL object, optionally overriding jOOQ Exceptions with Spring Exceptions
     *
     * jOOQ 는 DSL (Domain Specific Language) 을 통해 SQL 을 다룬다. 그래서 DSLContext 를 기본적으로 필요로 한다.
     * DSLContext 를 생성할 때에 jOOQ 의 configuration 을 전달해야 하는데,
     * 아래는 connectionProvider 와 ExecuteListenerProvider 를 설정한 configuration 을 DSLContext 에 전달한다.
     *
     * @return
     */
    @Bean(name = "h2Dsl")
    //@Lazy(value = true)
    public DSLContext dsl() {

        DSLContext dsl = new DefaultDSLContext(configuration());

        return dsl;

    }

    private DefaultConfiguration configuration() {

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.H2);
        jooqConfiguration.set((ConnectionProvider) appContext.getBean("h2ConnectionProvider"));
        jooqConfiguration.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));

        return jooqConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        HikariDataSource h2DataSource = (HikariDataSource) appContext.getBean("h2DataSource");
        LOG.debug("USER = {} / DB POOL NAME = {}", h2DataSource.getUsername(), h2DataSource.getPoolName());
        LOG.debug("jOOQ Configuration = {}", appContext.getBean("h2Dsl"));
    }
}
