package org.clarksnut.models.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class XmlValidator {

    public static boolean test(byte[] bytes) {
        try {
            Document document = toDocument(bytes);
            String documentType = getDocumentType(document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDocumentType(Document document) throws Exception {
        Element documentElement = document.getDocumentElement();
        return documentElement.getTagName();
    }

    public static Document toDocument(byte[] bytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
    }

}
