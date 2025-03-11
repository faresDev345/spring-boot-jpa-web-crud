package com.app.theme.batch;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
@Log4j2
@Configuration
public class DataSourceConfig {

    @Bean(name = "sourceDataSource")
    @Primary
    @ConfigurationProperties(prefix = "source.datasource")
    public DataSource sourceDataSource() {
    	System.out.println("  ***********************  DataSource  1 bean built HikariDataSource");
      //  return DataSourceBuilder.create().type(HikariDataSource.class).build();
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "destinationDataSource")
    @ConfigurationProperties(prefix = "destination.datasource")
    public DataSource destinationDataSource() {
    	
        //  return DataSourceBuilder.create().type(HikariDataSource.class).build();

    	System.out.println("  ***********************  DataSource  2 bean built HikariDataSource");
        
        return DataSourceBuilder.create().build();
    }
    
    
    @Bean
    @ConfigurationProperties("source.datasource")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }
}