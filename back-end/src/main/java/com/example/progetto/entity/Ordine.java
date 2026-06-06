package com.example.progetto.entity;

import java.time.LocalDate;
import java.util.ArrayList;

import com.example.progetto.database.OrdineDAO;

import exception.DAOException;
import exception.DBConnectionException;

public class Ordine {
	
	private String codice;
	private LocalDate data;
	private double prezzoComplessivo;
	private String cliente;
	private ArrayList<Acquisto> acquisti;
	
	public Ordine(String codice, LocalDate data, double prezzoComplessivo, String cliente) {
		this.codice = codice;
		this.data = data;
		this.prezzoComplessivo = prezzoComplessivo;
		this.cliente = cliente;
		this.acquisti = new ArrayList<Acquisto>();
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public double getPrezzoComplessivo() {
		return prezzoComplessivo;
	}

	public void setPrezzoComplessivo(double prezzoComplessivo) {
		this.prezzoComplessivo = prezzoComplessivo;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public ArrayList<Acquisto> getAcquisti() {
		return new ArrayList<>(this.acquisti);
	}

	public void setAcquisti(ArrayList<Acquisto> acquisti) {
		this.acquisti = acquisti;
	}
	
	public void salvaOrdine() throws DAOException, DBConnectionException {
		OrdineDAO.createOrdine(this);
	}

}
