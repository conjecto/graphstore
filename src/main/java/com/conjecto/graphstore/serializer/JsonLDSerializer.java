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
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JsonLDSerializer
 */
public class JsonLDSerializer implements Serializer {

    Map<String, String> context = new HashMap<>();

    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        JsonLDWriter$ w = new JsonLDWriter$(store, prefixMapping) ;
        w.setContext(context);
        w.write(out);
    }

    public JsonLDSerializer setContext(Map<String, String> context) {
        this.context = context;
        return this;
    }

    public JsonLDSerializer setContext(String key, String value) {
        this.context.put(key, value);
        return this;
    }

    static class JsonLDWriter$ {
        GraphStore store;
        PrefixMapping prefixMapping;
        Map<String, String> context = new HashMap<>();

        public JsonLDWriter$(GraphStore store, PrefixMapping prefixMapping) {
            this.store = store;
            this.prefixMapping = prefixMapping.clone();
        }

        public void setContext(Map<String, String> context) {
            this.context = context;
        }

        public void write(OutputStream out) throws IOException {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
            writer.setIndent("  ");

            writer.beginObject();

            writeContext(writer);   // @context
            writeGraph(writer);     // @graph

            writer.endObject();
            writer.flush();
        }

        /**
         * @param writer
         * @throws IOException
         */
        protected void writeContext(JsonWriter writer) throws IOException {
            //Gson gson = new Gson();
            writer.name("@context");
            writer.beginObject();

            for(Map.Entry<String, String> entry : context.entrySet()) {
                writer.name(entry.getKey());
                //gson.toJson(entry.getValue(), Object.class, writer);
                writer.value(entry.getValue());
            }

            for(Map.Entry<String, String> entry : prefixMapping.getNsPrefixMap().entrySet()) {
                if(context.containsKey("@vocab") && context.get("@vocab").equals(entry.getValue())) {
                    continue;
                }
                writer.name(entry.getKey()).value(entry.getValue());
            }

            writer.endObject();
        }

        /**
         * @param writer
         * @throws IOException
         */
        protected void writeGraph(JsonWriter writer) throws IOException {
            writer.name("@graph");
            writer.beginArray();
            writer.setIndent("");

            SubjectIterator iterator = store.subjectIterator();
            while(iterator.hasNext()) {
                writeResource(writer, iterator.next());
            }

            writer.setIndent("");
            writer.endArray();
            writer.setIndent(" ");
        }

        /**
         * @param writer
         * @param iteration
         */
        protected void writeResource(JsonWriter writer, SubjectIterator.SubjectIteration iteration) {
            try {
                writer.beginObject();
                writer.setIndent("  ");
                writeNodeReferenceId(writer, iteration.subject);
                while(iteration.iterator.hasNext()) {
                    writePredicate(writer, iteration.iterator.next(), null);
                }
                writer.endObject(); // }
                writer.setIndent("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * @param writer
         * @param iteration
         * @param visited
         */
        protected void writePredicate(JsonWriter writer, PredicateIterator.PredicateIteration iteration, Set<Node> visited) {
            try {
                writer.setIndent("  ");
                boolean special = false;
                Resource predicate = iteration.predicate;

                switch(predicate.getLabel()) {
                    case "http://www.w3.org/1999/02/22-rdf-syntax-ns#type":
                        writer.name("@type");
                        special = true;
                        break;
                    default:
                        writer.name(shortForm(predicate.getLabel()));
                }

                Boolean isArray = false;

                while(iteration.iterator.hasNext()) {
                    Triplet triplet = iteration.iterator.next();

                    if(iteration.iterator.hasNext() && !isArray) {
                        writer.beginArray();
                        writer.setIndent("");
                        isArray = true;
                    }

                    Node node = triplet.getObject();
                    if(node instanceof Resource || node instanceof BNode) {
                        if(special) {
                            writer.value(shortForm(node.getLabel()));
                        } else {
                            writeNodeReference(writer, node, visited);
                        }
                    } else if(node instanceof Literal) {
                        writeLiteral(writer, (Literal) node);
                    }
                }

                if(isArray) {
                    writer.endArray();
                    writer.setIndent("  ");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * @param node
         */
        protected void writeNodeReference(JsonWriter writer, Node node, Set<Node> visited) throws IOException {
            writer.beginObject();
            writeNodeReferenceId(writer, node);
            writer.endObject();
        }

        /**
         * @param writer
         * @param node
         * @throws IOException
         */
        protected void writeNodeReferenceId(JsonWriter writer, Node node) throws IOException {
            if(node instanceof BNode) {
                writer.name("@id").value("_:" + node.getLabel());
            } else {
                writer.name("@id").value(shortForm(node.getLabel()));
            }
        }

        /**
         * @param writer
         * @param literal
         * @throws IOException
         */
        protected void writeLiteral(JsonWriter writer, Literal literal) throws IOException {
            if(literal.getDatatype() != null || literal.getLanguageTag() != null) {
                writer.beginObject();
                writer.setIndent("  ");

                String value = literal.getLabel();
                if(literal.getDatatype() != null) {
                    switch(literal.getDatatype().getLabel()) {
                        case "http://www.w3.org/2001/XMLSchema#integer":
                            writer.name("@value").value(new Integer(value));
                            break;
                        case "http://www.w3.org/2001/XMLSchema#double":
                            writer.name("@value").value(new Double(value));
                            break;
                        default:
                            writer.name("@value").value(value);
                    }
                } else {
                    writer.name("@value").value(value);
                }

                if(literal.getDatatype() != null) {
                    writer.name("@type").value(shortForm(literal.getDatatype().getLabel()));
                }
                if(literal.getLanguageTag() != null) {
                    writer.name("@language").value(literal.getLanguageTag());
                }
                writer.endObject();
                writer.setIndent("");
            } else {
                writer.value(literal.getLabel());
            }
        }

        /**
         * @param uri
         * @return
         */
        protected String shortForm(String uri) {

            // #1 : find the exact match in context
            for(Map.Entry<String, String> entry : context.entrySet()) {
                if(entry.getValue().equals(uri) && !entry.getKey().startsWith("@")) {
                    return entry.getKey();
                }
            }

            // #2 : test @vocab
            if(context.containsKey("@vocab")) {
                String namespace = PrefixMapping.extractNamespace(uri);
                if(namespace != null && context.get("@vocab").equals(namespace)) {
                    return uri.substring(namespace.length());
                }
            }

            // #3 else, use prefix mapping
            return prefixMapping.shortForm(uri);
        }
    }
}
