import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

public class BuildCourseXML {

  public static SAXBuilder builder = new SAXBuilder();
  public static File logFile = new File("../log/course_build.log");

  public static SimpleDateFormat dfy = new SimpleDateFormat("yy");
  public static String yr = new String();
  public static String nyr = new String();

  public static void main (String [] args) throws Exception {

    Vector<String> summerY = new Vector<String>();
    Vector<String> summerN = new Vector<String>();
    Vector<String> springY = new Vector<String>();
    Vector<String> springN = new Vector<String>();
    Vector<String> winterY = new Vector<String>();
    Vector<String> winterN = new Vector<String>();
    Vector<String> fallY = new Vector<String>();
    Vector<String> fallN = new Vector<String>();

    Calendar cal = Calendar.getInstance();   // Gets the current date and time
    Date year = cal.getTime();
    cal.add(Calendar.YEAR, +1);
    Date nextYear = cal.getTime();
    yr = dfy.format(year);
    nyr = dfy.format(nextYear);

    Properties props = PropGet.getProps("../conf/terms.conf");
    String [] quarter = props.getProperty("TERMS").replaceAll("\\s+","").split(",");
		System.err.println("Processing terms: " + Arrays.toString(quarter));

    for(int t = 0; t < quarter.length; t++) {
      File fileY = new File("../include/courses/" + quarter[t] + "Y.reg.xml");
      File fileN = new File("../include/courses/" + quarter[t] + "N.reg.xml");

      if (!fileY.exists()) {
         fileY.createNewFile();
      }
      if (!fileN.exists()) {
         fileN.createNewFile();
      }

      BufferedReader readerY = new BufferedReader(new FileReader(fileY));
      BufferedReader readerN = new BufferedReader(new FileReader(fileN));
      System.err.println("Reading file(s): " + fileY + " " + fileN);
      String fileLine = "";

      try {
        while((fileLine = readerY.readLine()) != null) {
          if(quarter[t].equals("summer") && fileLine.indexOf("term=\"1"+yr+"8\"") > 0) {
            summerY.add(fileLine);
            System.err.println("summer "+yr+" size:" + summerY.size());
          }
          if(quarter[t].equals("spring") && fileLine.indexOf("term=\"1"+yr+"6\"") > 0) {
            springY.add(fileLine);
            System.err.println("spring "+yr+" size:" + springY.size());
          }
          if(quarter[t].equals("winter") && fileLine.indexOf("term=\"1"+yr+"4\"") > 0) {
            winterY.add(fileLine);
            System.err.println("winter "+yr+" size:" + winterY.size());
          }
          if(quarter[t].equals("fall") && fileLine.indexOf("term=\"1"+yr+"2\"") > 0) {
            fallY.add(fileLine);
            System.err.println("fall "+yr+" size:" + fallY.size());
          }
        }
        while((fileLine = readerN.readLine()) != null) {
          if(quarter[t].equals("summer") && fileLine.indexOf("term=\"1"+nyr+"8\"") > 0) {
            summerN.add(fileLine);
            System.err.println("summer "+nyr+" size:" + summerN.size());
          }
          if(quarter[t].equals("spring") && fileLine.indexOf("term=\"1"+nyr+"6\"") > 0) {
            springN.add(fileLine);
            System.err.println("spring "+nyr+" size:" + springN.size());
          }
          if(quarter[t].equals("winter") && fileLine.indexOf("term=\"1"+nyr+"4\"") > 0) {
            winterN.add(fileLine);
            System.err.println("winter "+nyr+" size:" + winterN.size());
          }
          if(quarter[t].equals("fall") && fileLine.indexOf("term=\"1"+nyr+"2\"") > 0) {
            fallN.add(fileLine);
            System.err.println("fall "+nyr+" size:" + fallN.size());
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        readerY.close();
        readerN.close();
      }
    }

    // These are the harvested courses from the registry
    for (int f=0; f < args.length; f++) {
      BufferedReader br = new BufferedReader(new FileReader(new File (args[f])));
      BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
      out.write("Processed: " + args[f] + "\n");
      String line = "";

      while ((line = br.readLine()) != null) {
        if (line.indexOf("<?xml") == 0) {
          continue;
        }
        else if (line.indexOf("<?xml") > 0) {
          int idx = line.indexOf("<?xml");
          String lineNew = line.substring(0, idx);

          DocType dtype = new DocType("CourseClass");
          dtype.setPublicID("http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd");
          InputSource is = new InputSource();
          is.setCharacterStream(new StringReader(lineNew));
          Document classCourse = builder.build(is);
          classCourse.setDocType(dtype);

          Element root = classCourse.getRootElement();
          String id = root.getAttributeValue("id");
          String term = root.getAttributeValue("term");

          String termStr = BuildTermString.getTerm(term);
          String shortYear = BuildTermString.getShortYear(term);

          if (Arrays.asList(quarter).contains("summer")
              && termStr.equals("SU") && shortYear.equals(yr)) {
            addOrSetContentForTerm(summerY, lineNew, id);
            saveFile(summerY, "summerY");
            transformAndSaveCourseClass(summerY, "Su" + shortYear);
          }
          if (Arrays.asList(quarter).contains("summer")
              && termStr.equals("SU") && shortYear.equals(nyr)) {
            addOrSetContentForTerm(summerN, lineNew, id);
            saveFile(summerN, "summerN");
            transformAndSaveCourseClass(summerN, "Su" + shortYear);
          }

          if (Arrays.asList(quarter).contains("spring")
              && termStr.equals("SP") && shortYear.equals(yr)) {
            addOrSetContentForTerm(springY, lineNew, id);
            saveFile(springY, "springY");
            transformAndSaveCourseClass(springY, "Sp" + shortYear);
          }
          if (Arrays.asList(quarter).contains("spring")
              && termStr.equals("SP") && shortYear.equals(nyr)) {
            addOrSetContentForTerm(springN, lineNew, id);
            saveFile(springN, "springN");
            transformAndSaveCourseClass(springN, "Sp" + shortYear);
          }

          if (Arrays.asList(quarter).contains("winter")
              && termStr.equals("W") && shortYear.equals(yr)) {
            addOrSetContentForTerm(winterY, lineNew, id);
            saveFile(winterY, "winterY");
            transformAndSaveCourseClass(winterY, "W" + shortYear);
          }
          if (Arrays.asList(quarter).contains("winter")
              && termStr.equals("W") && shortYear.equals(nyr)) {
            addOrSetContentForTerm(winterN, lineNew, id);
            saveFile(winterN, "winterN");
            transformAndSaveCourseClass(winterN, "W" + shortYear);
          }

          if (Arrays.asList(quarter).contains("fall")
              && termStr.equals("F") && shortYear.equals(yr)) {
            addOrSetContentForTerm(fallY, lineNew, id);
            saveFile(fallY, "fallY");
            transformAndSaveCourseClass(fallY, "F" + shortYear);
          }
          if (Arrays.asList(quarter).contains("fall")
              && termStr.equals("F") && shortYear.equals(nyr)) {
            addOrSetContentForTerm(fallN, lineNew, id);
            saveFile(fallN, "fallN");
            transformAndSaveCourseClass(fallN, "F" + shortYear);
          }
        }
      }
      out.close();
    }
  }

  public static void transformAndSaveCourseClass (Vector<String> v, String term) {
    StringBuilder sb = new StringBuilder();

    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
    + "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">"
    + "<RegData>");

    for (String string : v) {
      sb.append(string);
    }
    sb.append("</RegData>");

    try {
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(sb.toString()));
      Document doc = builder.build(is);
      DocType dtype = new DocType("response");
      doc.setDocType(dtype);

      Document courseDoc = CourseTrans.courseDoc(doc);

      XMLOutputter out = new XMLOutputter();

      String outFileName = "../include/courses/courseXML_" + term + ".xml";

      out.output(courseDoc, new FileWriter(outFileName));

    } catch (Exception e) { e.printStackTrace(); }
  }

  public static void saveFile (Vector<String> v, String term) {
    StringBuilder sb = new StringBuilder();
    for (String string : v) {
      sb.append(string + "\n");
    }
    try{
      File file = new File("../include/courses/" + term + ".reg.xml");
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      out.write(sb.toString());
      out.close();
    } catch (IOException e) { }
  }

  public static void addOrSetContentForTerm (Vector<String> v, String regData, String id) {
    Date time = Calendar.getInstance().getTime();
    String currCourseLn = "";
    boolean set = false;
    int i = 0;
    for (String string : v) {
      currCourseLn = string;
      if (currCourseLn.indexOf(" id=\"" + id + "\"") > -1) {
        logUpdate(time, id);
        v.set(i, regData);
        set = true;
      }
      i++;
    }
    if (!set){
      logAddition(time, id);
      v.add(regData);
    }
  }

  public static void logUpdate(Date time, String id) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
      out.write(time.toString() + "\t");
      out.write(id + "\t");
      out.write("updated\n");
      out.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void logAddition(Date time, String id) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
      out.write(time.toString() + "\t");
      out.write(id + "\t");
      out.write("added\n");
      out.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
