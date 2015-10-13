import java.util.Map;
import java.util.LinkedHashMap;

public class GetProfiles {

  public static Map <String, String> profiles (Map profiles) throws Exception {

    profiles.put("Student", "ARTU");
    profiles.put("Academic Staff", "CNAC");
    profiles.put("Faculty", "CNF");
    profiles.put("Staff", "CNS");
    profiles.put("Academic Staff", "MXAC");
    profiles.put("Affiliate", "MXAS");
    profiles.put("Staff", "MXD");
    profiles.put("Faculty", "MXF");
    profiles.put("Staff", "MXS");
    profiles.put("Fee Borrower", "MXFEE");
    profiles.put("Doctoral Student", "RED");
    profiles.put("Graduate Student", "REG");
    profiles.put("Graduate Student", "REG-SUM");
    profiles.put("Student", "REU");
    profiles.put("Student", "REU-SUM");
    profiles.put("Sponsored", "HMSU");
    profiles.put("Graduate Student", "HMSG");
    profiles.put("Visiting Scholar", "HMSD");
    profiles.put("Staff", "HMSS");
    profiles.put("Visiting Scholar", "HMSAC");
    profiles.put("Faculty", "HMSF");

    return profiles;
  }
}
