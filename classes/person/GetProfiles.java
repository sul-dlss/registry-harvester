import java.util.Map;
import java.util.LinkedHashMap;

public class GetProfiles {

  public static Map <String, String> profiles () throws Exception {

    Map <String, String> profiles = new LinkedHashMap <String, String>();

    profiles.put("ARTU", "Undergraduate");
    profiles.put("CNAC", "Academic Staff");
    profiles.put("CNF", "Faculty");
    profiles.put("CNS", "Staff");
    profiles.put("MXAC", "Academic Staff");
    profiles.put("MXAS", "Affiliate");
    profiles.put("MXD", "Post Doctoral");
    profiles.put("MXF", "Faculty");
    profiles.put("MXS", "Staff");
    profiles.put("MXFEE", "Fee Borrower");
    profiles.put("RED", "Post Doctoral");
    profiles.put("REG", "Graduate");
    profiles.put("REG-SUM", "Graduate");
    profiles.put("REU", "Undergraduate");
    profiles.put("REU-SUM", "Undergraduate");
    profiles.put("HMSU", "Sponsored");
    profiles.put("HMSG", "Graduate");
    profiles.put("HMSD", "Visiting Schol");
    profiles.put("HMSS", "Staff");
    profiles.put("HMSAC", "Visiting Schol");
    profiles.put("HMSF", "Faculty");

    return profiles;
  }
}
