import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Prova {

	public static void main(String[] args) {
		Connection connection = this.dataSource.getConnection("jdbc:postgresql://localhost:5432/");
		String stm = "SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('dbname')";
		PreparedStatement ps = connection.prepareStatement(stm);
		ResultSet rs = ps.executeQuery();
		if(rs.next() && rs.getInt(1) == 1){
			System.out.println("Database già creato");
		}
	}

}
