package com.example.progetto.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import com.example.progetto.ProgettoApplication;

public class DBManager {
	
	private static Connection conn = null;
	
	private DBManager(){}
	
	public static Connection getConnection() throws SQLException {
	    if (conn == null || conn.isClosed()) {
	        try {
	            VaultTemplate vaultTemplate = new VaultTemplate(
	                VaultEndpoint.from("https://localhost:8200"),
	                new TokenAuthentication(ProgettoApplication.token)
	            );
	 
	            // Usa il path corretto (controlla se è 'myapp' o 'app')
	            String path = "secret/data/myapp";
	 
	            // 1. Leggi una risposta generica (senza forzare Secret.class)
	            VaultResponse response = vaultTemplate.read(path);
	 
	            if (response == null || response.getData() == null) {
	                throw new SQLException("Nessun segreto trovato in: " + path);
	            }
	 
	            // 2. Estrai la mappa di primo livello
	            Map<String, Object> topLevelData = response.getData();
	            // 3. GESTIONE KV v2: Controlla se c'è un ulteriore oggetto "data" annidato
	            String username = null;
	            String password = null;
	 	           
            
                Map<String, Object> realData = (Map<String, Object>) topLevelData.get("data");
                username = (String) realData.get("username");
                password = (String) realData.get("password");
	 
	            if (username == null || password == null) {
	                throw new SQLException("Username o Password sono nulli. Controlla i nomi delle chiavi su Vault");
	            }
	 
	            conn = DriverManager.getConnection(
	                "jdbc:mysql://localhost:3306/NegozioAntiquariato",
	                username, 
	                password
	            );
	 
	        } catch (Exception e) {
	        	System.out.println("Errore di vault" + e.getMessage());
	            throw new SQLException("Errore Vault: " + e.getMessage());
	        }
	    }
	    return conn;
	}
	
	public static void closeConnection() throws SQLException {
		if(conn != null) {
			conn.close();
		}
	}
	
}
