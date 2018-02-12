package org.clarksnut.models.utils;

import org.clarksnut.models.DocumentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class XmlValidator {

    @Inject
    @ConfigurationValue("clarksnut.document.additionalTypesSupported")
    private Optional<String> additionalTypesSupported;

    private List<String> additionalTypes;

    @PostConstruct
    private void init() {
        String[] split = additionalTypesSupported.orElse("").trim().split(",");
        additionalTypes = Arrays.asList(split);
    }

    public boolean isValidUblFile(byte[] bytes) {
        try {
            Document document = toDocument(bytes);
            String documentType = getDocumentType(document);

            if (documentType == null || documentType.trim().isEmpty()) {
                return false;
            }

            if (DocumentType.getByType(documentType) != null) {
                return true;
            }

            if (additionalTypes.contains(documentType)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String getDocumentType(Document document) throws Exception {
        Element documentElement = document.getDocumentElement();
        return documentElement.getTagName();
    }

    private Document toDocument(byte[] bytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(bytes));
    }

}
