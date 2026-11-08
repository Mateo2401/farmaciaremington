package com.uniremingto.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Punto de entrada de la aplicación de inventario farmacéutico. */
@SpringBootApplication
public class InventarioApplication {

	/** Inicia el contexto de Spring Boot y el servidor web embebido. */
	public static void main(String[] args) {
		SpringApplication.run(InventarioApplication.class, args);
	}

}
