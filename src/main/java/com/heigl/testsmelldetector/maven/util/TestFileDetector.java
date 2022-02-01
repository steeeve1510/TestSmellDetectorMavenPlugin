package com.heigl.testsmelldetector.maven.util;

import testsmell.TestFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestFileDetector {

    public List<TestFile> getTestFiles(List<String> directories, String appName) {
        return directories.stream()
                .map(d -> getTestFiles(d, appName))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<TestFile> getTestFiles(String directory, String appName) {
        try (Stream<Path> stream = Files.walk(Paths.get(directory))) {
            return getTestFilesForStream(stream, appName);
        } catch (IOException e) {
            throw new RuntimeException("Could not list test files", e);
        }
    }

    private List<TestFile> getTestFilesForStream(Stream<Path> stream, String appName) {
        return stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .filter(this::isTestFile)
                .map(f -> new TestFile(appName, f, ""))
                .collect(Collectors.toList());
    }

    // https://maven.apache.org/surefire-archives/surefire-3.0.0-M2/maven-surefire-plugin/examples/inclusion-exclusion.html
    private boolean isTestFile(String path) {
        return path.matches(".*/Test[^/]*\\.java")
                || path.matches(".*/[^/]*Test\\.java")
                || path.matches(".*/[^/]*Tests\\.java")
                || path.matches(".*/[^/]*TestCase\\.java");
    }
}
