/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

/**
 * An utility class for exporting benchmark reports in different formats.
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class ExporterUtil {

    private ExporterUtil() {
        //util class, no instance
    }

    /**
     * Formats the datetime string from the given {@code BenchmarkReport} by transforming it into a
     * compact and readable format. The formatting involves removing fractional seconds,
     * replacing the 'T' delimiter with an underscore ('_'), and removing dashes and colons.
     * This is necessary since some operating systems (such as windows) cannot have filenames with colons.
     *
     * @param report The {@code BenchmarkReport} object containing the datetime to be formatted.
     *               The datetime string must adhere to the format: {@code yyyy-MM-dd'T'HH:mm:ss.SSS}.
     * @return A formatted datetime string with fractional seconds removed and symbols replaced
     *         as described. For example, a datetime string like {@code 2026-08-14T09:19:45.842589364}
     *         will be formatted as {@code 20260814_091945}.
     */
    public static String formatDatetime(BenchmarkReport report) {
        String datetime = report.datetime();
        //datetime is of the format 2026-08-14T09:19:45.842589364
        return datetime.substring(0, datetime.indexOf('.'))
            .replace('T', '_')
            .replace("-", "")
            .replace(":", "");
    }
}
