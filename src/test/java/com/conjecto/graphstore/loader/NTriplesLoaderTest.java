/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.loader;

import com.conjecto.graphstore.AbstractTest;
import com.conjecto.graphstore.GraphLoader;
import com.conjecto.graphstore.GraphStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

/**
 * Created by blaise on 16/12/17.
 */
public class NTriplesLoaderTest extends AbstractTest {
    @Test
    public void testLoad() throws FileNotFoundException {
        loadFromFixture();
        Assert.assertTrue(store.querySPO().toList().size() > 0);
    }
}