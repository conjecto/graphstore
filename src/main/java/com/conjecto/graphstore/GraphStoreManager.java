/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * GraphStoreManager
 */
public class GraphStoreManager {

    /**
     * Load a graph into the given store
     *
     * @param store GraphStore
     * @param inputStream InputStream
     * @param format String
     */
    public static void load(GraphStore store, InputStream inputStream, String format) {
        GraphLoader loader = new GraphLoader(store, format);
        loader.load(inputStream);
    }

    /**
     * Serialize a graph in the given format
     *
     * @param out OutputStream
     * @param store GraphStore
     * @param format String
     * @param prefixMapping PrefixMapping
     * @throws IOException IOException
     */
    public static void serialize(OutputStream out, GraphStore store, String format, PrefixMapping prefixMapping) throws IOException {
        GraphSerializer serializer = new GraphSerializer(store, format, prefixMapping);
        serializer.serialize(out);
    }
}
