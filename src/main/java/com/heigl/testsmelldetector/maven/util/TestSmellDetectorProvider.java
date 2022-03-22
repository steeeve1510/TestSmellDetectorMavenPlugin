package com.heigl.testsmelldetector.maven.util;

import testsmell.TestSmellDetector;
import thresholds.DefaultThresholds;

import java.util.List;

public class TestSmellDetectorProvider {

    public TestSmellDetector getTestSmellDetector() {
        return new TestSmellDetector(new DefaultThresholds());
    }

    public List<String> getTestSmellNames() {
        return getTestSmellDetector().getTestSmellNames();
    }
}
