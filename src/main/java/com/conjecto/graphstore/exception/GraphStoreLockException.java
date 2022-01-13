/*
 * This file is part of the graphstore project.
 * 2022
 * @author Conjecto <contact@conjecto.com>
 * SPDX-License-Identifier: Apache-2.0
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */

package com.conjecto.graphstore.exception;

/**
 * com.conjecto.graphstore
 */
public class GraphStoreLockException extends GraphStoreException {
    public GraphStoreLockException() {
    }

    public GraphStoreLockException(String s) {
        super(s);
    }

    public GraphStoreLockException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GraphStoreLockException(Throwable throwable) {
        super(throwable);
    }

    public GraphStoreLockException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
