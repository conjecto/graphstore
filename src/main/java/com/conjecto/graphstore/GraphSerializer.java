/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import java.io.IOException;
import java.io.OutputStream;

/**
 * GraphSerializer
 */
public class GraphSerializer {
    private final GraphStore store;
    private final String format;

    private PrefixMapping prefixMapping = new PrefixMapping();

    /**
     * @param store GraphStore
     * @param format String
     * @param prefixMapping PrefixMapping
     */
    public GraphSerializer(GraphStore store, String format, PrefixMapping prefixMapping) {
        this.store = store;
        this.format = format;
        if(prefixMapping != null) {
            this.prefixMapping = prefixMapping;
        }
    }

    /**
     * @param out OutputStream
     * @throws IOException I/O exception
     */
    public void serialize(OutputStream out) throws IOException {
        SerializerFactory wf = GraphSerializerRegistry.getFactory(format);
        if ( wf == null )
            throw new RuntimeException("No graph serializer for " + format);
        Serializer w =  wf.create();
        w.serialize(out, store, prefixMapping);
    }
}
