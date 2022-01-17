/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import java.io.InputStream;

/**
 * GraphLoader
 */
public class GraphLoader {
    private final GraphStore store;
    private final String format;

    /**
     * @param store GraphStore
     * @param format String
     */
    public GraphLoader(GraphStore store, String format) {
        this.store = store;
        this.format = format;
    }

    /**
     * @param input InputStream
     */
    public void load(InputStream input) {
        LoaderFactory pf = GraphLoaderRegistry.getFactory(format);
        if ( pf == null )
            throw new RuntimeException("No graph loader for " + format);
        Loader w =  pf.create();
        w.load(store, input);
    }
}
