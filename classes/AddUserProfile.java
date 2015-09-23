import java.util.Iterator;
import java.util.Arrays;
import org.jdom2.Element;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;

public class AddUserProfile {

  public static Map profiles (Map<String, Date> profiles, Date untilDate, int position, String type, String locationCode,
                              List affdatas, List privgroups, String description) throws Exception {

    String [] MXAS = {"VOS","VO","STS", "VSCP", "SFS", "RFK"};
    String [] MXAC = {"CA","TA", "VA", "SO"};
    String [] CNS = {"SN"};
    String [] CNF = {"FA"};
    String [] CNAC = {"SA","EFEL"};
    String [] MXF = {"VF","CF","BOT","BF"};
    String [] MXS = {"PA","HS","HHMI","CARNEGIE","AF"};
    String [] REG = {"MLS","CS"};
    String [] MXFEE = {"LIBBO-LAW"};
    String [] LIBRJCT = {"LIBBO-LANE","LIBBO-GSB"};
    String [] KEEP = {"LIBBO","LIBBO-SUL"};

    String affdataCode = "";

    if (type.indexOf("affiliate") == 0) {
      Iterator <Element> affdatasIterator = affdatas.iterator();

      while (affdatasIterator.hasNext()) {
        Element affdata = (Element) affdatasIterator.next();

        if (affdata != null) {
          affdataCode = affdata.getAttributeValue("code");

          if (affdataCode != null) {
            if (Arrays.asList(MXAS).contains(affdataCode)) {
              if (!profiles.containsKey("MXAS")) profiles.put("MXAS", untilDate);
            }
            if (Arrays.asList(MXAC).contains(affdataCode)) {
              if (!profiles.containsKey("MXAC")) profiles.put("MXAC", untilDate);
            }
            if (Arrays.asList(CNS).contains(affdataCode)) {
              if (!profiles.containsKey("CNS")) profiles.put("CNS", untilDate);
            }
            if (position == 2 && type.indexOf("staff:retired") > -1) {
              if (!profiles.containsKey("CNS")) {
                profiles.put("CNS", untilDate);
              }
            }
            if (Arrays.asList(CNF).contains(affdataCode)) {
              if (!profiles.containsKey("CNF")) {
                profiles.put("CNF", untilDate);
              }
            }
            if (position == 2 && type.indexOf("faculty:retired") > -1) {
              if (!profiles.containsKey("CNF")) {
                profiles.put("CNF", untilDate);
              }
            }
            if (Arrays.asList(CNAC).contains(affdataCode)) {
              if (!profiles.containsKey("CNAC")) {
                profiles.put("CNAC", untilDate);
              }
            }
            if (Arrays.asList(MXF).contains(affdataCode)) {
              if (!profiles.containsKey("MXF")) {
                profiles.put("MXF", untilDate);
              }
            }
            if (Arrays.asList(MXS).contains(affdataCode)) {
              if (!profiles.containsKey("MXS")) {
                profiles.put("MXS", untilDate);
              }
            }
            if (Arrays.asList(REG).contains(affdataCode)) {
              if (!profiles.containsKey("REG")) {
                profiles.put("REG", untilDate);
              }
            }
            if (Arrays.asList(MXFEE).contains(affdataCode)) {
              if (!profiles.containsKey("MXFEE")) {
                profiles.put("MXFEE", untilDate);
              }
            }
            if (Arrays.asList(LIBRJCT).contains(affdataCode)) {
              if (!profiles.containsKey("LIBRJCT")) {
                profiles.put("LIBRJCT", untilDate);
              }
            }
            if (Arrays.asList(KEEP).contains(affdataCode)) {
              if (!profiles.containsKey("KEEP")) {
                profiles.put("KEEP", untilDate);
              }
            }
          }
        }
      }
    }

    if (type.indexOf("student") == 0) {
      if (type.equals("student:mla")) {
        if (!profiles.containsKey("REG")) {
          profiles.put("REG", untilDate);
        }
      }

      Iterator <Element> privgroupIterator = privgroups.iterator();

      while (privgroupIterator.hasNext()) {
        Element privgroup = (Element) privgroupIterator.next();

        if (privgroup != null) {
          if (privgroup.equals("student:phd")) {
            if (locationCode.equals("0010")) {
              if (!profiles.containsKey("HMSD")) {
                profiles.put("HMSD", untilDate);
              }
            }
            else {
              if (!profiles.containsKey("RED")) {
                profiles.put("RED", untilDate);
              }
            }
          }
          else if (privgroup.equals("student:postdoc")) {
            if (locationCode.equals("0010")) {
              if (!profiles.containsKey("HMSD")) {
                profiles.put("HMSD", untilDate);
              }
            }
            else {
              if (!profiles.containsKey("RED")) {
                profiles.put("RED", untilDate);
              }
            }
          }
          else if (privgroup.equals("student:doctoral")) {
            if (locationCode.equals("0010")) {
              if (!profiles.containsKey("HMSD")) {
                profiles.put("HMSD", untilDate);
              }
            }
            else {
              if (!profiles.containsKey("RED")) {
                profiles.put("RED", untilDate);
              }
            }
          }
        }
      }

      Iterator <Element> affdatasIterator = affdatas.iterator();

      String affdataValue = "";

      while (affdatasIterator.hasNext()) {
        Element affdata = (Element) affdatasIterator.next();

        if (affdata != null) {
          affdataValue = affdata.getTextTrim();

          affdataCode = affdata.getAttributeValue("code");

          if (affdataCode != null) {
            if (affdataValue.equals("LAW JSD")) {
              if (!profiles.containsKey("RED")) {
                profiles.put("RED", untilDate);
              }
            }
            else if (description.indexOf("Graduate") == 0) {
              if (locationCode.equals("0010")) {
                if (!profiles.containsKey("HMSG")) {
                  profiles.put("HMSG", untilDate);
                }
              }
              else if (affdataCode != null && affdataCode.indexOf("GRNM") > -1 ) {
                if (!profiles.containsKey("REG-SUM")) {
                  profiles.put("REG-SUM", untilDate);
                }
              }
              else {
                if (!profiles.containsKey("REG")) {
                  profiles.put("REG", untilDate);
                }
              }
            }
            else if (description.indexOf("Undergraduate") == 0) {
              if (locationCode.equals("0010")) {
                if (!profiles.containsKey("HMSU")) {
                  profiles.put("HMSU", untilDate);
                }
              }
              else if (affdataCode != null && affdataCode.indexOf("UGNM") > -1 ) {
                if (!profiles.containsKey("REU-SUM")) {
                  profiles.put("REU-SUM", untilDate);
                }
              }
              else {
                if (!profiles.containsKey("REU")) {
                  profiles.put("REU", untilDate);
                }
              }
            }
          }
        }
      }
    }

    if (type.indexOf("faculty") == 0) {
      if (locationCode.equals("0010")) {
        if (!profiles.containsKey("HMSF")) {
          profiles.put("HMSF", untilDate);
        }
      }
      else if (type.indexOf("otherteaching") > -1) {
        if (!profiles.containsKey("MXF")) {
          profiles.put("MXF", untilDate);
        }
      }
      else {
        if (!profiles.containsKey("CNF")) {
          profiles.put("CNF", untilDate);
        }
      }
    }

    if (type.indexOf("staff") == 0) {
      if (locationCode.equals("0010")) {
        if (type.indexOf("academic") > 0 || type.indexOf("otherteaching") > 0 | type.indexOf("emeritus") > 0) {
          if (!profiles.containsKey("HMSAC")) {
            profiles.put("HMSAC", untilDate);
          }
        }
        else {
          if (!profiles.containsKey("HMSS")) {
            profiles.put("HMSS", untilDate);
          }
        }
      }
      else if (type.indexOf("academic") > 0 || type.indexOf("otherteaching") > 0 | type.indexOf("emeritus") > 0) {
        if (!profiles.containsKey("CNAC")) {
          profiles.put("CNAC", untilDate);
        }
      }
      else {
        if (!profiles.containsKey("CNS")) {
          profiles.put("CNS", untilDate);
        }
      }
    }

    return profiles;
  }
}
