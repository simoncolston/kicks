package org.colston.lib.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class XML {

    public static Element getElement(Element parent, String name) {
        if (parent == null) return null;
        NodeList children = parent.getElementsByTagName(name);
        return children.getLength() == 0 ? null : (Element) parent.getElementsByTagName(name).item(0);
    }

    public static Element item(Element parent, int index) {
        NodeList children = parent.getChildNodes();
        int ei = 0;
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (ei == index) {
                    return (Element) children.item(i);
                }
                ei++;
            }
        }
        return null;
    }

    public static List<Element> children(Element parent) {
        List<Element> list = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                list.add((Element) children.item(i));
            }
        }
        return list;
    }
}
