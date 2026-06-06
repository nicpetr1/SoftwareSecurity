package com.example.progetto.entity;

import java.util.ArrayList;

import com.example.progetto.database.ProdottoDAO;

import exception.DAOException;
import exception.DBConnectionException;

public class Catalogo {
	
	private ArrayList<Prodotto> prodotti;
	
	public Catalogo() {
		this.prodotti = new ArrayList<Prodotto>();
	}
	
	public ArrayList<Prodotto> leggiProdotti() throws DAOException, DBConnectionException{
		this.prodotti = ProdottoDAO.readProdotti();
		return new ArrayList<>(this.prodotti);
	}
	
}
