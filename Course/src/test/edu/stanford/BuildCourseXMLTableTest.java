package edu.stanford;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class BuildCourseXMLTableTest {

    private SimpleDateFormat dfy = BuildCourseXMLTable.dfy;

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
    public void main() throws Exception {
    }

    @Test
    public void processQuarters() throws Exception {
    }

    @Test
    public void buildCourseTable() throws Exception {
    }

    @Test
    public void lineNew() throws Exception {
        String courseClass = new XMLOutputter().outputString(course_class_doc.getRootElement());

        String registryStr = courseClass + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">";

        String line = BuildCourseXMLTable.lineNew(registryStr);
        assertNotNull(line);
        assertTrue(line.contains("<CourseClass"));
        assertTrue(line.contains("</CourseClass>"));
        assertFalse(line.contains("<?xml version=\"1.0\""));
        assertFalse(line.contains("CourseClass.dtd\">"));
    }

    @Test
    public void getClassCourse() throws Exception {
        InputStream file = ClassLoader.getSystemResourceAsStream("oneCourseClass.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String course = reader.lines().collect(Collectors.joining("\n"));
        Document courseDoc = BuildCourseXMLTable.getClassCourse(course);

        assertNotNull(courseDoc.getDocument());
        assertTrue(0 < courseDoc.getContentSize());
    }

    @Test
    public void saveCourseClass() throws Exception {
        BuildCourseXMLTable.saveCourseClass(course_doc, "1176");
        File file = new File(System.getProperty("user.dir") + "/course_files/courseXML_Sp17.xml");
        assertTrue(file.exists());
    }

    @Test
    public void transformCourseClass() throws Exception {

    }

    @Test
    public void courseXML() throws Exception {
    }

    @Test
    public void getDocument() throws Exception {
        assertNotNull(course_class_doc.getDocument());
        assertNotNull(course_class_doc.getDocType());
        assertTrue(0 < course_class_doc.getContentSize());
    }

    @Test
    public void getYear() throws Exception {
        Calendar cal = Calendar.getInstance();
        String yr = BuildCourseXMLTable.getYear();
        assertEquals(dfy.format(cal.getTime()), yr);
    }

    @Test
    public void getNextYear() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, +1);
        String ny = BuildCourseXMLTable.getNextYear();
        assertEquals(dfy.format(cal.getTime()), ny);
    }
}