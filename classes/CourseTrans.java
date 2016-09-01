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
    Element response = new Element("response");

    List courseClasses = regData.getChildren("CourseClass");
    Iterator <Element> regDataIterator = courseClasses.iterator();
    while(regDataIterator.hasNext()) {
      Element courseClass = (Element) regDataIterator.next();
      String courseTitle = courseClass.getAttributeValue("title");
      String courseTerm = courseClass.getAttributeValue("term");

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
          String category = section.getChild("component").getAttributeValue("value");
          String sectionId = section.getAttributeValue("id");

          Element newSection = new Element("section");
          newSection.setAttribute("category", category);
          newSection.setAttribute("id", sectionId);

          Element instructors = new Element("instructors");
          // GET EVERYTHING ELSE UNDER section THAT WE NEED for instructors
          IteratorIterable<Content> descendants = section.getDescendants();
          while(descendants.hasNext()) {
            Content descendant = descendants.next();

            if (descendant.getCType().equals(CType.Element)) {
              Element element = (Element) descendant;

              if (element.getName().equals("instructor")) {
                Element person = element.getChild("person");
                String instructorSunet = person.getAttributeValue("sunetid");
                String instructorId = person.getAttributeValue("univid");
                String instructorName = person.getText();

                Element instructor = new Element("instructor");
                if (instructorSunet != null){
                  instructor.setAttribute("sunetid", instructorSunet);
                }
                if (instructorId != null) {
                  instructor.setAttribute("sucardnumber", instructorId);
                }
                if (instructorName != null) {
                  instructor.setText(instructorName);
                }

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

    DocType dtype = new DocType(response.getName());
    Document doc = new Document(response, dtype);
    XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
    // XMLOutputter out = new XMLOutputter();

    try {
      out.output(doc, System.out);
    } catch (IOException e) {
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
    } catch (ArrayIndexOutOfBoundsException e) {
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
      quarter = term.substring(3,4);
      year = Integer.parseInt(academicYear);

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