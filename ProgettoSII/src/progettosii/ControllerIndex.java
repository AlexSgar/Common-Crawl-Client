/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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

/**
 *
 * @author Rob
 */
public class ControllerIndex {
	private ConnectorDB connector;
	private GenerateObjectURL generateObjectURL;
	//private String folderWat="C:\\Users\\Rob\\Documents\\NetBeansProjects\\ProgettoSII\\src\\file wat\\";
	private String folderWat;
	private static ObjectConf oc;
	
	public ControllerIndex(ObjectConf oc){
		this.oc = oc;
	}
	
	public void createIndex(String fileWatPath) throws FileNotFoundException, IOException, SQLException{
		folderWat=oc.getFolderWat();
		FileReader fr= new FileReader(fileWatPath+"wat.path");
		BufferedReader in = new BufferedReader(fr);
		String riga;
		String urlWet=null;
		String stm =null;

		//int i=0;at sun.reflect.NativeCon
		while ((riga=in.readLine())!=null){
			//while(i<=0){
			//riga=in.readLine();
//			String stringaurl = "https://aws-publicdatasets.s3.amazonaws.com/"+riga;
			String stringaurl = "https://commoncrawl.s3.amazonaws.com/"+riga;
			URL url = new URL(stringaurl);
			String fileName = url.getFile();


			int downloaded=0;
			int size=-1;
			// Open connection to URL.
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Specify what portion of file to download.
			connection.setRequestProperty("Range","bytes=" + downloaded + "-");

			// Connect to server.
			connection.connect();

			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2) {
				System.out.println("error");
			}

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				System.out.println("error");
			}

			/* Set the size for this download if it
            hasn't been already set. */
			if (size == -1) {
				size = contentLength;   
				System.out.println("grandezza file: "+contentLength+" byte");
			}
			// Open file and seek to the end of it.

			File f=new File(folderWat+fileName);
			//System.out.println("path: "+folderWat+f.getName());

			BufferedInputStream inD = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream outD = new BufferedOutputStream(new FileOutputStream(folderWat+f.getName()));
			String INPUT_GZIP_FILE = folderWat + f.getName();
			//System.out.println("inputzip:"+INPUT_GZIP_FILE);
			String OUTPUT_FILE = INPUT_GZIP_FILE.split(".gz")[0];
			//System.out.println("outputunzip:"+OUTPUT_FILE);
			FileInputStream is = null;

			int n;
			System.out.println("sto scaricando: " + f.getName());
			int count=0;
			int j=0;
			GZIPInputStream gzis = null;
			FileOutputStream outzip =null;
			DataInputStream inStream = null;
			byte[] bufferzip = new byte[4096];
			byte[] buffer = new byte[4096];
			String entry="false";
			int len;
			while ((n = inD.read(buffer)) > 0) {
				if (count>j*(contentLength/10)){
					j++;
					System.out.print("=");
				}
				outD.write(buffer,0,n);
				outD.flush();
				count+=4096; 
				if (entry=="false"){
					gzis= new GZIPInputStream(new FileInputStream(INPUT_GZIP_FILE));
					outzip= new FileOutputStream(OUTPUT_FILE);
					is = new FileInputStream(INPUT_GZIP_FILE);
					inStream=new DataInputStream(gzis);
					entry="true";
				}
				//len = gzis.read(bufferzip);
				//outzip.write(bufferzip, 0, len);
				//outzip.flush();
			}
			outD.close();
			inD.close();
			System.out.println("");
			System.out.println("Download completato");  
			System.out.print("Decompressione in corso..."); 

			while ((len = gzis.read(bufferzip)) > 0) {
				outzip.write(bufferzip, 0, len);
				outzip.flush();
			}
			outzip.flush();
			outzip.close();
			is.close();
			inStream.close();
			gzis.close();
			System.out.println("COMPLETATA");
			connector= new ConnectorDB(oc);
			Connection connectionDB = connector.getConnection();
			ParseWat parseWat =new ParseWat();
			parseWat.ParsingWat(OUTPUT_FILE, connectionDB);

			System.out.println("cancello file");
			System.out.println(f.getAbsolutePath());
			File fZip = new File(folderWat+f.getName());
			System.out.println("unzip: "+folderWat+f.getName().split(".gz")[0]);
			File fUnZip = new File(folderWat+f.getName().split(".gz")[0]);
			if (fZip.exists()){
				if(fZip.delete()){
					System.out.println(fZip.getName() + " is deleted!");
				}else{
					System.out.println("Delete operation is failed.");
				}
				System.out.println("file esiste");

			}

			if (fUnZip.exists()){
				if(fUnZip.delete()){
					System.out.println(fUnZip.getName() + " is deleted!");
				}else{
					System.out.println("Delete operation is failed.");
				}
				System.out.println("file esiste");

			}

			//i++;
		}
	}
}

