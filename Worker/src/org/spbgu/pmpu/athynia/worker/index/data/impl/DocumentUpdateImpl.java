package org.spbgu.pmpu.athynia.worker.index.data.impl;

import org.spbgu.pmpu.athynia.worker.index.data.DocumentUpdate;
import org.spbgu.pmpu.athynia.worker.index.data.util.Tokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: Pisar Vasiliy
 * <p/>
 * <p/>
 * Note: The class throws RuntimeException at <b>loading</b> if DocumentBuilder can't be constructed
 */
public class DocumentUpdateImpl implements DocumentUpdate {
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder DOCUMENT_BUILDER = null;

    static {
        try {
            DOCUMENT_BUILDER = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private final String address;
    private final String plainText;

    /**
     * @param xmlDocument Example:
     *                    &lt; document address="data/update/memoryUpdate.xml">
     *                    &lt; entity>FC Chelsea and FC Zenith are the best teams in football &lt;/entity>
     *                    &lt; /document>
     *                    Note: only one &lt;document>-tag and only one &lt;entity>-tag should be in xml-document
     * @throws org.xml.sax.SAXException - if any parsing error occurs
     */
    public DocumentUpdateImpl(String xmlDocument) throws SAXException {
        Document doc = null;
        try {
            doc = DOCUMENT_BUILDER.parse(new InputSource(new CharArrayReader(xmlDocument.toCharArray())));
        } catch (IOException e) {/*ignore, IOException can't happen here*/}
        assert doc != null;
        Node documentTagNode = doc.getFirstChild();
        NamedNodeMap docAttributes = documentTagNode.getAttributes();
        Node addressNode = docAttributes.getNamedItem("address");
        address = addressNode.getNodeValue();
        Node entityTagNode = documentTagNode.getFirstChild();
        plainText = entityTagNode.getTextContent();
    }

    public String getAddress() {
        return address;
    }

    public String getPlainText() {
        return plainText;
    }

    public String[] getWords() {
        return Tokenizer.tokenize(plainText);
    }

    public String[] getUniqueWords() {
        Set<String> uniqueWords = new HashSet<String>(Arrays.asList(Tokenizer.tokenize(plainText)));
        return uniqueWords.toArray(new String[0]);
    }
}
