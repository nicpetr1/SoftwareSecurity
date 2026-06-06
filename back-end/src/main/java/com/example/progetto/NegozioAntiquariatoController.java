package com.example.progetto;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMethod;


import com.example.progetto.database.ProdottoDAO;
import com.example.progetto.entity.Acquisto;
import com.example.progetto.entity.Carrello;
import com.example.progetto.entity.Catalogo;

import com.example.progetto.entity.Inserimento;
import com.example.progetto.entity.Ordine;
import com.example.progetto.entity.Prodotto;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin(
		 origins = "https://localhost",
		 allowCredentials = "true",
		 methods = {RequestMethod.GET, RequestMethod.POST},
		 allowedHeaders = {"Authorization", "Content-Type"}
		)

@RestController
public class NegozioAntiquariatoController {

	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@GetMapping("/cliente/prodotti")
	public ResponseEntity<?> prodotti() {
		try {
			Catalogo catalogo = new Catalogo();
			ArrayList<Prodotto> prodotti = catalogo.leggiProdotti();	
			
			return ResponseEntity.ok(prodotti);
		} 
		catch (DAOException | DBConnectionException e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_gestore')")
	@GetMapping("/gestore/prodotti")
	public ResponseEntity<?> prodottiGestore() {
		try {
			Catalogo catalogo = new Catalogo();
			ArrayList<Prodotto> prodotti = catalogo.leggiProdotti();	
			
			return ResponseEntity.ok(prodotti);
		} 
		catch (DAOException | DBConnectionException e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@PostMapping("/cliente/prodotti/inserisci/{codice}")
	public ResponseEntity<?> inserisciProdotto(@PathVariable(value="codice") String codice, 
			@RequestParam Map<String,String> allRequestParams, JwtAuthenticationToken auth) {
        
		int quantitaInserita = 0;
        try {
            quantitaInserita = Integer.parseInt(allRequestParams.get("quantitaInserita"));
        } 
        catch (NumberFormatException e) {
            // Se la quantità non è un numero, restituisci un errore 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Quantità non valida."));
        }

        // Mappa per la risposta (sia successo che errore)
        Map<String, Object> response = new HashMap<>();

		if(quantitaInserita > 0) {
			try {
				String username = auth.getToken().getClaimAsString("preferred_username");
				// Costruisco il carrello associato al cliente
				Carrello carrello = new Carrello(username);
				// Costruisco il prodotto da aggiungere
				Prodotto prodotto = new Prodotto(codice);
				// Controllo se il prodotto si trova nel carrello
				int[] risultato = carrello.checkProdottoInCarrello(codice);
				int trovato = risultato[0];
				
                if(trovato == 1) { // Prodotto già nel carrello
					int index = risultato[1];
					int quantitaPrecedente = carrello.getInserimenti().get(index).getQuantitaInserita();
					// Controllo la nuova quantità inserita
					if(quantitaInserita <= prodotto.getQuantita()) {
						// Aggiorno la quantità inserita
						carrello.getInserimenti().get(index).setQuantitaInserita(quantitaPrecedente + quantitaInserita);
						carrello.getInserimenti().get(index).aggiornaQuantitaInserita();
						// Aggiorno la quantità disponibile del prodotto
						prodotto.setQuantita(prodotto.getQuantita() - quantitaInserita);
						prodotto.aggiornaQuantita();
					} else {
                        // 400 Bad Request: Errore logico dell'utente
                        response.put("message", "Non è possibile aggiornare la quantità inserita del prodotto");
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
					}
				}
				else { // Prodotto non nel carrello
					// Controllo la quantità inserita
					if(quantitaInserita <= prodotto.getQuantita()) {
						// Aggiungo il prodotto al carrello
						Inserimento inserimento = new Inserimento(username, codice, quantitaInserita);
						carrello.inserisciProdotto(inserimento);
						// Aggiorno la quantità disponibile del prodotto
						prodotto.setQuantita(prodotto.getQuantita()-quantitaInserita);
						prodotto.aggiornaQuantita();
					} else {
                        // 400 Bad Request
						response.put("message", "Non è possibile inserire il prodotto nelle quantita specificate");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
					}
				}
			} 
			catch (DAOException | DBConnectionException e) {
                // 500 Internal Server Error: Errore del database o della logica
				response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		
		}
		else {
            // 400 Bad Request
			response.put("message", "La quantità inserita non può essere minore o uguale di 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
        // 200 OK: Successo
        response.put("message", "Prodotto aggiunto/aggiornato nel carrello");
		return ResponseEntity.ok(response);
	}
	
	
	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@GetMapping("/cliente/visualizza")
	public ResponseEntity<?> visualizzaCarrello(JwtAuthenticationToken auth) {
		try {
			// Costruisco il carrello associato al cliente
			String username = auth.getToken().getClaimAsString("preferred_username");
			Carrello carrello = new Carrello(username);
			return ResponseEntity.ok(carrello.getInserimentiConProdotto());
		} 
		catch (DAOException | DBConnectionException e) {
			return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(e.getMessage());
		}	
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@GetMapping("/cliente/visualizza/decrementa/{codice}")
	public ResponseEntity<?> decrementaQuantitaInserita(
	        @PathVariable(value="codice") String codice, 
	        JwtAuthenticationToken auth) {
	    
	    Map<String, Object> responseBody = new HashMap<>();

	    try {
	        // 1. Recupero Username e Carrello
	        String username = auth.getToken().getClaimAsString("preferred_username");
	        Carrello carrello = new Carrello(username);

	        // 2. Controllo esistenza prodotto
	        int[] risultato = carrello.checkProdottoInCarrello(codice);
	        int trovato = risultato[0];

	        // Se il prodotto NON è nel carrello
	        if (trovato != 1) {
	            responseBody.put("message", "Il prodotto non è presente nel carrello.");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
	        }

	        // 3. Logica di decremento
	        int index = risultato[1];
	        int quantitaInserita = carrello.getInserimenti().get(index).getQuantitaInserita();
	        
	        // Decremento in memoria
	        carrello.getInserimenti().get(index).setQuantitaInserita(quantitaInserita - 1);
	        int nuovaQuantita = carrello.getInserimenti().get(index).getQuantitaInserita();

	        // Se la quantità diventa 0 -> Rimuovi
	        if (nuovaQuantita == 0) {
	            Inserimento inserimentoRimosso = carrello.getInserimenti().remove(index);
	            carrello.setDimensione(carrello.getDimensione() - 1);
	            inserimentoRimosso.eliminaInserimento();
	            responseBody.put("action", "removed");
	        } 
	        // Se la quantità è ancora > 0 -> Aggiorna
	        else {
	            carrello.getInserimenti().get(index).aggiornaQuantitaInserita();
	            responseBody.put("action", "decremented");
	        }

	        // 4. Aggiornamento Magazzino (Incremento disponibilità prodotto)
	        Prodotto prodotto = new Prodotto(codice);
	        int quantitaDisponibile = prodotto.getQuantita();
	        prodotto.setQuantita(quantitaDisponibile + 1);
	        prodotto.aggiornaQuantita();

	        responseBody.put("message", "Quantità aggiornata con successo");
	        responseBody.put("nuovaQuantita", nuovaQuantita);
	        
	        return ResponseEntity.ok(responseBody);

	    } catch (DAOException | DBConnectionException e) {
	        // Gestione errore lato server (Database)
	        responseBody.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
	    }
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@GetMapping("/carrello/prodotti/{codice}")
	public ResponseEntity<Map<String, Object>> rimuoviProdotto(@PathVariable(value="codice") String codice, JwtAuthenticationToken auth) {
	    Map<String, Object> response = new HashMap<>();

	    try {
	        String username = auth.getToken().getClaimAsString("preferred_username");
	        
	        // Costruisco il carrello associato all'utente
	        Carrello carrello = new Carrello(username);
	        
	        // Controllo che il prodotto avente il codice specificato si trovi nel carrello
	        int[] risultato = carrello.checkProdottoInCarrello(codice);
	        int trovato = risultato[0];

	        // Se il prodotto è presente
	        if (trovato == 1) {
	            int index = risultato[1];
	            
	            // Rimuovo il prodotto dal carrello
	            Inserimento inserimentoRimosso = carrello.getInserimenti().remove(index);
	            carrello.setDimensione(carrello.getDimensione() - 1);
	            inserimentoRimosso.eliminaInserimento();
	            
	            // Aggiorno la quantità disponibile del prodotto
	            Prodotto prodotto = new Prodotto(codice);
	            prodotto.setQuantita(prodotto.getQuantita() + inserimentoRimosso.getQuantitaInserita());
	            prodotto.aggiornaQuantita();

	            // Successo: Restituisco 200 OK con un messaggio JSON
	            response.put("message", "Prodotto rimosso con successo");
	            response.put("codice_prodotto", codice);
	            return ResponseEntity.ok(response);
	        } else {
	            // Errore Client: Il prodotto non c'era. Restituisco 404 Not Found
	            response.put("error", "Il prodotto specificato non è presente nel carrello");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	    } catch (DAOException | DBConnectionException e) {
	        // Errore Server: Restituisco 500 Internal Server Error
	        response.put("error", "Errore durante l'operazione: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	/* Il metodo acquista è invocato a seguito di una richiesta GET alla risorsa /acquista
	 * Renderizza il template acquisto.html */
	
	

	/* Il metodo acquista è invocato quando è richiesta la risorsa /acquista con il metodo POST
	 * Controllo la dimensione del carrello
	 * Controlla che il numero di carta di credito sia corretto
	 * Se è corretto, allora preleva i prodotti dal carrello 
	 * Crea un oggetto della classe Ordine
	 * Crea una lista di acquisti 
	 * Salvo i prodotti acquistati nella base di dati
	 * Salvo l'ordine nella base di dati
	 * Salvo gli acquisti nella base di dati
	 * Svuoto il carrello 
	 * */
	

	@PreAuthorize("hasAuthority('ROLE_client_cliente')")
	@PostMapping("/cliente/acquista")
	public ResponseEntity<?> acquista(JwtAuthenticationToken auth) {
	    String username = auth.getToken().getClaimAsString("preferred_username");
	    
	    try {
	        Carrello carrello = new Carrello(username);
	        // Leggo tutti i prodotti inseriti nel carrello
	        ArrayList<Prodotto> prodotti = carrello.leggiProdottiInseriti();
	        
	        if (prodotti.isEmpty()) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Il carrello è vuoto"));
	        }

	        // Aggiungo i prodotti acquistati nella tabella ProdottiAcquistati
	        for(int i=0; i<prodotti.size(); i++) {
	            // La lista contiene la quantità disponibile dei prodotti
	            // I prodotti acquistati devono avere come quantità quella inserita nel carrello
	            prodotti.get(i).setQuantita(carrello.getInserimenti().get(i).getQuantitaInserita());
	            
	            // Controllo se il prodotto già è stato acquistato o meno (statistiche/storico)
	            Prodotto prodottoAcquistato = prodotti.get(i).leggiProdottoAcquistato();
	            
	            if(prodotti.get(i).getCodice().equals(prodottoAcquistato.getCodice())) {
	                
	                prodottoAcquistato.setQuantita(prodottoAcquistato.getQuantita() + prodotti.get(i).getQuantita());
	                prodottoAcquistato.aggiornaQuantitaAcquistata();
	            }
	            else {
	                
	                prodotti.get(i).inserisciProdottoAcquistato();
	            }
	        }
	        

	        LocalDate data = LocalDate.now();

	        String codiceOrdine = Utility.creaCodiceOrdine(data, username);

	        double prezzoComplessivo = Utility.calcolaTotaleProdottiInseriti(prodotti);     
	        

	        Ordine ordine = new Ordine(codiceOrdine, data, prezzoComplessivo, username);
	        ordine.salvaOrdine();
	        

	        for(int i=0; i<prodotti.size(); i++) {
	            Acquisto acquisto = new Acquisto(codiceOrdine, prodotti.get(i).getCodice(), prodotti.get(i).getQuantita());
	            acquisto.salvaAcquisto();
	        }

	        carrello.svuotaCarrello();

	        return ResponseEntity.ok(Map.of(
	            "message", "Acquisto effettuato con successo",
	            "codiceOrdine", codiceOrdine,
	            "prezzoTotale", prezzoComplessivo
	        ));
	        
	    } 
	    catch (DAOException | DBConnectionException e) {
	        // Gestione errore server 500
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", e.getMessage()));
	    }

	}
	
	@PreAuthorize("hasAuthority('ROLE_client_gestore')")
	@PostMapping("gestore/aggiungi")
	public ResponseEntity<?> aggiungiProdotto(@RequestParam Map<String,String> allRequestParams) {
	    String nome = HtmlUtils.htmlEscape(allRequestParams.get("nome"));
	    String descrizione = HtmlUtils.htmlEscape(allRequestParams.get("descrizione"));
	    double prezzo = 0;
	    int quantita = 0;

	    // Blocco try-catch per il parsing dei numeri
	    try {
	        prezzo = Double.parseDouble(allRequestParams.get("prezzo"));
	        quantita = Integer.parseInt(allRequestParams.get("quantita"));
	    } catch (NumberFormatException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Prezzo o quantità non validi."));
	    }

	    // Validazione (spostata in alto per chiarezza)
	    if(nome == null || nome.trim().isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Il nome non può essere una stringa vuota"));
	    }
	    if(descrizione == null || descrizione.trim().isEmpty()) {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "La descrizione non può essere una stringa vuota"));
	    }
	    if(prezzo <= 0.0) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Il prezzo non può essere inferiore a 0"));
	    }
	    if(quantita <= 0) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "La quantità non può essere minore o uguale a 0"));
	    }
	    
	    try {
	        String codice = Utility.creaCodiceProdotto();
	        Prodotto prodotto = new Prodotto(codice, nome, descrizione, prezzo, quantita);
	        prodotto.salvaProdotto();
	        
	        // 201 Created: Restituisce l'oggetto creato
	        return ResponseEntity.status(HttpStatus.CREATED).body(prodotto);
	    }
	    catch (DAOException | DBConnectionException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
	    }
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_gestore')")
	@PostMapping("/gestore/rimuovi/{codice}")
	public ResponseEntity<?> rimuoviProdotto(@PathVariable(value = "codice") String codiceProdotto) {
	    Map<String, String> responseBody = new HashMap<>();

	    try {
	        Carrello carrelloGlobale = new Carrello();
	        
	        // Controllo se il prodotto è "bloccato" (es. nel carrello di qualcuno)
	        if(carrelloGlobale.checkProdotto(codiceProdotto)) {
	            responseBody.put("message", "Rimozione non possibile: il prodotto è attualmente in un carrello attivo.");
	            // 409 CONFLICT è lo status corretto quando lo stato attuale della risorsa impedisce l'azione
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
	        }
	        
	        // Procedo con l'eliminazione
	        Prodotto prodotto = new Prodotto(codiceProdotto);
	        prodotto.eliminaProdotto();
	        
	        responseBody.put("message", "Prodotto eliminato con successo.");
	        return ResponseEntity.ok(responseBody);

	    } catch (DAOException | DBConnectionException e) {
	        responseBody.put("error", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
	    }
	}
	
	@PreAuthorize("hasAuthority('ROLE_client_gestore')")
	@GetMapping("/gestore/prodotto/{codice}")
    public ResponseEntity<?> getProdotto(@PathVariable("codice") String codice) {
        try {
            Prodotto prodotto = ProdottoDAO.readProdottoByCodice(codice);

            return ResponseEntity.ok(prodotto);
        } catch (DAOException | DBConnectionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore del server: " + e.getMessage()));
        }
    }

	
	@PreAuthorize("hasAuthority('ROLE_client_gestore')")
	@PostMapping("/gestore/aggiorna/{codice}")
    public ResponseEntity<?> aggiornaProdotto(
            @PathVariable("codice") String codice, 
            @RequestBody Map<String, Object> payload
    ) {
        try {

            Prodotto prodottoEsistente = ProdottoDAO.readProdottoByCodice(codice);

            // 2. Estrazione dati dal payload
            String nome = (String) payload.get("nome");
            String descrizione = (String) payload.get("descrizione");
            
            // Gestione sicura dei numeri (JSON -> Number -> Double/Integer)
            Double prezzo = payload.get("prezzo") != null ? ((Number) payload.get("prezzo")).doubleValue() : null;
            Integer quantita = payload.get("quantita") != null ? ((Number) payload.get("quantita")).intValue() : null;

            // 3. Validazione
            if (quantita != null && quantita < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "La quantità non può essere negativa"));
            }
            if (prezzo != null && prezzo < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Il prezzo non può essere negativo"));
            }

            // 4. Aggiornamento dei campi (solo se presenti nel JSON)
            if (nome != null) prodottoEsistente.setNome(nome);
            if (descrizione != null) prodottoEsistente.setDescrizione(descrizione);
            if (prezzo != null) prodottoEsistente.setPrezzo(prezzo);
            if (quantita != null) prodottoEsistente.setQuantita(quantita);

            // 5. Salvataggio
            ProdottoDAO.updateProdotto(prodottoEsistente);

            return ResponseEntity.ok(Map.of(
                "message", "Prodotto aggiornato con successo", 
                "codice", codice,
                "prodotto", prodottoEsistente
            ));

        } catch (DAOException | DBConnectionException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (ClassCastException e) {
             return ResponseEntity.badRequest().body(Map.of("error", "Formato dati non valido"));
        }
    }	
	
}