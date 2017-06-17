/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progettosii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBRepository{

	private ObjectConf oc;

	public DBRepository(ObjectConf oc){
		this.oc=oc;
	}

	public Connection getConnection(String URL) throws SQLException {
		Connection connection = null;

		String USER = this.oc.getUser();
		String PASS = this.oc.getPass();
		connection = DriverManager.getConnection(URL,USER,PASS);



		return connection;
	}

	public Connection getConnection() throws SQLException {
		return getConnection("jdbc:postgresql://localhost:5432/indexurl");
	}
}
