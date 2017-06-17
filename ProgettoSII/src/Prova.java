import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import progettosii.Configurations;
import progettosii.ConnectorDB;
import progettosii.ObjectConf;
import progettosii.ParseWat;

public class Prova {

	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		String configurationPath = "/home/nicholas/Documenti/CommonCrawl-ProgettoSII-final-with-fileconf2/ProgettoSII/src/file di configurazione.txt";
		ObjectConf oc = new Configurations().getConf(configurationPath);
		ConnectorDB connector = new ConnectorDB(oc);
		Connection connectionDB = connector.getConnection();

		String filePath = "/home/nicholas/Scaricati/CC-MAIN-20170116095119-00000-ip-10-171-10-70.ec2.internal.warc.wat";
		String lineWat = "crawl-data/CC-MAIN-2017-04/segments/1484560285337.76/wat/CC-MAIN-20170116095125-00577-ip-10-171-10-70.ec2.internal.warc.wat.gz";
		ParseWat pw = new ParseWat();
		
		
		pw.parsingWat(filePath, lineWat.replaceAll("/wat/", "/warc/").replaceAll("wat.", ""), connectionDB);
		
		
		
//		Connection connection = this.dataSource.getConnection("jdbc:postgresql://localhost:5432/");
//		String stm = "SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('dbname')";
//		PreparedStatement ps = connection.prepareStatement(stm);
//		ResultSet rs = ps.executeQuery();
//		if(rs.next() && rs.getInt(1) == 1){
//			System.out.println("Database già creato");
//		}
	}

}
