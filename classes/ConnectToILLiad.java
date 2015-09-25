import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToILLiad {

    public static void connect (String NVTGC, String transactSQL) {

        try {

            Properties props = PropGet.getProps("../conf/server.conf");
            String user = props.getProperty("USER");
            String pass = props.getProperty("PASS");
            String server = props.getProperty("SERVER");
            String domain_port = props.getProperty("DOMAIN_PORT");

            String url = server + "." + domain_port;

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(url + ";user=" + user + ";password=" + pass);

            Statement stmt = connection.createStatement();

            stmt.executeUpdate(transactSQL);
            connection.close();
        }
        catch (Exception e) {

            System.err.println(e.getMessage());
            System.err.println(NVTGC + "\n-----------");
        }
    }
}
