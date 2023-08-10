package com.powsybl.benchmark;

import com.powsybl.iidm.network.test.EuropeanLvTestFeederFactory;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import com.powsybl.iidm.network.Network;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class EuropeanLvTestFeederFactoryState {

    private Network europeeanLv;

    @Setup(Level.Trial)
    public void doSetup() {
        europeeanLv = EuropeanLvTestFeederFactory.create();
    }

    public Network getEuropeeanLvNetwork() {
        return europeeanLv;
    }
}
