/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rob
 */
public class ControllerRequest implements Request{
	private ConnectorDB connector;
	private ObjectURL objectURL;
	private GenerateObjectURL generateObjectURL;
	private String folderCache;
//	private int maxNumberWARCinCache;
//	private int maxSizeCache;
	private ObjectConf oc;

	public ControllerRequest(ObjectConf oc){
		this.oc=oc;
	}

	public byte[] getRequest(String URL){
		
		folderCache = oc.getFolderCache();
//		maxNumberWARCinCache = oc.getMaxNumberWARCinCache();
//		maxSizeCache = oc.getMaxSizeCache();

		objectURL = new ObjectURL();
		generateObjectURL= new GenerateObjectURL();
		connector= new ConnectorDB(oc);
		Connection connectionDB = connector.getConnection();
		
		byte[] rawData = null;
		try {
			objectURL = generateObjectURL.getObjectURL(URL,connectionDB);
			Cache cache = new Cache(oc);
			
			//Disable cache logic,download every time the warc slice containing the url htmlcontent
			
//			if(!cache.isPresent(objectURL.getSegmentWARC(), connectionDB)){
//				if (cache.getNumberSegmentWarc(connectionDB) <= maxNumberWARCinCache){
//					//if (cache.getSizeCache(connectionDB)<=maxSizeCache){
//					System.out.println("Download segmento warc fino a Offset richiesto in corso");
//					cache.download(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
//				}
//				else{
//					String OldWARC=cache.getOldWARC(connectionDB);
//					File file = new File(folderCache+OldWARC);
//					file.delete();
//					cache.download(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
//				}
//			}
//			else{
//				System.out.println("Resume download segmento warc fino a Offset richiesto in corso");
//				cache.resume(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);
//			}
			System.out.println("Download segmento warc richiesto in corso: da offset a offset+1MB");
			cache.download(objectURL.getSegmentWARC(), objectURL.getOffset(), connectionDB);

			WarcReader warcReader = new WarcReader();
			String segmentWarc=objectURL.getSegmentWARC().split(" ")[0];
			rawData = warcReader.retriveContentURL(folderCache, segmentWarc,URL);
			
			//delete current warc slice due to cache disabled
			File f = new File(this.folderCache + segmentWarc);
			
			if (f.exists()){
				if(f.delete()){
					System.out.println("Cancello segmento WARC processato "+f.getName());
				}else{
					System.out.println("Delete operation is failed.");
				}
			}
			
			connectionDB.close();


		} catch (SQLException ex) {
			Logger.getLogger(ControllerRequest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(ControllerRequest.class.getName()).log(Level.SEVERE, null, ex);
		}
		//definisco il foldere della cache
		

		return rawData;
	}
}
