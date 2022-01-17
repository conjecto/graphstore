/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore;

/**
 * SerializerFactory
 */
public interface SerializerFactory {
    /**
     * @return Serializer
     */
    public Serializer create() ;
}
