package com.heigl.testsmelldetector.maven.util;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;
import testsmell.AbstractSmell;
import testsmell.TestFile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestSmellWriter {

    private final TestSmellDetectorProvider testSmellDetectorProvider;
    private final Log logger;

    public void write(List<TestFile> testFiles, String outputFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
            writer.writeNext(getHeader().toArray(new String[0]));
            testFiles.forEach(f -> {
                List<String> row = getRow(f);
                writer.writeNext(row.toArray(new String[0]));
            });
        } catch (IOException e) {
            throw new RuntimeException("Could not write csv file", e);
        }
    }

    private List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add("App");
        header.add("TestClass");
        header.add("TestFilePath");
        header.add("ProductionFilePath");
        header.add("RelativeTestFilePath");
        header.add("RelativeProductionFilePath");
        header.add("NumberOfMethods");
        header.addAll(testSmellDetectorProvider.getTestSmellNames());
        return header;
    }

    private List<String> getRow(TestFile testFile) {
        List<String> row = new ArrayList<>();

        row.add(testFile.getApp());
        row.add(testFile.getTestFileName());
        row.add(testFile.getTestFilePath());
        row.add(testFile.getProductionFilePath());
        row.add(testFile.getRelativeTestFilePath());
        row.add(testFile.getRelativeProductionFilePath());
        row.add(String.valueOf(testFile.getNumberOfTestMethods()));

        String smells = testFile.getTestSmells().stream()
                .filter(Objects::nonNull)
                .map(s -> s.getSmellName() + ": " + s.getNumberOfSmellyTests())
                .collect(Collectors.joining(","));
        logger.info("Smells: " + smells);

        for (AbstractSmell smell : testFile.getTestSmells()) {
            try {
                row.add(String.valueOf(smell.getNumberOfSmellyTests()));
            } catch (NullPointerException e) {
                row.add("");
            }
        }
        return row;
    }
}
