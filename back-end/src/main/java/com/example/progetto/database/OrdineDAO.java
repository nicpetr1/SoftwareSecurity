package com.example.progetto.database;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.progetto.entity.Ordine;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

public class OrdineDAO {
	
	public static void createOrdine(Ordine ordine) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			String query = "INSERT INTO Ordini(codice, data, prezzoComplessivo, cliente) VALUES (?, ?, ?, ?)";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setBytes(1, Utility.encrypt(ordine.getCodice()));
				stmt.setBytes(2, Utility.encrypt(ordine.getData().toString()));
				stmt.setBytes(3, Utility.encrypt(String.valueOf(ordine.getPrezzoComplessivo())));
				stmt.setBytes(4, Utility.encrypt(ordine.getCliente()));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella creazione dell'ordine");
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
