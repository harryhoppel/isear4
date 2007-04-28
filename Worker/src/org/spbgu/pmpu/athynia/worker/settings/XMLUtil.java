package org.spbgu.pmpu.athynia.worker.settings;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Date: 09.11.2005
 */
public class XMLUtil {
    private static final String ROOT_ELEMENT_NAME = "athynia";

    public static void writeXMLSerializible(XMLSerializible xmlSer, Writer writer) throws IllegalConfigValueException {
        try {
            Document document = new Document(new Element(ROOT_ELEMENT_NAME));
            xmlSer.write(document.getRootElement());

            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.output(document, writer);
        } catch (IOException e) {
            throw new IllegalConfigValueException(e);
        }
    }

    public static void readXMLSerializible(XMLSerializible xmlSer, Reader reader) throws IllegalConfigValueException {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(reader);
            xmlSer.load(document.getRootElement());
        } catch (JDOMException e) {
            throw new IllegalConfigValueException(e);
        } catch (IOException e) {
            throw new IllegalConfigValueException(e);
        }
    }
}
