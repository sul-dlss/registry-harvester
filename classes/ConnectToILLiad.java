//import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import java.util.Properties;

public class ConnectToILLiad {

    public static void connect (String transactSQL, Connection connection) throws SQLException {

      try {

        Statement stmt = connection.createStatement();

        stmt.executeUpdate(transactSQL);
        connection.close();
      }
      catch (SQLException s) {
        System.err.println(s.getSQLState() + " ERROR\n-----------");
      }
  }
}
