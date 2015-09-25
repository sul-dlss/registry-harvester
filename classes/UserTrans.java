import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Attribute;
import org.jdom2.JDOMException;

import javax.xml.transform.dom.DOMSource;

public class UserTrans {

  public static String LINE1 = "";
  public static String LINE2 = "";
  public static String LINE3 = "";

  public static String ILLiadData = "";
  public static String ILLAddr1 = "";
  public static String ILLAddr2 = "";
  public static String ILLAddrCity = "";
  public static String ILLAddrState = "";
  public static String ILLAddrZip = "";

  public static ArrayList<String> policies = new ArrayList<String>(17);
  public static ArrayList<String> expiries = new ArrayList<String>();
  public static Map<String, Date> profiles = new LinkedHashMap<String, Date>();

  public static String makeflat (org.jdom2.Document document) throws Exception {

    GetPolicies.policies(policies);

    Map <String, String> illData = new LinkedHashMap <String, String>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf_ill = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    boolean proceed = false;
    String result = "";

    String USER_ALT_ID = "";
    String USER_ID = "";
    String USER_NAME = "";
    String USER_FIRST_NAME = "";
    String USER_MIDDLE_NAME = "";
    String USER_LAST_NAME = "";
    String EMAIL = "";
    String PHONE = "";
    String USER_DEPARTMENT = "";
    String USER_PROFILE = "";
    String USER_PRIV_GRANTED = "";
    String USER_PRIV_EXPIRES = "";
    String USER_GROUP_ID = "";
    String USER_WEB_AUTH = "";

    try {
      Element person  = document.getRootElement();
      List people = person.getChildren("Person");
      Iterator <Element> peopleIterator = people.iterator();

      while (peopleIterator.hasNext()) {
        person = (Element) peopleIterator.next();

        String SPACER = "   |a";
        String type = "";
        String value = "";
        String affid = "";
        String sunetid = "";
        String relationship = "";
        String affiliatedUntil = "";
        String effectiveDate = "";
        String stanfordEndDate = "";

        relationship = person.getAttributeValue("relationship");
        stanfordEndDate = person.getAttributeValue("stanfordenddate");

        LINE1 = "";
        LINE2 = "";
        LINE3 = "";

        List identifiers = person.getChildren("identifier");

        Iterator <Element> identifiersIterator = identifiers.iterator();
        while (identifiersIterator.hasNext()) {
          Element ident = (Element) identifiersIterator.next();

          if (ident != null) {
            type = ident.getAttribute("type").getValue();
            value = ident.getTextTrim();

            if (type.equals("card")) {
              proceed = true;
              USER_ID = value.substring(value.length() - 10);
            }

            if (type.equals("univid")) {
              USER_ALT_ID = value;
            }
            else if (type.equals("refid")) {
              if (value.indexOf("campuscard") > -1) {
                USER_ALT_ID = value.substring(12);
              }
              else if (value.indexOf("campcomm") > -1) {
                USER_ALT_ID = value.substring(10);
              }
            }
          }
        }

        if (USER_ID.length() < 1) {
          break;
        }

        result = "*** DOCUMENT BOUNDARY ***" + "\n";
        result += "FORM=LDUSER" + "\n";
        result += ".USER_ID." + SPACER + USER_ID.trim() + "\n";
        result += ".USER_ALT_ID." + SPACER + USER_ALT_ID.trim() + "\n";


        List names = person.getChildren("name");
        Iterator <Element> namesIterator = names.iterator();

        while (namesIterator.hasNext()) {
          Element name = (Element) namesIterator.next();

          if (name != null) {
            type = name.getAttributeValue("type");

            if (type.equals("registered")) {
              USER_NAME = name.getTextTrim();

              Element firstName = name.getChild("first");
              if (firstName != null) {
                USER_FIRST_NAME = firstName.getTextTrim();
              }

              Element middleName = name.getChild("middle");
              if (middleName != null) {
                USER_MIDDLE_NAME = middleName.getTextTrim();
              }

              Element lastName = name.getChild("last");
              if (lastName != null) {
                USER_LAST_NAME = lastName.getTextTrim();
              }
            }
          }
        }

        result += ".USER_NAME." + SPACER + USER_NAME.trim() + "\n";
        result += ".USER_FIRST_NAME." + SPACER + USER_FIRST_NAME.trim() + "\n";
        result += ".USER_MIDDLE_NAME." + SPACER + USER_MIDDLE_NAME.trim() + "\n";
        result += ".USER_LAST_NAME." + SPACER + USER_LAST_NAME.trim() + "\n";

        List addresses = person.getChildren("address");

        List places = person.getChildren("place");

        result += ".USER_ADDR1_BEGIN." + "\n";

        ChooseAddressElement(addresses, places, relationship, 1);

        EMAIL = "";

        List emails = person.getChildren("email");

        Iterator <Element> emailsIterator = emails.iterator();

        while (emailsIterator.hasNext()) {
          Element email = (Element) emailsIterator.next();

          if (email != null) {
            type = email.getAttributeValue("type");

            if (type != null && type.equals("primary")) {
              EMAIL = email.getTextTrim();
            }
          }
        }

        PHONE = "";

        String locationCode = "";

        Iterator <Element> placesIterator = places.iterator();

        while (placesIterator.hasNext()) {
          Element place = (Element) placesIterator.next();

          if (place != null) {
            Element location = place.getChild("location");

            if (location != null) {
              locationCode = location.getAttributeValue("code");
            }

            List phones = place.getChildren("telephone");

            Iterator <Element> phonesIterator = phones.iterator();

            while (phonesIterator.hasNext()) {
              Element phone = (Element) phonesIterator.next();

              if (phone != null) {
                type = phone.getAttributeValue("type");

                if (type.equals("residence")) {
                  PHONE = phone.getTextTrim();
                }
                else if (type.equals("work")) {
                  PHONE = phone.getTextTrim();
                }
                else if (type.equals("home")) {
                  PHONE = phone.getTextTrim();
                }
              }
            }
          }
        }

        result += ".LINE1." + SPACER + LINE1.trim() + "\n";
        result += ".LINE2." + SPACER + LINE2.trim() + "\n";

        ILLAddr1 = "";
        ILLAddr2 = "";
        ILLAddrCity = "";
        ILLAddrState = "";
        ILLAddrZip = "";

        if (LINE1.length() > 0) {
          result += ".LINE3." + SPACER + LINE3.trim() + "\n";

          ILLAddr1 = LINE1.replaceAll("\\p{Punct}","");
        }
        else {
          result += ".LINE3." + SPACER + "\n";
        }

        if (LINE2.length() > 0) {
          ILLAddr2 = LINE2.replaceAll("\\p{Punct}","");
        }

        result += ".EMAIL." + SPACER + EMAIL.trim() + "\n";
        result += ".PHONE." + SPACER + PHONE.trim() + "\n";
        result += ".USER_ADDR1_END." + "\n";
        result += ".USER_ADDR2_BEGIN." + "\n";

        ChooseAddressElement(addresses, places, relationship, 2);

        if (LINE1.length() > 0) {
          result += ".LINE1." + SPACER + LINE1.trim() + "\n";
        }
        else {
          result += ".LINE1." + SPACER + "\n";
        }

        result += ".LINE2." + SPACER + LINE2.trim() + "\n";

        if (LINE1.length() > 0) {
          result += ".LINE3." + SPACER + LINE3.trim() + "\n";
        }
        else {
          result += ".LINE3." + SPACER + "\n";
        }

        PHONE = "";

        List phones = person.getChildren("telephone");

        Iterator <Element> phonesIterator = phones.iterator();

        while (phonesIterator.hasNext()) {
          Element phone = (Element) phonesIterator.next();

          if (phone != null) {
            type = phone.getAttributeValue("type");

            if (type.equals("general") || type.equals("permanent") || type.equals("home")) {
              PHONE = phone.getTextTrim();
            }

          }
        }

        result += ".PHONE." + SPACER + PHONE.trim() + "\n";
        result += ".USER_ADDR2_END." + "\n";

        String USER_DEPT = "";
        String USER_PROF = "";
        String USER_DESC = "";

        List privgroups = person.getChildren("privgroup") ;

        List affiliations = person.getChildren("affiliation");

        Iterator <Element> affiliationsIterator = affiliations.iterator();

        profiles.clear();

        Date today = new Date();
        Date untilDate = new Date();
        Date effective = new Date();

        while (affiliationsIterator.hasNext()) {
          Element affiliation = (Element) affiliationsIterator.next();

          String affnum = affiliation.getAttributeValue("affnum");
          int affiliationPosition = Integer.parseInt(affnum);

          type = affiliation.getAttributeValue("type");

          affiliatedUntil = affiliation.getAttributeValue("until");
          effectiveDate = affiliation.getAttributeValue("effective");

          Element department = (Element) affiliation.getChild("department");

          if (department != null) {
            Element organization = (Element) department.getChild("organization");

            if (organization != null) {
              if (affiliationPosition == 1) {
                if (organization.getAttributeValue("adminid") != null) {
                  affid = organization.getAttributeValue("adminid");
                }
                else if (organization.getAttributeValue("acadid") != null) {
                  affid = organization.getAttributeValue("acadid");
                }
              }
            }
          }

          if (type.equals("affiliate:visitscholarvs") || type.equals("affiliate:sponsored")) {
            //KEEP
            untilDate = sdf.parse("2999-09-09");
          }
          else if (type.equals("affiliate:fellow") || type.indexOf("faculty") > -1 || type.indexOf("staff") > -1 ) {

            if (type.indexOf("nonactive") > -1 || type.indexOf("notregistered") > -1) {
              //EXPIRED
              untilDate = sdf.parse("1999-09-09");
            }
            else if (type.indexOf("student") < 0 || type.indexOf("casual") < 0) {
              //NEVER
              untilDate = sdf.parse("9999-09-09");
            }
          }
          else if (affiliatedUntil != null) {
            untilDate = sdf.parse(affiliatedUntil);
          }
          else if (stanfordEndDate != null) {
            untilDate = sdf.parse(stanfordEndDate);
          }
          else {
            //EXPIRED
            untilDate = sdf.parse("1999-09-09");
          }

          if (effectiveDate != null && effectiveDate.length() > 0) {
            effective = sdf.parse(effectiveDate);
          }

          Element description = (Element) affiliation.getChild("description");

          List affdatas = affiliation.getChildren("affdata");

          USER_DEPT = affid;

          if (description != null) {
            USER_DESC = description.getTextTrim();
          }

          if (today.compareTo(effective) >= 0 ) {
            AddUserProfile.profiles(profiles, untilDate, affiliationPosition, type, locationCode, affdatas, privgroups, USER_DESC);
          }
        }

        String user_profile [] = GetBest.userProfile(policies, profiles);

        USER_PROFILE = user_profile[0];
        USER_PRIV_EXPIRES = user_profile[1];

        if (USER_PROFILE == null || USER_PROFILE.equals("")) {
          USER_PROFILE = "KEEP";
        }

        if (USER_PRIV_EXPIRES == null || USER_PRIV_EXPIRES.equals("")) {
          USER_PRIV_EXPIRES = "KEEP";
        }

        String todayStr = sdf2.format(today);
        USER_PRIV_GRANTED = todayStr;

        if (USER_DEPT != null) {
          result += ".USER_DEPARTMENT." + SPACER + USER_DEPT.trim() + "\n";
        }

        if (!USER_PROFILE.equals("KEEP")) {
          result += ".USER_PROFILE." + SPACER + USER_PROFILE.trim() + "\n";
        }

        result += ".USER_PRIV_GRANTED." + SPACER + USER_PRIV_GRANTED.trim() + "\n";

        if (!USER_PRIV_EXPIRES.equals("KEEP")) {
          result += ".USER_PRIV_EXPIRES." + SPACER + USER_PRIV_EXPIRES.trim() + "\n";
        }

        sunetid = person.getAttributeValue("sunetid");
        if (sunetid != null) {
          USER_GROUP_ID = sunetid;
        }

        result += ".USER_GROUP_ID." + SPACER + USER_GROUP_ID.trim() + "\n";
        result += ".USER_WEB_AUTH." + SPACER + USER_GROUP_ID.trim() + "\n";


        /* Only insert update rows if it has all of the required New User Registration form field values
        */
        if ((sunetid != null && sunetid.length() > 0) && (USER_LAST_NAME != null && USER_LAST_NAME.length() > 0)
          && (USER_FIRST_NAME != null && USER_FIRST_NAME.length() > 0)
          && (USER_ID != null && USER_ID.length() > 0)
          && (EMAIL != null && EMAIL.length() > 0))
        {
          String NVTGC = "";

          ArrayList<String> privGroupStr = new ArrayList<String> ();
          for (int x = 0; x < privgroups.size(); x++){
            Element priv = (Element) privgroups.get(x);
            privGroupStr.add(priv.getTextTrim());
            }

          if (privGroupStr.contains("stanford:academic") || privGroupStr.contains("dlss:staff")) {

            if (ILLAddr1.length() > 39) {
              ILLAddr1 = ILLAddr1.substring(0,40);
            }
            else if (ILLAddr1.length() < 1) {
              ILLAddr1 = "Lathrop Library";
            }

            if (ILLAddr2.length() > 39) {
              ILLAddr2 = ILLAddr2.substring(0,40);
            }

            if (PHONE.length() > 14) {
              PHONE = PHONE.substring(0,14);
            }
            else if (PHONE.length() < 1) {
              PHONE = "(999)999-9999";
            }

            if (ILLAddrCity.length() <1) {
              ILLAddrCity = "Stanford";
            }

            if (ILLAddrState.length() < 1) {
              ILLAddrState = "CA";
            }

            if (ILLAddrZip.length() <1) {
              ILLAddrZip = "94305";
            }

            if (privGroupStr.contains("organization:sulair")) {
              NVTGC = "ST2";
            }
            else if (privGroupStr.contains("organization:law")) {
              NVTGC = "RCJ";
            }
            else if (privGroupStr.contains("organization:gsb")) {
              NVTGC = "S7Z";
            }
            else {
              NVTGC = "ST2";
            }
            // ILLData.dbo.UsersALL:
            illData.put("UserName", "'" + sunetid + "'"); //50 *
            illData.put("LastName", "'" + USER_LAST_NAME.replaceAll("[',]","") + "'"); //40 *
            illData.put("FirstName", "'" + USER_FIRST_NAME.replaceAll("[',]","") + "'"); //40 *
            illData.put("SSN", "'" + USER_ID + "'"); //20 *
            illData.put("Status", "'Affiliate'"); //15
            illData.put("EMailAddress", "'" + EMAIL + "'"); //50 *
            illData.put("Phone", "'" + PHONE + "'"); //15 *
            illData.put("MobilePhone", "'NULL'"); //15 *
            illData.put("Department", "'Stanford Libraries'"); //255
            illData.put("NVTGC", "'" + NVTGC + "'"); //20
            illData.put("Password", "''"); //64
            illData.put("NotificationMethod", "'E-Mail'"); //8
            illData.put("DeliveryMethod", "'Pickup'"); //25
            illData.put("LoanDeliveryMethod", "'Hold for Pickup'"); //25
            illData.put("LastChangedDate", "'" + sdf_ill.format(today) + "'");
            illData.put("AuthorizedUsers", "'SUL'"); //255
            illData.put("Cleared", "'Yes'");
            illData.put("Web", "'Yes'"); //3
            illData.put("Address", "'" + ILLAddr1 + "'"); //40 *
            illData.put("Address2", "'" + ILLAddr2 + "'"); //40
            illData.put("City", "'" + ILLAddrCity + "'"); //30 *
            illData.put("State", "'" + ILLAddrState + "'"); //2 *
            illData.put("Zip", "'" + ILLAddrZip + "'"); //10 *
            illData.put("Site", "'SUL'"); //40
            if (untilDate != null) {
              illData.put("ExpirationDate", "'" + sdf_ill.format(untilDate) + "'");
            }
            else {
              illData.put("ExpirationDate", "NULL");
            }
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

            ConnectToILLiad.connect(NVTGC, GetTransactSQL.transactSql(NVTGC, sunetid));
          }
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException a) {
      System.err.println("Usage: UserTrans [ xmlFile ]");
      System.err.println(a.getMessage());
    }
    catch (Throwable t) {
      t.printStackTrace();
    }

    return result;
  }


  public static void ChooseAddressElement(List addresses, List places, String relationship, int addr) {
    String type = "";

    Iterator <Element> addressesIterator = addresses.iterator();
    while (addressesIterator.hasNext()) {

      Element address = (Element) addressesIterator.next();
      type = address.getAttributeValue("type");

      if (addr == 1) {
        if (relationship.equals("student") &&
        (type.equals("homemail") || type.equals("mail") || type.equals("local"))) {
          SetAddressLines(address, addr);
          break;
        }
        else if ((relationship.equals("faculty") || relationship.equals("staff") || relationship.equals("affiliate"))
        && (type.equals("mail") || type.equals("work") || type.equals("workmail"))) {
          SetAddressLines(address, addr);
          break;
        }
      }
      else if (addr == 2) {
        if (relationship.equals("student") && (type.equals("work") || type.equals("permanent") || type.equals("general"))) {
          SetAddressLines(address, addr);
          break;
        }
        else if (!relationship.equals("student") && (type.equals("home") || type.equals("homemail"))) {
          SetAddressLines(address, addr);
          break;
        }
        else if (type.equals("general") || type.equals("permanent") || type.equals("billing")) {
          SetAddressLines(address, addr);
          break;
        }
      }
    }

    Iterator <Element> placesIterator = places.iterator();
    while (placesIterator.hasNext()) {

      Element place = (Element) placesIterator.next();
      List placeAddresses = place.getChildren("address");

      Iterator <Element> placeAddressIterator = placeAddresses.iterator();
      while (placeAddressIterator.hasNext()) {

        Element placeAddress = (Element) placeAddressIterator.next();
        String placeAddressType = placeAddress.getAttributeValue("type");

        if (addr == 1) {
          if (relationship.equals("student") &&
          (placeAddressType.equals("home") || placeAddressType.equals("homemail"))){
            SetAddressLines(placeAddress, addr);
            break;
          }
          else if (!relationship.equals("student") &&
          (placeAddressType.equals("mail") || placeAddressType.equals("work") || placeAddressType.equals("workmail"))){
            SetAddressLines(placeAddress, addr);
            break;
          }
        }
        else if (addr == 2) {
          if (relationship.equals("student") && placeAddressType.equals("work")){
            SetAddressLines(placeAddress, addr);
            break;
          }
          else if (!relationship.equals("student") &&
          (placeAddressType.equals("home") || placeAddressType.equals("homemail"))){
            SetAddressLines(placeAddress, addr);
            break;
          }
        }
      }
    }
  }

  public static void SetAddressLines(Element address, int addr) {
    String CITY = "";
    String STATE = "";
    String PROVINCE = "";
    String ZIP = "";
    String COUNTRY = "";
    String stateCode = "";

    List lines = address.getChildren("line");

    for (int i = 0; i < lines.size(); i ++) {
      Element line = (Element) lines.get(i);
      if ( i == 0 ) {
        LINE1 = line.getTextTrim();
      }
      if ( i == 1 ) {
        LINE2 = line.getTextTrim();
      }
      if ( i > 1 ) {
        LINE2 += ", " + line.getTextTrim();
      }
    }

    if ( lines.size() < 2 ) {
      LINE2 = "";
    }

    Element city = address.getChild("city");
    Element state = address.getChild("state");
    Element province = address.getChild("province");
    Element zip = address.getChild("postalcode");
    Element country = address.getChild("country");

    if (city != null) {
      CITY = city.getTextTrim();
    }
    if (state != null) {
      STATE = state.getTextTrim();
      stateCode = state.getAttributeValue("code");
    }
    if (zip != null) {
      ZIP = zip.getTextTrim();
    }
    if (province != null) {
      PROVINCE = province.getTextTrim();
    }
    if (country != null) {
      COUNTRY = country.getAttributeValue("alpha2") + " ";
      if (COUNTRY.equals("US")) {
        COUNTRY = "";
      }
    }

    LINE3 = CITY + ", " + stateCode + PROVINCE + " " + COUNTRY + ZIP;

    if (addr == 1) {
      ILLAddrCity = CITY.replaceAll("\\p{Punct}","");
      ILLAddrState = stateCode;
      ILLAddrZip = ZIP;
    }
  }
}
