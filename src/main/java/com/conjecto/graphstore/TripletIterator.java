/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import java.io.Closeable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * TripletIterator
 */
public class TripletIterator implements Iterator<Triplet>, Closeable {
    protected String prefix;
    protected RocksIterator iterator;

    public TripletIterator(RocksIterator iterator, String prefix) {
        this.prefix = prefix;
        this.iterator = iterator;
        iterator.seek(prefix.getBytes());
    }

    @Override
    public boolean hasNext() {
        return iterator.isValid() && new String(iterator.key()).startsWith(prefix);
    }

    @Override
    public Triplet next() {
        Triplet triplet = Triplet.parse(new String(iterator.value()));
        iterator.next();
        return triplet;
    }

    @Override
    public void forEachRemaining(Consumer<? super Triplet> consumer) {
        Objects.requireNonNull(consumer);
        while(this.hasNext()) {
            Triplet triplet = this.next();
            consumer.accept(triplet);
        }
        this.close();
    }

    public void close() {
        iterator.close();
    }

    public List<Triplet> toList() {
        List<Triplet> list = new ArrayList<>();
        forEachRemaining(list::add);
        return list;
    }

    public Stream<Triplet> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED),false);
    }
}

