package com.atguigu.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
// import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * <p>Title: MySeataConfig</p>
 * Description：
 * date：2020/7/3 14:04
 */
@Configuration
public class MySeataConfig {

	// @Bean
	// public DataSource dataSource(DataSourceProperties dataSourceProperties){
	// 	HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	// 	if(StringUtils.hasText(dataSourceProperties.getName())){
	// 		dataSource.setPoolName(dataSourceProperties.getName());
	// 	}
	// 	return new DataSourceProxy(dataSource);
	// }
}
