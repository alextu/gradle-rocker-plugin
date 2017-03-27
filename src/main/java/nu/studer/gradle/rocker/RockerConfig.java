package nu.studer.gradle.rocker;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

import java.io.File;

public class RockerConfig {

    final String name;
    private final Project project;

    private Action<? super JavaExecSpec> javaExecSpec;
    private Action<? super ExecResult> execResultHandler;

    private boolean optimize;
    private File templateDir;
    private File outputDir;

    RockerConfig(String name, Project project) {
        this.name = name;
        this.project = project;
    }

    @Internal
    Action<? super JavaExecSpec> getJavaExecSpec() {
        return javaExecSpec;
    }

    @SuppressWarnings("unused")
    public void setJavaExecSpec(Action<? super JavaExecSpec> javaExecSpec) {
        this.javaExecSpec = javaExecSpec;
    }

    @Internal
    Action<? super ExecResult> getExecResultHandler() {
        return execResultHandler;
    }

    @SuppressWarnings("unused")
    public void setExecResultHandler(Action<? super ExecResult> execResultHandler) {
        this.execResultHandler = execResultHandler;
    }

    @Input
    boolean isOptimize() {
        return optimize;
    }

    @SuppressWarnings("unused")
    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    @InputDirectory
    File getTemplateDir() {
        return templateDir;
    }

    @SuppressWarnings("unused")
    public void setTemplateDir(File templateDir) {
        this.templateDir = templateDir;
    }

    @OutputDirectory
    File getOutputDir() {
        return outputDir;
    }

    @SuppressWarnings("unused")
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;

        SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
        SourceSet sourceSet = sourceSets.findByName(name);
        if (sourceSet != null) {
            sourceSet.getJava().srcDir(outputDir);
        }
    }

    @Override
    public String toString() {
        return "RockerConfig{" +
            "name='" + name + '\'' +
            ", javaExecSpec=" + javaExecSpec +
            ", execResultHandler=" + execResultHandler +
            ", optimize=" + optimize +
            ", templateDir=" + templateDir +
            ", outputDir=" + outputDir +
            '}';
    }

}
