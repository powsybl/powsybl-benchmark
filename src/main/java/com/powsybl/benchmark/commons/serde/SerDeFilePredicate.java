/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

/**
 * @author Nicolas Rol {@literal <nicolas.rol at rte-france.com>}
 */
public class SerDeFilePredicate {

    private final String startToMatch;
    private final String endToMatch;
    private final String stringThatShouldNotBeContained;

    public SerDeFilePredicate(String startToMatch, String endToMatch, String stringThatShouldNotBeContained) {
        this.startToMatch = startToMatch;
        this.endToMatch = endToMatch;
        this.stringThatShouldNotBeContained = stringThatShouldNotBeContained;
    }

    public boolean test(String fileName) {
        return fileName.startsWith(startToMatch)
            && fileName.endsWith(endToMatch)
            && (stringThatShouldNotBeContained.isEmpty() || !fileName.contains(stringThatShouldNotBeContained));
    }

    @Override
    public String toString() {
        return "SerDeFilePredicate{" +
            "startToMatch='" + startToMatch + '\'' +
            ", endToMatch='" + endToMatch + '\'' +
            ", stringThatShouldNotBeContained='" + stringThatShouldNotBeContained + '\'' +
            '}';
    }
}
