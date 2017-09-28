package edu.stanford;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

class CourseTrans {
  static org.jdom2.Document courseDoc(org.jdom2.Document docin) throws Exception {

    Element regData = docin.getRootElement();
    Element response = new Element("response");

    List <Element> courseClasses = regData.getChildren("CourseClass");

    for (Element courseClass : courseClasses) {
      String courseTitle = courseClass.getAttributeValue("title");
      String courseTerm = courseClass.getAttributeValue("term");

      Element course = new Element("courseclass");
      String termYear = BuildTermString.getLongTerm(courseTerm) + " " + BuildTermString.getYear(courseTerm);
      course.setAttribute("term", termYear);
      course.setAttribute("title", courseTitle);


      List<Element> classes = courseClass.getChildren("class");
      for (Element _class : classes) {
        String classId = _class.getAttribute("id").getValue();

        Element newClass = new Element("class");
        String id = BuildTermString.classId(classId);
        newClass.setAttribute("id", id);

        List<Element> sections = _class.getChildren("section");

        for (Element section : sections) {
          String category = section.getChild("component").getAttributeValue("value");
          String sectionId = section.getAttributeValue("id");

          Element newSection = new Element("section");
          newSection.setAttribute("category", category);
          newSection.setAttribute("id", sectionId);

          Element instructors = ProcessSection.instructors(section);

          newSection.addContent(instructors);
          newClass.addContent(newSection);
        }

        course.addContent(newClass);
      }

      response.addContent(course);
    }

    DocType dtype = new DocType(response.getName());
    return new Document(response, dtype);
  }
}
