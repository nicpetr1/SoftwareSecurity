package com.example.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.progetto.entity.Acquisto;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

public class AcquistoDAO {

	public static void createAcquisto(Acquisto acquisto) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "INSERT INTO Acquisti (ordine, prodotto, quantitaAcquistata) VALUES (?, ?, ?)";
				PreparedStatement stmt = conn.prepareStatement(query);
				
				stmt.setBytes(1, Utility.encrypt(acquisto.getOrdine()));
				stmt.setBytes(2, Utility.encrypt(acquisto.getProdotto()));
				stmt.setBytes(3, Utility.encrypt(String.valueOf(acquisto.getQuantitaAcquistata())));
				
				stmt.executeUpdate();
			}	
			catch (SQLException e) {
				throw new DAOException("Errore nella conferma dell'acquisto");
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

