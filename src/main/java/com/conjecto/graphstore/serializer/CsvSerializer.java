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
import org.apache.commons.lang.StringEscapeUtils;
import org.semanticweb.yars.nx.Literal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * CsvSerializer
 */
public class CsvSerializer implements Serializer {
    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        writer.write("subject,predicate,object,lang\n");
        Iterator<Triplet> iterator = store.querySPO();
        while(iterator.hasNext()) {
            Triplet triplet = iterator.next();
            writer.write(StringEscapeUtils.escapeCsv(triplet.getSubject().getLabel()));
            writer.write(",");
            writer.write(StringEscapeUtils.escapeCsv(triplet.getPredicate().getLabel()));
            writer.write(",");
            writer.write(StringEscapeUtils.escapeCsv(triplet.getObject().getLabel()));
            writer.write(",");
            if(triplet.getObject() instanceof Literal) {
                Literal object = (Literal) triplet.getObject();
                if(object.getLanguageTag() != null) {
                    writer.write(StringEscapeUtils.escapeCsv(object.getLanguageTag()));
                }
            }
            writer.write("\n");
        }
        writer.flush();
    }
}
