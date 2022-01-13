/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.iterator;

import com.conjecto.graphstore.Triplet;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import java.util.Iterator;

/**
 * PredicateIterator
 */
public class PredicateIterator extends AbstractIterator<PredicateIterator.PredicateIteration> {

    public PredicateIterator(Iterator<Triplet> tripletIterator) {
        super(tripletIterator);
    }

    @Override
    public Node selectNode(Triplet triplet) {
        return triplet.getPredicate();
    }

    @Override
    public PredicateIteration createIteration(Node predicate, Iterator<Triplet> iterator) {
        return new PredicateIteration((Resource) predicate, iterator);
    }

    public static class PredicateIteration extends AbstractIterator.Iteration {
        public Resource predicate;
        public Iterator<Triplet> iterator;

        public PredicateIteration(Resource predicate, Iterator<Triplet> iterator) {
            this.predicate = predicate;
            this.iterator = iterator;
        }
    }
}
