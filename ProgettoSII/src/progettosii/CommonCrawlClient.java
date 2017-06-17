/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import progettosii.Configurations;


/**
 *
 * @author Rob
 */
public class CommonCrawlClient {

	private ObjectConf oc;
	private boolean watIndexCreated;

	public CommonCrawlClient(String pathFileConfigurations){
		this.watIndexCreated = false;
		this.oc = new Configurations().getConf(pathFileConfigurations);
	}

	public void createWatIndex() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException{
		TableManager tm = new TableManager(this.oc);

		tm.createTable();
		System.out.println("Database creato correttamente");

		//Creazione dell'indice indexurl(index,url,segmentwarc,Length,offset)
		ControllerIndex ci = new ControllerIndex(oc);

		String folderwatpath = oc.getFolderwatpath();
		ci.createIndex(folderwatpath);

		this.watIndexCreated = true;
		System.out.println("INDICE WAT GIÀ CREATO");
	}

	public void deleteWatIndex() throws ClassNotFoundException, SQLException{
		TableManager tm = new TableManager(this.oc);
		if(tm.existsIndexTable())
			tm.dropTable();

	}

	public String getContentUrl(String urlRequest){
		ControllerRequest cr = new ControllerRequest(oc);

		byte[] rawData = cr.getRequest(urlRequest);
		return new String(rawData);
	}


	public static void main(String[] args) throws IOException, FileNotFoundException, SQLException, ClassNotFoundException {
		String configurationPath = "/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/file di configurazione.txt";
		CommonCrawlClient ccc = new CommonCrawlClient(configurationPath);

		//		ccc.deleteWatIndex();
		ccc.createWatIndex();


		String[] url = new String[] {"http://03online.com/news/3383", 
				"http://05sese.com/news/class/160566.html", "http://0lik.ru/templates/fotomontag/115531-free-arthur-radley-website-template.html",
				"http://1.163.com/detail/2924-401120986.html", "http://1portable.ru/index.php?newsid=7879", "https://www.pokemon-france.com/tag/event/", 
				"https://www.pokerstarter.online/blogs/DanPoker77/Chast-shestaya", "https://www.promodeclic.fr/chalonnes-sous-le-lude", 
				"https://www.promodescuentos.com/discusiones/memoria-ram-en-amazon-32555", "https://znanija.com/task/5725347"};

		url = new String[]{"https://znaytovar.ru/finance/firm0256018092.html"};

		List<String> results = new LinkedList<>();

		for(int i = 0; i < url.length; i++){
			results.add(ccc.getContentUrl(url[i]));
		}

		System.out.println(url.length == results.size());

		PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
		for (String string : results) {
			writer.println(string + "\n\n===========================================\n\n");
		}
		writer.close();





		//      byte[] rawData = cr.getRequest("http://0x20.be/Special:WhatLinksHere/Meeting36");

		// 		String url = "http://03online.com/news/3383";
		//      String url = "http://05sese.com/news/class/160566.html";
		//        String url = "http://0lik.ru/templates/fotomontag/115531-free-arthur-radley-website-template.html";
		//        String url = "http://1.163.com/detail/2924-401120986.html";

		//        da qui è oltre 1MB
		//        String url = "http://1portable.ru/index.php?newsid=7879";
		// 		String url = "https://www.pokemon-france.com/tag/event/";
		//        String url = "https://www.pokerstarter.online/blogs/DanPoker77/Chast-shestaya";

		// 		String url = "https://www.promodeclic.fr/chalonnes-sous-le-lude";
		// 		String url = "https://www.promodescuentos.com/discusiones/memoria-ram-en-amazon-32555";
		//        String url = "https://www.psxhax.com/members/k4ne.110419/";
		//        String url = "https://znanija.com/task/5725347";



	}

}
