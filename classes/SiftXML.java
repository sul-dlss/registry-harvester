import java.io.StringReader;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

public class SiftXML {

  public static void main (String [] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(new File (args[0])));
    SAXBuilder builder = new SAXBuilder();

    String line = "";

    try {
      while ((line = br.readLine()) != null) {
        Document doc;

        String lineNew = "";

        String declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
        + "<!DOCTYPE Person SYSTEM \"http://registry.stanford.edu/xml/person/1.2/Person.dtd\">"
        + "<RegistryData>";

        if (line.indexOf("<?xml") == 0) {
          continue;
        }
        else if (line.indexOf("<!--datastore") > -1) {
          int idx = line.indexOf("<!--datastore");
          lineNew = declaration + line.substring(0, idx) + "</RegistryData>";

          doc = builder.build(new StringReader(lineNew));
          System.out.printf(UserTrans.makeflat(doc));
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
