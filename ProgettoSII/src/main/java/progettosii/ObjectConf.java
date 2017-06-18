/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.progettosii;

/**
 *
 * @author Rob
 */
public class ObjectConf {
	
    private String folderWat;
    private String folderwatpath;
    private String folderCache;
    private String folderFileWarcPath;
    private int maxNumberWARCinCache;
    private int maxSizeCache;
    private String user;
    private String pass;

    public String getFolderWat() {
        return folderWat;
    }

    public void setFolderWat(String folderWat) {
        this.folderWat = folderWat;
    }

    public String getFolderwatpath() {
        return folderwatpath;
    }

    public void setFolderwatpath(String folderwatpath) {
        this.folderwatpath = folderwatpath;
    }

    public String getFolderCache() {
        return folderCache;
    }

    public void setFolderCache(String folderCache) {
        this.folderCache = folderCache;
    }

    public String getFolderFileWarcPath() {
        return folderFileWarcPath;
    }

    public void setFolderFileWarcPath(String folderFileWarcPath) {
        this.folderFileWarcPath = folderFileWarcPath;
    }

    public int getMaxNumberWARCinCache() {
        return maxNumberWARCinCache;
    }

    public void setMaxNumberWARCinCache(int maxNumberWARCinCache) {
        this.maxNumberWARCinCache = maxNumberWARCinCache;
    }

    public int getMaxSizeCache() {
        return maxSizeCache;
    }

    public void setMaxSizeCache(int maxSizeCache) {
        this.maxSizeCache = maxSizeCache;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
   
    
}
