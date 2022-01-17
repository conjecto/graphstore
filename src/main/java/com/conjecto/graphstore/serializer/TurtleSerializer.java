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
import com.conjecto.graphstore.iterator.PredicateIterator;
import com.conjecto.graphstore.iterator.SubjectIterator;
import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.util.NxUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * TurtleSerializer
 */
public class TurtleSerializer implements Serializer {

    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        TurtleWriter$ w = new TurtleWriter$(store, prefixMapping) ;
        w.write(out);
    }

    static class TurtleWriter$ {
        GraphStore store;
        PrefixMapping prefixMapping;

        public TurtleWriter$(GraphStore store, PrefixMapping prefixMapping) {
            this.store = store;
            this.prefixMapping = prefixMapping;
        }

        public void write(OutputStream out) throws IOException {
            OutputStreamWriter writer = new OutputStreamWriter(out);
            writeProlog(writer);
            writeResources(writer);
            writer.flush();
        }

        private void writeProlog(OutputStreamWriter writer) throws IOException {
            for(Map.Entry<String, String> entry : prefixMapping.getNsPrefixMap().entrySet()) {
                writer.write("@prefix " + entry.getKey() + ": <"+ entry.getValue() + "> .\n");
            }
            if(prefixMapping.getNsPrefixMap().size() > 0) {
                writer.write("\n");
            }
        }

        /**
         * @param writer
         * @throws IOException
         */
        private void writeResources(OutputStreamWriter writer) throws IOException {
            SubjectIterator iterator = store.subjectIterator();
            while(iterator.hasNext()) {
                writeResource(writer, iterator.next());
                writer.write("\n");
            }
        }

        /**
         * @param writer
         * @param iteration
         */
        private void writeResource(OutputStreamWriter writer, SubjectIterator.SubjectIteration iteration) throws IOException {
            writeNode(writer, iteration.subject);
            writer.write("\n");
            Boolean first = true;
            while(iteration.iterator.hasNext()) {
                if(!first) {
                    writer.write(" ;\n");
                }
                writePredicate(writer, iteration.iterator.next());
                first = false;
            }
            writer.write(" .\n");
        }

        /**
         * @param writer
         * @param iteration
         */
        private void writePredicate(OutputStreamWriter writer, PredicateIterator.PredicateIteration iteration) throws IOException {
            Resource predicate = iteration.predicate;
            writer.write("\t");

            switch(predicate.getLabel()) {
                case "http://www.w3.org/1999/02/22-rdf-syntax-ns#type":
                    writer.write("a");
                    break;
                default:
                    writeNode(writer, predicate);
            }

            writer.write("\t");
            Boolean first = true;
            while(iteration.iterator.hasNext()) {
                if(!first) {
                    writer.write(" , ");
                }
                Node object = iteration.iterator.next().getObject();
                writeNode(writer, object);
                first = false;
            }

        }

        /**
         * @param writer
         * @throws IOException
         */
        private void writeNode(OutputStreamWriter writer, Node node) throws IOException {

            if(node instanceof Resource) {
                // Node is a Resource
                String iri = node.getLabel();
                String reference = prefixMapping.shortForm(iri);
                if(reference.equals(iri)) {
                    reference =  "<" + iri + ">";
                }
                writer.write(reference);

            } else if (node instanceof BNode) {
                // Node is a BNode
                writer.write("_:" + node.getLabel());

            } else if (node instanceof Literal) {
                // Node is a Literal
                Literal literal = (Literal) node;
                writer.write("\"");
                writer.write(NxUtil.escapeLiteral(literal.getLabel())); // todo : escape
                writer.write("\"");

                if(literal.getLanguageTag() != null) {
                    writer.write("@" + literal.getLanguageTag());
                } else if(literal.getDatatype() != null) {
                    writer.write("^^");
                    writeNode(writer, literal.getDatatype());
                }
            }
        }
    }
}
