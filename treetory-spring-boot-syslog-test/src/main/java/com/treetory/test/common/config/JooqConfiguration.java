package com.treetory.test.common.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class JooqConfiguration {

    /**
     * 스프링 부트의 auto configuration 을 그대로 쓴다.
     * 즉, dataSource 의 필요 property 는 application.properties 에 설정된 기본값을 쓴다.
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Configure jOOQ's ConnectionProvider to use Spring's TransactionAwareDataSourceProxy,
     * which can dynamically discover the transaction context
     *
     * 스프링 부트가 DataSourceTransactionManager 를 직접 물고 있기 때문에,
     * jOOQ 는 proxy 를 이용하여 transaction 관리를 하는 것 같다.
     *
     * @return  DataSourceConnectionProvider
     */
    @Bean(name = "connectionProvider")
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
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
    @Bean(name = "dsl")
    public DSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    private DefaultConfiguration configuration() {

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.MARIADB);
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));

        return new DefaultConfiguration();
    }

}
