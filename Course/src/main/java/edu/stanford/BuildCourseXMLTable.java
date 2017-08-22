package edu.stanford;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class BuildCourseXMLTable {

  private static Logger log = LogManager.getLogger(BuildCourseXMLTable.class.getName());

  private static SAXBuilder builder = new SAXBuilder();
  private static SimpleDateFormat dfy = new SimpleDateFormat("yy");

  public static void main (String [] args) throws Exception {

    Calendar cal = Calendar.getInstance();   // Gets the current date and time

    Date year = cal.getTime();
    String yr = dfy.format(year);

    cal.add(Calendar.YEAR, +1);
    Date nextYear = cal.getTime();
    String nyr = dfy.format(nextYear);
    String fall_yr = dfy.format(nextYear); // fall academic year is next calendar year

    cal.add(Calendar.YEAR, +2);
    Date nextNextYear = cal.getTime();
    String fall_nyr = dfy.format(nextNextYear); // fall next academic year is 2nd next calendar year

    CourseDBService.openConnection();

    // These are the harvested courses from the registry being added to the COURSES table as CLOBs + metadata
    for (String arg : args) {
      BufferedReader br = new BufferedReader(new FileReader(new File(arg)));
      log.info("Processed: " + arg + "\n");
      String line;

      while (null != (line = br.readLine())) {
        if (line.indexOf("</CourseClass>") > 0) {
          int idx = line.indexOf("</CourseClass>");
          String lineNew = line.substring(0, idx);
          lineNew = lineNew.concat("</CourseClass>");

          DocType dtype = new DocType("CourseClass");
          dtype.setPublicID("http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd");
          InputSource is = new InputSource();
          is.setCharacterStream(new StringReader(lineNew));
          Document classCourse = builder.build(is);
          classCourse.setDocType(dtype);

          Element root = classCourse.getRootElement();
          String id = root.getAttributeValue("id");

          String rowExists = CourseDBLookup.lookupCourse(id);

          if (rowExists.length() > 0) {
            CourseDBUpdate.updateCourse(id, lineNew);
            log.info(id + " updated");
          } else {
            CourseDBInsert.insertCourse(id, lineNew);
            log.info(id + " inserted");
          }
        }
      }

      String TERMS = System.getenv("COURSE_TERMS");
      if (TERMS == null) TERMS = "Fall, Spring, Winter, Summer";

      String [] quarter = TERMS.replaceAll("\\s+","").split(",");

      System.err.println("Processing terms: " + Arrays.toString(quarter));

      for (String aQuarter : quarter) {
        if (aQuarter.equals("Fall")) {
          transformAndSaveCourseClass(BuildTermString.getTermCode(aQuarter + " " + fall_yr));
          transformAndSaveCourseClass(BuildTermString.getTermCode(aQuarter + " " + fall_nyr));
        } else {
          transformAndSaveCourseClass(BuildTermString.getTermCode(aQuarter + " " + yr));
          transformAndSaveCourseClass(BuildTermString.getTermCode(aQuarter + " " + nyr));
        }
      }
    }

    CourseDBService.closeConnection();
  }

  // Query the COURSES table with each term id for this year and next year and create document to transform
  private static void transformAndSaveCourseClass(String termCode) {
    StringBuilder sb = new StringBuilder();

    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
    + "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">"
    + "<RegData>");

    // XML from CLOB GOES HERE
    sb.append(CourseDBLookup.lookupCourseXML(termCode));

    sb.append("</RegData>");

    try {
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(sb.toString()));
      Document doc = builder.build(is);
      DocType dtype = new DocType("response");
      doc.setDocType(dtype);

      Document courseDoc = CourseTrans.courseDoc(doc);

      XMLOutputter out = new XMLOutputter();

      String outFileName = System.getProperty("user.dir") + "/course_files/courseXML_" +
                           BuildTermString.getTerm(termCode) +
                           BuildTermString.getShortYear(termCode) + ".xml";

      out.output(courseDoc, new FileWriter(new File(outFileName)));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
