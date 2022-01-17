/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

import com.conjecto.graphstore.exception.GraphStoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * AbstractTest
 */
public abstract class AbstractTest {
    @Rule
    public TemporaryFolder db = new TemporaryFolder();

    protected GraphStore store;
    protected PrefixMapping prefixMapping;

    @Before
    public void setUpClass() throws GraphStoreException {
        // load store
        GraphStoreOptions options = (new GraphStoreOptions()).setCreateIfMissing(true);
        store = GraphStore.open(db.getRoot().getAbsolutePath(), options);

        prefixMapping = new PrefixMapping();
        prefixMapping.set("owl", "http://www.w3.org/2002/07/owl#");
        prefixMapping.set("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        prefixMapping.set("xml", "http://www.w3.org/XML/1998/namespace");
        prefixMapping.set("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixMapping.set("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixMapping.set("schema", "http://schema.org/");
        prefixMapping.set("", "http://example.org/");
        prefixMapping.set("elmt", "http://example.org/elements/");
        prefixMapping.set("wiki", "http://en.wikipedia.org/wiki/");
    }

    @After
    public void afterClass() {
        if(store != null) {
            store.close();
        }
    }

    protected void loadFromFixture() throws FileNotFoundException {
        File sampleFile = new File(getClass().getClassLoader().getResource("fixtures/sample.nt").getFile());
        GraphLoader loader = new GraphLoader(store, "nt");
        loader.load(new FileInputStream(sampleFile));
    }
}
