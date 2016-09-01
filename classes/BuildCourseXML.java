import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class BuildCourseXML {

  public static void main (String [] args) throws Exception {
    SAXBuilder builder = new SAXBuilder();
    Element element = new Element("response");
    Document doc = new Document(element);

    String declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
    + "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">"
    + "<RegData>";

    String currentCourses = "";
    try {
      File file = new File("../include/course.xml");
      if (!file.exists()) {
        file.createNewFile();
      }
      else {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String fileLine;
        StringBuilder sb = new StringBuilder();

        try {
          while((fileLine = reader.readLine()) != null) {
            sb.append(fileLine);
          }
          currentCourses = sb.toString();
        } finally {
          reader.close();
        }
      }

      for (int f=0; f < args.length; f++) {
        BufferedReader br = new BufferedReader(new FileReader(new File (args[f])));
        String line = "";

        // Only take lines without the xml declarations
        while ((line = br.readLine()) != null) {
          if (line.indexOf("<?xml") == 0) {
            continue;
          }
          else if (line.indexOf("<?xml") > 0) {
            int idx = line.indexOf("<?xml");
            String lineNew = line.substring(0, idx);

            // Only save the new courses
            if (currentCourses.indexOf(lineNew) < 0) {
              currentCourses += lineNew;
            }
          }
        }
      }

      // Overwrite the courses.xml file with the newest courses
      // and send them to be transformed
      FileWriter fw = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(currentCourses);
      bw.close();

      String xmlBody = declaration + currentCourses + "</RegData>";
      doc = builder.build(new StringReader(xmlBody));
      CourseTrans.courseDoc(doc);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
