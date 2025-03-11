package com.app.batch.util;

import org.springframework.beans.factory.annotation.Value;

public class MysqlDB {
	 public static JDBCUtil jdbcUtil;
	@Value("${spring.datasource.username}")
	    String userName ="root";
	 @Value("${spring.datasource.password}")
	    String password = "root123456";
	 @Value("${spring.datasource.url}")
	    String URL = "jdbc:mysql://localhost:3306/workingdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	 
	 @Value("${spring.datasource.driver-class-name}")
	    String className = "com.mysql.cj.jdbc.Driver"; 
	 
	public void initiateConnection() {
	 
        jdbcUtil = new JDBCUtil(className,URL, userName,  password);
        jdbcUtil.getConnection();
	}

}
