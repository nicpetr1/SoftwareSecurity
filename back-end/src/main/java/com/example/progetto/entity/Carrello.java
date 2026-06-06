package com.example.progetto.entity;

import java.util.ArrayList;

import com.example.progetto.database.InserimentoDAO;

import exception.DAOException;
import exception.DBConnectionException;

/*
 * La classe Carrello è un contenitore di inserimenti di un cliente:
 * 		- non ha senso rendere persistente un carrello
 * 		- quando creo un ordine, leggo dalla base di dati tutti gli inserimenti fatti
 * 		- passo direttamente per la classe Carrello e non più inserimento
 *  
 * */

public class Carrello {
	
	private String cliente;
	private ArrayList<Inserimento> inserimenti;
	private int dimensione;
		
	public Carrello(){
		this.cliente = "";
		this.inserimenti = new ArrayList<Inserimento>();
		this.dimensione = 0;
	}

	public Carrello(String username){
		this.cliente = username;
		this.inserimenti = new ArrayList<Inserimento>();
		this.dimensione = 0;
	}
	
	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public int getDimensione() {
		return dimensione;
	}

	public void setDimensione(int dimensione) {
		this.dimensione = dimensione;
	}

	public ArrayList<Inserimento> getInserimenti() {
		return new ArrayList<>(this.inserimenti);
	}

	public void setInserimenti(ArrayList<Inserimento> inserimenti) {
		this.inserimenti = new ArrayList<>(inserimenti);
		setDimensione(this.inserimenti.size());
	}
	
	public ArrayList<Inserimento> getInserimentiConProdotto(){
		for(int i=0; i<this.inserimenti.size(); i++) {
			try {
				this.inserimenti.get(i).caricaProdotto();
			} 
			catch (DAOException | DBConnectionException e) {
				return new ArrayList<>(this.inserimenti);
			}
		}
		return new ArrayList<>(this.inserimenti);
	}
	
	public int[] checkProdottoInCarrello(String codice) {
		return Utility.checkProdottoInCarrello(this.inserimenti, codice);
	}
	
	public boolean checkProdotto(String codice) {
		return Utility.checkProdotto(this.inserimenti, codice);
	}

	public void inserisciProdotto(Inserimento inserimento) throws DAOException, DBConnectionException {
		this.inserimenti.add(inserimento);
		this.dimensione = this.inserimenti.size();
		inserimento.salvaInserimento();
	}
	
	public ArrayList<Prodotto> leggiProdottiInseriti() throws DAOException, DBConnectionException{
	    ArrayList<Prodotto> prodotti = new ArrayList<Prodotto>();
	    
	    if (this.inserimenti == null) {
	        return prodotti;
	    }

	    for(int i=0; i<this.inserimenti.size(); i++) {
	        
	        String codiceDelProdotto = this.inserimenti.get(i).getCodiceProdotto();
	       
	        Prodotto prodotto = new Prodotto(codiceDelProdotto);
			prodotto.leggiProdottoByCodice(); 

	        
	        prodotti.add(prodotto);
	    }
	    return prodotti;
	}
	
	public void svuotaCarrello() throws DAOException, DBConnectionException {
		for(int i=0; i<this.inserimenti.size(); i++) {
			this.inserimenti.get(i).cancellaInserimento();
		}
		this.dimensione = 0;
	}

	public void leggiInserimenti() throws DAOException, DBConnectionException{
		ArrayList<Inserimento> inserimenti = InserimentoDAO.readInserimenti();
		this.inserimenti = inserimenti;
		this.dimensione = inserimenti.size();
	}

	public void leggiInserimentiByUsername() throws DAOException, DBConnectionException{
		ArrayList<Inserimento> inserimenti = InserimentoDAO.readInserimentiByUsername(this.cliente);
		this.inserimenti = inserimenti;
		this.dimensione = inserimenti.size();
	}

	
}
