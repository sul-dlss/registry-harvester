import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.util.IteratorIterable;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ProcessSection {
  public static Element instructors(Element section) throws Exception {
    Element instructors = new Element("instructors");

    List <Element> meeting = section.getChildren("meeting");
    Iterator <Element> meetingIterator = meeting.iterator();

    while(meetingIterator.hasNext()) {
      Element instructor = new Element("instructor");
      Element singleMeeting = (Element) meetingIterator.next();

      IteratorIterable<Content> descendants = singleMeeting.getDescendants();
      while(descendants.hasNext()) {
        Content descendant = descendants.next();

        if (descendant.getCType().equals(CType.Element)) {
          Element element = (Element) descendant;
          if (element.getName().equals("instructor")) {
            Element person = element.getChild("person");

            String instructorSunet = person.getAttributeValue("sunetid");
            String instructorName = person.getText();
            Vector<String> sunetList = new Vector<String>();

            if (instructorSunet != null && instructorSunet.length() > -1) {
              instructor.setAttribute("sunetid", instructorSunet);
              sunetList.add(instructorSunet);
              if (instructorName != null) {
                instructor.setText(instructorName);
              }
            }
          }
        }
      }

      if (!sunetList.contains(instructor.getAttributeValue("sunetid"))) {
        instructors.addContent(instructor);
      }
    }

    return instructors;
  }
}
