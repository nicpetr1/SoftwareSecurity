package com.example.progetto.entity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import com.example.progetto.ProgettoApplication;

public class Utility {
	
	private static final Random random = new Random();

	public static int[] checkProdottoInCarrello(ArrayList<Inserimento> inserimenti, String codice) {
		int trovato = 0;
		int index = 0;
		int[] risultato = new int[2];

		// Aggiunto controllo per null
		if (inserimenti == null) {
			risultato[0] = 0;
			risultato[1] = -1; // -1 è un indice più sicuro per "non trovato"
			return risultato;
		}
		
		while(index < inserimenti.size() && trovato == 0) {
			
			// --- MODIFICA QUI ---
			// Chiama getCodiceProdotto() (la stringa) invece di getProdotto() (l'oggetto null)
			if(inserimenti.get(index).getCodiceProdotto().equals(codice)) {
				trovato = 1;
			}
			else {
				index++;
			}
		}
		
		risultato[0] = trovato;
		risultato[1] = (trovato == 1) ? index : -1; // Restituisce -1 se non trovato
		return risultato;
	}
	
	public static boolean checkProdotto(ArrayList<Inserimento> inserimenti, String codice) {
		boolean trovato = false;
		int index = 0;

		// Aggiunto controllo per null
		if (inserimenti == null) {
			return false;
		}

		while(index < inserimenti.size() && !trovato) {

			// --- MODIFICA QUI ---
			// Chiama getCodiceProdotto() (la stringa) invece di getProdotto() (l'oggetto null)
			if(inserimenti.get(index).getCodiceProdotto().equals(codice)) {
				trovato = true;
			}
			else {
				index++;
			}
		}
		return trovato;
	}
	
	public static String creaCodiceOrdine(LocalDate data, String username) {
		String dataValues[] = data.toString().split("-");
		String baseGeneratore = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(dataValues[0]);
		stringBuilder.append(dataValues[1]);
		stringBuilder.append(dataValues[2]);
		stringBuilder.append(username.substring(0, 2));
		int paddingLength = 16 - stringBuilder.length();
		for(int i = 0; i<paddingLength; i++) {
			stringBuilder.append(baseGeneratore.charAt(random.nextInt(baseGeneratore.length())));
		}
		return stringBuilder.toString();
	}
	
	public static double calcolaTotaleProdottiInseriti(ArrayList<Prodotto> prodotti) {
		double somma = 0.0;
		for(int i=0; i<prodotti.size(); i++) {
			somma = somma + (prodotti.get(i).getPrezzo() * prodotti.get(i).getQuantita());
		}
		return somma;
	}
	
	public static String creaCodiceProdotto() {
		String baseGeneratore = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder stringBuilder = new StringBuilder();
		int length = 16;
		for(int i=0; i<length; i++) {
			stringBuilder.append(baseGeneratore.charAt(random.nextInt(baseGeneratore.length())));
		}
		return stringBuilder.toString();
	}
	
	public static byte[] encrypt(String value) {
		try {
            VaultTemplate vaultTemplate = new VaultTemplate(
                VaultEndpoint.from("https://localhost:8200"),
                new TokenAuthentication(ProgettoApplication.token)
            );

            String path = "secret/data/symmetricKey";
        
            VaultResponse response = vaultTemplate.read(path);
 
            if (response == null || response.getData() == null) {
                System.out.println("Errore");
            }
 
            Map<String, Object> topLevelData = response.getData();           
          
            Map<String, Object> realData = (Map<String, Object>) topLevelData.get("data");
            String base64Key = (String) realData.get("key"); 
            byte[] decodedKey = Base64.getDecoder().decode(base64Key.getBytes(StandardCharsets.UTF_8));         
            // 2. Crea la SecretKey (specificando l'algoritmo, es. "AES")    
            SecretKey key = new SecretKeySpec(decodedKey, "AES");
           // SecretKey key = (SecretKey) realData.get("key");
            
            Cipher cipher = Cipher.getInstance("AES");
    		cipher.init(Cipher.ENCRYPT_MODE, key);
    		byte[] ciphertext = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
    		return ciphertext;
 
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
 
		return new byte[0];
	}
	
	public static String decrypt(byte[] value) {
		try {
            VaultTemplate vaultTemplate = new VaultTemplate(
                VaultEndpoint.from("https://localhost:8200"),
                new TokenAuthentication(ProgettoApplication.token)
            );

            String path = "secret/data/symmetricKey";
        
            VaultResponse response = vaultTemplate.read(path);
 
            if (response == null || response.getData() == null) {
                System.out.println("Errore");
            }
 
            Map<String, Object> topLevelData = response.getData();           
          
            Map<String, Object> realData = (Map<String, Object>) topLevelData.get("data");
            String base64Key = (String) realData.get("key"); 
            byte[] decodedKey = Base64.getDecoder().decode(base64Key.getBytes(StandardCharsets.UTF_8));         
            // 2. Crea la SecretKey (specificando l'algoritmo, es. "AES")    
            SecretKey key = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
    		cipher.init(Cipher.DECRYPT_MODE, key);
    		String cleartext = new String(cipher.doFinal(value), StandardCharsets.UTF_8);
    		return cleartext;
 
        } catch (Exception e) {
            e.getMessage();
        }
 
		return "";
	}

	
	
	
		
}