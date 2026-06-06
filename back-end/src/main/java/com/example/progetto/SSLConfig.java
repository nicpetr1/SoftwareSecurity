package com.example.progetto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.stereotype.Component;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.net.URI;
import java.util.Map;

@Component
public class SSLConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private static final Logger logger = LoggerFactory.getLogger(SSLConfig.class);

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        logger.info("Configurazione SSL Dinamica avviata...");


        if (ProgettoApplication.getToken() == null || ProgettoApplication.getToken().isEmpty()) {
            throw new IllegalStateException("Impossibile avviare SSL: Token di Vault mancante!");
        }

        try {

            VaultTemplate vaultTemplate = new VaultTemplate(
                VaultEndpoint.from(URI.create("https://localhost:8200")),
                new TokenAuthentication(ProgettoApplication.getToken())
            );


            String path = "secret/data/keystorePassword";
            logger.info("Tentativo di lettura password da Vault al path: {}", path);

            VaultResponse response = vaultTemplate.read(path);

            if (response == null || response.getData() == null) {
                throw new IllegalStateException("Nessuna risposta da Vault per il path: " + path);
            }

            
            Map<String, Object> topLevelData = response.getData();
            Map<String, Object> realData = (Map<String, Object>) topLevelData.get("data");

            if (realData == null) {
                throw new IllegalStateException("Formato risposta Vault non valido (manca 'data' interno). Sei sicuro di usare KV v2?");
            }

            String password = (String) realData.get("password");

            if (password == null) {
                throw new IllegalStateException("Password non trovata dentro il segreto Vault!");
            }

            logger.info("Password recuperata con successo da Vault. Configurazione Tomcat...");

            // Configurazione SSL su Spring Boot
            Ssl ssl = new Ssl();
            ssl.setEnabled(true);
            ssl.setKeyStore("classpath:springboot.p12");
            ssl.setKeyStorePassword(password);
            ssl.setKeyStoreType("PKCS12");
            ssl.setKeyAlias("springbootnegant");
            
            ssl.setClientAuth(Ssl.ClientAuth.NEED);


            ssl.setTrustStore("classpath:truststore.p12");
            ssl.setTrustStorePassword(password);
            ssl.setTrustStoreType("PKCS12");
            

            factory.setSsl(ssl);
            factory.setPort(9999);

            logger.info("SSL Configurato correttamente sulla porta 9999");
            logger.info("SECURITY LEVEL: Mutual TLS ATTIVO (ClientAuth=NEED). Solo Apache con certificato valido potrà connettersi.");
        } catch (Exception e) {
            
            logger.error("Errore fatale durante la configurazione SSL con Vault", e);
            throw new IllegalStateException("Impossibile configurare SSL, arresto applicazione.", e);
        }
    }
}