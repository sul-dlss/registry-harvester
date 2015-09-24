import java.util.Map;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectToILLiad {

    public static void connect (String sunetid, String NVTGC, Map<String, String> illData) {

        String sql = "";
        String table_name = "";

        try {

            String is_prod = System.getenv("IS_PRODUCTION");
            String host = "";
            if (is_prod == null || !is_prod.equals("yes")) {
                host = "-test";
            }
            String url = "";
            String user = "";
            String pass = "";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(url + ";user=" + user + ";password=" + pass);

            Statement stmt = connection.createStatement();

            sql = "BEGIN TRAN\n\r";
            sql += " IF EXISTS (select * from ILLData.dbo." + table_name + " where UserName = '" + sunetid + "')\n\r";
            sql += " BEGIN\n\r";
            sql += "  UPDATE ILLData.dbo." + table_name + "\n\r";
            sql += "  SET\n\r";

            int cnt = 1;
            for (Map.Entry<String, String> entry : illData.entrySet()) {
                sql += entry.getKey() + "=" + entry.getValue();

                if (cnt < illData.size()) {
                    sql += ",";
                }
                sql += "\n\r";
                cnt++;
            }

            sql += "   WHERE UserName = '" + sunetid + "'\n\r";
            sql += "  END\n\r";
            sql += " ELSE\n\r";
            sql += " BEGIN\n\r";
            sql += "  INSERT INTO ILLData.dbo." + table_name + "\n\r";
            sql += "  (";

            cnt=1;
            for (Map.Entry<String, String> entry : illData.entrySet()) {
                sql += entry.getKey();

                if (cnt < illData.size()) {
                    sql += ", ";
                }
                cnt++;
            }

            sql += ")\n\r";
            sql += " VALUES\n\r";
            sql += "(";

            cnt=1;
            for (Map.Entry<String, String> entry : illData.entrySet()) {
                sql += entry.getValue();

                if (cnt < illData.size()) {
                    sql += ",";
                }
                cnt++;
            }

            sql += ")\n\r";
            sql += "  END\n\r";
            sql += "COMMIT TRAN";

            System.out.println(sql + "\n--------------------");
            
            stmt.executeUpdate(sql);
            connection.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(sunetid + "\n--------------------");
        }
    }
}
