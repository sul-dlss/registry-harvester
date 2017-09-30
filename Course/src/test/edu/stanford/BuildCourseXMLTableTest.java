package edu.stanford;

import org.jdom2.Document;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.*;

public class BuildCourseXMLTableTest {

    private Calendar cal = BuildCourseXMLTable.cal;
    private SimpleDateFormat dfy = BuildCourseXMLTable.dfy;

    private String courseClass = "<CourseClass courseid=\"205135\" id=\"1182-205135\" shorttitle=\"INTRO TO FILM/VIDEO PROD\"" +
            " source=\"academic\" term=\"1182\" title=\"Introduction to Film and Video Production\">Hands-on. Techniques of film" + "" +
            " and video making including conceptualization, visualization, story structure, cinematography, sound recording, and editing." +
            " Enrollment limited to 12 students. Priority to junior/senior Film &amp; Media Studies majors.Admission determined on the" +
            " first day of class.<class catalognum=\"114\" id=\"1182-FILMPROD-114\" offering=\"1\" subject=\"FILMPROD\" term=\"1182\">" +
            "<owner percent=\"100.0\"><organization acadid=\"ART\" adminid=\"PDFJ\" regid=\"414c50d0986611d3acf6ab400b0baa77\">" +
            "Art &amp; Art History</organization></owner><section classnum=\"17792\" enrolled=\"5\" enrollment=\"yes\" id=\"01\" maxenroll=\"18\"" +
            " maxunits=\"5\" minunits=\"5\" status=\"open\"><component code=\"PRC\" value=\"Practicum\">Practicum</component>" +
            "<attribute code=\"AUT\" type=\"NQTR\">Autumn</attribute><attribute code=\"SPR\" type=\"NQTR\">Spring</attribute><meeting number=\"1\">" +
            "<instructor type=\"primary\"><person regid=\"20f4e9e6b6274004be22e86157cc9afc\" sunetid=\"lauralg\" univid=\"05058170\">" +
            "Green, Laura</person></instructor><location building=\"ART\" id=\"ART370\" room=\"370\">McMurtry Building rm 370</location>" +
            "<schedule day=\"MON,WED\" startdate=\"09/25/2017\" starttime=\"09:30\" stopdate=\"12/08/2017\" stoptime=\"11:20\"/></meeting>" +
            "<student status=\"enrolled\"><person regid=\"36272a4342c64e4ca4b4df13c8ebd61b\" sunetid=\"lauraczs\" univid=\"05963401\">" +
            "Stickells, Laura Claire Zack</person></student><student status=\"enrolled\"><person regid=\"3f89bae4b6cd4418af3dae30640e8445\" " +
            "sunetid=\"siena\" univid=\"06020030\">Jeakle, Siena Maria</person></student><student status=\"enrolled\">" +
            "<person regid=\"408232f581774ca69a6e460c9c4cf975\" sunetid=\"ailyn\" univid=\"06021115\">Rivera, Ailyn</person></student>" +
            "<student status=\"enrolled\"><person regid=\"41439e0ded19445c9ff191bfad8edb4b\" sunetid=\"pmccloud\" univid=\"06026475\">" +
            "{McCloud, Paula Rene</person></student><student status=\"enrolled\"><person regid=\"3fb94399751b446daef3240870fec46e\" " +
            "sunetid=\"nettaw\" univid=\"06060563\">Wang, Netta</person></student></section></class></CourseClass>";

    private String courseDoc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE response><response><courseclass term=\"Spring 2017\" title=\"Employment Discrimination\">" +
            "<class id=\"SP17-LAW-7019\"><section category=\"Lecture\" id=\"01\"><instructors><instructor sunetid=\"rford\">" +
            "Ford, Richard</instructor></instructors></section></class></courseclass><courseclass term=\"Spring 2017\" " +
            "title=\"Family Law\"><class id=\"SP17-LAW-7021\"><section category=\"Lecture\" id=\"01\"><instructors>" +
            "<instructor sunetid=\"rbanks\">Banks, Prof Ralph Richard</instructor></instructors></section></class>" +
            "</courseclass></response>";


    @Test
    public void main() throws Exception {
    }

    @Test
    public void buildCourseTable() throws Exception {
    }

    @Test
    public void lineNew() throws Exception {
        String registryStr = courseClass + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<!DOCTYPE CourseClass SYSTEM \"http://registry.stanford.edu/xml/courseclass/1.0/CourseClass.dtd\">";

        String line = BuildCourseXMLTable.lineNew(registryStr);
        assertTrue(line.contains("<CourseClass"));
        assertTrue(line.contains("</CourseClass>"));
        assertFalse(line.contains("<?xml version=\"1.0\""));
        assertFalse(line.contains("CourseClass.dtd\">"));
    }

    @Test
    public void getClassCourse() throws Exception {
        Document classCourse = BuildCourseXMLTable.getClassCourse(courseClass);
        assertNotNull(classCourse.getDocument());
        assertNotNull(classCourse.getDocType());
        assertTrue(0 < classCourse.getContentSize());
    }

    @Test
    public void saveCourseClass() throws Exception {
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(courseDoc));
        Document doc = BuildCourseXMLTable.getDocument(is);
        BuildCourseXMLTable.saveCourseClass(doc, "1176");
        File file = new File(System.getProperty("user.dir") + "/course_files/courseXML_SP17.xml");
        assertTrue(file.exists());
    }

    @Test
    public void transformCourseClass() throws Exception {

    }

    @Test
    public void courseXML() throws Exception {
//        CourseDBLookup mockLookup = Mockito.mock(CourseDBLookup.class);
//        Mockito.when(mockLookup.lookupCourse("1182")).thenReturn("1182-205135");
//        Mockito.when(mockLookup.lookupCourseXML("1182")).thenReturn(courseClass);
//        Mockito.when(mockLookup.queryCourse("someSql")).thenReturn(courseClass);
    }

    @Test
    public void getDocument() throws Exception {
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(courseClass));
        Document doc = BuildCourseXMLTable.getDocument(is);
        assertNotNull(doc.getDocument());
        assertNotNull(doc.getDocType());
        assertTrue(0 < doc.getContentSize());
    }

    @Test
    public void getYear() throws Exception {
        String yr = BuildCourseXMLTable.getYear();
        assertEquals(dfy.format(cal.getTime()), yr);
    }

    @Test
    public void getNextYear() throws Exception {
        cal.add(Calendar.YEAR, +1);
        String ny = BuildCourseXMLTable.getNextYear();
        assertEquals(dfy.format(cal.getTime()), ny);
    }

    @Test
    public void getNextNextYear() throws Exception {
        cal.add(Calendar.YEAR, +2);
        String nny = BuildCourseXMLTable.getNextNextYear();
        assertEquals(dfy.format(cal.getTime()), nny);
    }

}