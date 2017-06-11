/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import progettosii.Configurations;


/**
 *
 * @author Rob
 */
public class ProgettoSII {
    private static String pathFileConfiguarations="/home/nicholas/Documenti/CommonCrawl ProgettoSII-final-with-fileconf2/ProgettoSII/src/file di configurazione.txt";
    
    public static void main(String[] args) throws IOException, FileNotFoundException, SQLException, ClassNotFoundException {
        
        //estraggo campi dal file di configurazione
        Configurations c = new Configurations();
        ObjectConf oc = new ObjectConf();
        oc = c.getConf(pathFileConfiguarations);
        
        //creazione database e tabelle
        TableManager tm = new TableManager(oc);
//        tm.dropTable();
//        tm.createTable();
//        System.out.println("Database creato correttamente");
//
//        //Creazione dell'indice indexurl(index,url,segmentwarc,Length,offset)
//        ControllerIndex ci = new ControllerIndex(oc);
//        
//        //Configurations c= new Configurations();
//        //String folderwatpath="C:\\Users\\Rob\\Documents\\NetBeansProjects\\ProgettoSII\\src\\file wat.path\\";
//        String folderwatpath=oc.getFolderwatpath();
//        ci.createIndex(folderwatpath);
       
        
        
        ControllerRequest cr = new ControllerRequest(oc);
//        byte[] rawData = cr.getRequest("http://0x20.be/Special:WhatLinksHere/Meeting36");
        
        byte[] rawData = cr.getRequest("http://abcnews.go.com/Politics/the-note/story?id=3180049&page=6");
        System.out.println(rawData);
        
        String content = new String(rawData);
        System.out.println(content);
        
        
    }
    
}
