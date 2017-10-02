package edu.stanford;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

class CourseTrans {

  private static Element newCourse;
  private static Element newClass;
  private static Element response = new Element("response");

  static org.jdom2.Document courseDoc(org.jdom2.Document docin) throws Exception {

    Element regData = docin.getRootElement();

    List <Element> courseClasses = regData.getChildren("CourseClass");

    for (Element courseClass : courseClasses) {
      processCourseClasses(courseClass);
    }

    DocType dtype = new DocType(response.getName());
    return new Document(response, dtype);
  }

  static void processCourseClasses(Element courseClass) throws Exception {

    response.addContent(createNewCourse(courseClass));

    List<Element> classes = courseClass.getChildren("class");

    for (Element _class : classes) {

      newCourse.addContent(createNewClass(_class));

      List<Element> sections = _class.getChildren("section");

      for (Element section : sections) {

        newClass.addContent(createNewSection(section));
      }
    }
  }

  static Element createNewCourse(Element courseClass) {
    String courseTitle = courseClass.getAttributeValue("title");
    String courseTerm = courseClass.getAttributeValue("term");

    newCourse = new Element("courseclass");

    String termYear = BuildTermString.getLongTerm(courseTerm) + " " + BuildTermString.getYear(courseTerm);

    newCourse.setAttribute("term", termYear);
    newCourse.setAttribute("title", courseTitle);

    return newCourse;
  }

  static Element createNewClass(Element _class) {
    String classId = _class.getAttribute("id").getValue();

    newClass = new Element("class");

    String id = BuildTermString.classId(classId);

    newClass.setAttribute("id", id);

    return newClass;
  }

  static Element createNewSection(Element section) throws Exception {
    String category = section.getChild("component").getAttributeValue("value");
    String sectionId = section.getAttributeValue("id");

    Element newSection = new Element("section");
    newSection.setAttribute("category", category);
    newSection.setAttribute("id", sectionId);

    Element instructors = ProcessSection.instructors(section);

    newSection.addContent(instructors);

    return newSection;
  }

}
