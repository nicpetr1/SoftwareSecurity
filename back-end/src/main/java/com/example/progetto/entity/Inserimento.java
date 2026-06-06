package com.example.progetto.entity;

import com.example.progetto.database.InserimentoDAO;
import com.example.progetto.database.ProdottoDAO;
import exception.DAOException;
import exception.DBConnectionException;

// Importa questa per rinominare il campo nel JSON
import com.fasterxml.jackson.annotation.JsonProperty;

public class Inserimento {
	
	private String cliente;
	
	// 1. RINOMINATO: da "prodotto" (String) a "codiceProdotto"
	private String codiceProdotto; 
	
	private int quantitaInserita;
	
	// 2. RINOMINATO: da "prod" (Prodotto) a "prodotto"
	private Prodotto prodotto;
	
	// 3. COSTRUTTORE AGGIORNATO
	public Inserimento(String cliente, String codiceProdotto, int quantitaInserita) {
		this.cliente = cliente;
		this.codiceProdotto = codiceProdotto; // Aggiornato
		this.quantitaInserita = quantitaInserita;
		this.prodotto = null; // Aggiornato
	}

	// 4. METODO CARICA PRODOTTO AGGIORNATO
	public void caricaProdotto() throws DAOException, DBConnectionException {
		// Ora chiama il setter corretto: setProdotto(Prodotto p)
		this.setProdotto(ProdottoDAO.readProdottoByCodice(this.codiceProdotto));
	}

	// --- GETTER E SETTER AGGIORNATI ---

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	// Getter/Setter per il NUOVO 'codiceProdotto' (String)
	public String getCodiceProdotto() {
		return codiceProdotto;
	}

	public void setCodiceProdotto(String codiceProdotto) {
		this.codiceProdotto = codiceProdotto;
	}

	public int getQuantitaInserita() {
		return quantitaInserita;
	}

	public void setQuantitaInserita(int quantitaInserita) {
		this.quantitaInserita = quantitaInserita;
	}
	
	// Getter/Setter per il NUOVO 'prodotto' (Prodotto)
	// Questo è il metodo che RISOLVE il tuo errore
	@JsonProperty("prodotto") 
	public Prodotto getProdotto() {
		return new Prodotto(prodotto);
	}

	@JsonProperty("prodotto")
	public void setProdotto(Prodotto prodotto) {
		Prodotto prod = new Prodotto(prodotto);
		this.prodotto = prod;
	}
	
	// --- Metodi DAO (aggiornati per usare 'codiceProdotto') ---
	
	public void salvaInserimento() throws DAOException, DBConnectionException {
		InserimentoDAO.createInserimento(this);
	}
	
	public void aggiornaQuantitaInserita() throws DAOException, DBConnectionException {
		InserimentoDAO.updateQuantitaInserita(this.cliente, this.codiceProdotto, this.quantitaInserita);
	}
	
	public void eliminaInserimento() throws DAOException, DBConnectionException {
		InserimentoDAO.deleteInserimento(this.cliente, this.codiceProdotto);
	}
	
	public void cancellaInserimento() throws DAOException, DBConnectionException {
		InserimentoDAO.deleteInserimento(this.cliente, this.codiceProdotto);
	}
}