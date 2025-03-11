package com.app.batch.util;

import org.springframework.beans.factory.annotation.Value;

public class PostgresDB {
	 public static JDBCUtil jdbcUtil;
	@Value("${spring.datasource.username}")
	    String userName;
	 @Value("${spring.datasource.password}")
	    String password;
	 @Value("${spring.datasource.url}")
	    String URL;
	 
	 @Value("${spring.datasource.driver-class-name}")
	    String className; 
	 
	 public void initiateConnection() {
		 
	        jdbcUtil = new JDBCUtil(className,URL.toString(), userName, new String(password));
	        jdbcUtil.getConnection();
		}
}
