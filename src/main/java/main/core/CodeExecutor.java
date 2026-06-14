package main.core;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.profile.GCProfiler;

public class CodeExecutor {

    public static class PerformanceMetrics {
        public final double executionTime;
        public final double memoryUsed;

        public PerformanceMetrics(double executionTime, double memoryUsed) {
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
        }
    }

    private String executionOutput = "";

    public String getExecutionOutput() {
        return executionOutput;
    }

    @State(Scope.Benchmark)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class UserCodeBenchmark {

        @Param({""})
        public String classDir;

        @Param({""})
        public String className;

        @Param({""})
        public String inputFilePath;

        private Method mainMethod;
        private byte[] inputBytes;
        private InputStream originalIn;
        private PrintStream originalOut;
        private PrintStream originalErr;

        @Setup(Level.Trial)
        public void setupTrial() throws Exception {
            URL url = new File(classDir).toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());
            Class<?> clazz = classLoader.loadClass(className);
            mainMethod = clazz.getMethod("main", String[].class);
            
            if (inputFilePath != null && !inputFilePath.isEmpty()) {
                inputBytes = Files.readAllBytes(Paths.get(inputFilePath));
            } else {
                inputBytes = new byte[0];
            }
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            originalIn = System.in;
            originalOut = System.out;
            originalErr = System.err;
            
            System.setIn(new ByteArrayInputStream(inputBytes));
            // Suppress output during benchmarking
            System.setOut(new PrintStream(new OutputStream() { public void write(int b) {} }));
            System.setErr(new PrintStream(new OutputStream() { public void write(int b) {} }));
        }

        @TearDown(Level.Invocation)
        public void tearDownInvocation() {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        @Benchmark
        public void executeCode() throws Exception {
            mainMethod.invoke(null, (Object) new String[]{});
        }
    }

    public PerformanceMetrics executeAndMeasure(File directory, String className, String input) throws Exception {
        // Step 1: Run the process once to capture execution output and verify it succeeds
        ProcessBuilder runBuilder = new ProcessBuilder(
                "java",
                "-XX:+UseSerialGC",
                "-Xms64m",
                "-Xmx512m",
                "-cp",
                directory.getAbsolutePath(),
                className
        );
        runBuilder.redirectErrorStream(true);
        Process runProcess = runBuilder.start();
        StringBuilder output = new StringBuilder();

        Thread inputThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(runProcess.getOutputStream()))) {
                String[] inputLines = input.split("\n");
                for (String line : inputLines) {
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        inputThread.start();
        outputThread.start();
        inputThread.join();
        outputThread.join();

        if (!runProcess.waitFor(10, TimeUnit.SECONDS)) {
            runProcess.destroyForcibly();
            throw new Exception("Process timed out");
        }

        int exitCode = runProcess.exitValue();
        if (exitCode != 0) {
            throw new Exception("Program execution failed with exit code " + exitCode + ": " + output);
        }

        this.executionOutput = output.toString();

        // Step 2: Write input to a temp file for the JMH benchmark
        Path tempInputFile = Files.createTempFile("jmh_input_", ".txt");
        try {
            Files.write(tempInputFile, input.getBytes());

            // Step 3: Configure and run JMH
            Options opt = new OptionsBuilder()
                    .include(".*" + UserCodeBenchmark.class.getSimpleName() + ".*")
                    .param("classDir", directory.getAbsolutePath())
                    .param("className", className)
                    .param("inputFilePath", tempInputFile.toAbsolutePath().toString())
                    .forks(1)
                    .warmupIterations(2)
                    .warmupTime(org.openjdk.jmh.runner.options.TimeValue.seconds(1))
                    .measurementIterations(3)
                    .measurementTime(org.openjdk.jmh.runner.options.TimeValue.seconds(1))
                    .addProfiler(GCProfiler.class)
                    .build();

            Collection<RunResult> results = new Runner(opt).run();
            if (results == null || results.isEmpty()) {
                throw new Exception("JMH benchmarking failed to produce results.");
            }

            RunResult result = results.iterator().next();
            
            // Average time per operation in milliseconds (as configured by @OutputTimeUnit)
            double timeMs = result.getPrimaryResult().getScore();
            
            // Extract memory allocated (bytes per operation)
            double memoryUsedBytes = 0;
            if (result.getSecondaryResults().containsKey("gc.alloc.rate.norm")) {
                memoryUsedBytes = result.getSecondaryResults().get("gc.alloc.rate.norm").getScore();
            }

            return new PerformanceMetrics(timeMs, memoryUsedBytes);

        } finally {
            Files.deleteIfExists(tempInputFile);
        }
    }
}
