package com.example.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.progetto.entity.Prodotto;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

public class ProdottoAcquistatoDAO {
	
	public static Prodotto readProdottoAcquistato(String codice) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "SELECT * FROM ProdottiAcquistati WHERE codice = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(codice));
				ResultSet result = stmt.executeQuery();
				Prodotto prodotto = new Prodotto();
				if(result.next()) {
					prodotto.setCodice(Utility.decrypt(result.getBytes("codice")));
					prodotto.setNome(Utility.decrypt(result.getBytes("nome")));
					prodotto.setDescrizione(Utility.decrypt(result.getBytes("descrizione")));
					prodotto.setPrezzo(Double.parseDouble(Utility.decrypt(result.getBytes("prezzo"))));
					prodotto.setQuantita(Integer.parseInt(Utility.decrypt(result.getBytes("quantita"))));
					return prodotto;
				}
				return prodotto;
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura del prodotto acquistato");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
		
	}
	
	public static void updateQuantitaAcquistata(String codice, int quantita) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "UPDATE ProdottiAcquistati SET quantita = ? WHERE codice = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(String.valueOf(quantita)));
				stmt.setBytes(2, Utility.encrypt(codice));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nell'aggiornamento della quantità del prodotto acquistato");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static void createProdottoAcquistato(Prodotto prodotto) throws DAOException, DBConnectionException{
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "INSERT INTO ProdottiAcquistati (codice, nome, descrizione, prezzo, quantita) "
						+ "VALUES (?, ?, ?, ?, ?)";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(prodotto.getCodice()));
				stmt.setBytes(2, Utility.encrypt(prodotto.getNome()));
				stmt.setBytes(3, Utility.encrypt(prodotto.getDescrizione()));
				stmt.setBytes(4, Utility.encrypt(String.valueOf(prodotto.getPrezzo())));
				stmt.setBytes(5, Utility.encrypt(String.valueOf(prodotto.getQuantita())));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella creazione del prodotto");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}	
	
	
}
