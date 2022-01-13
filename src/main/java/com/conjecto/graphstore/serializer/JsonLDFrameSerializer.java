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
import com.google.gson.stream.JsonWriter;
import org.semanticweb.yars.nx.Node;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * JsonLDFrameSerializer
 */
public class JsonLDFrameSerializer extends JsonLDSerializer {

    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        JsonLDFrameWriter$ w = new JsonLDFrameWriter$(store, prefixMapping) ;
        w.setContext(context);
        w.write(out);
    }

    static class JsonLDFrameWriter$ extends JsonLDSerializer.JsonLDWriter$ {

        public JsonLDFrameWriter$(GraphStore store, PrefixMapping prefixMapping) {
            super(store, prefixMapping);
        }

        /**
         * @param writer
         * @throws IOException
         */
        @Override
        protected void writeGraph(JsonWriter writer) throws IOException {
            writer.name("@graph");
            writer.beginArray();
            writer.setIndent("");

            Set<Node> visited = new HashSet<>();
            Iterator<Triplet> iterator = store.queryPOS("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>", "<urn:resource>");
            while(iterator.hasNext()) {
                //visited.clear();
                writeFramedNode(writer, iterator.next().getSubject(), visited);
            }

            writer.setIndent("");
            writer.endArray();
            writer.setIndent(" ");
        }

        /**
         * @param writer
         * @param node
         * @param visited
         * @throws IOException
         */
        protected void writeFramedNode(JsonWriter writer, Node node, Set<Node> visited) throws IOException {
            visited.add(node);

            writer.beginObject();
            writer.setIndent("  ");
            writeNodeReferenceId(writer, node);

            PredicateIterator iterator = new PredicateIterator(store.querySPO(node.toString()));
            while(iterator.hasNext()) {
                writePredicate(writer, iterator.next(), visited);
            }

            writer.endObject(); // }
            writer.setIndent("");
        }

        /**
         * @param node
         */
        @Override
        protected void writeNodeReference(JsonWriter writer, Node node, Set<Node> visited) throws IOException {
            if(!visited.contains(node)) {
                // never visited
                writeFramedNode(writer, node, visited);
            } else {
                super.writeNodeReference(writer, node, visited);
            }
        }
    }
}
