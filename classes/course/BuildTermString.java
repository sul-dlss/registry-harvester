public class BuildTermString {
  public static String getTerm (String term) {
    String result = "";

    try {
      switch(Integer.parseInt(term.substring(3,4))) {
        case 2: result = "F";
        break;
        case 4: result = "W";
        break;
        case 6: result = "SP";
        break;
        case 8: result = "SU";
        break;
        default: result = "";
        break;
      }
    }
    catch (java.lang.StringIndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }

  public static String getLongTerm (String term) {
    String result = "";

    try {
      switch(Integer.parseInt(term.substring(3,4))) {
        case 2: result = "Fall";
        break;
        case 4: result = "Winter";
        break;
        case 6: result = "Spring";
        break;
        case 8: result = "Summer";
        break;
        default: result = "";
        break;
      }
    }
    catch (java.lang.StringIndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }

  public static String getYear (String term){
    String result = "";
    String century = "";
    if(term.substring(0,1).equals("1")) {
      century = "20";
    }
    String academicYear = term.substring(1,3);
    int year = Integer.parseInt(academicYear);
    if (getTerm(term).equals("F")) {
      year = year - 1;
    }

    result = century + String.valueOf(year);
    return result;
  }

  public static String getShortYear (String term){
    String result = "";
    String academicYear = term.substring(1,3);
    int year = Integer.parseInt(academicYear);
    if (getTerm(term).equals("F")) {
      year = year - 1;
    }

    result = String.valueOf(year);
    return result;
  }

  public static String classId (String id) {
    String result = "";
    // id="1174-MS&amp;E-408A"
    String [] parts = id.split("-");

    try {
      result = getTerm(parts[0]);
      result += getShortYear(parts[0]);
      for (int i = 1; i < parts.length; i++) {
        result += "-" + parts[i];
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }
};
