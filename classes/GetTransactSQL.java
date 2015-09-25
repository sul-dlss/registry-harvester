import java.util.Map;
import java.util.LinkedHashMap;

import java.util.Properties;

public class GetTransactSQL {

  public static String transactSql (Map<String, String> illData, String sunetid) throws Exception {

    Properties props = PropGet.getProps("../conf/server.conf");
    String table_name = props.getProperty("TABLE_NAME");

    String sql = "\n\rBEGIN TRAN\n\r";
    String sqlv = "";

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
    sqlv += " VALUES\n\r";
    sqlv += "(";

    cnt=1;
    for (Map.Entry<String, String> entry : illData.entrySet()) {
      sqlv += entry.getValue();

      if (cnt < illData.size()) {
        sqlv += ",";
      }
      cnt++;
    }

    sqlv += ")\n\r";
    sql += sqlv;
    sql += "  END\n\r";
    sql += "COMMIT TRAN\n\r-----------";

    System.err.println(sqlv + "\n-----------");

    return sql;
  }
}
