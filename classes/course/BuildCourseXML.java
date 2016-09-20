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

  public static String logFileName = "../../include/courses/updates.log";
  public static File logFile = new File(logFileName);

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
      String fileLine;
      try {
        while((fileLine = reader.readLine()) != null) {
          if(quarter[t].equals("summer") && fileLine.indexOf("term=\"1"+yr+"8\"") < 0)
          summer.add(fileLine);
          else if(quarter[t].equals("spring") && fileLine.indexOf("term=\"1"+yr+"6\"") < 0)
          spring.add(fileLine);
          else if(quarter[t].equals("winter") && fileLine.indexOf("term=\"1"+yr+"4\"") < 0)
          winter.add(fileLine);
          else if(quarter[t].equals("fall") && fileLine.indexOf("term=\"1"+lyr+"2\"") < 0)
          fall.add(fileLine);
        }
      } finally {
        reader.close();
      }
    }

    for (int f=0; f < args.length; f++) {
      BufferedReader br = new BufferedReader(new FileReader(new File (args[f])));
      String line = "";
      int x = 0;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("<?xml") == 0) {
          continue;
        }
        else if (line.indexOf("<?xml") > 0) {
          System.err.println(x);
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

          if (termStr.equals("SU")){
            addOrSetContentForTerm(summer, lineNew, id, "summer");
            saveFile(summer, "summer");
            transformAndSaveCourseClass(summer);
          }
          else if (termStr.equals("SP")){
            addOrSetContentForTerm(spring, lineNew, id, "spring");
            saveFile(spring, "spring");
            transformAndSaveCourseClass(spring);
          }
          else if (termStr.equals("W")){
            addOrSetContentForTerm(winter, lineNew, id, "winter");
            saveFile(winter, "winter");
            transformAndSaveCourseClass(winter);
          }
          else if (termStr.equals("F")){
            addOrSetContentForTerm(fall, lineNew, id, "fall");
            saveFile(fall, "fall");
            transformAndSaveCourseClass(fall);
          }
        }
        x++;
      }
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

      System.err.println(courseDoc.toString());

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
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(logFile));
      String currCourseLn = "";
      boolean set = false;
      int i = 0;
      for (String string : v) {
        currCourseLn = string;
        if (currCourseLn.indexOf(" id=\"" + id + "\"") > -1) {
          out.append(id + "\n");
          v.set(i, regData);
          set = true;
        }
        i++;
      }
      if (!set){
        v.add(regData);
      }
      out.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
