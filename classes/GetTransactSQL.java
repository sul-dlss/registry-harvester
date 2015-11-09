import java.util.Map;
import java.util.LinkedHashMap;

import java.util.Properties;

public class GetTransactSQL {

  public static String transactBegin(){
    return "BEGIN TRAN\n\r";
  }

  public static String transactCommit(){
    return "COMMIT TRAN\n\r-----------";
  }

  public static String transactSql (Map<String, String> illData, String sunetid) throws Exception {

    Properties props = PropGet.getProps("../conf/server.conf");
    String table_name = props.getProperty("TABLE_NAME");
    String do_not_update_field = props.getProperty("NO_UPDATE");

    String sql = "";
    String sqlv = "";

    sql += " declare @dept_" + sunetid + " varchar(50)\n\r";
    sql += " IF EXISTS (select * from ILLData.dbo." + table_name + " where UserName = '" + sunetid + "')\n\r";
    sql += " BEGIN\n\r";
    sql += "  SET @dept_" + sunetid + " = (select Department from ILLData.dbo.UsersALL where UserName = '" + sunetid + "')\n\r";
    sql += "  UPDATE ILLData.dbo." + table_name + "\n\r";
    sql += "  SET\n\r";

    int cnt = 1;
    for (Map.Entry<String, String> entry : illData.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      /* Keep the same ignore_fields as previously loaded and update the rest with new values */
      if (key.indexOf(do_not_update_field) > 0) {

        sql += key + "= @dept_" + sunetid;
      }
      else {
        sql += key + "=" + value;
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
