/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import org.semanticweb.yars.nx.BNode;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import java.util.Arrays;

/**
 * Triplet
 */
public class Triplet {
    private Node subject;
    private Resource predicate;
    private Node object;

    /**
     * @param subject Node
     * @param predicate Node
     * @param object Node
     */
    public Triplet(Node subject, Node predicate, Node object) {
        this.subject = subject;
        this.predicate = (Resource) predicate;
        this.object = object;
    }

    /**
     * @return Node
     */
    public Node getSubject() {
        return subject;
    }

    /**
     * @return Resource
     */
    public Resource getPredicate() {
        return predicate;
    }

    /**
     * @return Node
     */
    public Node getObject() {
        return object;
    }

    /**
     * @param source String
     * @return Triplet
     */
    public static Triplet parse(String source) {
        String[] nodes = source.split("\\|");
        Node subject = parseNode(nodes[0]);
        Node predicate = parseNode(nodes[1]);
        Node object = parseNode(String.join("|", Arrays.copyOfRange(nodes, 2, nodes.length)));
        return new Triplet(subject, predicate, object);
    }

    /**
     * @param source String
     * @return Node
     */
    public static Node parseNode(String source) {
        if(source.charAt(0) == '<') {
            // resource
            return new Resource(source, true);
        } else if(source.charAt(0) == '_') {
            // bnode
            return new BNode(source, true);
        } else if(source.charAt(0) == '"') {
            // literal
            return new Literal(source, true);
        } else{
            throw new RuntimeException("Exception while parsing: '" + source +"'");
        }
    }

    /**
     * @return String
     */
    @Override
    public String toString() {
        return subject + " " + predicate + " " + object;
    }
}
