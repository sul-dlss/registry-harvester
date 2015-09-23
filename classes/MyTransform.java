import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class MyTransform {
    public static void main(String[] args) throws IOException, URISyntaxException, TransformerException, ArrayIndexOutOfBoundsException
    {
        try 
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File(args[1]));
            Transformer transformer = factory.newTransformer(xslt);
            
            Source text = new StreamSource(new File(args[0]));
            //transformer.transform(text, new StreamResult(new File("transform-result.txt")));
            transformer.transform(text, new StreamResult(System.out));
        }
        catch (Exception e)
        {
            System.out.println("Usage: MyTransform xml-file xslt-file");
        }
    }
}
