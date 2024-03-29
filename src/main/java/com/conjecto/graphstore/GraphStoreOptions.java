/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import org.rocksdb.DBOptions;

/**
 * com.conjecto.graphstore
 */
public class GraphStoreOptions {
    private DBOptions dbOptions = new DBOptions();
    private boolean disablePOSIndex = false;


    /**
     * @return DBOptions
     */
    public DBOptions getDBOptions() {
        return dbOptions;
    }

    /**
     * @return boolean
     */
    public boolean isDisablePOSIndex() {
        return disablePOSIndex;
    }

    /**
     * @param disablePOSIndex boolean
     * @return GraphStoreOptions
     */
    public GraphStoreOptions setDisablePOSIndex(final boolean disablePOSIndex) {
        this.disablePOSIndex = disablePOSIndex;
        return this;
    }

    /**
     * @param flag boolean
     * @return GraphStoreOptions
     */
    public GraphStoreOptions setCreateIfMissing(final boolean flag) {
        this.dbOptions.setCreateIfMissing(flag);
        return this;
    }
}
