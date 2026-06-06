package com.example.progetto.database;

//import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.progetto.entity.Prodotto;
import com.example.progetto.entity.Utility;

import exception.DAOException;
import exception.DBConnectionException;

public class ProdottoDAO {
	
	/*Il metodo readProdotti legge dalla base di dati tutti i prodotti disponibili*/
	public static ArrayList<Prodotto> readProdotti() throws DAOException, DBConnectionException{
		try {
			Connection conn = DBManager.getConnection();
		
				String query = "SELECT * FROM Prodotti";
				try(PreparedStatement stmt = conn.prepareStatement(query)){
					try(ResultSet result = stmt.executeQuery()){
						ArrayList<Prodotto> prodotti = new ArrayList<Prodotto>();
						while(result.next()) {
							Prodotto prodotto = new Prodotto(Utility.decrypt(result.getBytes("codice")), Utility.decrypt(result.getBytes("nome")),
									Utility.decrypt(result.getBytes("descrizione")), Double.parseDouble(Utility.decrypt(result.getBytes("prezzo"))), Integer.parseInt(Utility.decrypt(result.getBytes("quantita"))));
							prodotti.add(prodotto);
						}
						return prodotti;
					}
					catch (SQLException e) {
						throw new DAOException("Errore nella lettura dei prodotti");
					}

					finally {
						DBManager.closeConnection();
					}
				}
				catch (SQLException e) {
					throw new DAOException("Errore nella lettura dei prodotti");
				}

				finally {
					DBManager.closeConnection();
				}		
			}

		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static Prodotto readProdottoByCodice(String codice) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			
				String query = "SELECT * FROM Prodotti WHERE codice = ?";
				try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setBytes(1,Utility.encrypt(codice));
				try(ResultSet result = stmt.executeQuery()){
				Prodotto prodotto = new Prodotto();
				
				if(result.next()) {
					prodotto.setCodice(codice);
					prodotto.setNome(Utility.decrypt(result.getBytes("nome")));
					prodotto.setDescrizione(Utility.decrypt(result.getBytes("descrizione")));
					prodotto.setPrezzo(Double.parseDouble(Utility.decrypt(result.getBytes("prezzo"))));
					prodotto.setQuantita(Integer.parseInt(Utility.decrypt(result.getBytes("quantita"))));
				}
				return prodotto;
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura del prodotto");
			}
			
			finally {
				DBManager.closeConnection();
			}
			}
			catch (SQLException e) {
				throw new DAOException("Errore nella lettura del prodotto");
			}
			
			finally {
				DBManager.closeConnection();
			}
			
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static void updateQuantita(String codice, int quantita) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
	
				String query = "UPDATE Prodotti SET quantita = ? WHERE codice = ?";
				try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setBytes(1,Utility.encrypt(String.valueOf(quantita)));
				stmt.setBytes(2,Utility.encrypt(codice));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nell'aggiornamento della quantità del prodotto");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
	
	public static void deleteProdottoByCodice(String codice) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			
				String query = "DELETE FROM Prodotti WHERE codice = ?";
				try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setBytes(1,Utility.encrypt(codice));
				stmt.executeUpdate();
			}
			catch (SQLException e) {
				throw new DAOException("Errore nell'eliminazione del prodotto");
			}
			finally {
				DBManager.closeConnection();
			}
		} 
		catch (SQLException e) {
			throw new DBConnectionException("Errore nella connessione con la base di dati");
		}
	}
		
	public static void createProdotto(Prodotto prodotto) throws DAOException, DBConnectionException {
		try {
			Connection conn = DBManager.getConnection();
			
				String query = "INSERT INTO Prodotti (codice, nome, descrizione, prezzo, quantita) "
						+ "VALUES (?, ?, ?, ?, ?)";
				try(PreparedStatement stmt = conn.prepareStatement(query)){
					stmt.setBytes(1,Utility.encrypt(prodotto.getCodice()));
					stmt.setBytes(2,Utility.encrypt(prodotto.getNome()));
					stmt.setBytes(3,Utility.encrypt(prodotto.getDescrizione()));
					stmt.setBytes(4,Utility.encrypt(String.valueOf(prodotto.getPrezzo())));
					stmt.setBytes(5,Utility.encrypt(String.valueOf(prodotto.getQuantita())));
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
	
    public static void updateProdotto(Prodotto prodotto) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            
                // Query che aggiorna tutti i campi basandosi sulla chiave primaria (codice)
                String query = "UPDATE prodotti SET nome = ?, descrizione = ?, prezzo = ?, quantita = ? WHERE codice = ?";
                
                try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setBytes(1,Utility.encrypt(prodotto.getNome()));
				stmt.setBytes(2,Utility.encrypt(prodotto.getDescrizione()));
				stmt.setBytes(3,Utility.encrypt(String.valueOf(prodotto.getPrezzo())));
				stmt.setBytes(4,Utility.encrypt(String.valueOf(prodotto.getQuantita())));
				stmt.setBytes(5,Utility.encrypt(prodotto.getCodice()));
                
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DAOException("Errore nell'aggiornamento del prodotto: " + e.getMessage());
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DBConnectionException("Errore nella connessione con la base di dati");
        }
    }
	
}
