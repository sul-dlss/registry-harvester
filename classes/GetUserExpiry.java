import java.util.ArrayList;

public class GetUserExpiry {

  public static String expiry(ArrayList expiries, String expiryDate, String type) {

    expiries.clear();

    String expires = "";

    if (expiryDate.length() > 0) {
      expires = expiryDate;
    }
    else if (type.equals("affiliate:visitscholarvs") || type.equals("affiliate:sponsored")) {
      expiries.add("KEEP");
    }
    else if (type.equals("affiliate:fellow") || type.indexOf("faculty") > -1 || type.indexOf("staff") > -1 ) {
      if (type.indexOf("nonactive") > -1 || type.indexOf("notregistered") > -1) {
        expiries.add("EXPIRED");
      }
      else if (type.indexOf("student") < 0 || type.indexOf("casual") < 0) {
        expiries.add("NEVER");
      }
    }
    else if (expires.equals("NEVER")) {
      expiries.add("NEVER");
    }
    else {
      expiries.add("EXPIRED");
    }

    if (expiries.contains("NEVER")) {
      expires = "NEVER";
    }
    else if (expiries.contains("KEEP")) {
      expires = "KEEP";
    }
    else if (expiries.contains("EXPIRED")) {
      expires = "EXPIRED";
    }

    return expires;
  }
}
