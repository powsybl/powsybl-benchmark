/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde;

import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * An utility class for exporting benchmark reports in different formats.
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public final class SerDeUtil {

    private SerDeUtil() {
        //util class, no instance
    }

    /**
     * Formats the ISO datetime string by transforming it into a compact and readable format.
     * The formatting involves removing fractional seconds, replacing the 'T' delimiter with an underscore ('_'),
     * and removing dashes and colons.
     * This is necessary since some operating systems (such as windows) cannot have filenames with colons.
     *
     * @param datetime The datetime string to be formatted.
     *                 The datetime string must adhere to the ISO format: {@code yyyy-MM-dd'T'HH:mm:ss.SSS}.
     * @return A formatted datetime string with fractional seconds removed and symbols replaced
     *         as described. For example, a datetime string like {@code 2026-08-14T09:19:45.842589364}
     *         will be formatted as {@code 20260814_091945}.
     */
    public static String formatISODatetimeToSimplified(String datetime) {
        //datetime is of the format 2026-08-14T09:19:45.842589364
        return datetime.substring(0, datetime.indexOf('.'))
            .replace('T', '_')
            .replace("-", "")
            .replace(":", "");
    }

    public static Predicate<Path> checkFileMatches(String startToMatch, String endToMatch, String stringThatShouldNotBeContained) {
        return p -> p.getFileName().toString().startsWith(startToMatch)
            && p.getFileName().toString().endsWith(endToMatch)
            && (stringThatShouldNotBeContained.isEmpty() || !p.getFileName().toString().contains(stringThatShouldNotBeContained));
    }

    public static Path getMatchingFileFromPath(Path directory, SerDeFilePredicate predicate) {
        try (Stream<Path> files = Files.list(directory)) {
            List<Path> matchingFiles = files.filter(p -> predicate.test(p.getFileName().toString())).toList();
            if (matchingFiles.size() > 1) {
                throw new PowsyblException("Multiple matching files found in directory " + directory + " for predicate "
                    + predicate + ": " + matchingFiles.stream().map(p -> p.getFileName().toString()).toList());
            } else if (matchingFiles.isEmpty()) {
                throw new PowsyblException("No matching file found in directory " + directory + " for predicate " + predicate);
            } else {
                return matchingFiles.getFirst();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
