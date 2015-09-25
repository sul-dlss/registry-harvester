import java.util.Map;
import java.util.LinkedHashMap;

public class GetTransactSQL {

  public static String transactSQL (Map<String, String> illData, String sunetid) throws Exception {

    String sql = "";
    String sqlv = "";
    String table_name = "";

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
    sql += "COMMIT TRAN";

    System.err.println(sqlv + "\n-----------");

    return sql;
  }
}
