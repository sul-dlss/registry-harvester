package edu.stanford;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

public class LibraryPatron {
    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException, ArrayIndexOutOfBoundsException
    {
        try
        {
            String line = "";

            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File(args[1]));
            Transformer transformer = factory.newTransformer(xslt);

            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

            while ((line = br.readLine()) != null)
            {
                Source text = new StreamSource(new StringReader(line));
                transformer.transform(text, new StreamResult(System.out));
            }
        }
        catch (Exception e)
        {
            System.out.println("Usage: LibraryPatron xml-file xslt-file");
        }
    }
}
