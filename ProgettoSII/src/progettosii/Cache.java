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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *
 * @author Rob
 */
public class Cache {
	private String pathCacheFolder;
	private int chunkSize = 1000000;
	private String folderCache;
	private String folderFileWarcPath;
	private ObjectConf oc;

	public Cache(String folder, ObjectConf oc){
		this.pathCacheFolder = folder;
		this.oc = oc;
	}

	public boolean isPresent(String segmentwarc,Connection connection) throws SQLException{
		String stm = "SELECT segmentwarc,filesize FROM cache WHERE segmentwarc='" + segmentwarc + "'";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	}

	public int getNumberSegmentWarc(Connection connection) throws SQLException{
		PreparedStatement ps = null;
		String stm = "SELECT COUNT(*) FROM cache";
		ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if(!rs.next())
			return 0;
		else
			return rs.getInt("COUNT");
	}

	public int getSizeCache(Connection connection) throws SQLException{
		String stm = "SELECT segmentwarc,filesize FROM cache";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		int sizeCache=0;
		while (rs.next()) {
			sizeCache+=rs.getInt(2);
		}
		return sizeCache;
	}

	public String getOldWARC(Connection connection) throws SQLException{
		folderCache = oc.getFolderCache();
		String stm = "SELECT segmentwarc,filesize FROM cache";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		int i = 0;
		long lastModified = 0;
		String OldWARC=null;
		while (rs.next()) {
			File file = new File(folderCache+rs.getString(1));
			System.out.println(file.lastModified());
			if (i==0){
				lastModified=file.lastModified();
				OldWARC=rs.getString(1);
			}
			else{
				if (lastModified>file.lastModified()){
					lastModified=file.lastModified();
					OldWARC=rs.getString(1);
				}
			}
			i++;
		}

		return OldWARC;
	}


	public void download(String segmentwarc, int Offset, Connection connection) throws MalformedURLException, IOException, SQLException{
		folderFileWarcPath = oc.getFolderFileWarcPath();
		FileReader fr = new FileReader(folderFileWarcPath + "warc.path");
		BufferedReader in = new BufferedReader(fr);
		String riga = in.readLine();
		in.close();
		riga = riga.split("warc")[0].concat("warc/");

		//String stringaurl = "https://aws-publicdatasets.s3.amazonaws.com/" + riga + segmentwarc;
		String stringaurl = "https://commoncrawl.s3.amazonaws.com/" + riga + segmentwarc;

		URL url = new URL(stringaurl);
		String fileName = url.getFile();
		HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
		int TotalLength = connectionTest.getContentLength();
		int downloaded=0;
		int size = -1;
		// Open connection to URL.
		HttpURLConnection connectionHTTP = (HttpURLConnection) url.openConnection();

		// Specify what portion of file to download.
		String range = new Integer(Offset + chunkSize - 1).toString();

		System.out.println(TotalLength);
		if (TotalLength > Offset + chunkSize - 1)
			connectionHTTP.setRequestProperty("Range","bytes=" + downloaded + "-"+range);
		else
			connectionHTTP.setRequestProperty("Range","bytes=" + downloaded + "-");
		// Connect to server.
		connectionHTTP.connect();

		// Make sure response code is in the 200 range.
		if (connectionHTTP.getResponseCode() / 100 != 2)
			System.out.println("error");

		// Check for valid content length.
		int contentLength = connectionHTTP.getContentLength();
		if (contentLength < 1)
			System.out.println("error");

		/* Set the size for this download if it
            hasn't been already set. */
		if (size == -1) {
			size = contentLength;   
			System.out.println("grandezza file: "+contentLength);
		}
		// Open file and seek to the end of it.
		File f = new File(pathCacheFolder + fileName);

		BufferedInputStream inD = new BufferedInputStream(connectionHTTP.getInputStream());
		BufferedOutputStream outD = new BufferedOutputStream(new FileOutputStream(pathCacheFolder + f.getName()));
		String INPUT_GZIP_FILE = pathCacheFolder + f.getName();
		System.out.println("inputzip:"+INPUT_GZIP_FILE);
		String OUTPUT_FILE = INPUT_GZIP_FILE.split(".gz")[0];
		System.out.println("outputunzip:"+OUTPUT_FILE);

		int n;
		System.out.println("sto scaricando: " + f.getName());
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
			count += 4096;      
		}
		outD.close();
		inD.close();

		String stm = "INSERT INTO cache(segmentwarc,filesize) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(stm);
		ps.setString(1, segmentwarc);
		ps.setInt(2, Offset + chunkSize);
		try{
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}

	public void resume(String segmentwarc, int Offset, Connection connectionDB) throws SQLException, IOException{
		String stm = "SELECT segmentwarc,filesize FROM cache WHERE segmentwarc='"+segmentwarc+"'";
		PreparedStatement ps = connectionDB.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int filesize = rs.getInt(2);
		String warc = rs.getString(1);
		if ((filesize < Offset) || (Offset > filesize - 500000)){
			// Apre il file e ne cerca la fine.
			//String stringaurl = "https://aws-publicdatasets.s3.amazonaws.com/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1408500800168.29/warc/"+segmentwarc;
			String stringaurl = "https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2017-04/segments/1484560279933.49/warc/"+segmentwarc;		
			System.out.println(stringaurl);
			URL url = new URL(stringaurl);
			String fileName = url.getFile();
			System.out.println(fileName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String endfile = new Integer(filesize).toString();
			String endfile2 = new Integer(Offset + chunkSize - 1).toString();
			HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
			int TotalLength = connectionTest.getContentLength();
			boolean finefile = false;
			if (TotalLength > Offset + chunkSize - 1)
				connection.setRequestProperty("Range", "bytes=" + endfile + "-" + endfile2);
			else{
				finefile = true;
				connection.setRequestProperty("Range","bytes=" + endfile + "-");
			}
			connection.connect();
			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2)
				System.out.println("error");

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1)
				System.out.println("error");

			// Apre il file e ne cerca la fine.
			RandomAccessFile file = new RandomAccessFile(pathCacheFolder + warc, "rw");
			file.seek(filesize);
			System.out.println(file.getFilePointer());
			InputStream stream = connection.getInputStream();
			/* Dimensiona il buffer secondo la quantità di
                file restata da scaricare. */
			byte buffer[];
			buffer = new byte[4096];
			int n;
			while ((n = stream.read(buffer)) > 0) {
				file.write(buffer, 0, n);
			}
			file.close();

			//aggiorno Database cache
			int totalsize;
			if (!finefile)
				totalsize = Offset + chunkSize;
			else 
				totalsize=TotalLength;

			stm = "UPDATE cache set filesize='" + totalsize + "' WHERE segmentwarc='" + warc + "'";
			ps = connectionDB.prepareStatement(stm);
			try{
				ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
			}
		}
	}
}
