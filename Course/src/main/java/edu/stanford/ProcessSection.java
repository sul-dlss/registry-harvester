package edu.stanford;

import org.jdom2.Content;
import org.jdom2.Content.CType;
import org.jdom2.Element;
import org.jdom2.util.IteratorIterable;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

class ProcessSection {
    static Element instructors(Element section) throws Exception {
        Element instructors = new Element("instructors");

        List <Element> meeting = section.getChildren("meeting");
        Iterator <Element> meetingIterator = meeting.iterator();

        ArrayList <String> sunetList = new ArrayList<>();

        while(meetingIterator.hasNext()) {
            Element singleMeeting = meetingIterator.next();
            IteratorIterable<Content> descendants = singleMeeting.getDescendants();

            while(descendants.hasNext()) {
                Content descendant = descendants.next();

                if (descendant.getCType().equals(CType.Element)) {
                    Element element = (Element) descendant;
                    if (element.getName().equals("instructor")) {
                        Element person = element.getChild("person");

                        String instructorSunet = person.getAttributeValue("sunetid");
                        String instructorName = person.getText();

                        if ((instructorSunet != null) && !instructorSunet.isEmpty()) {

                            if (!sunetList.contains(instructorSunet + "\u001d" + instructorName)) {
                                sunetList.add(instructorSunet + "\u001d" + instructorName);
                            }
                        }
                    }
                }
            }
        }

        for (String aSunetList : sunetList) {
            String[] details = aSunetList.split("\u001d");
            Element instructor = new Element("instructor");
            instructor.setAttribute("sunetid", details[0]);
            instructor.setText(details[1]);
            instructors.addContent(instructor);
        }

        return instructors;
    }
}
