package com.epam.training.spring_boot_epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SpringBootEpamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEpamApplication.class, args);
	}

}
