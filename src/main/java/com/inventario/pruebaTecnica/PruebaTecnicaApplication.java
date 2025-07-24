package com.inventario.pruebaTecnica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients(basePackages = "com.inventario.pruebaTecnica.helper.client")
@SpringBootApplication
@EnableAsync  
public class PruebaTecnicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PruebaTecnicaApplication.class, args);
	}

}
