/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.IOException;
import java.sql.SQLException;

import progettosii.Configurations;


/**
 *
 * @author Alex,Nicholas
 */
public class CommonCrawlClient {

	private ObjectConf oc;
	private TableManager tableManager;

	public CommonCrawlClient(String pathFileConfigurations) throws IOException{
		this.oc = new Configurations().getConf(pathFileConfigurations);
		this.tableManager = new TableManager(this.oc);
	}

	public void createWatIndex() throws SQLException, IOException{
	
		if(!this.tableManager.existsDatabase()){
	
			this.tableManager.createDatabase();
			
			ControllerIndex ci = new ControllerIndex(oc);
			ci.createIndex();
		}
		else{
			System.out.println("Database esistente,indice non creato. Usare deleteWatIndex per cancellarlo!");
		}

	}

	public void deleteWatIndex() throws SQLException{
		
		this.tableManager.dropDatabase();
	}

	public String getContentUrl(String urlRequest) throws SQLException{
		ControllerRequest cr = new ControllerRequest(oc);

		byte[] rawData = cr.getRequest(urlRequest);
		return new String(rawData);
	}

}
