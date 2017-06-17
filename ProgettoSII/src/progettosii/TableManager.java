/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
			String stm = "SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('indexurl')";
			PreparedStatement ps = connection.prepareStatement(stm);
			ResultSet rs = ps.executeQuery();
			if(rs.next() && rs.getString(1).equals("indexurl"))
				System.out.println("Already Existing DataBase");
			else {
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
			}
		} catch (SQLException e) {
			System.out.println("Creazione tabella indexurl fallita");
			e.printStackTrace();
		}
		finally{
			connection.close();
		}
	}

	public void dropTable() throws ClassNotFoundException, SQLException{
		Connection connection =  this.dataSource.getConnection("jdbc:postgresql://localhost:5432/");

		try {
			String stm = "SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('indexurl')";
			PreparedStatement ps = connection.prepareStatement(stm);
			ResultSet rs = ps.executeQuery();
			if(rs.next() && rs.getString(1).equals("indexurl")){
				Statement statementDB = connection.createStatement();
				String query = "drop database indexurl";
				statementDB.executeUpdate(query);
			}
			else
				System.out.println("Not Existing Database");
			} catch (SQLException e1) {
				System.out.println("Cancellazione Database fallita");
			}
			finally {
				connection.close();
			}
		}
	}

