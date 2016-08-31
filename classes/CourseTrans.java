import java.io.File;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import java.util.Iterator;
import java.util.List;

public class CourseTrans {
  public static void courseDoc (org.jdom2.Document docin) throws Exception {

    Element regData = docin.getRootElement();
    List courseClasses = regData.getChildren("CourseClass");
    Iterator <Element> regDataIterator = courseClasses.iterator();
    Element response = new Element("response");

    while(regDataIterator.hasNext()) {
      Element courseClass = (Element) regDataIterator.next();
      String courseTitle = courseClass.getAttribute("title").getValue();
      String courseTerm = courseClass.getAttribute("term").getValue();

      Element course = new Element("courseclass");
      course.setAttribute("term", BuildTermString(courseTerm));
      course.setAttribute("title", courseTitle);

      List classes = courseClass.getChildren("class");
      Iterator <Element> classIterator = classes.iterator();

      while(classIterator.hasNext()){
        Element _class = (Element) classIterator.next();
        String classId = _class.getAttribute("id").getValue();

        Element newClass = new Element("class");
        String id = BuildClassId(classId);
        newClass.setAttribute("id", id);

        List sections = _class.getChildren("section");
        Iterator <Element> sectionsIterator = sections.iterator();

        while(sectionsIterator.hasNext()) {
          Element section = (Element) sectionsIterator.next();
          String category = section.getChild("component").getAttribute("value").getValue();
          String sectionId = section.getAttribute("id").getValue();

          Element newSection = new Element("section");
          newSection.setAttribute("category", category);
          newSection.setAttribute("id", sectionId);

          Element instructors = new Element("instructors");

          IteratorIterable<Content> descendants = section.getDescendants();
          while(descendants.hasNext()) {
            Content descendant = descendants.next();

            if (descendant.getCType().equals(CType.Element)) {
              Element element = (Element) descendant;

              if (element.getName().equals("instructor")) {
                Element person = element.getChild("person");

                String instructorSunet = person.getAttribute("sunetid").getValue();
                String instructorId = person.getAttribute("univid").getValue();
                String instructorName = person.getText();

                Element instructor = new Element("instructor");
                instructor.setAttribute("sunetid", instructorSunet);
                instructor.setAttribute("sucardnumber", instructorId);
                instructor.setText(instructorName);

                instructors.addContent(instructor);
              }
            }
          }

          newSection.addContent(instructors);
          newClass.addContent(newSection);
        }

        course.addContent(newClass);
      }

      response.addContent(course);
    }

    // response.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
    DocType dtype = new DocType(response.getName());
    Document doc = new Document(response, dtype);
    XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
    // XMLOutputter out = new XMLOutputter();

    try {
      out.output(doc, System.out);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return;
  }

  public static String BuildClassId (String id) {
    String result = "";
    String termStr = "";
    String term = "";

    String [] parts = id.split("-");

    try {
      termStr = BuildTermString(parts[0]);
      term = termStr.substring(0,2) + termStr.substring(termStr.lastIndexOf(" ") + 3);
      result = term;
      for (int i = 1; i < parts.length; i++) {
        result += "-" + parts[i];
      }
    }
    // catch (java.io.UnsupportedEncodingException e) {
    //   System.out.println(e.getMessage());
    // }
    catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }

  public static String BuildTermString (String term) {
    String result = "";
    String century = "";
    String academicYear = "";
    String quarter = "";

    int year;

    try {
      if(term.substring(0,1).equals("1")) {
        century = "20";
      }

      academicYear = term.substring(1,3);
      year = Integer.parseInt(academicYear);

      quarter = term.substring(3,4);
      String termStr;
      switch(Integer.parseInt(quarter)) {
        case 2: termStr = "Autumn";
          year = year - 1;
          break;
        case 4: termStr = "Winter";
          break;
        case 6: termStr = "Spring";
          break;
        case 8: termStr = "Summer";
          break;
        default: termStr = "";
          break;
      }

      result = termStr + " " + century + String.valueOf(year);
    }
    catch (java.lang.StringIndexOutOfBoundsException e) {
      System.out.println(e.getMessage());
    }

    return result;
  }
}
