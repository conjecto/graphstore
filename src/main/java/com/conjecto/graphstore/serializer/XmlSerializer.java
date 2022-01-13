/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.serializer;

import com.conjecto.graphstore.GraphStore;
import com.conjecto.graphstore.PrefixMapping;
import com.conjecto.graphstore.Serializer;
import com.conjecto.graphstore.Triplet;
import com.conjecto.graphstore.iterator.PredicateIterator;
import com.conjecto.graphstore.iterator.SubjectIterator;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * XmlSerializer
 */
public class XmlSerializer implements Serializer {

    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        XmlWriter$ w = new XmlWriter$(store, prefixMapping) ;
        w.write(out);
    }

    static class XmlWriter$ {
        GraphStore store;
        PrefixMapping prefixMapping;
        Set<String> additionalPrefixes;

        /**
         * @param store
         * @param prefixMapping
         */
        public XmlWriter$(GraphStore store, PrefixMapping prefixMapping) {
            this.store = store;
            this.prefixMapping = prefixMapping;
            this.additionalPrefixes = new HashSet<>();
        }

        /**
         * @param out
         */
        public void write(OutputStream out) throws IOException {
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writeHeader(writer);
            writeResources(writer);
            writeFooter(writer);
            writer.flush();
        }

        /**
         * @param writer
         * @throws IOException
         */
        private void writeHeader(OutputStreamWriter writer) throws IOException {
            writer.write("<?xml version=\"1.0\"?>\n");
            writer.write("<rdf:RDF \n\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
            // add default namespaces
            for(Map.Entry<String, String> entry : prefixMapping.getNsPrefixMap().entrySet()) {
                if(!entry.getKey().equals("rdf")) {
                    writer.write("\n\txmlns:" + entry.getKey() + "=\"" + entry.getValue() + "\"");
                }
            }
            writer.write(">\n");
        }

        /**
         * @param writer
         * @throws IOException
         */
        private void writeResources(OutputStreamWriter writer) throws IOException {
            SubjectIterator iterator = store.subjectIterator();
            while(iterator.hasNext()) {
                writeResource(writer, iterator.next());
            }
        }

        /**
         * @param writer
         * @param iteration
         */
        private void writeResource(OutputStreamWriter writer, SubjectIterator.SubjectIteration iteration) throws IOException {
            writer.write("\t<rdf:Description");
            if(iteration.subject instanceof BNode) {
                writer.write(" rdf:nodeID=\"" + generateBnodeId(iteration.subject.getLabel()) + "\"");
            } else {
                writer.write(" rdf:about=\"" + iteration.subject.getLabel() + "\"");
            }
            writer.write(">\n");

            while(iteration.iterator.hasNext()) {
                writePredicate(writer, iteration.iterator.next());
            }
            writer.write("\t</rdf:Description>\n");
        }

        /**
         * @param writer
         * @param iteration
         */
        private void writePredicate(OutputStreamWriter writer, PredicateIterator.PredicateIteration iteration) throws IOException {
            Resource predicate = iteration.predicate;

            String tag, additional = "";
            Map.Entry<String, String> namespace = prefixMapping.findNamespace(predicate.getLabel());
            if(namespace == null || predicate.getLabel().substring(namespace.getValue().length()).contains("/")) {
                namespace = prefixMapping.create(predicate.getLabel());
                additionalPrefixes.add(namespace.getKey());
            }
            tag = prefixMapping.shortForm(predicate.getLabel());
            if(additionalPrefixes.contains(namespace.getKey())) {
                additional = " xmlns:" + namespace.getKey() + "=\"" + namespace.getValue() + "\"";
            }

            while(iteration.iterator.hasNext()) {
                Triplet triplet = iteration.iterator.next();
                Node object = triplet.getObject();
                writer.write("\t\t<" + tag + additional);
                if(object instanceof Resource) {
                    writer.write(" rdf:resource=\"" + object.getLabel() + "\" />\n");
                } else if(object instanceof BNode) {
                    writer.write(" rdf:nodeID=\"" + generateBnodeId(object.getLabel()) + "\" />\n");
                } else {
                    if(object instanceof Literal) {
                        Literal literal = (Literal) object;
                        if(literal.getLanguageTag() != null) {
                            writer.write(" xml:lang=\"" + literal.getLanguageTag() + "\"");
                        }
                        if(literal.getDatatype() != null) {
                            writer.write(" rdf:datatype=\"" + literal.getDatatype().getLabel() + "\"");
                        }
                    }
                    writer.write(">" + escapeText(object.getLabel()));
                    writer.write("</"  + tag + ">\n");
                }
            }
        }

        /**
         * @param writer
         * @throws IOException
         */
        private void writeFooter(OutputStreamWriter writer) throws IOException {
            writer.write("</rdf:RDF>");
        }

        /**
         * @param iri
         * @throws IOException
         */
        private String generateBnodeId(String iri) throws IOException {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            md.update(iri.getBytes());
            byte[] digest = md.digest();
            String hash = DatatypeConverter.printHexBinary(digest).toLowerCase();

            return "n" + hash;
        }

        /**
         * @param t
         * @return
         */
        private String escapeText(String t) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < t.length(); i++){
                char c = t.charAt(i);
                switch(c){
                    case '<': sb.append("&lt;"); break;
                    case '>': sb.append("&gt;"); break;
                    case '&': sb.append("&amp;"); break;
                    default:
                        sb.append(c);
                }
            }
            return sb.toString();
        }
    }
}
