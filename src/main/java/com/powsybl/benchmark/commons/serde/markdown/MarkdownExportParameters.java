/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class MarkdownExportParameters {
    private boolean useColors = false;
    private double resultVariationWarningThreshold = 0.1;

    public boolean useColors() {
        return useColors;
    }

    /**
     * Sets whether colors should be used to highlight variation to the baseline.
     * Default is {@code false}
     *
     * @param useColors true to enable the use of colors, false to disable it
     */
    public void setUseColors(boolean useColors) {
        this.useColors = useColors;
    }

    public double getResultVariationWarningThreshold() {
        return resultVariationWarningThreshold;
    }

    /**
     * Sets the threshold for warning about results that are within error margins but still have a big relative variations.
     * All variations above this threshold that are within error margins will be highlighted in the exported Markdown tables.
     * If it is outside error margins, another color will be used.
     * Default is {@code 0.1}
     *
     * @param resultVariationWarningThreshold the threshold value
     */
    public void setResultVariationWarningThreshold(double resultVariationWarningThreshold) {
        this.resultVariationWarningThreshold = resultVariationWarningThreshold;
    }
}
