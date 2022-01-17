/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.serializer;

import com.conjecto.graphstore.AbstractTest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by blaise on 16/12/17.
 */
public class JsonLDFrameSerializerTest extends AbstractTest {
    @Test
    public void testSerialize() throws IOException {
        loadFromFixture();
        JsonLDFrameSerializer serializer = new JsonLDFrameSerializer();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        serializer.serialize(output, store, prefixMapping);

        System.out.println(output.toString());
    }
}