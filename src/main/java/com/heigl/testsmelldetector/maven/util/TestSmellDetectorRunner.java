package com.heigl.testsmelldetector.maven.util;

import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;
import testsmell.TestFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestSmellDetectorRunner {

    private final TestSmellDetectorProvider testSmellDetectorProvider;
    private final Log logger;

    public List<TestFile> getTestSmells(List<TestFile> testFiles) {
        return testFiles.stream()
                .map(this::safelyDetectSmells)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private TestFile safelyDetectSmells(TestFile testFile) {
        try {
            return testSmellDetectorProvider.getTestSmellDetector().detectSmells(testFile);
        } catch (IOException e) {
            logger.warn("Could not detect smells", e);
            return null;
        }
    }
}
