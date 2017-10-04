package edu.stanford;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class CourseTransTest {

    private Document course_doc;
    private Document course_class_doc;

    @Before
    public void setUp() throws Exception {
        InputSource is = new InputSource();

        File course_file = new File(getClass().getClassLoader().getResource("one_course.xml").getFile());
        String courseDoc = new String(Files.readAllBytes(course_file.toPath()));
        is.setCharacterStream(new StringReader(courseDoc));
        course_doc = BuildCourseXMLTable.getDocument(is);

        File course_class_file = new File(getClass().getClassLoader().getResource("oneCourseClass.xml").getFile());
        String courseClassDoc = new String(Files.readAllBytes(course_class_file.toPath()));
        is.setCharacterStream(new StringReader(courseClassDoc));
        course_class_doc = BuildCourseXMLTable.getDocument(is);
    }

    @Test
    public void courseDoc() throws Exception {
        Document transformedDoc = CourseTrans.courseDoc(course_doc);
        assertNotNull(transformedDoc.getDocument());
        assertNotNull(transformedDoc.getDocType());
        assertTrue(0 < transformedDoc.getContentSize());
    }

    @Test
    public void processCourseClasses() throws Exception {
        Element response = new Element("response");
        Element regData = course_class_doc.getRootElement();
        CourseTrans.processCourseClasses(response, regData);
    }

    @Test
    public void createNewCourse() throws Exception {
        Element regData = course_class_doc.getRootElement();
        Element newCourse = CourseTrans.createNewCourse(regData);
        assertNotNull(newCourse.getContent());
        assertEquals("courseclass", newCourse.getName());
        assertEquals("Winter 2018", newCourse.getAttributeValue("term"));
        assertEquals("Experimental Design and Probability", newCourse.getAttributeValue("title"));
    }

    @Test
    public void createNewClass() throws Exception {
        Element regData = course_class_doc.getRootElement();
        Element classData = regData.getChild("class");
        Element newClass = CourseTrans.createNewClass(classData);
        assertNotNull(newClass.getContent());
        assertEquals("W18-BIOHOPK-174H", newClass.getAttributeValue("id"));
    }

    @Test
    public void createNewSection() throws Exception {
        Element regData = course_class_doc.getRootElement();
        Element classData = regData.getChild("class");
        Element sectionData = classData.getChild("section");
        Element newSection = CourseTrans.createNewSection(sectionData);

        assertNotNull(newSection.getContent());
        assertEquals("section", newSection.getName());
        assertEquals("Lecture", newSection.getAttributeValue("category"));
        assertEquals("01", newSection.getAttributeValue("id"));

        Element instructors = newSection.getChild("instructors");

        assertNotNull(instructors.getContent());
        assertTrue(0 < instructors.getContentSize());
        assertEquals("Watanabe, James M", instructors.getChildText("instructor"));
    }
}