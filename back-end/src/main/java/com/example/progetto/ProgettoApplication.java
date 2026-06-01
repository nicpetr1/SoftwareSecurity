package com.example.progetto;

import java.io.Console;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProgettoApplication {
	
	public static String token;
	
	public static void main(String[] args) {
		Console console = System.console();
        char[] passwordArray = console.readPassword(">> Inserisci la stringa di configurazione : ");             
        token = new String(passwordArray);
        console.flush();
		SpringApplication.run(ProgettoApplication.class, args);
	}
	
}


