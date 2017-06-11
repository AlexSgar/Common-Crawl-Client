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
import org.apache.commons.io.IOUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;

/**
 *
 * @author Rob
 */
public class WarcReader {
	public byte[] retriveContentURL(String PathCacheFolder,String fileName,String URL) throws FileNotFoundException, IOException{
		File f = new File(PathCacheFolder + fileName);
		String INPUT_GZIP_FILE2 = PathCacheFolder + f.getName();
		FileInputStream is = null;
		//ritorna contenuto URL cercato
		is = new FileInputStream(INPUT_GZIP_FILE2);
		ArchiveReader ar = WARCReaderFactory.get(INPUT_GZIP_FILE2, is, true);

		int k = 0;
		String UrlWarc;    
		byte[] rawData=null;
		Integer obj = new Integer(194818);

		// returns the value of this Integer as a long
		//long l = obj.longValue();
		//System.out.println("Value of l = " + l);

		for(ArchiveRecord r : ar) {

			UrlWarc = r.getHeader().getUrl();
			//System.out.println(UrlWarc);



			if (UrlWarc!=null){
				if ((UrlWarc.equals(URL))
						&&(r.getHeader().getMimetype().equals("application/http; msgtype=response"))){
					System.out.println(r.getHeader().getMimetype());
					System.out.println(r.getHeader().getUrl());
					System.out.println("contentLength"+r.getHeader().getContentLength());
					rawData = IOUtils.toByteArray(r, r.available());
					//String content = new String(rawData);
					//System.out.println(content);
					System.out.println("=-=-=-=-=-=-=-=-=");
					return rawData;
				}
			}

		}
		return rawData;
	}
}
