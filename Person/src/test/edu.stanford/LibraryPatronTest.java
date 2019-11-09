package edu.stanford;

import org.junit.Test;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.junit.Assert.assertEquals;

public class LibraryPatronTest {

    @Test
    public void hasActiveIDs() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        File xslt_file = new File(getClass().getClassLoader().getResource("library_patron.xsl").getFile());
        Source xslt = new StreamSource(xslt_file);
        Transformer transformer = factory.newTransformer(xslt);

        File patron_file = new File(this.getClass().getResource("/patron_one.xml").getFile());
        Source text = new StreamSource(patron_file);
        StringWriter outWriter = new StringWriter();
        transformer.transform(text, new StreamResult(outWriter));
        StringBuffer sb = outWriter.getBuffer();
        String symphony_user_rec = sb.toString();

        File fixture_record = new File(this.getClass().getResource("/symph_user_one").getFile());
        String filePath = fixture_record.getPath();
        assertEquals(symphony_user_rec, fileToString(filePath));
    }

    @Test
    public void hasBlankActiveIDs() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        File xslt_file = new File(getClass().getClassLoader().getResource("library_patron.xsl").getFile());
        Source xslt = new StreamSource(xslt_file);
        Transformer transformer = factory.newTransformer(xslt);

        File patron_file = new File(this.getClass().getResource("/patron_two.xml").getFile());
        Source text = new StreamSource(patron_file);
        StringWriter outWriter = new StringWriter();
        transformer.transform(text, new StreamResult(outWriter));
        StringBuffer sb = outWriter.getBuffer();
        String symphony_user_rec = sb.toString();

        File fixture_record = new File(this.getClass().getResource("/symph_user_two").getFile());
        String filePath = fixture_record.getPath();
        assertEquals(symphony_user_rec, fileToString(filePath));
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