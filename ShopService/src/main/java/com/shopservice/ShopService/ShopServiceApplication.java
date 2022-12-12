package com.shopservice.ShopService;

import com.shopservice.ShopService.controller.ProductController;
import com.shopservice.ShopService.routing.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableEurekaClient
public class ShopServiceApplication {

	// On prod will use only master from cloud
	@Bean("masterDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	DataSource masterDataSource() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * Slave (read only) data source.
	 */
	@Bean("slaveDataSource")
	@ConfigurationProperties(prefix = "spring.ro-datasource")
	DataSource slaveDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@Primary
	DataSource primaryDataSource(
			@Autowired @Qualifier("masterDataSource") DataSource masterDataSource,
			@Autowired @Qualifier("slaveDataSource") DataSource slaveDataSource
	) {
		Map<Object, Object> map = new HashMap<>();
		map.put("masterDataSource", masterDataSource);
		map.put("slaveDataSource", slaveDataSource);
		RoutingDataSource routing = new RoutingDataSource();
		routing.setTargetDataSources(map);
		routing.setDefaultTargetDataSource(masterDataSource);
		return routing;
	}
	public static void main(String[] args) {
		SpringApplication.run(ShopServiceApplication.class, args);
	}

}
