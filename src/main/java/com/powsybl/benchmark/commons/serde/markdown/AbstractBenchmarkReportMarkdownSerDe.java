/**
 * Copyright (c) 2026, RTE (https://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.benchmark.commons.serde.markdown;

import com.powsybl.benchmark.commons.serde.BenchmarkReport;
import com.powsybl.benchmark.commons.serde.BenchmarkResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractBenchmarkReportMarkdownSerDe {

    /**
     * Return the names of the columns to be put in the first line of the Markdown table.
     * @return name of columns, each string will correspond to a column
     */
    protected abstract String[] columnNames();

    /**
     * Return the values of each line to be put in the Markdown table.
     * Each <code>String[]</code> should contain the same number of values as there are columns
     * (as defined by {@link #columnNames()}).
     * @return values of columns, each stream will correspond to a column
     */
    protected abstract String[][] valuesByLine(BenchmarkReport report);

    public String serialize(BenchmarkReport report, Path filePath) throws IOException {
        String serializedReport = reportToString(report);
        Files.writeString(filePath.resolve(report.benchmarkClass() + ".md"), serializedReport);
        return serializedReport;
    }

    private String reportToString(BenchmarkReport report) {
        StringBuilder tableBuilder = new StringBuilder();
        String[] columnNames = columnNames();
        String[][] valuesByLine = valuesByLine(report);
        int[] widthByColumn = calculateWidthPerColumn(columnNames, valuesByLine);
        buildHeader(tableBuilder, columnNames, widthByColumn);
        for (String[] lineValues : valuesByLine) {
            buildLine(tableBuilder, lineValues, widthByColumn);
        }

        return tableBuilder.toString();
    }

    private static int[] calculateWidthPerColumn(String[] columnNames, String[][] valuesByLine) {
        int[] widthByColumn = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; ++i) {
            //add 2 so there is at least one space on each side of each string
            widthByColumn[i] = columnNames[i].length() + 2;
        }
        for (String[] lineValues : valuesByLine) {
            for (int columnIndex = 0; columnIndex < columnNames.length; ++columnIndex) {
                int stringSize = lineValues[columnIndex] != null ? lineValues[columnIndex].length() : 0;
                widthByColumn[columnIndex] = Math.max(widthByColumn[columnIndex], stringSize + 2);
            }
        }
        return widthByColumn;
    }

    private static void buildHeader(StringBuilder tableBuilder, String[] columnNames, int[] widthByColumn) {
        buildLine(tableBuilder, columnNames, widthByColumn);
        for (int width : widthByColumn) {
            tableBuilder.append("|");
            tableBuilder.repeat("-", width);
        }
        tableBuilder.append("|");
        tableBuilder.append("\n");
    }

    private static void buildLine(StringBuilder tableBuilder, String[] lineValues, int[] widthByColumn) {
        tableBuilder.append("|");
        for (int i = 0; i < lineValues.length; ++i) {
            String name = lineValues[i];
            tableBuilder.append(" ");
            tableBuilder.append(name);
            tableBuilder.repeat(" ", widthByColumn[i] - name.length() - 1);
            tableBuilder.append("|");
        }
        tableBuilder.append("\n");
    }

    protected String getFormattedScore(BenchmarkResult result) {
        return String.format("%.2f %s", result.score(), result.scoreUnit());
    }
}
