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

public class WarcReader {
	public byte[] retriveContentURL(String PathCacheFolder, String fileName, String URL) throws FileNotFoundException, IOException{
		File f = new File(PathCacheFolder + fileName);
		String INPUT_GZIP_FILE2 = PathCacheFolder + f.getName();
		//ritorna contenuto URL cercato
		FileInputStream is = new FileInputStream(INPUT_GZIP_FILE2);
		ArchiveReader ar = WARCReaderFactory.get(INPUT_GZIP_FILE2, is, true);

		String UrlWarc;    
		byte[] rawData=null;

		for(ArchiveRecord r : ar) {
			UrlWarc = r.getHeader().getUrl();

			if ((UrlWarc!=null) && (UrlWarc.equals(URL)) && (r.getHeader().getMimetype().equals("application/http; msgtype=response"))){
				rawData = IOUtils.toByteArray(r, r.available());

				return rawData;
			}
		}
		return rawData;
	}
}
