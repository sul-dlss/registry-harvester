import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.Properties;

import java.lang.Runtime;
import java.lang.Process;

import java.sql.Connection;

import java.text.SimpleDateFormat;
import org.apache.commons.io.IOUtils;

import com.microsoft.sqlserver.jdbc.*;

public class Pop2ILLiad {

    public static Map <String, String> profiles = new LinkedHashMap <String, String>();

    public static void main (String [] args) throws Exception {

        Process proc;
        Date today = new Date();
        SimpleDateFormat sdf_ill = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        GetProfiles.profiles(profiles);

        try {

            Properties props = PropGet.getProps("../conf/server.conf");
            String user = props.getProperty("USER");
            String pass = props.getProperty("PASS");
            String server = props.getProperty("SERVER");

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");

            SQLServerDataSource st2 = new SQLServerDataSource();
            st2.setUser("ST2" + user);
            st2.setPassword("ST2" + pass);
            st2.setServerName(server);
            st2.setPortNumber(1433);
            st2.setDatabaseName("ILLData");
            Connection st2Conn = st2.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), st2Conn);

            SQLServerDataSource s7z = new SQLServerDataSource();
            s7z.setUser("S7Z" + user);
            s7z.setPassword("S7Z" + pass);
            s7z.setServerName(server);
            s7z.setPortNumber(1433);
            s7z.setDatabaseName("ILLData");
            Connection s7zConn = s7z.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), s7zConn);

            SQLServerDataSource rcj = new SQLServerDataSource();
            rcj.setUser("RCJ" + user);
            rcj.setPassword("RCJ" + pass);
            rcj.setServerName(server);
            rcj.setPortNumber(1433);
            rcj.setDatabaseName("ILLData");
            Connection rcjConn = rcj.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), rcjConn);

            Map <String, String> illData = new LinkedHashMap <String, String>();

            String userkey;
            String result = "";

            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

            while ((userkey = br.readLine()) != null)
            {

                Process p1 = Runtime.getRuntime().exec(new String[] { "echo", userkey });
                InputStream input = p1.getInputStream();

                Process p2 = Runtime.getRuntime().exec(new String[] { "seluser", "-iU", "-oxDBpX.9007.X.9036.X.9032.Y.9032.X.9002." });
                OutputStream output = p2.getOutputStream();

                IOUtils.copy(input, output);
                output.close();

                result = IOUtils.toString(p2.getInputStream(), "UTF-8");
                System.out.println(result);

                String sunetid = ""; //0 x
                String last = ""; //1 D
                String first = ""; //1 D
                String barcode = ""; //2 B
                String profile = ""; //3 p
                String email = ""; //4 X.9007.
                String phone = ""; //5 X.9036.
                String department = ""; //6 X.9032.
                String nvtgc = ""; //7 Y.9032.

                String NVTGC = "";
                String status = "";
                String fullName = "";

                try {

                    String [] userFields = result.toString().split("\\|");
                    sunetid = userFields[0];

                    fullName = userFields[1].replace("'", "''");

                    String [] splitname = fullName.split(",");
                    last = splitname[0];
                    first = splitname[1];

                    barcode = userFields[2];
                    profile = userFields[3];
                    email = userFields[4];
                    phone = userFields[5];
                    department = userFields[6];
                    nvtgc = userFields[7];

                    if (nvtgc.indexOf("gsb") > 0)
                    {
                        NVTGC = "S7Z";
                    }
                    else if (nvtgc.indexOf("law") > 0)
                    {
                        NVTGC = "RCJ";
                    }
                    else
                    {
                        NVTGC = "ST2";
                    }

                    if (department == null || department.length() < 1)
                    {
                        department = "Affiliate";
                    }

                    for (Map.Entry<String, String> entry : profiles.entrySet()) {
                        if (entry.getValue().equals(profile))
                        {
                            status = entry.getKey();
                        }
                    }

                    if (status.length() == 0)
                    {
                        status = "Affiliate";
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException a)
                {}

                if (sunetid != null && sunetid.matches("\\w+"))
                {
                    illData.clear();

                    illData.put("UserName", "'" + sunetid + "'"); //50 *
                    illData.put("LastName", "'" + last + "'"); //40 *
                    illData.put("FirstName", "'" + first + "'"); //40 *
                    illData.put("SSN", "'" + barcode + "'"); //20
                    illData.put("Status", "'" + status + "'"); //15`
                    illData.put("EMailAddress", "'" + email + "'"); //50 *
                    illData.put("Phone", "'" + phone + "'"); //15 *
                    illData.put("MobilePhone", "'NULL'"); //15
                    illData.put("Department", "'" + department + "'"); //255
                    illData.put("NVTGC", "'" + NVTGC + "'"); //20 *
                    illData.put("Password", "''"); //64
                    illData.put("NotificationMethod", "'Electronic'"); //8
                    illData.put("DeliveryMethod", "'Pickup'"); //25
                    illData.put("LoanDeliveryMethod", "'Hold for Pickup'"); //25
                    illData.put("LastChangedDate", "'" + sdf_ill.format(today) + "'");
                    illData.put("AuthorizedUsers", "'SUL'"); //255
                    illData.put("Cleared", "'Yes'");
                    illData.put("Web", "'Yes'"); //3
                    illData.put("Address", "''"); //40
                    illData.put("Address2", "''"); //40
                    illData.put("City", "''"); //30
                    illData.put("State", "''"); //2
                    illData.put("Zip", "''"); //10
                    illData.put("Site", "'SUL'"); //40
                    illData.put("ExpirationDate", "NULL");
                    illData.put("Number", "NULL"); //
                    illData.put("UserRequestLimit", "NULL");
                    illData.put("Organization", "'NULL'"); //
                    illData.put("Fax", "NULL"); //
                    illData.put("ShippingAcctNo", "NULL");
                    illData.put("ArticleBillingCategory", "NULL"); //
                    illData.put("LoanBillingCategory", "NULL"); //
                    illData.put("Country", "NULL"); //
                    illData.put("SAddress", "NULL"); //
                    illData.put("SAddress2", "NULL"); //
                    illData.put("SCity", "NULL"); //
                    illData.put("SState", "NULL"); //
                    illData.put("SZip", "NULL"); //
                    illData.put("PasswordHint", "NULL"); //
                    illData.put("SCountry", "NULL"); //
                    illData.put("RSSID", "NULL");
                    illData.put("AuthType", "'ILLiad'");
                    illData.put("UserInfo1", "NULL"); //
                    illData.put("UserInfo2", "NULL"); //
                    illData.put("UserInfo3", "NULL"); //
                    illData.put("UserInfo4", "NULL"); //
                    illData.put("UserInfo5", "NULL"); //

                    System.out.println(illData.size());
                    String sql = GetTransactSQL.transactSql(illData, sunetid);
                    System.out.println("Sql:\n" + sql);

                    if (NVTGC.equals("ST2")){
                        ConnectToILLiad.connect(sql, st2Conn);
                    }
                    if (NVTGC.equals("S7Z")){
                        ConnectToILLiad.connect(sql, s7zConn);
                    }
                    if (NVTGC.equals("RCJ")){
                        ConnectToILLiad.connect(sql, rcjConn);
                    }
                }
            }

            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), st2Conn);
            st2Conn.close();

            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), s7zConn);
            s7zConn.close();

            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), rcjConn);
            rcjConn.close();
        }
        catch (Exception e)
        {
            System.err.println("Pop2ILLiad: " + e.getMessage());
        }
    }
}
