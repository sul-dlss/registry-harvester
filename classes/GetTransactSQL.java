import java.util.Map;
import java.util.LinkedHashMap;

import java.util.Properties;

public class GetTransactSQL {

  public static String transactBegin(){
    return "\n\rBEGIN TRAN\n\r";
  }

  public static String transactCommit(){
    return "COMMIT TRAN\n\r-----------";
  }

  public static String transactSql (Map<String, String> illData, String sunetid) throws Exception {

    Properties props = PropGet.getProps("../conf/server.conf");
    String table_name = props.getProperty("TABLE_NAME");

    String sql = "";
    String sqlv = "";

    sql += " declare @status varchar(20)\n\r";
    sql += " declare @org varchar(10)\n\r";
    sql += " IF EXISTS (select * from ILLData.dbo." + table_name + " where UserName = '" + sunetid + "')\n\r";
    sql += " BEGIN\n\r";
    sql += "  SET @status = (select Status from ILLData.dbo." + table_name +" where UserName = '" + sunetid + "')\n\r";
    sql += "  SET @org = (select Organization from ILLData.dbo." + table_name +" where UserName = '" + sunetid + "')\n\r";
    sql += "  UPDATE ILLData.dbo." + table_name + "\n\r";
    sql += "  SET\n\r";

    int cnt = 1;
    for (Map.Entry<String, String> entry : illData.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      /* Keep the same status and organization as before and update the rest with new values */
      if (key.equals("Status")) {
        sql += entry.getKey() + "= @status";
      }
      else if (key.equals("Organization")) {
        sql += entry.getKey() + "= @org";
      }
      else {
        sql += entry.getKey() + "=" + entry.getValue();
      }

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

    System.err.println(sqlv + "\n-----------");

    return sql;
  }
}
