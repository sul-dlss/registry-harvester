package edu.stanford;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class Pop2ILLiad {

    private static final String EMAIL_PATTERN =
    "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    public static void main (String [] args) throws Exception {

        Date today = new Date();
        Date expiry;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_ill = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

        Map profiles = GetProfiles.profiles();

        try
        {
            Properties props = new Properties();
            props.load(new FileInputStream("Person/src/main/resources/server.conf"));
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

            // SQLServerDataSource rcj = new SQLServerDataSource();
            // rcj.setUser("RCJ" + user);
            // rcj.setPassword("RCJ" + pass);
            // rcj.setServerName(server);
            // rcj.setPortNumber(1433);
            // rcj.setDatabaseName("ILLData");

            Map <String, String> illData = new LinkedHashMap<>();

            String userKey;
            String result;

            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

            String inputType = args[1];

            StringBuilder sqlST2 = new StringBuilder();
            StringBuilder sqlS7Z = new StringBuilder();
            // StringBuilder sqlRCJ = new StringBuilder();

            String selFlag = "U";

            if (inputType != null && inputType.contains("sunet")) {
                selFlag = "x";
            }

            //For each userkey in the userload.keys file
            //
            while ((userKey = br.readLine()) != null) {

                Process p1 = Runtime.getRuntime().exec(new String[] { "echo", userKey.toLowerCase() });
                InputStream input = p1.getInputStream();

                Process p2 = Runtime.getRuntime().exec(new String[] { "seluser", "-i" + selFlag, "-oxDBpX.9007.Z.9007.X.9036.X.9032.Y.9032.eE" });
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
                String email_alt = ""; //5 Z.9007.
                String phone = ""; //6 X.9036.
                String department = ""; //7 X.9032.
                String nvtgc = ""; //8 Y.9032.
                String expiration = ""; //9 e
                String univid = ""; //10

                // NOTE: "nvtgc" is the code for the user's ILLiad instance that comes out of the registry userload
                // and writen into the symphony user record in a custom user field as "gsb", "law", or "medicine" based
                // on the user's privgroup statuses (see <xsl:template match="privgroup"> in xslt/library_patron.xsl).
                // "NVTGC" is the code that gets set in the ILLiad user record via ILLData.put below...

                String NVTGC = "";
                String organization = "";
                String fullName = "";
                String firstName = "";
                String status = "";
                boolean hasEmail = false;
                boolean hasAltEmail = false;

                int firstIndex;

                try {
                    String[] userFields = result.split("\\|");
                    sunetid = userFields[0];

                    fullName = userFields[1].replace("'", "''");

                    String[] splitname = fullName.split(",");
                    last = splitname[0].trim();
                    first = splitname[1].trim();

                    if (first.indexOf(" ") > 0) {
                        firstIndex = first.indexOf(" ");
                    } else {
                        firstIndex = first.length();
                    }
                    firstName = first.substring(0, firstIndex);

                    barcode = userFields[2];
                    profile = userFields[3];
                    email = userFields[4];
                    email_alt = userFields[5];
                    phone = userFields[6];
                    department = userFields[7];
                    nvtgc = userFields[8];
                    expiration = userFields[9];
                    univid = userFields[10];

                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Pop2ILLiad: " + e.getMessage());
                }

                if (nvtgc.indexOf("gsb") > 0) {
                  NVTGC = "S7Z";
                  organization = "GSB";
                }
                // else if (nvtgc.indexOf("law") > 0) {
                //   NVTGC = "RCJ";
                //   organization = "SLS";
                // }
                else if (nvtgc.indexOf("medicine") > 0) {
                  NVTGC = "ST2";
                  organization = "MED";
                }
                else {
                  NVTGC = "ST2";
                  organization = "SUL";
                }

                if (expiration.length() == 0) {
                    expiration = sdf.format(today);
                }
                else if (expiration.equals("0")) {
                  expiration = "99990101";
                }

                if (email != null && email.matches(EMAIL_PATTERN)) {
                    hasEmail = true;
                }
                else if (email_alt != null && email_alt.matches(EMAIL_PATTERN)) {
                    hasAltEmail = true;
                }

                expiry = sdf.parse(expiration);

                //Get the patron Status based on the GetProfiles map
                //
                for (Map.Entry<String, String> p : (Iterable<Map.Entry<String, String>>) profiles.entrySet()) {
                    if (p.getKey().equals(profile)) {
                        status = p.getValue();
                        break;
                    }
                }

                if (status == null || status.length() == 0) {
                  status = "Affiliate";
                }

                if (organization.equals("MED")) {
                    status = "MED " + status;
                }

                if (department.length() > 0) {
                  department = department.replace("&", "and");
                  department = department.replace("'","''");
                }

                if ( !hasEmail && !hasAltEmail ) {
                    System.out.println("SKIPPING: " + sunetid + " [no email address in registry]");
                }

                if (!expiry.after(today)) {
										System.out.println("SKIPPING: " + sunetid
											+ " [expiry is not after today:" + sdf_ill.format(today)
											+ ", expiry:" + sdf_ill.format(expiry) + "]");
								}

                if ((sunetid != null && sunetid.matches("\\w+"))
                && (hasEmail || hasAltEmail)
                && expiry.after(today)) {
                    illData.clear();
                    illData.put("UserName", "'" + sunetid + "'"); //50 *
                    illData.put("LastName", "'" + last + "'"); //40 *
                    illData.put("FirstName", "'" + firstName + "'"); //40 *
                    illData.put("SSN", "'" + barcode + "'"); //20
                    illData.put("Status", "'" + status + "'"); //15
                    if (hasEmail) {
                        illData.put("EMailAddress", "'" + email + "'"); //50 *
                    } else if (hasAltEmail) {
                        illData.put("EMailAddress", "'" + email_alt + "'"); //50 *
                    }
                    illData.put("Phone", "'" + phone + "'"); //15 *
                    illData.put("MobilePhone", "'NULL'"); //15
                    illData.put("Department", "'" + department + "'"); //255
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
                    illData.put("AuthType", "'RemoteAuth'");
                    illData.put("UserInfo1", "'" + univid + "'"); //
                    illData.put("UserInfo2", "NULL"); //
                    illData.put("UserInfo3", "NULL"); //
                    illData.put("UserInfo4", "NULL"); //
                    illData.put("UserInfo5", "NULL"); //

                    if (NVTGC.equals("ST2")){
                      sqlST2.append(GetTransactSQL.transactSql(illData, sunetid)).append("\n\r");
                    }
                    if (NVTGC.equals("S7Z")){
                      sqlS7Z.append(GetTransactSQL.transactSql(illData, sunetid)).append("\n\r");
                    }
                    // if (NVTGC.equals("RCJ")){
                    //   sqlRCJ.append(GetTransactSQL.transactSql(illData, sunetid)).append("\n\r");
                    // }
                }
            }

            Connection st2Conn = st2.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), st2Conn);
            ConnectToILLiad.connect(sqlST2.toString(), st2Conn);
            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), st2Conn);
            st2Conn.close();

            Connection s7zConn = st2.getConnection();
            ConnectToILLiad.connect(GetTransactSQL.transactBegin(), s7zConn);
            ConnectToILLiad.connect(sqlS7Z.toString(), s7zConn);
            ConnectToILLiad.connect(GetTransactSQL.transactCommit(), s7zConn);
            s7zConn.close();

            // Connection rcjConn = rcj.getConnection();
            // ConnectToILLiad.connect(GetTransactSQL.transactBegin(), rcjConn);
            // ConnectToILLiad.connect(sqlRCJ.toString(), rcjConn);
            // ConnectToILLiad.connect(GetTransactSQL.transactCommit(), rcjConn);
            // rcjConn.close();
        }
        catch (Exception e) {
            System.err.println("Pop2ILLiad: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
