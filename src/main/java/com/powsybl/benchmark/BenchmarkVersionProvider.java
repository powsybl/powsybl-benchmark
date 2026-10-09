/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark;

import com.powsybl.tools.Version;
import picocli.CommandLine.IVersionProvider;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class BenchmarkVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        return Version.list().stream()
            .filter(v -> "powsybl-benchmark".equals(v.getRepositoryName()))
            .map(Version::getMavenProjectVersion)
            .findFirst()
            .map(version -> new String[] {version})
            .orElse(new String[] {"unknown"});
    }
}
