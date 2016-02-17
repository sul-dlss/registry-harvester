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
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import java.lang.Runtime;
import java.lang.Process;

import java.sql.Connection;

import java.text.SimpleDateFormat;
import org.apache.commons.io.IOUtils;

import com.microsoft.sqlserver.jdbc.*;

public class Pop2ILLiad {

    public static void main (String [] args) throws Exception {

        Date today = new Date();
        Date expiry = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_ill = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        Map profiles = GetProfiles.profiles();
        Map departments = PickDepartment.dept();

        try
        {
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

            SQLServerDataSource s7z = new SQLServerDataSource();
            s7z.setUser("S7Z" + user);
            s7z.setPassword("S7Z" + pass);
            s7z.setServerName(server);
            s7z.setPortNumber(1433);
            s7z.setDatabaseName("ILLData");

            SQLServerDataSource rcj = new SQLServerDataSource();
            rcj.setUser("RCJ" + user);
            rcj.setPassword("RCJ" + pass);
            rcj.setServerName(server);
            rcj.setPortNumber(1433);
            rcj.setDatabaseName("ILLData");

            Map <String, String> illData = new LinkedHashMap <String, String>();

            String userkey;
            String result = "";

            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

            String sqlST2 = "";
            String sqlS7Z = "";
            String sqlRCJ = "";

            //For each userkey in the userload.keys file
            //
            while ((userkey = br.readLine()) != null) {
                Process p1 = Runtime.getRuntime().exec(new String[] { "echo", userkey });
                InputStream input = p1.getInputStream();

                Process p2 = Runtime.getRuntime().exec(new String[] { "seluser", "-iU", "-oxDBpX.9007.X.9036.X.9032.Y.9032.e" });
                OutputStream output = p2.getOutputStream();

                IOUtils.copy(input, output);
                output.close();

                result = IOUtils.toString(p2.getInputStream(), "UTF-8");

                String sunetid = ""; //0 x
                String last = ""; //1 D
                String first = ""; //1 D
                String barcode = ""; //2 B
                String profile = ""; //3 p
                String email = ""; //4 X.9007.
                String phone = ""; //5 X.9036.
                String department = ""; //6 X.9032.
                String nvtgc = ""; //7 Y.9032.
                String expiration = ""; //8 e

                String NVTGC = "";
                String DEPT = "";
                String status = "";
                String organization  = "";
                String fullName = "";

                try
                {
                    String [] userFields = result.toString().split("\\|");
                    sunetid = userFields[0];

                    fullName = userFields[1].replace("'", "''");

                    String [] splitname = fullName.split(",");
                    last = splitname[0].trim();
                    first = splitname[1].trim();

                    barcode = userFields[2];
                    profile = userFields[3];
                    email = userFields[4];
                    phone = userFields[5];
                    department = userFields[6];
                    nvtgc = userFields[7];
                    expiration = userFields[8];

                    if (nvtgc.indexOf("gsb") > 0 || department.indexOf("School of Business") > 0) {
                      NVTGC = "S7Z";
                      organization = "GSB";
                    }
                    else if (nvtgc.indexOf("law") > 0 || department.indexOf("Law School") > 0) {
                      NVTGC = "RCJ";
                      organization = "SLS";
                    }
                    else if (nvtgc.indexOf("medicine") > 0 || department.indexOf("School of Medicine") > 0) {
                      NVTGC = "ST2";
                      organization = "MED";
                    }
                    else {
                      NVTGC = "ST2";
                      organization = "SUL";
                    }

                    if (expiration.equals("0")) {
                      expiration = "99990101";
                    }
                    expiry = sdf.parse(expiration);

                    //Get the patron Status based on the GetProfiles map
                    //
                    @SuppressWarnings("unchecked")
                    Iterator<Map.Entry<String, String>> stats = profiles.entrySet().iterator();
                    while (stats.hasNext()) {
                      Map.Entry<String, String> p = (Map.Entry<String, String>)stats.next();
                      if (p.getKey().equals(profile)) {
                        status = p.getValue();
                        break;
                      }
                    }

                    if (status.length() == 0) {
                      status = "Affiliate";
                    }

                    //Get the department from the @adminid and PickDepartment
                    // or default to scanning entire dept-codes list.
                    @SuppressWarnings("unchecked")
                    Iterator<Map.Entry<String, String>> depts = departments.entrySet().iterator();
                    while (depts.hasNext()) {
                      Map.Entry<String, String> d = (Map.Entry<String, String>)depts.next();
                      String key = d.getKey();
                      if (department.length() > 0) {
                        if (key.equals(department)) {
                          DEPT = d.getValue();
                          break;
                        }
                        else if (key.equals(department.substring(0,2))) {
                          DEPT = d.getValue();
                          break;
                        }
                        else if (key.equals(department.substring(0,1))) {
                          DEPT = d.getValue();
                          break;
                        }
                      }
                    }

                    if (DEPT.length() < 1) {
                        Scanner sc = new Scanner(new File("../include/dept_codes"));

                        while (sc.hasNextLine()) {
                            String str = sc.findInLine(Pattern.compile(department + ".+"));

                            if (str != null) {
                                DEPT = str.split("--")[1].replace("'","''");
                            }
                            sc.nextLine();
                        }
                    }
                }
                catch (java.lang.ArrayIndexOutOfBoundsException a)
                {}

                if (sunetid != null && sunetid.matches("\\w+")) {
                    illData.clear();

                    illData.put("UserName", "'" + sunetid + "'"); //50 *
                    illData.put("LastName", "'" + last + "'"); //40 *
                    illData.put("FirstName", "'" + first + "'"); //40 *
                    illData.put("SSN", "'" + barcode + "'"); //20
                    illData.put("Status", "'" + status + "'"); //15
                    illData.put("EMailAddress", "'" + email + "'"); //50 *
                    illData.put("Phone", "'" + phone + "'"); //15 *
                    illData.put("MobilePhone", "'NULL'"); //15
                    illData.put("Department", "'" + DEPT + "'"); //255
                    illData.put("NVTGC", "'" + NVTGC + "'"); //20 *
                    illData.put("Password", "''"); //64
                    illData.put("NotificationMethod", "'Electronic'"); //8
                    illData.put("DeliveryMethod", "'Hold for Pickup'"); //25
                    illData.put("LoanDeliveryMethod", "'NULL'"); //25
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
                    illData.put("ExpirationDate", "'" + sdf_ill.format(expiry) + "'");
                    illData.put("Number", "NULL"); //
                    illData.put("UserRequestLimit", "NULL");
                    illData.put("Organization", "'" + organization + "'"); //
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

                    if (NVTGC.equals("ST2")){
                      sqlST2 += GetTransactSQL.transactSql(illData, sunetid) + "\n\r";
                    }
                    if (NVTGC.equals("S7Z")){
                      sqlS7Z += GetTransactSQL.transactSql(illData, sunetid) + "\n\r";
                    }
                    if (NVTGC.equals("RCJ")){
                      sqlRCJ += GetTransactSQL.transactSql(illData, sunetid) + "\n\r";
                    }
                }
            }

            Connection st2Conn = st2.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), st2Conn);
            ConnectToILLiad.connect(sqlST2, st2Conn);
            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), st2Conn);
            st2Conn.close();

            Connection s7zConn = st2.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), s7zConn);
            ConnectToILLiad.connect(sqlS7Z, s7zConn);
            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), s7zConn);
            s7zConn.close();

            Connection rcjConn = rcj.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), rcjConn);
            ConnectToILLiad.connect(sqlRCJ, rcjConn);
            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), rcjConn);
            rcjConn.close();
        }
        catch (Exception e) {
            System.err.println("Pop2ILLiad: " + e.getMessage());
        }
    }
}
