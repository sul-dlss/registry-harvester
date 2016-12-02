import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.StringReader;

public class LibraryPatron {
    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException, ArrayIndexOutOfBoundsException
    {
        try
        {
            String line = "";
            String lineNew = "";

            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File(args[1]));
            Transformer transformer = factory.newTransformer(xslt);

            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));

            String declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
                + "<!DOCTYPE Person SYSTEM \"http://registry.stanford.edu/xml/person/1.2/Person.dtd\">";

            while ((line = br.readLine()) != null)
            {
                if (line.indexOf("<?xml") == 0)
                {
                    continue;
                }
                else if (line.indexOf("<!--datastore") > -1)
                {
                    int idx = line.indexOf("<!--datastore");
                    lineNew = line.substring(0, idx);

                    Source text = new StreamSource(new StringReader(lineNew));
                    transformer.transform(text, new StreamResult(System.out));
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Usage: LibraryPatron xml-file xslt-file");
        }
    }
}
