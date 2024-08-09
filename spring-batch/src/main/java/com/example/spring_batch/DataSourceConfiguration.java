package com.example.spring_batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

//	@Bean
//	public DataSource dataSource() {
//		return new EmbeddedDatabaseBuilder().addScript("/org/springframework/batch/core/schema-drop-hsqldb.sql")
//			.addScript("/org/springframework/batch/core/schema-hsqldb.sql")
//			.addScript("/org/springframework/batch/samples/common/business-schema-hsqldb.sql")
//			.generateUniqueName(true)
//			.build();
//	}

//	@Bean
//	public JdbcTransactionManager transactionManager(DataSource dataSource) {
//		return new JdbcTransactionManager(dataSource);
//	}

}