import java.util.Map;
import java.util.LinkedHashMap;

public class GetProfiles {

  public static Map <String, String> profiles (Map profiles) throws Exception {

    profiles.put("Affiliate", "MXFEE");
    profiles.put("Affiliate", "MXAS");
    profiles.put("Student", "HMSU");
    profiles.put("Student", "REU");
    profiles.put("Student", "HMSG");
    profiles.put("Graduate Student", "REG");
    profiles.put("Graduate Student", "HMSD");
    profiles.put("Graduate Student", "RED");
    profiles.put("Affiliate", "MXS");
    profiles.put("Staff", "HMSS");
    profiles.put("Staff", "CNS");
    profiles.put("Academic Staff", "HMSAC");
    profiles.put("Affiliate", "MXAC");
    profiles.put("Affiliate", "CNAC");
    profiles.put("Faculty", "HMSF");
    profiles.put("Faculty", "MXF");
    profiles.put("Faculty", "CNF");

    return profiles;
  }
}
