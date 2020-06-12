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
        String symphony_user_record = symphUser("symph_user_one");
        String flat_user_record = transformedUserRecord("patron_one.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void hasBlankActiveIDs() throws Exception {
        String symphony_user_record = symphUser("symph_user_two");
        String flat_user_record = transformedUserRecord("patron_two.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void hasDisplayName() throws Exception {
        String symphony_user_record = symphUser("symph_user_three");
        String flat_user_record = transformedUserRecord("patron_three.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void usesRegisteredFirstName() throws Exception {
        String symphony_user_record = symphUser("symph_user_four");
        String flat_user_record = transformedUserRecord("patron_four.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void noNameNodes() throws Exception {
        String symphony_user_record = symphUser("symph_user_five");
        String flat_user_record = transformedUserRecord("patron_five.xml");
        assertEquals(flat_user_record, symphony_user_record);
    }

    @Test
    public void coterminalStudent() throws Exception {
        String symphony_user_record = symphUser("symph_user_six");
        String flat_user_record = transformedUserRecord("patron_six.xml");
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