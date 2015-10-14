import java.util.Map;
import java.util.LinkedHashMap;

public class GetProfiles {

  public static Map <String, String> profiles (Map profiles) throws Exception {

    profiles.put("ARTU", "Student");
    profiles.put("CNAC", "Academic Staff");
    profiles.put("CNF", "Faculty");
    profiles.put("CNS", "Staff");
    profiles.put("MXAC", "Academic Staff");
    profiles.put("MXAS", "Affiliate");
    profiles.put("MXD", "Staff");
    profiles.put("MXF", "Faculty");
    profiles.put("MXS", "Staff");
    profiles.put("MXFEE", "Fee Borrower");
    profiles.put("RED", "Doctoral Student");
    profiles.put("REG", "Graduate Student");
    profiles.put("REG-SUM", "Graduate Student");
    profiles.put("REU", "Student");
    profiles.put("REU-SUM", "Student");
    profiles.put("HMSU", "Sponsored");
    profiles.put("HMSG", "Graduate Student");
    profiles.put("HMSD", "Visiting Scholar");
    profiles.put("HMSS", "Staff");
    profiles.put("HMSAC", "Visiting Scholar");
    profiles.put("HMSF", "Faculty");

    return profiles;
  }
}
