/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * @author Rob
 */
public class ConnectorDB {
   static final String JDBC_DRIVER = "org.postgresql.Driver";  
   static final String DB_URL = "jdbc:postgresql://localhost:5432/indexurl";
   private static ObjectConf oc;
   //  Database credentials
   //static final String USER = "postgres";
   public ConnectorDB(ObjectConf oc){
       this.oc=oc;
   }

    //static final String PASS = "admin";
    public static Connection getConnection() {
        Connection connection = null;
        Statement st = null;
        try {
            String USER = oc.getUser();
            String PASS = oc.getPass();
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            
            
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
