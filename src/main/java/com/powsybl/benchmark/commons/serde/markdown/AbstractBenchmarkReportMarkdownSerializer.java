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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractBenchmarkReportMarkdownSerializer {

    /**
     * Return the names of the columns to be put in the first line of the Markdown table.
     * @return name of columns, each string will correspond to a column
     */
    protected abstract String[] columnNames();

    /**
     * Return a formatted array of string, extracting the relevant values from the benchmark results.
     * The returned <code>String[]</code> should contain the same number of values as there are columns
     * (as defined by {@link #columnNames()}).
     * @return an array of string, each element corresponding to the column whose order is defined by {@link #columnNames()}
     */
    protected abstract String[] getLine(List<BenchmarkResult> results);

    /**
     * Define the function that dictates how results should be grouped by line.
     * @return a function that says which line of the resulting table a given benchmark result should be in, by providing a string that identifies the line
     */
    protected abstract Function<BenchmarkResult, String> getLineSorter();

    /**
     * Sort all the benchmark results of the report by the relevant information per line.
     * @param report the report of a given class
     * @return the benchmark results grouped in lists, each sub-list is grouped according to a criteria
     * and should contain the same number of results as there are columns (as defined by {@link #columnNames()}).
     */
    private List<List<BenchmarkResult>> getResultsByTableLine(BenchmarkReport report) {
        LinkedHashMap<String, List<BenchmarkResult>> byLine = new LinkedHashMap<>();
        Function<BenchmarkResult, String> lineSorter = getLineSorter();
        for (BenchmarkResult result : report.results()) {
            byLine.computeIfAbsent(lineSorter.apply(result), k -> new ArrayList<>()).add(result);
        }
        return new ArrayList<>(byLine.values());
    }

    public String reportToString(BenchmarkReport report) {
        //ensure decimal separator is dot
        Locale.setDefault(Locale.US);
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

    private String[][] valuesByLine(BenchmarkReport report) {
        List<List<BenchmarkResult>> resultsByNetwork = getResultsByTableLine(report);
        String[][] valuesByLine = new String[resultsByNetwork.size()][columnNames().length];
        for (int i = 0; i < resultsByNetwork.size(); ++i) {
            valuesByLine[i] = getLine(resultsByNetwork.get(i));
        }
        return valuesByLine;
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

    public static String getFormattedScore(BenchmarkResult result) {
        return getFormattedScore(result, DoubleUnaryOperator.identity());
    }

    public static String getFormattedScore(BenchmarkResult result, DoubleUnaryOperator scorePerOperationFormatter) {
        return String.format("%.2f %s", scorePerOperationFormatter.applyAsDouble(result.score()), result.scoreUnit());
    }
}
