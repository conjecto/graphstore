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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * NTriplesSerializer
 */
public class NTriplesSerializer implements Serializer {
    @Override
    public void serialize(OutputStream out, GraphStore store, PrefixMapping prefixMapping) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        Iterator<Triplet> iterator = store.querySPO();
        while(iterator.hasNext()) {
            writer.write(iterator.next() + " .\n");
        }
        writer.flush();
    }
}
