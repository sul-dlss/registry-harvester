import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
  public static File logFile = new File("../../log/course_build.log");

  public static void main (String [] args) throws Exception {

    Vector<String> summer = new Vector<String>();
    Vector<String> spring = new Vector<String>();
    Vector<String> winter = new Vector<String>();
    Vector<String> fall = new Vector<String>();

    Calendar cal = Calendar.getInstance();   // Gets the current date and time
    Date year = cal.getTime();
    cal.add(Calendar.YEAR, -1);
    Date lastYear = cal.getTime();
    SimpleDateFormat dfy = new SimpleDateFormat("yy");
    String yr = dfy.format(year);
    String lyr = dfy.format(lastYear);

    String[] quarter = {"summer", "spring", "winter", "fall"};


    for(int t = 0; t < quarter.length; t++) {
      File file = new File("../../include/courses/" + quarter[t] + ".reg.xml");
      if (!file.exists()) {
         file.createNewFile();
      }

      BufferedReader reader = new BufferedReader(new FileReader(file));
      System.err.println("Reading" + file);
      String fileLine;
      try {
        while((fileLine = reader.readLine()) != null) {
          if(quarter[t].equals("summer") && fileLine.indexOf("term=\"1"+yr+"8\"") > 0) {
            summer.add(fileLine);
            System.err.println("summer size:" + summer.size());
          }
          if(quarter[t].equals("spring") && fileLine.indexOf("term=\"1"+yr+"6\"") > 0) {
            spring.add(fileLine);
            System.err.println("spring size:" + spring.size());
          }
          if(quarter[t].equals("winter") && fileLine.indexOf("term=\"1"+yr+"4\"") > 0) {
            winter.add(fileLine);
            System.err.println("winter size:" + winter.size());
          }
          if(quarter[t].equals("fall") && fileLine.indexOf("term=\"1"+yr+"2\"") > 0) {
            fall.add(fileLine);
            System.err.println("fall size:" + fall.size());
          }
        }
      } finally {
        reader.close();
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
          String termComp = BuildTermString.getShortYear(term);

          if ( termComp.equals(yr) ||
               (termComp.equals(lyr) && termStr.equals("F")) )
          {
            if (termStr.equals("SU")){
              addOrSetContentForTerm(summer, lineNew, id, "summer");
              saveFile(summer, "summer");
              transformAndSaveCourseClass(summer);
            }
            if (termStr.equals("SP")){
              addOrSetContentForTerm(spring, lineNew, id, "spring");
              saveFile(spring, "spring");
              transformAndSaveCourseClass(spring);
            }
            if (termStr.equals("W")){
              addOrSetContentForTerm(winter, lineNew, id, "winter");
              saveFile(winter, "winter");
              transformAndSaveCourseClass(winter);
            }
            if (termStr.equals("F")){
              addOrSetContentForTerm(fall, lineNew, id, "fall");
              saveFile(fall, "fall");
              transformAndSaveCourseClass(fall);
            }
          }
        }
      }
      out.close();
    }
  }

  public static void transformAndSaveCourseClass (Vector<String> v) {
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
      Element root = courseDoc.getRootElement();
      Element child = root.getChild("courseclass");
      String term = child.getAttributeValue("term");

      //XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
      XMLOutputter out = new XMLOutputter();

      String outFileName = "../../include/courses/courseXML_";
      if (term.indexOf("Fall") == 0) {
        outFileName += "F" + term.substring(term.length() - 2) + ".xml";
      }
      else if (term.indexOf("Winter") == 0) {
        outFileName += "W" + term.substring(term.length() - 2) + ".xml";
      }
      else {
        outFileName += term.substring(0,2) + term.substring(term.length() - 2) + ".xml";
      }

      out.output(courseDoc, new FileWriter(outFileName));

    } catch (Exception e) { e.printStackTrace(); }
  }

  public static void saveFile (Vector<String> v, String quarter) {
    StringBuilder sb = new StringBuilder();
    for (String string : v) {
      sb.append(string + "\n");
    }
    try{
      File file = new File("../../include/courses/" + quarter + ".reg.xml");
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      out.write(sb.toString());
      out.close();
    } catch (IOException e) { }
  }

  public static void addOrSetContentForTerm (Vector<String> v, String regData, String id, String term) {
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
