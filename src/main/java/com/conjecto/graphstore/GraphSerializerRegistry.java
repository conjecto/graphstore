/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import com.conjecto.graphstore.serializer.*;

import java.util.HashMap;
import java.util.Map;

/**
 * GraphSerializerRegistry
 */
public class GraphSerializerRegistry {

    private static Map<String, SerializerFactory> factories = new HashMap<>() ;

    static {
        register("nt", NTriplesSerializer::new) ;
        register("ttl",  TurtleSerializer::new) ;
        register("jsonld",  JsonLDSerializer::new) ;
        register("jsonld-frame",  JsonLDFrameSerializer::new) ;
        register("xml",  XmlSerializer::new) ;
        register("csv",  CsvSerializer::new) ;
    }


    /**
     * Register the serialization for graphs and it's associated factory
     * @param format String
     * @param writerFactory SerializerFactory
     */
    public static void register(String format, SerializerFactory writerFactory)
    {
        factories.put(format, writerFactory) ;
    }


    /**
     * Register the serialization for graphs and it's associated factory
     * @param format String
     * @return SerializerFactory
     */
    public static SerializerFactory getFactory(String format)
    {
        return factories.get(format);
    }

}
