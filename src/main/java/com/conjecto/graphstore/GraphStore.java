/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import com.conjecto.graphstore.exception.GraphStoreException;
import com.conjecto.graphstore.exception.GraphStoreLockException;
import com.conjecto.graphstore.exception.GraphStoreNotExistException;
import com.conjecto.graphstore.iterator.SubjectIterator;
import org.rocksdb.*;

import java.io.Closeable;
import java.util.*;

/**
 * GraphStore
 */
public class GraphStore implements Closeable {
    protected RocksDB db;
    protected GraphStoreOptions options;

    // a list which will hold the handles for the column families once the db is opened
    final List<ColumnFamilyHandle> cfHandleList = new ArrayList<>();

    static {
        RocksDB.loadLibrary();
    }

    /**
     * @param dbDir String
     * @param options  GraphStoreOptions
     * @param readOnly Boolean
     * @throws GraphStoreException GraphStoreException
     */
    public GraphStore(String dbDir, GraphStoreOptions options, Boolean readOnly) throws GraphStoreException {
        this.options = options;
        try (final ColumnFamilyOptions cfOpts = new ColumnFamilyOptions().optimizeUniversalStyleCompaction()) {
            // list of column family descriptors, first entry must always be default column family

            List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
            cfDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, cfOpts));
            cfDescriptors.add(new ColumnFamilyDescriptor("POS".getBytes(), cfOpts));
            try {
                options.getDBOptions().setCreateMissingColumnFamilies(true);
                if(readOnly) {
                    this.db = RocksDB.openReadOnly(options.getDBOptions(), dbDir, cfDescriptors, cfHandleList);
                } else {
                    this.db = RocksDB.open(options.getDBOptions(), dbDir, cfDescriptors, cfHandleList);
                }
            } catch (RocksDBException e) {
                if(e.getMessage().contains("LOCK")) {
                    throw new GraphStoreLockException(e);
                }
                if(e.getMessage().contains("does not exist")) {
                    throw new GraphStoreNotExistException(e);
                }
                throw new GraphStoreException(e);
            }
        }
    }

    /**
     * @param dbDir String
     * @param options GraphStoreOptions
     * @return GraphStore
     * @throws GraphStoreException GraphStoreException
     */
    public static GraphStore open(String dbDir, GraphStoreOptions options) throws GraphStoreException {
        return new GraphStore(dbDir, options, false);
    }

    /**
     * @param dbDir String
     * @return GraphStore
     * @throws GraphStoreException GraphStoreException
     */
    public static GraphStore open(String dbDir) throws GraphStoreException {
        final GraphStoreOptions options = new GraphStoreOptions();
        return open(dbDir, options);
    }

    /**
     * Open store, read-only
     *
     * @param dbDir String
     * @param options GraphStoreOptions
     * @return GraphStore
     * @throws GraphStoreException GraphStoreException
     */
    public static GraphStore openReadOnly(String dbDir, GraphStoreOptions options) throws GraphStoreException {
        return new GraphStore(dbDir, options, true);
    }

    /**
     * Open store, read-only
     *
     * @param dbDir String
     * @return GraphStore
     * @throws GraphStoreException GraphStoreException
     */
    public static GraphStore openReadOnly(String dbDir) throws GraphStoreException {
        final GraphStoreOptions options = new GraphStoreOptions();
        return openReadOnly(dbDir, options);
    }

    /**
     * Close store
     *
     */
    public void close() {
        // NOTE frees the column family handles before freeing the db
        for (final ColumnFamilyHandle columnFamilyHandle : cfHandleList) {
            columnFamilyHandle.close();
        }
        db.close();
    }


    /**
     * Compact store
     *
     * @throws GraphStoreException GraphStoreException
     */
    public void compact() throws GraphStoreException {
        try {
            db.compactRange();
        } catch (RocksDBException e) {
            throw new GraphStoreException(e);
        }
    }

    /**
     * Add a triplet
     *
     * @param triplet Triplet
     */
    public void add(Triplet triplet) {
        try {
            Map<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> indexes = tripletIndexMap(triplet);
            for(Map.Entry<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> entry : indexes.entrySet()) {
                db.put(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
            }
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add a collection of triplets
     *
     * @param triplets Collection of Triplet
     */
    public void add(Collection<Triplet> triplets) {
        try {
            WriteBatch batch = new WriteBatch();
            for(Triplet triplet: triplets) {
                Map<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> indexes = tripletIndexMap(triplet);
                for(Map.Entry<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> entry : indexes.entrySet()) {
                    batch.put(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue());
                }
            }
            db.write(new WriteOptions(), batch);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param triplet Triplet
     * @return Map
     */
    protected Map<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> tripletIndexMap(Triplet triplet) {
        Map<ColumnFamilyHandle, Map.Entry<byte[],byte[]>> indexes = new HashMap<>();
        final byte[] value = (triplet.getSubject().toString() + '|' + triplet.getPredicate().toString() + '|' + triplet.getObject().toString()).getBytes();

        String keySPO = triplet.getSubject().toString() + triplet.getPredicate().toString() + triplet.getObject().toString();
        indexes.put(cfHandleList.get(0), new AbstractMap.SimpleEntry<>(keySPO.getBytes(), value));

        if(!options.isDisablePOSIndex()) {
            String keyPOS = triplet.getPredicate().toString() + triplet.getObject().toString() + triplet.getSubject().toString();
            indexes.put(cfHandleList.get(1), new AbstractMap.SimpleEntry<>(keyPOS.getBytes(), value));
        }

        return indexes;
    }

    /**
     * @param cfHandle ColumnFamilyHandle
     * @param key1 String
     * @param key2 String
     * @param key3 String
     * @return TripletIterator
     */
    protected TripletIterator query(ColumnFamilyHandle cfHandle, String key1, String key2, String key3) {
        String prefix = "";
        if(key1 != null) {
            prefix += key1;
        }
        if(key2 != null) {
            prefix += key2;
        }
        if(key3 != null) {
            prefix += key3;
        }
        return new TripletIterator(db.newIterator(cfHandle), prefix);
    }

    /**
     * @param cfHandle ColumnFamilyHandle
     * @return TripletIterator
     */
    protected TripletIterator query(ColumnFamilyHandle cfHandle) {
        return query(cfHandle, null, null, null);
    }

    /**
     * @param cfHandle ColumnFamilyHandle
     * @param key1     String
     * @return TripletIterator
     */
    protected TripletIterator query(ColumnFamilyHandle cfHandle, String key1) {
        return query(cfHandle, key1, null, null);
    }

    /**
     * @param cfHandle ColumnFamilyHandle
     * @param key1 String
     * @param key2 String
     * @return TripletIterator
     */
    protected TripletIterator query(ColumnFamilyHandle cfHandle, String key1, String key2) {
        return query(cfHandle, key1, key2, null);
    }

    /**
     * @return TripletIterator
     */
    public TripletIterator querySPO() {
        return querySPO(null, null, null);
    }

    /**
     * @param subject String
     * @return TripletIterator
     */
    public TripletIterator querySPO(String subject) {
        return querySPO(subject, null, null);
    }

    /**
     * @param subject String
     * @param predicate String
     * @return TripletIterator
     */
    public TripletIterator querySPO(String subject, String predicate) {
        return querySPO(subject, predicate, null);
    }

    /**
     * @param subject String
     * @param predicate String
     * @param object String
     * @return TripletIterator
     */
    public TripletIterator querySPO(String subject, String predicate, String object) {
        return query(cfHandleList.get(0), subject, predicate, object);
    }

    /**
     * @return TripletIterator
     */
    public TripletIterator queryPOS() {
        return queryPOS(null, null, null);
    }

    /**
     * @param predicate String
     * @return TripletIterator
     */
    public TripletIterator queryPOS(String predicate) {
        return queryPOS(predicate, null, null);
    }

    /**
     * @param predicate String
     * @param object String
     * @return TripletIterator
     */
    public TripletIterator queryPOS(String predicate, String object) {
        return queryPOS(predicate, object, null);
    }

    /**
     * @param predicate String
     * @param object String
     * @param subject String
     * @return TripletIterator
     */
    public TripletIterator queryPOS(String predicate, String object, String subject) {
        if(options.isDisablePOSIndex()) {
            throw new RuntimeException("POS index has been disabled in options");
        }
        return query(cfHandleList.get(1), predicate, object, subject);
    }

    /**
     * @return SubjectIterator
     */
    public SubjectIterator subjectIterator() {
        return new SubjectIterator(querySPO());
    }
}
