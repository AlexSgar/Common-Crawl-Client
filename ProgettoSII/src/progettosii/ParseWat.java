/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ParseWat {

	public void parsingWat(String filePath, Connection connectionDB) throws FileNotFoundException, IOException, SQLException{
		ObjectURL ou = new ObjectURL();
		EstrattoreJson estrattoreJson=new EstrattoreJson();
		FileReader reader = new FileReader(filePath);
		BufferedReader in = new BufferedReader(reader);
		String line = "";
		System.out.println("Creazione dell'indice in corso...");
		for (int i = 0; i < 24; i++)
			line = in.readLine();
		int k=0;
		String JsonUrl = null;
		List<ObjectURL> objectUrls = new LinkedList<>();

		while((line = in.readLine()) != null){
			k++;
			if (k == 10){
				JsonUrl = line;
				if ((line = in.readLine()) != null){
					ou = estrattoreJson.CreaObjectURL(JsonUrl);
					objectUrls.add(ou);
				}
				k = 0;
				if(objectUrls.size() == 2000){
					batchInsertRecordsIntoTable(connectionDB, objectUrls);
					objectUrls = new LinkedList<>();
				}
			}
		}
		if(objectUrls.size() > 0)
			batchInsertRecordsIntoTable(connectionDB, objectUrls);

		reader.close();
		in.close();
		
		System.out.println("Creazione dell'indice COMPLETATA");
	}


	private void batchInsertRecordsIntoTable(Connection dbConnection, List<ObjectURL> objectUrls) throws SQLException {
		String stm = "INSERT INTO indexurl(url,segmentwarc,actualcontentlength,offsetwarc) VALUES(?,?,?,?)";
		PreparedStatement ps = dbConnection.prepareStatement(stm);
		dbConnection.setAutoCommit(false);

		for (ObjectURL ou : objectUrls)
			if (ou != null){
				ps.setString(1,ou.getURL() );
				ps.setString(2,ou.getSegmentWARC());
				ps.setInt(3,ou.getActualContentLength());
				ps.setInt(4,ou.getOffset());
				ps.addBatch();
			}
		try{
			ps.executeBatch();
			dbConnection.commit();
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			System.out.println(e.getMessage());
			dbConnection.rollback();
		}
		finally {
			if (ps != null)
				ps.close();
		}
	}
}
