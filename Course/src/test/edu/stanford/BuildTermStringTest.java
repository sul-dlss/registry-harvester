package edu.stanford;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BuildTermStringTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getTerm() throws Exception {
        String termString = BuildTermString.getTerm("9999");
        assertEquals("", termString);

        termString = BuildTermString.getTerm("1182");
        assertEquals("F", termString);

        termString = BuildTermString.getTerm("1184");
        assertEquals("W", termString);

        termString = BuildTermString.getTerm("1186");
        assertEquals("Sp", termString);

        termString = BuildTermString.getTerm("1188");
        assertEquals("Su", termString);

        try {
            BuildTermString.getTerm("118");
        } catch (Exception e) {
            assertEquals(e.getMessage(),  "String index out of range: 4");
        }
    }

    @Test
    public void getTermCode() throws Exception {
        String termString = BuildTermString.getTermCode("Christmas 99");
        assertEquals("199", termString);

        termString = BuildTermString.getTermCode("Fall 17");
        assertEquals("1182", termString);

        termString = BuildTermString.getTermCode("Winter 17");
        assertEquals("1174", termString);

        termString = BuildTermString.getTermCode("Spring 18");
        assertEquals("1186", termString);

        termString = BuildTermString.getTermCode("Summer 19");
        assertEquals("1198", termString);

        try {
            BuildTermString.getTermCode("");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void getLongTerm() throws Exception {
        String termString = BuildTermString.getLongTerm("9999");
        assertEquals("", termString);

        termString = BuildTermString.getLongTerm("1182");
        assertEquals("Fall", termString);

        termString = BuildTermString.getLongTerm("1184");
        assertEquals("Winter", termString);

        termString = BuildTermString.getLongTerm("1186");
        assertEquals("Spring", termString);

        termString = BuildTermString.getLongTerm("1188");
        assertEquals("Summer", termString);

        try {
            BuildTermString.getLongTerm("99");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "String index out of range: 4");
        }
    }

    @Test
    public void getYear() throws Exception {
        String term = "1182";
        String termString = BuildTermString.getYear(term);
        assertEquals("2017", termString);

        try {
            BuildTermString.getYear("");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "1");
        }
    }

    @Test
    public void getShortYear() throws Exception {
        String term = "1182";
        String termString = BuildTermString.getShortYear(term);
        assertEquals("17", termString);

        try {
            BuildTermString.getShortYear("");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "1");
        }
    }

    @Test
    public void classId() throws Exception {
        String classID = BuildTermString.classId("1174-MS&E-408A");
        assertEquals(classID, "W17-MS&E-408A");

        try {
            BuildTermString.classId(null);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "null");
        }
    }
}