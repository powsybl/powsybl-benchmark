/*
 * Copyright (c) 2022-2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.state;

import static com.powsybl.benchmark.Constants.RTE_6515;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Rte6515NetworkState extends AbstractMatpowerNetworkState {

    @Override
    protected String getName() {
        return RTE_6515;
    }
}
