/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.*;




public class DataSource{
//static final String USER = "postgres";
private static ObjectConf oc;
    
public DataSource(ObjectConf oc){
    this.oc=oc;
}

//static final String PASS = "admin";
    public Connection getConnection(String URL) {
        Connection connection = null;
        Statement st = null;
        try {
            String USER = oc.getUser();
            String PASS = oc.getPass();
            connection = DriverManager.getConnection(URL,USER,PASS);
            
            
            //String stm = "INSERT INTO indexurl(url, segmentwarc) VALUES('ciao3', 'ciao3')";
            //st.executeUpdate(stm);
            return connection;
        } catch (SQLException e) {
            
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            
            
        }
        
        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
        return connection;
    }
}
