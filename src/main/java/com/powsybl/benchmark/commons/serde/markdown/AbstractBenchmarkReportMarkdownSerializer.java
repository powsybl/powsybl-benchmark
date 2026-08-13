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

import java.util.*;
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
     * Return a map where each key corresponds to a column name, and each value will be displayed in the table at the matching line and column
     * The returned <code>Map&lt;String, String&gt;</code> should contain the same number of entries as there are columns (and the keys should match)
     * (as defined by {@link #columnNames()}).
     * We use a map since we have no guarantee for the order of the results compared to the order of the columns.
     * @return a Map of strings, each key is a column name (as defined by {@link #columnNames()}), each value to be displayed on the line at that column
     */
    protected abstract Map<String, String> getLine(List<BenchmarkResult> results);

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
    protected List<List<BenchmarkResult>> getResultsByTableLine(BenchmarkReport report) {
        LinkedHashMap<String, List<BenchmarkResult>> byLine = new LinkedHashMap<>();
        Function<BenchmarkResult, String> lineSorter = getLineSorter();
        for (BenchmarkResult result : report.results()) {
            byLine.computeIfAbsent(lineSorter.apply(result), k -> new ArrayList<>()).add(result);
        }
        return new ArrayList<>(byLine.values());
    }

    /**
     * Separate the report into multiple reports if needed (see {@link #splitReport(BenchmarkReport)}).
     * For each of the split reports, transform it into the associated table (defined depending on the benchmark by the different classes that extend from
     * this abstract class).
     * @param report the original report to be transformed into one or more table
     * @return a map where the key is a name related to the table, and the value is the corresponding table
     */
    public Map<String, String> reportToStrings(BenchmarkReport report) {
        List<BenchmarkReport> splitReports = splitReport(report);
        Map<String, String> reportStrings = new HashMap<>();
        for (BenchmarkReport partReport : splitReports) {
            String tableName = getTableName(partReport);
            StringBuilder tableBuilder = new StringBuilder();
            String[] columnNames = columnNames();
            String[][] valuesByLine = valuesByLine(partReport);
            int[] widthByColumn = calculateWidthPerColumn(columnNames, valuesByLine);
            buildHeader(tableBuilder, columnNames, widthByColumn);
            for (String[] lineValues : valuesByLine) {
                buildLine(tableBuilder, lineValues, widthByColumn);
            }
            reportStrings.put(tableName, tableBuilder.toString());
        }
        return reportStrings;
    }

    /**
     * Split a report into multiple reports, each one will get the same header but a different list of {@link org.openjdk.jmh.results.RunResult}.
     * This is needed for benchmarks that need to display information in multiple tables (for example, when there are 3 or more variables).
     * If overriding this, also provide different table names for each report with {@link #getTableName(BenchmarkReport)}.
     * @param report the original report
     * @return a list of reports, each one of those will get a separate table
     */
    protected List<BenchmarkReport> splitReport(BenchmarkReport report) {
        return Collections.singletonList(report);
    }

    /**
     * Return the name of the table to be used for the name of the markdown file.
     * This is only useful if multiple tables have to be generated from a single starting report.
     * @param report a part of the original report, which will be serialized to a table
     * @return the name of the table, which will be put after the benchmark class name
     */
    @SuppressWarnings("java:S172")
    protected String getTableName(BenchmarkReport report) {
        return "";
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
        List<List<BenchmarkResult>> resultsByLine = getResultsByTableLine(report);
        String[][] valuesByLine = new String[resultsByLine.size()][columnNames().length];
        for (int i = 0; i < resultsByLine.size(); ++i) {
            Map<String, String> lineValues = getLine(resultsByLine.get(i));
            for (int j = 0; j < columnNames().length; ++j) {
                valuesByLine[i][j] = lineValues.get(columnNames()[j]);
            }
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

    /**
     * Format the score of a benchmark result.
     * @param result the benchmark result
     * @return the formatted score
     */
    public static String getFormattedScore(BenchmarkResult result) {
        return String.format("%.2f", result.score());
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     * @param result the benchmark result
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result) {
        return getFormattedScoreAndUnit(result, DoubleUnaryOperator.identity());
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     * @param result the benchmark result
     * @param scorePerOperationFormatter an operation to apply on the score before formatting
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result, DoubleUnaryOperator scorePerOperationFormatter) {
        return String.format("%.2f %s", scorePerOperationFormatter.applyAsDouble(result.score()), result.scoreUnit());
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     * @param score the score
     * @param unit the unit
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(double score, String unit) {
        return String.format("%.2f %s", score, unit);
    }
}
