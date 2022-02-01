package com.heigl.testsmelldetector.maven;

import com.heigl.testsmelldetector.maven.util.TestFileDetector;
import com.heigl.testsmelldetector.maven.util.TestSmellDetectorRunner;
import com.heigl.testsmelldetector.maven.util.TestSmellWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import testsmell.TestFile;
import testsmell.TestSmellDetector;
import thresholds.DefaultThresholds;

import java.util.List;

@Mojo(
        name = "detect",
        defaultPhase = LifecyclePhase.COMPILE
)
public class TestSmellDetectorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    private final TestSmellDetector testSmellDetector = new TestSmellDetector(new DefaultThresholds());
    private final TestFileDetector testFileDetector = new TestFileDetector();
    private final TestSmellDetectorRunner testSmellDetectorRunner = new TestSmellDetectorRunner(testSmellDetector, getLog());
    private final TestSmellWriter testSmellWriter = new TestSmellWriter(testSmellDetector, getLog());

    @Override
    public void execute() {
        List<String> testCompileSourceRoots = project.getTestCompileSourceRoots();
        String appName = project.getName();
        String outputFile = project.getBuild().getDirectory() + "/test-smells.csv";

        List<TestFile> testFiles = testFileDetector.getTestFiles(testCompileSourceRoots, appName);
        List<TestFile> testFilesWithSmells = testSmellDetectorRunner.getTestSmells(testFiles);
        testSmellWriter.write(testFilesWithSmells, outputFile);

        getLog().info("Test files: " + testFilesWithSmells);
    }
}
