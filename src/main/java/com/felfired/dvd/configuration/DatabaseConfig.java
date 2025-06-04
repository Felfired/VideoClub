package com.felfired.dvd.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "db")
public class DatabaseConfig {

    private String name;     
    private String host;
    private String port;

    
    @Value("${db.username:}") 
    private String username; 
    @Value("${db.password:}") 
    private String password;

    // Environment variable overrides
    @Value("${DB_HOST:}")
    private String envDbHost;
    @Value("${DB_USER:}")
    private String envDbUser;
    @Value("${DB_PWD:}")
    private String envDbPwd;

    @PostConstruct
    public void applyEnvironmentVariables() {
        if (envDbHost != null && !envDbHost.isEmpty()) {
            this.host = envDbHost;
        }
        if (envDbUser != null && !envDbUser.isEmpty()) {
            this.username = envDbUser;
        }
        if (envDbPwd != null && !envDbPwd.isEmpty()) {
            this.password = envDbPwd;
        }
    }

    // Getters and Setters     
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void voidsetPort(String port) {
        this.port = port;
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
}