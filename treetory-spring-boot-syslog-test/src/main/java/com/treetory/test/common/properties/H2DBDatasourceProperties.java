package com.treetory.test.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(/*value = "spring.datasource"*/prefix = "h2.datasource")
public class H2DBDatasourceProperties {

    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private int minIdle;
    private int maxActive;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    @Override
    public String toString() {
        return "H2DBDatasourceProperties{" +
                "url='" + url + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", minIdle=" + minIdle +
                ", maxActive=" + maxActive +
                '}';
    }

}
