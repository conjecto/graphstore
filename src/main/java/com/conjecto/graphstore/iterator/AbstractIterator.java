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
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AbstractIterator
 */
public abstract class AbstractIterator<T extends AbstractIterator.Iteration> implements Iterator<T> {
    Iterator<Triplet> tripletIterator;

    public AbstractIterator(Iterator<Triplet> tripletIterator) {
        this.tripletIterator = tripletIterator;
    }

    private T current = null;
    private T next = null;
    private Set<Triplet> buffer = new LinkedHashSet<>();
    private final AtomicReference<Node> bufferNode = new AtomicReference<>();

    public abstract Node selectNode(Triplet triplet);

    public abstract T createIteration(Node node, Iterator<Triplet> iterator);

    @Override
    public boolean hasNext() {
        while(tripletIterator.hasNext()) {
            Triplet triplet = tripletIterator.next();
            Node node = selectNode(triplet);
            if(bufferNode.get() != null && !bufferNode.get().equals(node)) {
                next = createIteration(bufferNode.get(), buffer.iterator());
                buffer = new LinkedHashSet<>();
                buffer.add(triplet);
                bufferNode.set(node);
                return true;
            }
            buffer.add(triplet);
            bufferNode.set(node);
        }
        if(bufferNode.get() != null && buffer.size() > 0) {
            next = createIteration(bufferNode.get(), buffer.iterator());
            bufferNode.set(null);
            return true;
        }
        return false;
    }

    @Override
    public T next() {
        if(next != null && !next.equals(current)) {
            current = next;
            return current;
        }
        if(hasNext()) {
            return next();
        }
        throw new NoSuchElementException();
    }

    static abstract class Iteration {}
}
