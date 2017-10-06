package edu.stanford;

class   BuildTermString {
  static String getTerm(String term) {
    // term will be 1, 2-year digit, 2|4|6|8
    // i.e. the term for Fall 2017 is 1182
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
      System.err.println(e.getMessage());
    }

    return result;
  }

  static String getTermCode(String term) {
    // expects e.g. Fall 17
    //returns e.g. 1182
    String result = "";

    String [] parts = term.split(" ");
    String quarter = parts[0].toLowerCase();

    int year = Integer.parseInt(parts[1]);

    try {
      switch(quarter) {
        case "fall" :
          result = "2";
          //fall academic year is next calendar year
          year++;
          break;
        case "winter" : result = "4";
          break;
        case "spring" : result = "6";
          break;
        case "summer" : result = "8";
          break;
        default: result = "";
          break;
      }
    }
    catch (java.lang.ArrayIndexOutOfBoundsException e) {
      System.err.println(e.getMessage());
    }

    return "1" + String.valueOf(year) + result;
  }

  static String getLongTerm (String term) {
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
      System.err.println(e.getMessage());
    }

    return result;
  }

  static String getYear (String term){
    String century = "";
    int year = 0;

    try {
      if(term.substring(0,1).equals("1")) {
        century = "20";
      }

      String academicYear = term.substring(1,3);
      year = Integer.parseInt(academicYear);

      if (getTerm(term).equals("F")) {
        year = year - 1;
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.err.println(e.getMessage());
    }

    return century + String.valueOf(year);
  }

  static String getShortYear (String term){
    String academicYear;
    int year = 0;

    try {
      academicYear = term.substring(1,3);
      year = Integer.parseInt(academicYear);

      if (getTerm(term).equals("F")) {
        year = year - 1;
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.err.println(e.getMessage());
    }
    return String.valueOf(year);
  }

  static String classId (String id) {
    StringBuilder result = new StringBuilder();
    // id="1174-MS&amp;E-408A"

    try {
      String [] parts = id.split("-");
      result = new StringBuilder(getTerm(parts[0]));
      result.append(getShortYear(parts[0]));
      for (int i = 1; i < parts.length; i++) {
        result.append("-").append(parts[i]);
      }
    } catch (NullPointerException e) {
      System.err.println(e.getMessage());
    }

    return result.toString();
  }
}
