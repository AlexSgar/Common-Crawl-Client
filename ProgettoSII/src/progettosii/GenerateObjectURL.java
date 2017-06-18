/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rob
 */
public class GenerateObjectURL {
    
	public ObjectURL getObjectURL(String URL,Connection connection) throws SQLException{
        
		PreparedStatement ps = null;
        String stm = "SELECT index,url,segmentwarc,actualcontentlength,offsetwarc FROM indexurl WHERE url='"+URL+"'";
        ps = connection.prepareStatement(stm);
        ResultSet rs = ps.executeQuery();
       
        if (!rs.next()){
            System.out.println("URL non presente nel'indice WAT del database");
            return null;
        }
        else{
            //creazione dell'oggetto ObjectURL da restituire
            ObjectURL objectURL = new ObjectURL();
            objectURL.setURL(rs.getString(2));
            objectURL.setSegmentWARC(rs.getString(3));
            objectURL.setActualContentLength(rs.getInt(4));
            objectURL.setOffset(rs.getInt(5));
            
            return objectURL;
        }
    }
}
