package org.spbgu.pmpu.athynia.central.settings.impl;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.spbgu.pmpu.athynia.central.settings.IllegalConfigValueException;
import org.spbgu.pmpu.athynia.central.settings.Settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: migger
 * Date: 25.04.2006
 */
public class XmlSettings implements Settings {
    private Map<String, Object> map = new HashMap<String, Object>();

    public Settings childSettings(String name) {
        return (Settings) map.get(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getValue(String key) {
        String ret = (String) map.get(key);
        if (ret == null) {
            System.out.println("ret = " + ret);
            try {
                throw new RuntimeException();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
        return ret;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIntValue(String key) {
        return Integer.parseInt((String) map.get(key));  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getBoolValue(String key) {
        return Boolean.parseBoolean((String) map.get(key));  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void load(Element e) throws IllegalConfigValueException {

        List<Element> children = e.getChildren();
        for (Element i : children) {
            final List<Element> children1 = i.getChildren();
            if (children1.isEmpty()) {
                map.put(i.getName(), i.getAttributeValue("value"));
            } else {
                final XmlSettings xmlSettings = new XmlSettings();
                xmlSettings.load(i);
                map.put(i.getName(), xmlSettings);
            }
        }
    }

    public void write(Element e) throws IllegalConfigValueException {
        final Set<String> strings = map.keySet();
        for (String i : strings) {
            final Object o = map.get(i);
            if (o instanceof XmlSettings) {
                final Element element = new Element(i);
                e.addContent(element);
                ((XmlSettings) o).write(element);
            } else {
                final Element element = new Element(i);
                e.addContent(element);
                element.setAttribute("value", (String) o);
            }
        }
    }

    public static Settings load(String s) {
        try {
            SAXBuilder parser = new SAXBuilder();
            final Document document = parser.build(s);
            final XmlSettings xmlSettings = new XmlSettings();
            xmlSettings.load(document.getRootElement());
            return xmlSettings;
        } catch (JDOMException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalConfigValueException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
