import java.io.StringReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

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

    String lineNew = "";

    for (int f=0; f < args.length; f++) {
      BufferedReader br = new BufferedReader(new FileReader(new File (args[f])));
      String line = "";

      try {   // Only take lines without the xml declarations
        while ((line = br.readLine()) != null) {
          if (line.indexOf("<?xml") == 0) {
            continue;
          }
          else if (line.indexOf("<?xml") > 0) {
            int idx = line.indexOf("<?xml");
            lineNew += line.substring(0, idx);
          }
        }
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }

    String xmlBody = declaration + lineNew + "</RegData>";
    doc = builder.build(new StringReader(xmlBody));
    CourseTrans.courseDoc(doc);
  }
}
