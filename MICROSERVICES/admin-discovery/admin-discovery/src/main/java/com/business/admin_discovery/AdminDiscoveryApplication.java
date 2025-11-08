package com.business.admin_discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class AdminDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminDiscoveryApplication.class, args);
	}

}
