/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * PrefixMapping
 */
public class PrefixMapping {
    protected Map<String, String> prefixToURI = new HashMap<>();

    /**
     * Set a prefix
     *
     * @param prefix String
     * @param uri String
     * @return PrefixMapping
     */
    public PrefixMapping set(String prefix, String uri) {
        if(prefix == null || prefix.isEmpty()) {
            create(uri);
        } else {
            prefixToURI.put(prefix, uri) ;
        }
        return this;
    }

    /**
     * Process a whole map
     *
     * @param map Map
     * @return PrefixMapping
     */
    public PrefixMapping set( Map<String, String> map ) {
        for (Entry<String, String> e: map.entrySet())
            set( e.getKey(), e.getValue() );
        return this;
    }

    /**
     * Create a new custom prefix
     *
     * @param uri String
     * @return AbstractMap.SimpleEntry
     */
    public AbstractMap.SimpleEntry<String, String> create(String uri) {
        String namespace = extractNamespace(uri);
        if(namespace == null) {
            return null;
        }
        int index = 0;
        String prefix = "ns" + index;
        while(this.get(prefix) != null) {
            prefix = "ns" + (++index);
        }
        set(prefix, namespace);
        return new AbstractMap.SimpleEntry<>(prefix, namespace);
    }

    /**
     * @param prefix String
     * @return String
     */
    public String get(String prefix) {
        return prefixToURI.get(prefix) ;
    }

    /**
     * @param prefixed String
     * @return String
     */
    public String expandPrefix( String prefixed ) {
        int colon = prefixed.indexOf( ':' );
        if (colon < 0)
            return prefixed;
        else
        {
            String uri = get( prefixed.substring( 0, colon ) );
            return uri == null ? prefixed : uri + prefixed.substring( colon + 1 );
        }
    }

    /**
     * @param uri String
     * @return String
     */
    public String shortForm( String uri ) {
        Entry<String, String> e = findNamespace( uri );
        return e == null ? uri : e.getKey() + ":" + uri.substring( (e.getValue()).length() );
    }

    /**
     * @return Map
     */
    public Map<String, String> getNsPrefixMap() { return new HashMap<>( prefixToURI ); }

    /**
     * @param uri String
     * @return Entry
     */
    public Entry<String, String> findNamespace(String uri) {
        String namespace = extractNamespace(uri);
        if(namespace != null) {
            for (Entry<String, String> e: prefixToURI.entrySet()) {
                if(e.getValue().equals(namespace)) {
                    return e;
                }
            }
        }
        return null;
    }

    /**
     * @return PrefixMapping
     */
    public PrefixMapping clone() {
        return new PrefixMapping().set(this.getNsPrefixMap());
    }

    /**
     * @param uri String
     * @return String
     */
    public static String extractNamespace(String uri) {
        int pos = uri.lastIndexOf("#");
        if(pos < 0) {
            pos = uri.lastIndexOf("/");
        }
        if(pos >= 0) {
            return uri.substring(0, pos+1);
        }
        return null;
    }
}
