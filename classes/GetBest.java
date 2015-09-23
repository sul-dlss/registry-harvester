import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.SimpleDateFormat;

public class GetBest {

  public static Date date = new Date();
  public static Date today = new Date();

  public static String [] userProfile (ArrayList policies, Map<String, Date> profiles) {

    String [] finalProfile = new String[2];

    SimpleDateFormat dfmat = new SimpleDateFormat("yyyyMMdd");

    int rank = -1;
    int tempRank = -1;

    for (Map.Entry<String, Date> entry : profiles.entrySet()) {

      String profile = entry.getKey();
      date = entry.getValue();

      //System.out.println("profile-->" + profile +   " until-->" + date);

      if (date.compareTo(today) > 0) {
        tempRank = policies.indexOf(profile);
      }

      if (tempRank > rank) {
        rank = tempRank;
      }
    }

    if (rank > -1) {
      String profileStr = (String) policies.get(rank);
      finalProfile[0] = profileStr;
      finalProfile[1] = dfmat.format(profiles.get(profileStr));

      if (finalProfile[1].equals("29990909")) {
        finalProfile[1] = "KEEP";
      }

      if (finalProfile[1].equals("19990909")) {
        finalProfile[1] = "EXPIRED";
      }

      if (finalProfile[1].equals("99990909")) {
        finalProfile[1] = "NEVER";
      }
    }

    return finalProfile;
  }
}
