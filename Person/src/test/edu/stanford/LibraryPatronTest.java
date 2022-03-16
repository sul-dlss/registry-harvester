package edu.stanford;

import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class LibraryPatronTest {
    private File xslt_file;

    @Before
    public void init() {
        xslt_file = new File(getClass().getClassLoader().getResource("library_patron.xsl").getFile());
    }

    @Test
    public void hasActiveIDs() throws Exception {
        String symphony_user_record = symphUser("01_symph_user");
        String flat_user_record = transformedUserRecord("01_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void hasBlankActiveIDs() throws Exception {
        String symphony_user_record = symphUser("02_symph_user");
        String flat_user_record = transformedUserRecord("02_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void hasDisplayName() throws Exception {
        String symphony_user_record = symphUser("03_symph_user");
        String flat_user_record = transformedUserRecord("03_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void usesRegisteredFirstName() throws Exception {
        String symphony_user_record = symphUser("04_symph_user");
        String flat_user_record = transformedUserRecord("04_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void noNameNodes() throws Exception {
        String symphony_user_record = symphUser("05_symph_user");
        String flat_user_record = transformedUserRecord("05_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void coterminalStudent() throws Exception {
        String symphony_user_record = symphUser("06_symph_user");
        String flat_user_record = transformedUserRecord("06_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void visitingScholar() throws Exception {
        String symphony_user_record = symphUser("07_symph_user");
        String flat_user_record = transformedUserRecord("07_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void courtesyCard() throws Exception {
        String symphony_user_record = symphUser("08_symph_user");
        String flat_user_record = transformedUserRecord("08_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void tempStaff() throws Exception {
        String symphony_user_record = symphUser("09_symph_user");
        String flat_user_record = transformedUserRecord("09_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void casualStaff() throws Exception {
        String symphony_user_record = symphUser("10_symph_user");
        String flat_user_record = transformedUserRecord("10_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void doctoralStudent() throws Exception {
        String symphony_user_record = symphUser("11_symph_user");
        String flat_user_record = transformedUserRecord("11_patron.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    private String symphUser(String resourceName) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource(resourceName).toURI())));
    }

    private String transformedUserRecord(String filename) throws IOException, TransformerException {
        Source text = new StreamSource(getClass().getClassLoader().getResourceAsStream(filename));
        StringWriter outWriter = new StringWriter();
        libraryPatronTransformer(xslt_file).transform(text, new StreamResult(outWriter));
        StringBuffer sb = outWriter.getBuffer();
        return sb.toString();
    }

    private Transformer libraryPatronTransformer(File xslt_file) {
        TransformerFactory factory = TransformerFactory.newInstance();

        Source xslt = new StreamSource(xslt_file);
        Transformer transformer = null;
        try {
            transformer = factory.newTransformer(xslt);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformer;
    }
}