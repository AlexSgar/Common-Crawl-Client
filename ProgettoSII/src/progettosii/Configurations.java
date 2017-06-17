/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configurations {
    
    
    public ObjectConf getConf(String pathFileConfiguarations) throws IOException{
        
    	ObjectConf oc = new ObjectConf();
        FileReader fr = null;
        BufferedReader in = null;
       
        try {
        	fr = new FileReader(pathFileConfiguarations);
            in = new BufferedReader(fr);
            String riga;
            String campo;
            int i;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("folderWat")){
                    oc.setFolderWat(riga);
                }
                if (campo.equals("folderwatpath")){
                    oc.setFolderwatpath(riga);
                }
                if (campo.equals("folderCache")){
                    oc.setFolderCache(riga);
                }
                if (campo.equals("folderFileWarcPath")){
                    oc.setFolderFileWarcPath(riga);
                }
                if (campo.equals("maxNumberWARCinCache")){
                    i= Integer.parseInt(riga);
                    oc.setMaxNumberWARCinCache(i);
                }
                if (campo.equals("maxSizeCache")){
                    i= Integer.parseInt(riga);
                    oc.setMaxSizeCache(i);
                }
                if (campo.equals("user")){
                    oc.setUser(riga);
                }
                if (campo.equals("pass")){
                    oc.setPass(riga);
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
        	in.close();
        	fr.close();
        }
        return oc;
    }
    /*
    public String getfolderWat(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("folderWat")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getfolderwatpath(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("folderwatpath")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getfolderCache(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("folderCache")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getfolderFileWarcPath(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("folderFileWarcPath")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int getmaxNumberWARCinCache(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("NumberWARCinCache")){
                    int i= Integer.parseInt(riga);
                    return i;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public int getmaxSizeCache(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("maxSizeCache")){
                    int i= Integer.parseInt(riga);
                    return i;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    public String getUserDB(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("user")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String getPassDB(){
        FileReader fr;
        try {
            fr = new FileReader(pathFileConfiguarations);
            BufferedReader in = new BufferedReader(fr);
            String riga;
            String campo;
            while ((riga=in.readLine())!=null){
                campo=riga.split("=")[0];
                riga=riga.split("=")[1];
                if (campo.equals("pass")){
                    return riga;
                }
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Configurations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }*/
}
