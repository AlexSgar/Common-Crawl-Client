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
	private String PathCacheFolder;
	private int chunkSize=1000000;
	private String folderCache;
	private String folderFileWarcPath;
	private ObjectConf oc;

	public Cache(String folder,ObjectConf oc){
		this.PathCacheFolder=folder;
		this.oc=oc;
	}

	public boolean isPresent(String segmentwarc,Connection connection) throws SQLException{
		PreparedStatement ps = null;
		String stm = "SELECT segmentwarc,filesize FROM cache WHERE segmentwarc='"+segmentwarc+"'";
		ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()){
			return false;
		}
		else{
			return true;
		}
	}

	public int getNumberSegmentWarc(Connection connection) throws SQLException{
		PreparedStatement ps = null;
		String stm = "SELECT COUNT(*) FROM cache";
		ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if(!rs.next()){
			return 0;
		}
		else{
			return rs.getInt("COUNT");
		}
	}

	public int getSizeCache(Connection connection) throws SQLException{
		PreparedStatement ps = null;
		String stm = "SELECT segmentwarc,filesize FROM cache";
		ps = connection.prepareStatement(stm);
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
		String stm = "SELECT segmentwarc,filesize FROM cache";
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


	public void download(String segmentwarc, int Offset, Connection connection) throws MalformedURLException, IOException, SQLException{
		folderFileWarcPath = oc.getFolderFileWarcPath();
		FileReader fr= new FileReader(folderFileWarcPath + "warc.path");
		BufferedReader in = new BufferedReader(fr);
		String riga = in.readLine();
		riga = riga.split("warc")[0].concat("warc/");

		//String stringaurl = "https://aws-publicdatasets.s3.amazonaws.com/" + riga + segmentwarc;
		String stringaurl = "https://commoncrawl.s3.amazonaws.com/" + riga + segmentwarc;

		URL url = new URL(stringaurl);
		String fileName = url.getFile();
		HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
		int TotalLength=connectionTest.getContentLength();
		int downloaded=0;
		int size=-1;
		// Open connection to URL.
		HttpURLConnection connectionHTTP = (HttpURLConnection) url.openConnection();

		// Specify what portion of file to download.
		String endRange = new Integer(Offset+chunkSize-1).toString();
		connectionHTTP.setRequestProperty("Accept-Encoding", "gzip");//Here the change

		System.out.println(TotalLength);
		if (TotalLength > Offset + chunkSize - 1){
			connectionHTTP.setRequestProperty("Range","bytes=" + downloaded + "-"+endRange);
		}
		else{
			connectionHTTP.setRequestProperty("Range","bytes=" + downloaded + "-");
		}
		// Connect to server.
		connectionHTTP.connect();

		// Make sure response code is in the 200 range.
		if (connectionHTTP.getResponseCode() / 100 != 2) {
			System.out.println("error");
		}

		// Check for valid content length.
		int contentLength = connectionHTTP.getContentLength();
		if (contentLength < 1) {
			System.out.println("error");
		}

		/* Set the size for this download if it
            hasn't been already set. */
		if (size == -1) {
			size = contentLength;   
			System.out.println("grandezza file: "+contentLength);
		}
		// Open file and seek to the end of it.

		File f = new File(PathCacheFolder + fileName);

		//		BufferedInputStream inD = new BufferedInputStream(connectionHTTP.getInputStream());
		//		InputStream inD = connectionHTTP.getInputStream();

		//		BufferedOutputStream outD = new BufferedOutputStream(new FileOutputStream(PathCacheFolder+f.getName()));
		//		OutputStream bos = new FileOutputStream(PathCacheFolder+f.getName());


		String INPUT_GZIP_FILE = PathCacheFolder+f.getName();
		System.out.println("inputzip:"+INPUT_GZIP_FILE);
		String OUTPUT_FILE = INPUT_GZIP_FILE.split(".gz")[0];
		System.out.println("outputunzip:" + OUTPUT_FILE);
		//FileInputStream is = null;
		

		try(BufferedInputStream inD = new BufferedInputStream(connectionHTTP.getInputStream());
				FileOutputStream bos = new FileOutputStream(PathCacheFolder+f.getName())){
			byte[] buffer = new byte[8192];
			int intsRead;
			while ((intsRead = inD.read(buffer)) > 0) {
				bos.write(buffer, 0, intsRead);
			}
			bos.flush();
			bos.close();
			inD.close();
		}


		//decompress .gz
		//		try (GZIPInputStream gzipis = new GZIPInputStream(new FileInputStream(PathCacheFolder+f.getName()));
		//				OutputStream os = new FileOutputStream(PathCacheFolder+f.getName().split(".gz")[0])) {
		//			byte[] buffer = new byte[8192];
		//			int intsRead;
		//			while ((intsRead = gzipis.read(buffer)) > 0) {
		//				os.write(buffer, 0, intsRead);
		//			}
		//			gzipis.close();
		//			os.flush();
		//			os.close();
		//		} catch (EOFException e) { e.printStackTrace();	}



		//compress .gz
		//		byte[] buffer = new byte[1024];
		//		try{
		//			Files.delete(Paths.get(PathCacheFolder + f.getName()));
		//
		//			GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(PathCacheFolder + f.getName()));
		//
		//			FileInputStream ins = new FileInputStream(PathCacheFolder + f.getName().split(".gz")[0]);
		//
		//			int len;
		//			while ((len = ins.read(buffer)) > 0) {
		//				gzos.write(buffer, 0, len);
		//			}
		//
		//			ins.close();
		//
		//			gzos.finish();
		//			gzos.close();
		//
		//			System.out.println("Done");
		//
		//		}catch(IOException ex){
		//			ex.printStackTrace();
		//		}		

		/*int n;
		System.out.println("sto scaricando: "+f.getName());
		int count=0;
		int j=0;
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
		}
		outD.close();
		inD.close();*/

		String stm = "INSERT INTO cache(segmentwarc,filesize) VALUES(?,?)";
		PreparedStatement ps = connection.prepareStatement(stm);
		ps.setString(1,segmentwarc);
		ps.setInt(2,Offset+chunkSize);
		try{
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}

	public void resume(String segmentwarc, int Offset, Connection connectionDB) throws SQLException, IOException{
		PreparedStatement ps = null;
		String stm = "SELECT segmentwarc,filesize FROM cache WHERE segmentwarc='"+segmentwarc+"'";
		ps = connectionDB.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int filesize=rs.getInt(2);
		String warc= rs.getString(1);
		if ((filesize<Offset)||(filesize<Offset+500000)){

			InputStream stream = null;
			// Apre il file e ne cerca la fine.
			RandomAccessFile file = null;

			//String stringaurl = "https://aws-publicdatasets.s3.amazonaws.com/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1408500800168.29/warc/"+segmentwarc;
			String stringaurl = "https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2017-04/segments/1484560279933.49/warc/"+segmentwarc;		
			System.out.println(stringaurl);

			URL url = new URL(stringaurl);
			String fileName = url.getFile();
			System.out.println(fileName);

			File f=new File(PathCacheFolder+fileName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String endfile = new Integer(filesize).toString();
			String endfile2 = new Integer(Offset+chunkSize-1).toString();
			HttpURLConnection connectionTest = (HttpURLConnection) url.openConnection();
			int warcFileSize=connectionTest.getContentLength();
			boolean finefile=false;

			if (warcFileSize-Offset>chunkSize-1){
				connection.setRequestProperty("Range","bytes="+endfile+"-"+endfile2);
			}
			else{
				finefile=true;
				connection.setRequestProperty("Range","bytes=" + endfile + "-");
			}
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

			InputStream bStreamm = new FileInputStream(PathCacheFolder + warc);

			ByteArrayOutputStream bOutStreamm = new ByteArrayOutputStream();

			try{
				GZIPInputStream gis = new GZIPInputStream(bStreamm);
				byte[] buf = new byte[4096];
				int len;

				while((len = gis.read(buf)) != -1){
					bOutStreamm.write(buf, 0, len);
				}

				bOutStreamm.close();
				gis.close();

			} catch (IOException e) {
				e.printStackTrace();
				bOutStreamm.close();
				PrintWriter writer = new PrintWriter("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/prima", "UTF-8");
				writer.println(new String(bOutStreamm.toByteArray()));
				writer.close();
				//print unarchieved bytes
				//				System.out.println(new String(bOutStream.toByteArray()));
			}

			// Apre il file e ne cerca la fine.
			file = new RandomAccessFile(PathCacheFolder+warc, "rw");
			file.seek(file.length());
			System.out.println(file.getFilePointer());
			stream = connection.getInputStream();
			/* Dimensiona il buffer secondo la quantità di
			                file restata da scaricare. */
			byte buffer[];
			buffer = new byte[4096];
			int n;
			while ((n = stream.read(buffer)) > 0) {
				file.write(buffer, 0, n);
			}
			file.close();

			InputStream bStream = new FileInputStream(PathCacheFolder + warc);

			ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();

			try{
				GZIPInputStream gis = new GZIPInputStream(bStream);
				byte[] buf = new byte[4096];
				int len;

				while((len = gis.read(buf)) != -1){
					bOutStream.write(buf, 0, len);
				}

				bOutStream.close();
				gis.close();

			} catch (IOException e) {
				e.printStackTrace();
				bOutStream.close();
				PrintWriter writer = new PrintWriter("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/dopo", "UTF-8");
				writer.println(new String(bOutStream.toByteArray()));
				writer.close();
				//print unarchieved bytes
				//				System.out.println(new String(bOutStream.toByteArray()));
			}

			//aggiorno Database cache
			int totalsize;
			if (finefile==false)
				totalsize=Offset+chunkSize;
			else 
				totalsize=warcFileSize;

			stm = "UPDATE cache set filesize='"+totalsize+"' WHERE segmentwarc='"+warc+"'";
			ps = connectionDB.prepareStatement(stm);
			try{
				ps.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
			}
		}
	}

	/**
	 * delete lines from the file specified in filePath, 
	 * since the last occurrence of stringFromWhereCut
	 * @param stringFromWhereCut
	 */
	private void cutFile(String filePath, String stringFromWhereCut){
		try {
			RandomAccessFile raFile = new RandomAccessFile(new File(filePath), "rw");

			String line = "";
			long length = 0, lengthReg = 0;
			while((line = raFile.readLine()) != null){
				length += line.getBytes("ISO-8859-1").length + 1;
				if(line.contains(stringFromWhereCut))
					lengthReg = length;
			}
			raFile.setLength(lengthReg - 1);
			System.out.println(raFile.readLine() + (lengthReg - 1));
			raFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
