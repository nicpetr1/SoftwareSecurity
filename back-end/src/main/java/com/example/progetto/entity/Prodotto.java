package com.example.progetto.entity;

import com.example.progetto.database.ProdottoAcquistatoDAO;
import com.example.progetto.database.ProdottoDAO;

import exception.DAOException;
import exception.DBConnectionException;

public class Prodotto {
	
	private String codice;
	private String nome;
	private String descrizione;
	private double prezzo;
	private int quantita;
	
	public Prodotto(String codice, String nome, String descrizione, double prezzo, int quantita) {
		this.codice = codice;
		this.nome = nome;
		this.descrizione = descrizione;
		this.prezzo = prezzo;
		this.quantita = quantita;
	}
	
	public Prodotto(String codice) throws DAOException, DBConnectionException {
		Prodotto prodotto = ProdottoDAO.readProdottoByCodice(codice);
		this.codice = prodotto.codice;
		this.nome = prodotto.nome;
		this.descrizione = prodotto.descrizione;
		this.prezzo = prodotto.prezzo;
		this.quantita = prodotto.quantita;
	}
	
	public Prodotto() {
		this.codice = "";
		this.nome = "";
		this.descrizione = "";
		this.prezzo = 0.0;
		this.quantita = 0;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}

	public int getQuantita() {
		return quantita;
	}

	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}
	
	public void aggiornaQuantita() throws DAOException, DBConnectionException {
		ProdottoDAO.updateQuantita(this.codice, this.quantita);
	}
	
	public void salvaProdotto() throws DAOException, DBConnectionException {
		ProdottoDAO.createProdotto(this);
	}
	
	public Prodotto leggiProdottoAcquistato() throws DAOException, DBConnectionException {
		return ProdottoAcquistatoDAO.readProdottoAcquistato(this.codice);
	}
	
	public void aggiornaQuantitaAcquistata() throws DAOException, DBConnectionException {
		ProdottoAcquistatoDAO.updateQuantitaAcquistata(this.codice, this.quantita);
	}
	
	public void inserisciProdottoAcquistato() throws DAOException, DBConnectionException {
		ProdottoAcquistatoDAO.createProdottoAcquistato(this);
	}
	
	public void eliminaProdotto() throws DAOException, DBConnectionException {
		ProdottoDAO.deleteProdottoByCodice(this.codice);
	}
	
	public void aggiorna() throws DAOException, DBConnectionException {
	    ProdottoDAO.updateProdotto(this);
	}
	
}
