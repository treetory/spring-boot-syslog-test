package com.treetory.test.common.config;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class JooqConfiguration implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(JooqConfiguration.class);

    @Autowired
    private WebApplicationContext appContext;

    /**
     * 스프링 부트의 auto configuration 을 그대로 쓴다.
     * 즉, dataSource 의 필요 property 는 application.properties 에 설정된 기본값을 쓴다.
     */
    @Bean(name = "dataSource")
    public DataSource dataSource() throws SQLException {
        DataSource dataSource = new MariaDbPoolDataSource();
        ((MariaDbPoolDataSource) dataSource).setUrl("jdbc:mysql://172.16.59.129:3306/test?useSSL=false&useUnicode=true&allowMultiQueries=true");
        ((MariaDbPoolDataSource) dataSource).setDatabaseName("test");
        ((MariaDbPoolDataSource) dataSource).setUser("root");
        ((MariaDbPoolDataSource) dataSource).setPassword("qwer1234!@");
        ((MariaDbPoolDataSource) dataSource).setMaxPoolSize(20);
        ((MariaDbPoolDataSource) dataSource).setMinPoolSize(10);
        ((MariaDbPoolDataSource) dataSource).setPoolName("jOOQ");
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
    @Bean(name = "connectionProvider")
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy((DataSource) appContext.getBean("dataSource")));
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
    @Lazy(value = true)
    public DSLContext dsl() {

        DSLContext dsl = new DefaultDSLContext(configuration());

        return dsl;

    }

    private DefaultConfiguration configuration() {

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(SQLDialect.MARIADB);
        jooqConfiguration.set((ConnectionProvider) appContext.getBean("connectionProvider"));
        jooqConfiguration.set(new DefaultExecuteListenerProvider(new JooqExceptionTranslator()));

        return jooqConfiguration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        MariaDbPoolDataSource dataSource = (MariaDbPoolDataSource) appContext.getBean("dataSource");
        LOG.debug("USER = {} / SCHEMA = {}", dataSource.getUser(), dataSource.getDatabaseName());
        LOG.debug("jOOQ Configuration = {}", appContext.getBean("dsl"));
    }
}
