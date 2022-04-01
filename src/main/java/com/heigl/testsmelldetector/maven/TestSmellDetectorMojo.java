package com.heigl.testsmelldetector.maven;

import com.heigl.testsmelldetector.maven.util.TestFileDetector;
import com.heigl.testsmelldetector.maven.util.TestSmellDetectorProvider;
import com.heigl.testsmelldetector.maven.util.TestSmellDetectorRunner;
import com.heigl.testsmelldetector.maven.util.TestSmellWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import testsmell.TestFile;

import java.util.List;

@Mojo(
        name = "detect",
        defaultPhase = LifecyclePhase.COMPILE
)
public class TestSmellDetectorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "detect.outputPath")
    private String outputPath;

    private final TestSmellDetectorProvider testSmellDetectorProvider = new TestSmellDetectorProvider();
    private final TestFileDetector testFileDetector = new TestFileDetector();
    private final TestSmellDetectorRunner testSmellDetectorRunner = new TestSmellDetectorRunner(testSmellDetectorProvider, getLog());
    private final TestSmellWriter testSmellWriter = new TestSmellWriter(testSmellDetectorProvider, getLog());

    @Override
    public void execute() {
        List<String> testCompileSourceRoots = project.getTestCompileSourceRoots();
        String appName = project.getName();
        String path;
        if (outputPath == null) {
            path = project.getBuild().getDirectory() + "/test-smells.csv";
        } else {
            path = outputPath;
        }

        List<TestFile> testFiles = testFileDetector.getTestFiles(testCompileSourceRoots, appName);
        List<TestFile> testFilesWithSmells = testSmellDetectorRunner.getTestSmells(testFiles);
        testSmellWriter.write(testFilesWithSmells, path);
    }
}
