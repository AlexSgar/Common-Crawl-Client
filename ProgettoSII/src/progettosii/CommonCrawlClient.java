/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;


/**
 *
 * @author Alex,Nicholas
 */
public class CommonCrawlClient {

	String basePathNicholas = "/home/nicholas/Documenti";
	String basePathAlex = "/Users/alex/Documents/IdeaProjects";//"/Users/alex/Documents/workspace/sii";
	String configurationPath = basePathAlex + "/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/file_di_configurazione.txt";

	private ObjectConf oc;
	private TableManager tableManager;

	public CommonCrawlClient() throws IOException{
		this.oc = new Configurations().getConf(configurationPath);
		this.tableManager = new TableManager(this.oc);
	}

	public void createWatIndex() throws SQLException, IOException{
	
		if(!this.tableManager.existsDatabase()){
	
			this.tableManager.createDatabase();
			
			ControllerIndex ci = new ControllerIndex(oc);
			ci.createIndex();
		}
		else{
			System.out.println("Database esistente,indice non creato. Usare deleteWatIndex per cancellarlo!\n");
		}

	}

	public void deleteWatIndex() throws SQLException{
		
		this.tableManager.dropDatabase();
	}

	public String getContentUrl(String urlRequest) throws SQLException, JSONException, IOException{
		ControllerRequest cr = new ControllerRequest(oc);

		byte[] rawData = cr.getRequest(urlRequest);
		return (rawData!=null) ? new String(rawData,"UTF-8") : null;
	}

}
