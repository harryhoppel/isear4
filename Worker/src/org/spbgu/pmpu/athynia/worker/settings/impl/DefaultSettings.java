package org.spbgu.pmpu.athynia.worker.settings.impl;

import org.jdom.Element;
import org.spbgu.pmpu.athynia.worker.settings.Settings;
import org.spbgu.pmpu.athynia.worker.settings.IllegalConfigValueException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DefaultSettings implements Settings {
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();
    private final Node mainNode;

    private DefaultSettings(Node node) {
        mainNode = node;
    }

    public static Settings load(InputStream in) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(in);
            Node mainNode = (Node) XPATH.evaluate("preferences/root", document, XPathConstants.NODE);
            return new DefaultSettings(mainNode);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Settings load(String fileName) {
        try {
            final InputStream inputStream = new FileInputStream(fileName);
            return load(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Settings childSettings(String name) {
        try {
            Node childNode = (Node) XPATH.evaluate("node [@name='" + name + "']", mainNode, XPathConstants.NODE);
            return new DefaultSettings(childNode);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue(String key) {
        try {
            return (String) XPATH.evaluate("map/entry [@key='" + key + "']/@value", mainNode, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public int getIntValue(String key) {
        String ret = getValue(key);
        return Integer.parseInt(ret);
    }

    public boolean getBoolValue(String key) {
        String ret = getValue(key);
        return Boolean.parseBoolean(ret);
    }

    public void save() throws IOException {
    }

    public void load(Element e) throws IllegalConfigValueException {
    }

    public void write(Element e) throws IllegalConfigValueException {
    }
}
