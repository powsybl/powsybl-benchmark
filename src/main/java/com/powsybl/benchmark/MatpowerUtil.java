/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.network.Importer;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public final class MatpowerUtil {

    private MatpowerUtil() {
    }

    public static Network importMat(String name) {
        Network network = Importer.find("MATPOWER")
                .importData(new ResourceDataSource(name, new ResourceSet("/data", name + ".mat")),
                        new NetworkFactoryImpl(),
                        null);
        // FIX RTE cases as it seems there is data issue with phase shift
        for (var twt : network.getTwoWindingsTransformers()) {
            var ptc = twt.getPhaseTapChanger();
            if (ptc != null) {
                ptc.getCurrentStep().setAlpha(0);
            }
        }
        return network;
    }
}
