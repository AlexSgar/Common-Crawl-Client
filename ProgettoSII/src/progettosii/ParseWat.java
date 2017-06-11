/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rob
 */
public class ParseWat {
    public void ParsingWat(String filePath, Connection connectionDB) throws FileNotFoundException, IOException, SQLException{
        
        ObjectURL ou = new ObjectURL();
	EstrattoreJson estrattoreJson=new EstrattoreJson();
        FileReader reader = new FileReader(filePath);
	BufferedReader in = new BufferedReader(reader);
	String riga="";
	String stm =null;
        PreparedStatement ps = null;
        System.out.println("Creazione dell'indice in corso...");
	for (int i = 0; i < 24; i++) {
            riga=in.readLine();
	}
	int j=0;
	int k=0;
	String JsonUrl=null;;
	//System.out.println(in.readLine());
        int index=0;
        
        
        String stm2 = "SELECT COUNT(*) FROM indexurl";
        ps = connectionDB.prepareStatement(stm2);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            index=rs.getInt("COUNT");
        }
        //System.out.println("index: "+index);
        
        
	while((riga=in.readLine())!=null){
            k++;
            if (k==10){
		JsonUrl=riga;
		//System.out.println(JsonUrl);
                try {
                    if ((riga=in.readLine())!=null)
                        ou=estrattoreJson.CreaObjectURL(JsonUrl);
                        if (ou!=null){
                            index++;
                            stm = "INSERT INTO indexurl(index,url,segmentwarc,actualcontentlength,offsetwarc) VALUES(?,?,?,?,?)";
                            ps = connectionDB.prepareStatement(stm);
                            ps.setInt(1, index);
                            ps.setString(2,ou.getURL() );
                            ps.setString(3,ou.getSegmentWARC());
                            ps.setInt(4,ou.getActualContentLength());
                            ps.setInt(5,ou.getOffset());
                            try{
                                ps.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println("Connection Failed! Check output console");
                                System.out.println(ou.getURL());
                                e.printStackTrace();
                            }
                        }
                } catch (EOFException e) {
                // ... this is fine
                } catch(IOException e) {
                // handle exception which is not expected
                e.printStackTrace(); 
                }
                k=0;
            }
	}
        reader.close();
        in.close();
    }
}
