/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;
import org.archive.io.warc.WARCReaderFactory.CompressedWARCReader;

/**
 *
 * @author Rob
 */
public class WarcReader {
	public byte[] retriveContentURL(String PathCacheFolder, String fileName, String URL) throws FileNotFoundException, IOException{
		File f = new File(PathCacheFolder + fileName);
		String INPUT_GZIP_FILE2 = PathCacheFolder + f.getName();
		//ritorna contenuto URL cercato
		FileInputStream is = new FileInputStream(INPUT_GZIP_FILE2);
		ArchiveReader ar = WARCReaderFactory.get(INPUT_GZIP_FILE2, is, true);
//		ArchiveReader ar = WARCReaderFactory.get(f, 502550917);


		int k = 0;
		String UrlWarc;    
		byte[] rawData=null;
		Integer obj = new Integer(194818);
		

		// returns the value of this Integer as a long
		//long l = obj.longValue();
		//System.out.println("Value of l = " + l);
		Iterator<ArchiveRecord> warc = WARCReaderFactory.get(f).iterator();

	
		while (warc.hasNext()) {

			try(ArchiveRecord r = warc.next()){
				UrlWarc = r.getHeader().getUrl();
				//System.out.println(UrlWarc);
				//			System.out.println("MARIA: " + UrlWarc);



				if ((UrlWarc!=null) && (UrlWarc.equals(URL)) && (r.getHeader().getMimetype().equals("application/http; msgtype=response"))){
					//				System.out.println("MARIA: " + UrlWarc);

					System.out.println(r.getHeader().getMimetype());
					System.out.println(r.getHeader().getUrl());
					System.out.println("contentLength: " + r.getHeader().getContentLength());
					rawData = IOUtils.toByteArray(r, r.available());
					//String content = new String(rawData);
					//System.out.println(content);
					System.out.println("=-=-=-=-=-=-=-=-=");
					return rawData;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}




//		for(ArchiveRecord r : ar) {
//
//			UrlWarc = r.getHeader().getUrl();
//			//System.out.println(UrlWarc);
//			//			System.out.println("MARIA: " + UrlWarc);
//
//
//
//			if ((UrlWarc!=null) && (UrlWarc.equals(URL)) && (r.getHeader().getMimetype().equals("application/http; msgtype=response"))){
//				//				System.out.println("MARIA: " + UrlWarc);
//
//				System.out.println(r.getHeader().getMimetype());
//				System.out.println(r.getHeader().getUrl());
//				System.out.println("contentLength: " + r.getHeader().getContentLength());
//				rawData = IOUtils.toByteArray(r, r.available());
//				//String content = new String(rawData);
//				//System.out.println(content);
//				System.out.println("=-=-=-=-=-=-=-=-=");
//				return rawData;
//			}
//
//
//		}
		return rawData;
	}
}
