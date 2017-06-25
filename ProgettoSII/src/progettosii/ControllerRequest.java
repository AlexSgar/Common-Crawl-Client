/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;


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
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public byte[] getRequest(String pageURL) throws SQLException, JSONException, IOException{
		
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
			Cache cache = new Cache(oc);
			String fullWarcSegment = null;
			int warcOffset = 0;
			
			//check if pageURL is present in the WAT index of the database
			if(objectURL != null){
			
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
				
				fullWarcSegment = objectURL.getSegmentWARC();
				warcOffset = objectURL.getOffset();
				System.out.println("Segmento WARC selezionato: "+fullWarcSegment);
				System.out.println("Download del segmento WARC richiesto in corso: da offset a offset+1MB");
				
			}
			else{
				
				System.out.println("Error: URL cercato non presente nell'indice WAT del database!");
				System.out.println("Searched URL: " +searchedUrl);
				System.out.println("Cerco URL richiesto nelle URL Index API di CommonCrawl e scarico il segmento WARC associato");
				
				BufferedReader br = new BufferedReader(new FileReader(this.oc.getFolderwatpath() + "wat.path"));
				String crawlArchive = br.readLine().split("/")[1]; //CC-MAIN-2017-04
				br.close();
				
				String warcInfo = CommonCrawlUrlSearch.getWarcInfoFromCommonCrawlURLIndex(pageURL,crawlArchive);
				String[] warcInfoSplitted = warcInfo.split(",");
				
				fullWarcSegment = warcInfoSplitted[0];
				warcOffset = Integer.parseInt(warcInfoSplitted[1]);
			}		
			
			try{
				cache.download(fullWarcSegment, warcOffset, connectionDB);
				
				WarcReader warcReader = new WarcReader();
				rawData = warcReader.retriveContentURL(this.folderCache, fullWarcSegment,pageURL);
				
				//delete current warc slice after parsing due to cache disabled
				String warcSegmentName = fullWarcSegment.split("/warc/")[1];
				File warcSliceFile = new File(this.folderCache + warcSegmentName);
				
				if (warcSliceFile.exists()){
					if(warcSliceFile.delete())
						System.out.println("Il segmento WARC scaricato e' stato cancellato dopo il parsing!\n");
					else
						System.out.println("Operazione di delete file "+warcSegmentName +" fallita!");
				}
			}
			catch(IOException e ){
				//if an error occur with the connection or file downloaded
				
				System.out.println("HTTP Error or with cache folder: warc associato all'url saltato!");
				System.out.println("Searched url: " +searchedUrl + " Searched Warc: " + fullWarcSegment);
				
				e.printStackTrace();
				
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
