package progettosii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Prova {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		RandomAccessFile raFile = new RandomAccessFile(new File("cane"), "rw");
//		raFile.seek(raFile.length() - 1000);
//		String cane, mario = "";
//		int l = 0;
//		while((cane = raFile.readLine()) != null){
//			mario = cane;
//			l = mario.getBytes().length;
//
//		}
		System.out.println(raFile.length());
//		raFile.seek(raFile.length() - l - 1);
//		System.out.println(raFile.readLine());
//		long mario = getLastLine(raFile);
//		System.out.println(mario);
//		raFile.seek(mario);
//		System.out.println(raFile.readLine());
		
		long pos = getPreviousLine(raFile, getPreviousLine(raFile, getPreviousLine(raFile, getLastLine(raFile))));
		raFile.seek(pos);
		System.out.println(pos);
		System.out.println(raFile.readLine());

		
//		long mario = getPreviousLine(raFile, getLastLine(raFile));
//		System.out.println(mario);
//		raFile.seek(mario);
//		mario = getPreviousLine(raFile, mario);
//		System.out.println(mario);
//		raFile.seek(mario);
//		System.out.println(raFile.readLine());
		
		
//		raFile.seek(1504);
//		System.out.println(raFile.readLine());
		
		
		
		
//		long mario = findLine(raFile, "bh vitae nibh. ");
//		System.out.println(mario);
//		raFile.seek(mario);
//		System.out.println(raFile.readLine());
	}
	

	public static long findLine(RandomAccessFile raFile, String toFind) throws IOException{
		long app, currentLine = getLastLine(raFile);
		raFile.seek(currentLine);
//		System.out.println(currentLine);
		String line;
		do {
			app = currentLine;
			line = raFile.readLine();
			System.out.println(line);
			currentLine = getPreviousLine(raFile, getPreviousLine(raFile, currentLine));
			raFile.seek(currentLine);
			System.out.println(currentLine);
		} while (!line.contains(toFind));
		return app;
	}
	
	public static long getPreviousLine(RandomAccessFile raFile, long position) throws IOException{
		long startPoint;
		if(position - 2000 > 0)
			startPoint = position - 2000;
		else
			startPoint = 0;
		raFile.seek(startPoint);
		long app = 0;
		long lenBytes = startPoint;
		while(lenBytes < position){
			app = lenBytes;
//			System.out.println(lenBytes);
			String mario = raFile.readLine();
//			System.out.println(mario);
			if(mario.equals(""))
				lenBytes += 2;	//if reads an empty line is necessary to add 2B
			else
				lenBytes += mario.getBytes().length;
		}
		return app;
	}
	
	public static long getLastLine(RandomAccessFile raFile) throws IOException{
		long startPoint;
		if(raFile.length() - 3000 > 0)
			startPoint = raFile.length() - 3000;

		else
			startPoint = 0;
		raFile.seek(startPoint);
		String line, app = "";
		int lenBytes = 0;
		while((line = raFile.readLine()) != null){
			app = line;
//			System.out.println(app);
			lenBytes = app.replaceAll("\t", "").getBytes().length;
		}
		return raFile.length() - 1 - lenBytes;
	}

}
