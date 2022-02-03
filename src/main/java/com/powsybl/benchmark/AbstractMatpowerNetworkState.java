/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.benchmark;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@State(Scope.Thread)
public abstract class AbstractMatpowerNetworkState {

    private Network network;

    @Setup(Level.Trial)
    public void doSetup() {
        String name = getName();
        network = Importers.getImporter("MATPOWER")
                .importData(new ResourceDataSource(name, new ResourceSet("/data", name + ".mat")),
                            new NetworkFactoryImpl(),
                            null);
    }

    protected abstract String getName();

    public Network getNetwork() {
        return network;
    }
}
