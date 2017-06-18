package main.java.progettosii;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException, SQLException{
		String basePathNicholas = "/home/nicholas/Documenti";
		String basePathAlex = "/Users/alex/Documents/workspace/sii";
		String basePathAndrea = "F:\\Documenti\\Università\\II Anno\\SII";


		//String configurationPath = basePathAlex + "/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/file_di_configurazione.txt";
		String configurationPath = basePathAndrea + "/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/file_di_configurazione.txt";


		CommonCrawlClient ccc = new CommonCrawlClient(configurationPath);
		
//		ccc.deleteWatIndex();
		ccc.createWatIndex();

		// urls in first WAT segment
		String[] url = new String[] {"http://03online.com/news/3383", 
				"http://05sese.com/news/class/160566.html", "http://0lik.ru/templates/fotomontag/115531-free-arthur-radley-website-template.html",
				"http://1.163.com/detail/2924-401120986.html", "http://1portable.ru/index.php?newsid=7879", "https://www.pokemon-france.com/tag/event/", 
				"https://www.pokerstarter.online/blogs/DanPoker77/Chast-shestaya", "https://www.promodeclic.fr/chalonnes-sous-le-lude", 
				"https://www.promodescuentos.com/discusiones/memoria-ram-en-amazon-32555", "https://znanija.com/task/5725347"};

		//urls in the last WAT segment
		url = new String[]{"http://080cc.chat080.com/"};

		List<String> results = new LinkedList<>();

		for(int i = 0; i < url.length; i++){
			String contentUrl = ccc.getContentUrl(url[i]);
			//System.out.println(contentUrl);
			if(contentUrl != null){
				results.add(contentUrl);
			}
		}

		System.out.println("Richieste(url): "+url.length +" Risposte(content): "+results.size());
		System.out.println("Guarda dentro results.txt nella home del progetto per i risultati");

		PrintWriter writer = new PrintWriter("results.txt");
		for (String string : results) {
			writer.println(string + "\n\n===========================================\n\n");
		}
		writer.close();
	}
}
