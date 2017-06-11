/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableManager {
	private ObjectConf oc;
	public TableManager(ObjectConf oc){
		this.oc=oc;
	}
	public void createTable() throws ClassNotFoundException, SQLException{
		Connection connection =  new DataSource(oc).getConnection("jdbc:postgresql://localhost:5432/");
		try {
			Statement statementDB = connection.createStatement();
			System.out.println("Creazione Database e tabelle in corso...");
			String query = "create database \"indexurl\"";
			statementDB.executeUpdate(query);
			connection =  new DataSource(oc).getConnection("jdbc:postgresql://localhost:5432/indexurl");
			Statement statementTB = connection.createStatement();
			query = "create table indexurl (index integer NOT NULL,url TEXT,segmentwarc TEXT,actualcontentlength integer,offsetwarc integer,CONSTRAINT indexurl_pkey PRIMARY KEY (index))"; 
			statementTB.executeUpdate(query);
			query = "create table cache (segmentwarc TEXT NOT NULL,filesize integer,CONSTRAINT cache_pkey PRIMARY KEY (segmentwarc))"; 
			statementTB.executeUpdate(query);  
		} catch (SQLException e1) {
		}
	}
	public void dropTable() throws ClassNotFoundException, SQLException{
		Connection connection =  new DataSource(oc).getConnection("jdbc:postgresql://localhost:5432/");
		try {
			Statement statementDB = connection.createStatement();

			String query = "drop database indexurl";
			statementDB.executeUpdate(query);
			/*	  Statement statementTB = connection.createStatement();

		  query = "drop table interestData"; 
				statementTB.executeUpdate(query);*/
		} catch (SQLException e1) {
		}
		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

