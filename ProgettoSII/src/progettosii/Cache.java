/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 *
 * @author Rob
 */
public class Cache {
	private String pathCacheFolder;
	private int chunkSize=1000000;
	private String folderCache;
	private String pathFileWarcFolder;
	private ObjectConf oc;

	public Cache(ObjectConf oc){
		this.pathCacheFolder = oc.getFolderCache();
		this.pathFileWarcFolder = oc.getFolderFileWarcPath();
	}

	public boolean isPresent(String segmentwarc,Connection connection) throws SQLException{
		String stm = "SELECT segmentwarc,currentoffset FROM cache WHERE segmentwarc='"+segmentwarc+"'";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()){
			return false;
		}
		else{
			return true;
		}
	}

	public int getNumberSegmentWarc(Connection connection) throws SQLException{
		String stm = "SELECT COUNT(*) FROM cache";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if(!rs.next()){
			return 0;
		}
		else{
			return rs.getInt("COUNT");
		}
	}

	public int getSizeCache(Connection connection) throws SQLException{
		String stm = "SELECT segmentwarc,currentoffset FROM cache";
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
		PreparedStatement ps = null;
		String stm = "SELECT segmentwarc,currentoffset FROM cache";
		ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		int i=0;
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


	public void download(String segmentwarc, int offset, Connection connection) throws MalformedURLException, IOException, SQLException{

		FileReader fr= new FileReader(this.pathFileWarcFolder + "warc.path");
		BufferedReader in = new BufferedReader(fr);
		String riga = in.readLine();
		riga = riga.split("warc")[0].concat("warc/");
		in.close();

		String stringaurl = "https://commoncrawl.s3.amazonaws.com/" + riga + segmentwarc;

		URL url = new URL(stringaurl);
		String fileName = url.getFile();
		
		HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
		int TotalLength=connectionTest.getContentLength();
		int startRange = offset;
		int size =-1;
		// Open connection to URL.
		HttpURLConnection connectionHTTP = (HttpURLConnection) url.openConnection();

		// Specify what portion of file to download.
		String endRange = new Integer(offset + chunkSize).toString();
		connectionHTTP.setRequestProperty("Accept-Encoding", "gzip");//Here the change

		System.out.println(TotalLength);
		//		if (TotalLength > offset + chunkSize - 1)
		//			connectionHTTP.setRequestProperty("Range","bytes=" + offset + "-"+endRange);
		//		else
		connectionHTTP.setRequestProperty("Range","bytes=" + startRange + "-" + endRange);

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
			System.out.println("grandezza file: " + contentLength);
		}
		// Open file
		File f = new File(this.pathCacheFolder + fileName);		

		try(BufferedInputStream inD = new BufferedInputStream(connectionHTTP.getInputStream());
				FileOutputStream bos = new FileOutputStream(this.pathCacheFolder + f.getName())){
			byte[] buffer = new byte[8192];
			int intsRead;
			while ((intsRead = inD.read(buffer)) > 0) {
				bos.write(buffer, 0, intsRead);
			}
			bos.flush();
			bos.close();
			inD.close();
		}
		
		
		//disable update cache table

//		String stm = "INSERT INTO cache(segmentwarc,currentoffset) VALUES(?,?)";
//		PreparedStatement ps = connection.prepareStatement(stm);
//		ps.setString(1,segmentwarc);
//		//		ps.setInt(2,offset + chunkSize);
//		ps.setInt(2, offset);
//		try{
//			ps.executeUpdate();
//		} catch (SQLException e) {
//			System.out.println("Connection Failed! Check output console");
//			e.printStackTrace();
//		}
		
	}
	
/*Method resume disabled because of problem with append the new warc slice to the previous:
 * the new warc is readable from filesystem but WarcReader fails*/
	
	/*public void resume(String segmentwarc, int newOffset, Connection connectionDB) throws SQLException, IOException{
		
		
		PreparedStatement ps = null;
		String stm = "SELECT segmentwarc,currentoffset FROM cache WHERE segmentwarc='"+segmentwarc+"'";
		ps = connectionDB.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int currentOffset = rs.getInt(2);
		String warc= rs.getString(1);
		System.out.println(newOffset + " RESUME " + currentOffset);
		
		FileReader fr= new FileReader(this.pathFileWarcFolder + "warc.path");
		BufferedReader in = new BufferedReader(fr);
		String riga = in.readLine();
		riga = riga.split("warc")[0].concat("warc/");
		in.close();

		if (newOffset < currentOffset){
			String stringaurl = "https://commoncrawl.s3.amazonaws.com/"+riga + segmentwarc;		
			System.out.println(stringaurl);

			URL url = new URL(stringaurl);
			String fileName = url.getFile();
			System.out.println(fileName);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
			int warcFileSize = connectionTest.getContentLength();
			boolean finefile = false;
			
			connection.setRequestProperty("Accept-Encoding", "gzip");//Here the change
			connection.setRequestProperty("Range","bytes=" + newOffset + "-" + currentOffset);
			connection.connect();
			// Make sure response code is in the 200 range.
			if (connection.getResponseCode() / 100 != 2)
				System.out.println("error");

			// Check for valid content length.
			int contentLength = connection.getContentLength();
			if (contentLength < 1)
				System.out.println("error");

			 Apre il file e ne cerca la fine.
						RandomAccessFile file = new RandomAccessFile(pathCacheFolder + warc, "rw");
						file.seek(file.length());
						System.out.println(file.getFilePointer());
						stream = connection.getInputStream();
						
						byte buffer[];
						buffer = new byte[4096];
						int n;
						while ((n = stream.read(buffer)) > 0) {
							file.write(buffer, 0, n);
						}
						file.close();
			
			File f = new File(pathCacheFolder + fileName);		

			GZIPInputStream input = new GZIPInputStream(new BufferedInputStream(connection.getInputStream()));
			FileOutputStream zipFile = new FileOutputStream(this.pathCacheFolder + "newSlice.gz");
			int len;
			byte[] bufferzip = new byte[4096];

			while ((len = input.read(bufferzip)) > 0) {
				zipFile.write(bufferzip, 0, len);
			}
			zipFile.flush();
			zipFile.close();
			input.close();
			
			
			
//			File cachedWarc = new File(this.pathCacheFolder + warc);
//			File tempCachedWarc = new File(this.pathCacheFolder + "temporaryCachedWarcSlice.gz");
//			System.out.println(cachedWarc.renameTo(tempCachedWarc));
			

			try (	InputStream newWarcSlice = new FileInputStream(this.pathCacheFolder + "newSlice.gz");
					GZIPInputStream cachedWarcSlice = new GZIPInputStream(new FileInputStream(this.pathCacheFolder + warc));
					SequenceInputStream sis =  new SequenceInputStream(newWarcSlice, cachedWarcSlice);
					GZIPOutputStream newCachedWarcSlice = new GZIPOutputStream(new FileOutputStream(this.pathCacheFolder + "mario.gz"))) {
				byte[] buffer = new byte[8192];
				int intsRead;
				while ((intsRead = sis.read(buffer)) != -1) {
					newCachedWarcSlice.write(buffer, 0, intsRead);
				}
				newCachedWarcSlice.finish();
				newCachedWarcSlice.close();
				sis.close();
				newWarcSlice.close();
				cachedWarcSlice.close();
			}

			//aggiorno Database cache
			int totalsize;
			if (finefile==false)
				totalsize = newOffset + chunkSize;
			else 
				totalsize = warcFileSize;

			stm = "UPDATE cache set currentoffset='" + newOffset + "' WHERE segmentwarc='" + warc + "'";
			ps = connectionDB.prepareStatement(stm);
			try{
				ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
			}
		}
	}*/
}
