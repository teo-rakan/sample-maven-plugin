package com.epam.jira.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class XMLWriter {

    private final static String OUTPUT_FILE_PATH = "./target/tm.xml";

    public static void writeXmlFile(Map<String, String> tests)  {
        try {
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            Document doc = build.newDocument();

            // Create root node
            Element root = doc.createElement("tests");
            doc.appendChild(root);

            // Create child node for each test
            for (Map.Entry<String, String> test : tests.entrySet()) {
                Element testNode = doc.createElement("test");
                root.appendChild(testNode);

                Element key = doc.createElement("key");
                key.appendChild(doc.createTextNode(test.getKey()));
                testNode.appendChild(key);

                Element status = doc.createElement("status");
                status.appendChild(doc.createTextNode(test.getValue()));
                testNode.appendChild(status);
            }

            // Save the document to the disk file
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();

            // format the XML nicely
            aTransformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            try {
                FileWriter fos = new FileWriter(OUTPUT_FILE_PATH);
                StreamResult result = new StreamResult(fos);
                aTransformer.transform(source, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (TransformerException ex) {
            System.out.println("Error outputting document");
        } catch (ParserConfigurationException ex) {
            System.out.println("Error building document");
        }
    }
}
