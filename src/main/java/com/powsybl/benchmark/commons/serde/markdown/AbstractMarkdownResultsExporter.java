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
import com.powsybl.benchmark.commons.serde.ResultsExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Dissoubray Nathan {@literal <nathan.dissoubray at rte-france.com>}
 */
public abstract class AbstractMarkdownResultsExporter implements ResultsExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMarkdownResultsExporter.class);

    private final List<String> supportedBenchmarkClasses;

    protected AbstractMarkdownResultsExporter(List<String> supportedBenchmarkClasses) {
        this.supportedBenchmarkClasses = supportedBenchmarkClasses;
    }

    /**
     * Format the score of a benchmark result.
     *
     * @param result the benchmark result
     * @return the formatted score
     */
    public static String getFormattedScore(BenchmarkResult result) {
        return getFormattedScore(result, 2);
    }

    /**
     * Format the score of a benchmark result.
     *
     * @param result the benchmark result
     * @param decimals the number of decimals to keep
     * @return the formatted score
     */
    public static String getFormattedScore(BenchmarkResult result, int decimals) {
        String format = "%." + decimals + "f";
        return String.format(format, result.score());
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     *
     * @param result the benchmark result
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result) {
        return getFormattedScoreAndUnit(result, 2);
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     *
     * @param result the benchmark result
     * @param decimals the number of decimals to keep
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result, int decimals) {
        return getFormattedScoreAndUnit(result, DoubleUnaryOperator.identity(), decimals);
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     *
     * @param result                     the benchmark result
     * @param scorePerOperationFormatter an operation to apply on the score before formatting
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result, DoubleUnaryOperator scorePerOperationFormatter) {
        return getFormattedScoreAndUnit(result, scorePerOperationFormatter, 2);
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     *
     * @param result                     the benchmark result
     * @param scorePerOperationFormatter an operation to apply on the score before formatting
     * @param decimals the number of decimals to keep
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(BenchmarkResult result, DoubleUnaryOperator scorePerOperationFormatter,
                                                  int decimals) {
        String format = "%." + decimals + "f %s";
        return String.format(format, scorePerOperationFormatter.applyAsDouble(result.score()), result.scoreUnit());
    }

    /**
     * Format the score of a benchmark result with the associated unit.
     * @param score the score of the benchmark result
     * @param unit the unit of the benchmark result
     * @param scorePerOperationFormatter an operation to apply on the score before formatting
     * @return the formatted score with the associated unit
     */
    public static String getFormattedScoreAndUnit(double score, String unit, DoubleUnaryOperator scorePerOperationFormatter) {
        return String.format("%.2f %s", scorePerOperationFormatter.applyAsDouble(score), unit);
    }

    /**
     * Constructs an array of column names by combining a list of initial column names and
     * column names derived from a list of benchmark results using a column provider function.
     *
     * @param resultsForNetwork a list of {@link BenchmarkResult} objects representing the output of a benchmark for a network
     * @param columnsProvider a function that extracts a column name from a {@link BenchmarkResult} object
     * @param firstColumnNames an array of initial column names to be included at the start of the result
     * @return an array of combined column names including the initial column names and those derived from the benchmark results
     */
    protected static String[] getColumnNamesWithFirstColumnsAndColumnsProvider(List<BenchmarkResult> resultsForNetwork, Function<BenchmarkResult, String> columnsProvider, String... firstColumnNames) {
        String[] columnNames = new String[resultsForNetwork.size() + firstColumnNames.length];
        System.arraycopy(firstColumnNames, 0, columnNames, 0, firstColumnNames.length);
        resultsForNetwork.forEach(result -> columnNames[resultsForNetwork.indexOf(result) + firstColumnNames.length] = columnsProvider.apply(result));
        return columnNames;
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

    /**
     * Builds a Markdown table header by appending the column names and a separating line
     * to the provided StringBuilder.
     *
     * @param tableBuilder   the StringBuilder used to construct the table, where the header will be appended
     * @param columnNames    an array of strings representing the names of the columns to include in the header
     * @param widthByColumn  an array of integers representing the width of each column, used to align the content
     */
    private static void buildHeader(StringBuilder tableBuilder, String[] columnNames, int[] widthByColumn) {
        buildLine(tableBuilder, columnNames, widthByColumn);
        for (int width : widthByColumn) {
            tableBuilder.append("|");
            tableBuilder.repeat("-", width);
        }
        tableBuilder.append("|");
        tableBuilder.append("\n");
    }

    /**
     * Builds a line of a Markdown table by appending the provided line values,
     * appropriately aligned as per the specified column widths, to the given StringBuilder.
     *
     * @param tableBuilder   the StringBuilder used to construct the table, where the line will be appended
     * @param lineValues     an array of strings representing the values to include in this line of the table
     * @param widthByColumn  an array of integers representing the width of each column, used to align the content
     */
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
     * Separate the report into multiple reports if needed (see {@link #splitReport(BenchmarkReport)}).
     * For each of the split reports, transform it into the associated table (defined depending on the benchmark by the different classes that extend from
     * this abstract class).
     *
     * @param report the original report to be transformed into one or more table
     * @param baseline the baseline report to be compared with the original report (can be null)
     * @return a map where the key is a name related to the table, and the value is the corresponding table
     */
    @Override
    public Map<String, String> exportReport(BenchmarkReport report, BenchmarkReport baseline) {
        if (baseline != null && !report.benchmarkClass().equals(baseline.benchmarkClass())) {
            LOGGER.warn("Cannot compare reports with different benchmark classes: report {} and baseline {}", report.benchmarkClass(), baseline.benchmarkClass());
            return Map.of();
        }
        List<BenchmarkReport> splitReports = splitReport(report);
        Map<String, BenchmarkReport> baselineMap = baseline == null ? Map.of()
            : splitReport(baseline).stream()
            .collect(Collectors.toMap(this::getTableName, Function.identity()));
        Map<String, String> reportStrings = new HashMap<>();
        for (BenchmarkReport partReport : splitReports) {
            // Get the results grouped by line, each line will be a row in the table
            List<List<BenchmarkResult>> resultsByLine = getResultsByTableLine(partReport);

            // Initiate the table
            String tableName = getTableName(partReport);
            StringBuilder tableBuilder = new StringBuilder();

            // Get the column names from the first line of results, assuming all lines have the same columns
            String[] columnNames = columnNames(resultsByLine.getFirst());

            // Get the values for each line in the table
            String[][] valuesByLine = valuesByLine(resultsByLine, columnNames, baselineMap.get(tableName));

            // Calculate the width of each column based on the column names and the values in each line
            int[] widthByColumn = calculateWidthPerColumn(columnNames, valuesByLine);

            // Build the table and add it to the report strings map
            buildHeader(tableBuilder, columnNames, widthByColumn);
            for (String[] lineValues : valuesByLine) {
                buildLine(tableBuilder, lineValues, widthByColumn);
            }
            reportStrings.put(tableName, tableBuilder.toString());
        }
        return reportStrings;
    }

    @Override
    public boolean isBenchmarkClassSupported(String benchmarkClass) {
        return supportedBenchmarkClasses.contains(benchmarkClass);
    }

    /**
     * Return the names of the columns to be put in the first line of the Markdown table.
     *
     * @return name of columns, each string will correspond to a column
     */
    protected abstract String[] columnNames(List<BenchmarkResult> results);

    /**
     * Return a map where each key corresponds to a column name, and each value will be displayed in the table at the matching line and column
     * The returned <code>Map&lt;String, String&gt;</code> should contain the same number of entries as there are columns (and the keys should match)
     * (as defined by {@link #columnNames(List<BenchmarkResult>)}).
     * We use a map since we have no guarantee for the order of the results compared to the order of the columns.
     *
     * @return a Map of strings, each key is a column name (as defined by {@link #columnNames(List<BenchmarkResult>)}),
     * each value to be displayed on the line at that column
     */
    protected abstract Map<String, String> getLine(List<BenchmarkResult> results);

    protected abstract Map<String, Double> getLineScores(List<BenchmarkResult> results);

    /**
     * Define the function that dictates how results should be grouped by line.
     *
     * @return a function that says which line of the resulting table a given benchmark result should be in, by providing a string that identifies the line
     */
    protected abstract Function<BenchmarkResult, String> getLineSorter();

    /**
     * Sort all the benchmark results of the report by the relevant information per line.
     *
     * @param report the report of a given class
     * @return the benchmark results grouped in lists, each sub-list is grouped according to a criteria
     * and should contain the same number of results as there are columns (as defined by {@link #columnNames(List<BenchmarkResult>)}).
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
     * Split a report into multiple reports, each one will get the same header but a different list of {@link org.openjdk.jmh.results.RunResult}.
     * This is needed for benchmarks that need to display information in multiple tables (for example, when there are 3 or more variables).
     * If overriding this, also provide different table names for each report with {@link #getTableName(BenchmarkReport)}.
     *
     * @param report the original report
     * @return a list of reports, each one of those will get a separate table
     */
    protected List<BenchmarkReport> splitReport(BenchmarkReport report) {
        return Collections.singletonList(report);
    }

    /**
     * Return the name of the table to be used for the name of the markdown file.
     * This is only useful if multiple tables have to be generated from a single starting report.
     *
     * @param report a part of the original report, which will be serialized to a table
     * @return the name of the table, which will be put after the benchmark class name
     */
    @SuppressWarnings("java:S172")
    protected String getTableName(BenchmarkReport report) {
        return "";
    }

    private String[][] valuesByLine(List<List<BenchmarkResult>> resultsByLine, String[] columnNames, BenchmarkReport baseline) {
        List<List<BenchmarkResult>> baselineResultsByLine = baseline == null ? null : getResultsByTableLine(baseline);
        String[][] valuesByLine = new String[resultsByLine.size()][columnNames.length];
        for (int i = 0; i < resultsByLine.size(); ++i) {
            Map<String, String> lineValues = getLine(resultsByLine.get(i));
            Map<String, Double> lineScores = getLineScores(resultsByLine.get(i));
            Map<String, Double> baselineLineScores = baselineResultsByLine == null ? Map.of() : getLineScores(baselineResultsByLine.get(i));
            for (int j = 0; j < columnNames.length; ++j) {
                String columnName = columnNames[j];
                valuesByLine[i][j] = lineValues.get(columnName) + getBaselineRelativeDifference(
                    lineScores.get(columnName), baselineLineScores.get(columnName)
                );
            }
        }
        return valuesByLine;
    }

    private String getBaselineRelativeDifference(Double resultScore, Double baselineScore) {
        if (resultScore == null || baselineScore == null) {
            return "";
        }
        double relativeDifference = Math.round(100 * (resultScore / baselineScore - 1));
        String symbol = relativeDifference > 0 ? "+" : "";
        return String.format(" (%s%.0f%%)", symbol, relativeDifference);
    }
}
