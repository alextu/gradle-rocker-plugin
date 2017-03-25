package nu.studer.gradle.rocker

import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Unroll

@Unroll
class RockerFuncTest extends BaseFuncTest {

    void "can invoke rocker task derived from minimum configuration DSL"() {
        given:
        exampleTemplate()

        and:
        buildFile << """
plugins {
    id 'nu.studer.rocker'
}

repositories {
    jcenter()
}

rocker {
  foo {
    optimize = true
    templateDir = file('src/rocker')
    outputDir = file('src/generated/rocker')
  }
}
"""

        when:
        def result = runWithArguments('rockerFoo')

        then:
        fileExists('src/generated/rocker/Example.java')
        result.output.contains("Parsing 1 rocker template files")
        result.output.contains("Generated 1 rocker java source files")
        result.output.contains("Generated rocker configuration")
        result.task(':rockerFoo').outcome == TaskOutcome.SUCCESS
    }

    void "can invoke rocker task derived from configuration DSL with multiple items"() {
        given:
        template('src/rocker/main/Example.rocker.html')
        template('src/rocker/test/ExampleTest.rocker.html')

        and:
        buildFile << """
plugins {
    id 'nu.studer.rocker'
}

repositories {
    jcenter()
}

rocker {
  main {
    optimize = true
    templateDir = file('src/rocker/main')
    outputDir = file('src/generated/rocker/main')
  }
  integTest {
    optimize = true
    templateDir = file('src/rocker/test')
    outputDir = file('src/generated/rocker/test')
  }
}
"""

        when:
        def result = runWithArguments('rockerIntegTest')

        then:
        fileExists('src/generated/rocker/test/ExampleTest.java')
        result.task(':rockerIntegTest').outcome == TaskOutcome.SUCCESS

        !fileExists('src/generated/rocker/main/Example.java')
        !result.task(':rockerMain')
    }

    void "can compile Java source files generated by rocker as part of invoking Java compile task with the matching source set"() {
        given:
        exampleTemplate()

        and:
        buildFile << """
plugins {
    id 'nu.studer.rocker'
    id 'java'  // provides 'main' sourceSet
}

repositories {
    jcenter()
}

dependencies {
    compile 'com.fizzed:rocker-runtime:0.16.0'
}

rocker {
  main {
    optimize = true
    templateDir = file('src/rocker')
    outputDir = file('src/generated/rocker')
  }
}
"""

        when:
        def result = runWithArguments('classes')

        then:
        fileExists('src/generated/rocker/Example.java')
        fileExists('build/classes/main/Example.class')
        result.task(':rockerMain').outcome == TaskOutcome.SUCCESS
        result.task(':classes').outcome == TaskOutcome.SUCCESS
    }

    private File exampleTemplate() {
        template('src/rocker/Example.rocker.html')
    }

    private void template(String fileName) {
        file(fileName) << """
@args (String message)
Hello @message!
"""
    }

    private boolean fileExists(String filePath) {
        def file = new File(workspaceDir, filePath)
        file.exists() && file.file
    }

}
