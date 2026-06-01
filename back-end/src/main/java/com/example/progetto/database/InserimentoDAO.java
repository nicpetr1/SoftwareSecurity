package com.example.progetto.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.example.progetto.entity.Inserimento;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

public class InserimentoDAO {
	
	public static void createInserimento(Inserimento inserimento) throws DAOException, DBConnectionException{
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "INSERT INTO Inserimenti(cliente, prodotto, quantitaInserita) VALUES (?, ?, ?)";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(inserimento.getCliente()));
				stmt.setBytes(2, Utility.encrypt(inserimento.getCodiceProdotto()));
				stmt.setBytes(3, Utility.encrypt(String.valueOf(inserimento.getQuantitaInserita())));
								
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nell'inserimento del prodotto nel carrello");
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static ArrayList<Inserimento> readInserimentiByUsername(String username) throws DAOException, DBConnectionException{
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "SELECT prodotto, quantitaInserita FROM Inserimenti WHERE cliente = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(username));
				ResultSet result = stmt.executeQuery();
				ArrayList<Inserimento> inserimenti = new ArrayList<Inserimento>();
				while(result.next()) {
					Inserimento inserimento = new Inserimento(
							username, 
							Utility.decrypt(result.getBytes("prodotto")), 
							Integer.parseInt(Utility.decrypt(result.getBytes("quantitaInserita"))));
					inserimenti.add(inserimento);
				}
				return inserimenti;
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura degli inserimenti dell'utente");
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}

	public static ArrayList<Inserimento> readInserimenti() throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "SELECT * FROM Inserimenti";
				Statement stmt = conn.createStatement();
				ArrayList<Inserimento> inserimenti = new ArrayList<Inserimento>();
				ResultSet result = stmt.executeQuery(query);
				while(result.next()) {
					Inserimento inserimento = new Inserimento(
							Utility.decrypt(result.getBytes("cliente")),
							Utility.decrypt(result.getBytes("prodotto")),
							Integer.parseInt(Utility.decrypt(result.getBytes("quantitaInserita"))));
					inserimenti.add(inserimento);
				}
				return inserimenti;
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura degli inserimenti");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static void deleteInserimento(String cliente, String prodotto) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "DELETE FROM Inserimenti WHERE cliente = ? AND prodotto = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(cliente));
				stmt.setBytes(2, Utility.encrypt(prodotto));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura degli inserimenti");
			}
			finally {
				DBManager.closeConnection();
			} 
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}

	public static void updateQuantitaInserita(String cliente, String prodotto, int quantitaInserita) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			try {
				String query = "UPDATE Inserimenti SET quantitaInserita = ? WHERE cliente = ? AND prodotto = ?";
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.setBytes(1, Utility.encrypt(String.valueOf(quantitaInserita)));
				stmt.setBytes(2, Utility.encrypt(cliente));
				stmt.setBytes(3, Utility.encrypt(prodotto));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nell'aggiornamento della quantità inserita");
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
