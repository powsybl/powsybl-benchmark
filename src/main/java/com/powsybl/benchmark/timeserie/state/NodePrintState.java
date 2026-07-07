/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.timeserie.state;

import com.powsybl.timeseries.ast.BinaryOperation;
import com.powsybl.timeseries.ast.IntegerNodeCalc;
import com.powsybl.timeseries.ast.NodeCalc;
import org.openjdk.jmh.annotations.*;

/**
 * @author Samir Romdhani {@literal <samir.romdhani at rte-france.com>}
 */
@State(Scope.Thread)
public class NodePrintState {

    @Param({"1000", "2000", "4000", "8000"})
    private int size;
    private NodeCalc node;

    @Setup(Level.Trial)
    public void setup() {
        node = new IntegerNodeCalc(0);
        for (int i = 0; i < size; i++) {
            node = BinaryOperation.plus(new IntegerNodeCalc(1), node);
        }
    }

    public NodeCalc getNode() {
        return node;
    }
}
