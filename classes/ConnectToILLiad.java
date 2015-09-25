import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToILLiad {

    public static void connect (String NVTGC, String transactSQL) {

        try {

            String is_prod = System.getenv("IS_PRODUCTION");
            String host = "";
            if (is_prod == null || !is_prod.equals("yes")) {
                host = "-test";
            }
            String url = "jdbc:sqlserver://sul-illiad"+host+".stanford.edu:1433";
            String user = NVTGC;
            String pass = NVTGC + "Password";

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
