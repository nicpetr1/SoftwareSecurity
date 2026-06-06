package com.example.progetto;

import java.io.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProgettoApplication {
	
	private static String token;
	
	public static void main(String[] args) {
		Console console = System.console();
        char[] passwordArray = console.readPassword(">> Inserisci la stringa di configurazione : ");             
        token = new String(passwordArray);
        console.flush();
		SpringApplication.run(ProgettoApplication.class, args);
	}

	public static String getToken() {
		return token;
	}
	
}


