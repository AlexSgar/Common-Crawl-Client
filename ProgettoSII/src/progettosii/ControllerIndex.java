/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;

public class ControllerIndex {

	private DBRepository dbRepository;
	private String folderWat;
	private String folderWatPath;
	private ObjectConf oc;

	public ControllerIndex(ObjectConf oc){
		this.oc = oc;
		this.folderWat = this.oc.getFolderWat();
		this.folderWatPath = this.oc.getFolderwatpath();
	}

	public void createIndex() throws FileNotFoundException, IOException, SQLException{

		BufferedReader in = null;
		String fullWatSegmentName;
		Connection connectionDB = null;

		try{
			
			in = new BufferedReader(new FileReader(this.folderWatPath + "wat.path"));
			dbRepository = new DBRepository(oc);
			connectionDB = dbRepository.getConnection();

			while ((fullWatSegmentName = in.readLine()) != null){

				String commonCrawlWatUrl = "https://commoncrawl.s3.amazonaws.com/" + fullWatSegmentName;
				URL url = new URL(commonCrawlWatUrl);
				//				String fileName = url.getFile();

				System.out.println("fullWatSegmentName: "+fullWatSegmentName);

				int size = -1;
				// Open connection to URL.
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				// Connect to server.
				connection.connect();

				// Make sure response code is in the 200 range.
				if (connection.getResponseCode() / 100 != 2)
					System.out.println("Error: repsonse code !=200");

				// Check for valid content length.
				int contentLength = connection.getContentLength();
				if (contentLength < 1)
					System.out.println("Error: contentLength < 1");

				/* Set the size for this download if it
	            hasn't been already set. */
				if (size == -1) {
					size = contentLength;   
					System.out.println("Grandezza file: " + contentLength + " byte");
				}

				String watSegmentName = fullWatSegmentName.split("/wat/")[1];
				File watSegmentZippedFile = new File(this.folderWat + watSegmentName);

				BufferedInputStream inD = new BufferedInputStream(connection.getInputStream());
				BufferedOutputStream outD = new BufferedOutputStream(new FileOutputStream(watSegmentZippedFile));

				int n;
				System.out.println("Download in corso..: " + watSegmentName);
				int count = 0;
				int j = 0;
				byte[] buffer = new byte[4096];

				while ((n = inD.read(buffer)) > 0) {
					if (count>j*(contentLength/10)){
						j++;
						System.out.print("=");
					}
					outD.write(buffer,0,n);
					outD.flush();
					count+=4096;
				}
				outD.close();
				inD.close();
				System.out.println("");
				System.out.println("Download completato!");  
				System.out.print("Decompressione in corso..."); 

				String watSegmentFilePath = this.folderWat + (watSegmentName.split(".gz")[0]);
				File watSegmentFile = new File(watSegmentFilePath);

				GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(watSegmentZippedFile));;
				FileOutputStream outzip = new FileOutputStream(watSegmentFile);;
				byte[] bufferzip = new byte[4096];
				int len;

				while ((len = gzis.read(bufferzip)) > 0) {
					outzip.write(bufferzip, 0, len);
					outzip.flush();
				}
				outzip.flush();
				outzip.close();
				gzis.close();

				System.out.println("Decompressione completata!");

				ParseWat parseWat = new ParseWat();
				String fullWarcSegmentName = fullWatSegmentName.replaceAll("/wat/", "/warc/").replaceAll("wat.", "");
				parseWat.parsingWat(watSegmentFilePath, fullWarcSegmentName, connectionDB);

				System.out.println("Cancello i files associati al WAT processato");

				if (watSegmentZippedFile.exists()){
					if(watSegmentZippedFile.delete())
						System.out.print("Primo file cancellato, ");
					else{
						System.out.println("");
						System.out.println("Operazione di delete file "+watSegmentZippedFile.getName() +" fallita!");
					}
				}

				if (watSegmentFile.exists()){
					if(watSegmentFile.delete()){
						System.out.println("secondo file cancellato!");
					}else{
						System.out.println("");
						System.out.println("Operazione di delete file "+watSegmentZippedFile.getName() +" fallita!");
					}
				}
			}
		}
		finally{
			in.close();
			connectionDB.close();
		}
	}
}

