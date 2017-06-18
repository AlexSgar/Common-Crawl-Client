/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.progettosii;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerRequest{
	
	private DBRepository dbRepository;
	private ObjectURL objectURL;
	private GenerateObjectURL generateObjectURL;
	private String folderCache;
//	private int maxNumberWARCinCache;
//	private int maxSizeCache;
	private ObjectConf oc;

	public ControllerRequest(ObjectConf oc){
		this.oc=oc;
	}

	/**
	 * Method that take a page URL,make an HTTP request to download correlated WARC slice and return the page content
	 * If the request fails or correlated WARC doesn't exist in database index,the method return ""
	 * @param pageURL
	 * @return page content from URL
	 * @throws SQLException
	 */
	public byte[] getRequest(String pageURL) throws SQLException{
		
		folderCache = oc.getFolderCache();
//		maxNumberWARCinCache = oc.getMaxNumberWARCinCache();
//		maxSizeCache = oc.getMaxSizeCache();

		objectURL = new ObjectURL();
		generateObjectURL= new GenerateObjectURL();
		dbRepository= new DBRepository(oc);
		Connection connectionDB = dbRepository.getConnection();
		
		byte[] rawData = null;
		
		try {
			objectURL = generateObjectURL.getObjectURL(pageURL,connectionDB);
			String searchedUrl = pageURL;
			
			//check if pageURL is present in the WAT index of the database
			if(objectURL != null){
				
				Cache cache = new Cache(oc);
			
				/* Disable cache logic,download every time the warc slice containing the url htmlcontent
				
				if(!cache.isPresent(objectURL.getSegmentWARC(), connectionDB)){
					if (cache.getNumberSegmentWarc(connectionDB) <= maxNumberWARCinCache){
						//if (cache.getSizeCache(connectionDB)<=maxSizeCache){
						System.out.println("Download segmento warc fino a Offset richiesto in corso");
						cache.download(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
					}
					else{
						String OldWARC=cache.getOldWARC(connectionDB);
						File file = new File(folderCache+OldWARC);
						file.delete();
						cache.download(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
					}
				}
				else{
					System.out.println("Resume download segmento warc fino a Offset richiesto in corso");
					cache.resume(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
				}*/
				
				String fullWarcSegment = objectURL.getSegmentWARC();
				System.out.println("Segmento WARC selezionato: "+fullWarcSegment);
				System.out.println("Download del segmento WARC richiesto in corso: da offset a offset+1MB");
				try{

					cache.download(fullWarcSegment, objectURL.getOffset(), connectionDB);
					
					WarcReader warcReader = new WarcReader();
					rawData = warcReader.retriveContentURL(this.folderCache, fullWarcSegment,pageURL);
					
					//delete current warc slice after parsing due to cache disabled
					String warcSegmentName = fullWarcSegment.split("/warc/")[1];
					File warcSliceFile = new File(this.folderCache + warcSegmentName);
					
					if (warcSliceFile.exists()){
						if(warcSliceFile.delete())
							System.out.println("Il segmento WARC scaricato e' stato cancellato dopo il parsing!");
						else
							System.out.println("Operazione di delete file "+warcSegmentName +" fallita!");
					}
					
				}
				catch(IOException e ){
					//if an error occur with the connection or file downloaded
					
					System.out.println("Error: warc associato all'url non trovato!");
					System.out.println("Searched url: " +searchedUrl + " Searched Warc: " + fullWarcSegment);
					
					e.printStackTrace();
					
				}
			}
			else{
				System.out.println("Error: URL cercato non presente nell'indice WAT del database!");
				System.out.println("Searched URL: " +searchedUrl);
			}
			

		} catch (SQLException ex) {
			Logger.getLogger(ControllerRequest.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally{
			connectionDB.close();
			
		}
		
		return rawData;
	}
}
