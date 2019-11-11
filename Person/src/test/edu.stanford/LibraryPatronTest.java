package edu.stanford;

import org.junit.Test;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.Assert.assertEquals;

public class LibraryPatronTest {
    public File xslt_file = new File(getClass().getClassLoader().getResource("library_patron.xsl").getFile());

    @Test
    public void hasActiveIDs() throws Exception {
        String symphony_user_rec = symphonyUserRecord(new File(this.getClass().getResource("/patron_one.xml").getFile()));
        File fixture_record = new File(this.getClass().getResource("/symph_user_one").getFile());
        String filePath = fixture_record.getPath();
        assertEquals(symphony_user_rec, fileToString(filePath));
    }

    @Test
    public void hasBlankActiveIDs() throws Exception {
        String symphony_user_rec = symphonyUserRecord(new File(this.getClass().getResource("/patron_two.xml").getFile()));
        File fixture_record = new File(this.getClass().getResource("/symph_user_two").getFile());
        String filePath = fixture_record.getPath();
        assertEquals(symphony_user_rec, fileToString(filePath));
    }

    @Test
    public void hasDisplayName() throws Exception {
        String symphony_user_rec = symphonyUserRecord(new File(this.getClass().getResource("/patron_three.xml").getFile()));
        File fixture_record = new File(this.getClass().getResource("/symph_user_three").getFile());
        String filePath = fixture_record.getPath();
        assertEquals(symphony_user_rec, fileToString(filePath));
    }

    public Transformer libraryPatronTransformer(File xslt_file) {
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

    public String symphonyUserRecord(File personXmlFile) throws IOException, TransformerException {
        Source text = new StreamSource(personXmlFile);
        StringWriter outWriter = new StringWriter();
        libraryPatronTransformer(xslt_file).transform(text, new StreamResult(outWriter));
        StringBuffer sb = outWriter.getBuffer();
        return sb.toString();
    }

    public String fileToString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

}