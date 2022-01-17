/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import com.conjecto.graphstore.loader.NTriplesLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphLoaderRegistry
 */
public class GraphLoaderRegistry {

    private static Map<String, LoaderFactory> factories = new HashMap<>() ;

    static {
        register("nt",  NTriplesLoader::new) ;
    }

    /**
     * Register the serialization for graphs and it's associated factory
     * @param format String
     * @param parserFactory LoaderFactory
     */
    public static void register(String format, LoaderFactory parserFactory)
    {
        factories.put(format, parserFactory) ;
    }

    /** Register the serialization for graphs and it's associated factory
     * @param format String
     * @return LoaderFactory
     */
    public static LoaderFactory getFactory(String format)
    {
        return factories.get(format);
    }

}
