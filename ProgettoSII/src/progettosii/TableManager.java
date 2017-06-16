/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.util.PSQLException;

public class TableManager {
	private ObjectConf oc;
	private DataSource dataSource;
	
	public TableManager(ObjectConf oc){
		this.oc=oc;
		this.dataSource = new DataSource(oc);
	}
	
	public void createTable() throws ClassNotFoundException, SQLException{
		Connection connection = this.dataSource.getConnection("jdbc:postgresql://localhost:5432/");
		
		try {
			Statement statementDB = connection.createStatement();
			System.out.println("Creazione Database e tabelle in corso...");
			String query = "create database \"indexurl\"";
			statementDB.executeUpdate(query);
			connection =  this.dataSource.getConnection("jdbc:postgresql://localhost:5432/indexurl");
			
			Statement statementTB = connection.createStatement();
			query = "create table indexurl (index BIGSERIAL,url TEXT,segmentwarc TEXT,actualcontentlength integer,offsetwarc integer,CONSTRAINT indexurl_pkey PRIMARY KEY (index))"; 
			statementTB.executeUpdate(query);
			query = "create table cache (segmentwarc TEXT NOT NULL,currentoffset integer,CONSTRAINT cache_pkey PRIMARY KEY (segmentwarc))"; 
			statementTB.executeUpdate(query);  
		
		} catch (SQLException e) {
			System.out.println("Creazione tabella indexurl fallita");
			e.printStackTrace();
		}
		finally{
			connection.close();
		}
	}
	
	public boolean existsIndexTable(){
		boolean result = false;
		Connection connection = null;
		try {
			
			connection = this.dataSource.getConnection("jdbc:postgresql://localhost:5432/indexurl");
			result = true;
		} catch (Exception e) {
			result = false;
		}
		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}
	}
	
	public void dropTable() throws ClassNotFoundException, SQLException{
		Connection connection =  this.dataSource.getConnection("jdbc:postgresql://localhost:5432/");
		
		try {
			Statement statementDB = connection.createStatement();

			String query = "drop database indexurl";
			statementDB.executeUpdate(query);
			/*	  Statement statementTB = connection.createStatement();

		  query = "drop table interestData"; 
				statementTB.executeUpdate(query);*/
		} catch (SQLException e1) {
			System.out.println("Cancellazione Database fallita");
		}
		finally {
			
			connection.close();
		}
	}
}

