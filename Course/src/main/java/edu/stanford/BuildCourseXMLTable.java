package edu.stanford;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class BuildCourseXMLTable {

  private static Logger log = LogManager.getLogger(BuildCourseXMLTable.class.getName());

  private static SAXBuilder builder = new SAXBuilder();

  static SimpleDateFormat dfy = new SimpleDateFormat("yy");

  private static int counter = 0;

  @CoverageIgnore
  public static void main (String [] args) throws Exception {

    String TERMS = System.getenv("COURSE_TERMS");
    if (TERMS == null) TERMS = "Fall, Spring, Winter, Summer";

    String [] quarter = TERMS.replaceAll("\\s+","").split(",");

    log.info("Processing terms: " + Arrays.toString(quarter));

    // These are the harvested courses from the registry being added to the COURSES table as CLOBs + metadata
    for (String arg : args) {
      log.info("Loading data from: " + arg + " into table\n");

      BufferedReader br = new BufferedReader(new FileReader(new File(arg)));

      String line;

      while (null != (line = br.readLine())) {

        buildCourseTable(line);

      }

      for (String aQuarter : quarter) {

        String termCode;

        if (aQuarter.equals("Fall")) {

          termCode = BuildTermString.getTermCode(aQuarter + " " + getNextYear());
          saveCourseClass(transformCourseClass(termCode), termCode);

          termCode = BuildTermString.getTermCode(aQuarter + " " + getNextNextYear());
          saveCourseClass(transformCourseClass(termCode), termCode);

        } else {

          termCode = BuildTermString.getTermCode(aQuarter + " " + getYear());
          saveCourseClass(transformCourseClass(termCode), termCode);

          termCode = BuildTermString.getTermCode(aQuarter + " " + getNextYear());
          saveCourseClass(transformCourseClass(termCode), termCode);

        }
      }

      CourseDBService.closeConnection();
    }
  }

  @CoverageIgnore
  private static void buildCourseTable(String line) throws IOException, SQLException, JDOMException, URISyntaxException {
    if (CourseDBService.dbConnection == null) {
      CourseDBService.openConnection();
      CourseDBService.dbConnection.setAutoCommit(false);
    }

    if (lineNew(line) != null) {
      Element root = getClassCourse(lineNew(line)).getRootElement();
      String id = root.getAttributeValue("id");

      String rowExists = CourseDBLookup.lookupCourse(id);

      if (rowExists.length() > 0) {
        CourseDBUpdate.updateCourse(id, lineNew(line));
        log.info(id + " updated");
      } else {
        CourseDBInsert.insertCourse(id, lineNew(line));
        log.info(id + " inserted");
      }
    }

    counter++;
    if (counter > 100) {
      CourseDBService.dbConnection.commit();
      CourseDBService.closeConnection();
      counter = 0;
    }
  }

  static String lineNew(String line) throws  StringIndexOutOfBoundsException {
    int idx = line.indexOf("</CourseClass>");
    if (idx > -1){
      String lineNew = line.substring(0, idx);
      lineNew = lineNew.concat("</CourseClass>");
      return lineNew;
    }
    return null;
  }

  private static Document getClassCourse(String lineNew) throws JDOMException, IOException {
    DocType dtype = new DocType("CourseClass");
    dtype.setPublicID("http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd");
    InputSource is = new InputSource();
    is.setCharacterStream(new StringReader(lineNew));
    Document classCourse = builder.build(is);
    classCourse.setDocType(dtype);
    return classCourse;
  }

  static void saveCourseClass(Document courseDoc, String termCode) throws IOException {
    String termName = BuildTermString.getTerm(termCode) + BuildTermString.getShortYear(termCode);
    log.info("Saving " + termName + " XML File");
    String outFileName = System.getProperty("user.dir") + "/course_files/courseXML_" + termName + ".xml";

    XMLOutputter out = new XMLOutputter();

    out.output(courseDoc, new FileWriter(new File(outFileName)));
  }

  @CoverageIgnore
  private static Document transformCourseClass(String termCode) throws Exception {
    InputSource is = new InputSource();
    Document doc = new Document();
    try {
      is.setCharacterStream(new StringReader(courseXML(termCode)));
      doc = CourseTrans.courseDoc(getDocument(is));
    } catch (Exception e) {
      log.warn("No input source for document: " + e.getMessage());
    }

    return doc;
  }

  @CoverageIgnore
  private static String courseXML(String termCode) {

    String regData = null;

    try {
      String lookup = CourseDBLookup.lookupCourseXML(termCode);
      regData =  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
              + "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">"
              + "<RegData>" + lookup + "</RegData>";
    } catch (NullPointerException e) {
      log.warn("Lookup course returned " + e.getMessage());
    }

    return regData;
  }

  static Document getDocument(InputSource is) throws JDOMException, IOException {
    Document doc = builder.build(is);
    DocType dtype = new DocType("response");
    doc.setDocType(dtype);
    return doc;
  }

  static String getYear() {
    Calendar cal = Calendar.getInstance(); // Gets the current date and time
    Date year = cal.getTime();
    return dfy.format(year);
  }

  static String getNextYear() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, +1);
    Date nextYear = cal.getTime();
    return dfy.format(nextYear);
  }

  // fall next academic year is 2nd next calendar year
  static String getNextNextYear() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, +2);
    Date nextNextYear = cal.getTime();
    return dfy.format(nextNextYear);
  }
}
