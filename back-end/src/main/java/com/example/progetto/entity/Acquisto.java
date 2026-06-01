package com.example.progetto.entity;

import com.example.progetto.database.AcquistoDAO;

import exception.DAOException;
import exception.DBConnectionException;

public class Acquisto {
	private String ordine;
	private String prodotto;
	private int quantitaAcquistata;
	
	public Acquisto(String ordine, String prodotto, int quantitaAcquistata) {
		this.ordine = ordine;
		this.prodotto = prodotto;
		this.quantitaAcquistata = quantitaAcquistata;
	}

	public String getOrdine() {
		return ordine;
	}

	public void setOrdine(String ordine) {
		this.ordine = ordine;
	}

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public int getQuantitaAcquistata() {
		return quantitaAcquistata;
	}

	public void setQuantitaAcquistata(int quantitaAcquistata) {
		this.quantitaAcquistata = quantitaAcquistata;
	}
	
	public void salvaAcquisto() throws DAOException, DBConnectionException {
		AcquistoDAO.createAcquisto(this);
	}
	
}
