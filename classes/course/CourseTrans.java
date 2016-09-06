import java.io.File;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
//import org.jdom2.output.Format;
//import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import java.util.Iterator;
import java.util.List;

public class CourseTrans {
  public static org.jdom2.Document courseDoc (org.jdom2.Document docin) throws Exception {

    Element regData = docin.getRootElement();
    Element response = new Element("response");

    List courseClasses = regData.getChildren("CourseClass");
    Iterator <Element> regDataIterator = courseClasses.iterator();
    while(regDataIterator.hasNext()) {
      Element courseClass = (Element) regDataIterator.next();
      String courseTitle = courseClass.getAttributeValue("title");
      String courseTerm = courseClass.getAttributeValue("term");

      Element course = new Element("courseclass");
      String termYear = BuildTermString.getLongTerm(courseTerm) + " " + BuildTermString.getYear(courseTerm);
      course.setAttribute("term", termYear);
      course.setAttribute("title", courseTitle);

      List classes = courseClass.getChildren("class");
      Iterator <Element> classIterator = classes.iterator();
      while(classIterator.hasNext()){
        Element _class = (Element) classIterator.next();
        String classId = _class.getAttribute("id").getValue();

        Element newClass = new Element("class");
        String id = BuildTermString.classId(classId);
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
    return doc;
  }
}
