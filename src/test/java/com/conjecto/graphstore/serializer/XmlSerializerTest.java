/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.serializer;

import com.conjecto.graphstore.AbstractTest;
import com.conjecto.graphstore.GraphSerializer;
import com.conjecto.graphstore.Serializer;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static org.junit.Assert.*;

/**
 * Created by blaise on 16/12/17.
 */
public class XmlSerializerTest extends AbstractTest {
    @Test
    public void testSerialize() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        loadFromFixture();
        XmlSerializer serializer = new XmlSerializer();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        serializer.serialize(output, store, prefixMapping);

        String xml = output.toString();
        System.out.println(xml);

        // test parse
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(stream);

        // test serialize
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);

        System.out.println(writer.toString());
    }
}