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

import java.util.Iterator;

/**
 * SubjectIterator
 */
public class SubjectIterator extends AbstractIterator<SubjectIterator.SubjectIteration> {

    public SubjectIterator(Iterator<Triplet> tripletIterator) {
        super(tripletIterator);
    }

    @Override
    public Node selectNode(Triplet triplet) {
        return triplet.getSubject();
    }

    @Override
    public SubjectIteration createIteration(Node subject, Iterator<Triplet> iterator) {
        return new SubjectIteration(subject, iterator);
    }

    public static class SubjectIteration extends AbstractIterator.Iteration {
        public Node subject;
        public PredicateIterator iterator;

        public SubjectIteration(Node subject, Iterator<Triplet> iterator) {
            this.subject = subject;
            this.iterator = new PredicateIterator(iterator);
        }
    }
}
