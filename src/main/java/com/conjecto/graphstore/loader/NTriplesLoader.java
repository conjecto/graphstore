/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.loader;

import com.conjecto.graphstore.GraphStore;
import com.conjecto.graphstore.Loader;
import com.conjecto.graphstore.Triplet;
import org.semanticweb.yars.nx.parser.NxParser;

import java.io.InputStream;

/**
 * NTriplesLoader
 */
public class NTriplesLoader implements Loader {
    @Override
    public void load(GraphStore store, InputStream input) {
        NxParser parser = new NxParser();
        parser.parse(input);
        for (org.semanticweb.yars.nx.Node[] nx : parser) {
            Triplet triplet = new Triplet(nx[0], nx[1], nx[2]);
            store.add(triplet);
        }
    }
}
