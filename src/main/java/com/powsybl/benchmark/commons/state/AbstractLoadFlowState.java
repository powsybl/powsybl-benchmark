/*
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.state;

import com.powsybl.benchmark.commons.MatpowerUtil;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.ieeecdf.converter.IeeeCdfNetworkFactory;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import static com.powsybl.benchmark.commons.Constants.IEEE_118;
import static com.powsybl.benchmark.commons.Constants.IEEE_14;
import static com.powsybl.benchmark.commons.Constants.IEEE_300;
import static com.powsybl.benchmark.commons.Constants.REAL_GRID;
import static com.powsybl.benchmark.commons.Constants.RTE_1888;
import static com.powsybl.benchmark.commons.Constants.RTE_6515;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
@State(Scope.Thread)
public abstract class AbstractLoadFlowState {

    @Param({"OpenLoadFlow"})
    private String provider;

    protected Network network;
    protected LoadFlowParameters parameters;

    @Setup(Level.Trial)
    public void doSetup() {
        network = loadNetwork(getNetworkName());
        setLoadFlowParameters();
    }

    public Network getNetwork() {
        return network;
    }

    public String getProvider() {
        return provider;
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }

    public int getThreadCount() {
        return 1;
    }

    protected abstract String getNetworkName();

    protected abstract void setLoadFlowParameters();

    private Network loadNetwork(String networkName) {
        return switch (networkName) {
            case IEEE_14 -> IeeeCdfNetworkFactory.create14();
            case IEEE_118 -> IeeeCdfNetworkFactory.create118();
            case IEEE_300 -> IeeeCdfNetworkFactory.create300();
            case REAL_GRID -> Network.read(new ResourceDataSource(networkName, new ResourceSet("/data", networkName + ".zip")));
            case RTE_1888, RTE_6515 -> MatpowerUtil.importMat(networkName);
            default -> throw new IllegalArgumentException("Unknown network: " + networkName);
        };
    }
}
