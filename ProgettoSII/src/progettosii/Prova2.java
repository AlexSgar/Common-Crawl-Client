package progettosii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Prova2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		boolean run = true;
		String warcPath = "/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/CC-MAIN-20170116095119-00000-ip-10-171-10-70.ec2.internal.warc.gz";
		if (run) {
			//decompress .gz
//			try (GZIPInputStream gzipis = new GZIPInputStream(new FileInputStream(warcPath));
//					OutputStream os = new FileOutputStream(warcPath.split(".gz")[0])) {
//
//				byte[] buffer = new byte[8192];
//				int intsRead;
//				while ((intsRead = gzipis.read(buffer)) > 0) {
//					os.write(buffer, 0, intsRead);
//				}
//				gzipis.close();
//				os.flush();
//				os.close();
//			} catch (EOFException e) { e.printStackTrace();	}

			//compress .gz
			byte[] buffer = new byte[1024];
			try{
//				Files.delete(Paths.get(PathCacheFolder + f.getName()));

				GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(warcPath));

				FileInputStream ins = new FileInputStream(warcPath.split(".gz")[0]);

				int len;
				while ((len = ins.read(buffer)) > 0) {
					gzos.write(buffer, 0, len);
				}

				ins.close();

				gzos.finish();
				gzos.close();

				System.out.println("Done");

			}catch(IOException ex){
				ex.printStackTrace();
			}	


			//			RandomAccessFile raf = new RandomAccessFile(new File("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/CC-MAIN-20170116095119-00000-ip-10-171-10-70.ec2.internal.warc.gz"),"rw");
			//			GZIPInputStream gzipstream = new GZIPInputStream(new FileInputStream(new File("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/CC-MAIN-20170116095119-00000-ip-10-171-10-70.ec2.internal.warc.gz")));
			//
			//			FileWriter fw = new FileWriter(new File("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/mario1"));
			//			raf.seek(raf.length() - 1);
			//			System.out.println(raf.readLine());
			//			raf.writeChars("\n\naaaaa");
			//			raf.close();


			//			byte buffer[] = new byte[8192];
			//			int n;
			//			while ((n = gzipstream.read(buffer)) > 0) {
			////				raf.write(buffer, 0, n);
			//				fw.write(new String(buffer));
			//			}
			//			raf.close();





		}
		if(!run){
			InputStream fileStream = new FileInputStream("/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/cache/CC-MAIN-20170116095119-00000-ip-10-171-10-70.ec2.internal.warc.gz");

			InputStream gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
			BufferedReader buffered = new BufferedReader(decoder);
			String mario = "";
			while ((mario = buffered.readLine()) != null) {

				System.out.println(mario);
			}
			buffered.close();
			decoder.close();
			gzipStream.close();
			fileStream.close();
		}
	}

	private static void cutFile(String filePath, String stringFromWhereCut){
		try {
			RandomAccessFile raFile = new RandomAccessFile(new File(filePath), "rw");

			String line = "";
			long length = 0, lengthReg = 0;
			while((line = raFile.readLine()) != null){
				length += line.getBytes("ISO-8859-1").length + 1;
				if(line.contains(stringFromWhereCut))
					lengthReg = length;
			}
			System.out.println("Bytes red: "+length);
			System.out.println("File length: "+raFile.length());
			//			raFile.setLength(lengthReg - 1);
			//			System.out.println(raFile.readLine() + (lengthReg - 1));
			raFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
