
package progettosii;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class TableManager {
	private DBRepository dbRepository;

	public TableManager(ObjectConf oc){
		this.dbRepository = new DBRepository(oc);
	}

	public void createDatabase() throws SQLException{
		Connection connection = this.dbRepository.getConnection("jdbc:postgresql://localhost:5432/");

		try {
			if(!existsDatabase()){
				
				Statement statementDB = connection.createStatement();
				System.out.println("Creazione Database e tabelle in corso...");
				String query = "create database \"indexurl\"";
				statementDB.executeUpdate(query);
				connection =  this.dbRepository.getConnection("jdbc:postgresql://localhost:5432/indexurl");

				Statement statementTB = connection.createStatement();
				query = "create table indexurl (index BIGSERIAL,url TEXT,segmentwarc TEXT,actualcontentlength integer,offsetwarc integer,CONSTRAINT indexurl_pkey PRIMARY KEY (index))"; 
				statementTB.executeUpdate(query);
				query = "create table cache (segmentwarc TEXT NOT NULL,currentoffset integer,CONSTRAINT cache_pkey PRIMARY KEY (segmentwarc))"; 
				statementTB.executeUpdate(query);  

				System.out.println("Database creato correttamente!");
			}
			else{
				System.out.println("Database esistente,database non creato!");
			}
		} catch (SQLException e) {
			System.out.println("Creazione Database indexurl fallito!");
			e.printStackTrace();
		}
		finally{
			connection.close();
		}
	}

	public void dropDatabase() throws SQLException{
		Connection connection =  this.dbRepository.getConnection("jdbc:postgresql://localhost:5432/");

		try {
			if(existsDatabase()){
			
				Statement statementDB = connection.createStatement();
				String query = "drop database indexurl";
				statementDB.executeUpdate(query);
				System.out.println("Database cancellato correttamente!");
			}
			else
				System.out.println("Database non esistente,database non cancellato!");
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.out.println("Cancellazione Database fallita!");
		}
		finally {
			connection.close();
		}
	}

	public boolean existsDatabase() throws SQLException{
		
		boolean exists = false;
		Connection connection =  this.dbRepository.getConnection("jdbc:postgresql://localhost:5432/");

		String stm = "SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('indexurl')";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();

		if(rs.next() && rs.getString(1).equals("indexurl")){
			exists = true;
		}
		
		connection.close();

		return exists;
	}
}



