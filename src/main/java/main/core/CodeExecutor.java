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

    public static class PerformanceMetrics implements Serializable {
        private static final long serialVersionUID = 1L;
        public final double executionTime;
        public final double memoryUsed;
        public final double throughput;
        public final double gcPauseTime;
        public final double heapAllocationRate;
        public final double p50Latency;
        public final double p95Latency;
        public final double p99Latency;

        public PerformanceMetrics(double executionTime, double memoryUsed, double throughput,
                                  double gcPauseTime, double heapAllocationRate,
                                  double p50Latency, double p95Latency, double p99Latency) {
            this.executionTime = executionTime;
            this.memoryUsed = memoryUsed;
            this.throughput = throughput;
            this.gcPauseTime = gcPauseTime;
            this.heapAllocationRate = heapAllocationRate;
            this.p50Latency = p50Latency;
            this.p95Latency = p95Latency;
            this.p99Latency = p99Latency;
        }
    }

    private String executionOutput = "";

    public String getExecutionOutput() {
        return executionOutput;
    }

    @State(Scope.Benchmark)
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SampleTime})
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
                writer.write(input);
                if (!input.endsWith("\n")) {
                    writer.newLine();
                }
                writer.flush();
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
        
        try {
            inputThread.join();
            outputThread.join();

            if (!runProcess.waitFor(10, TimeUnit.SECONDS)) {
                runProcess.destroyForcibly();
                throw new Exception("Process timed out");
            }
        } catch (InterruptedException e) {
            runProcess.destroyForcibly();
            inputThread.interrupt();
            outputThread.interrupt();
            throw e;
        }

        int exitCode = runProcess.exitValue();
        if (exitCode != 0) {
            throw new Exception("Program execution failed with exit code " + exitCode + ": " + output);
        }

        this.executionOutput = output.toString();

        // Step 2: Write input to a temp file for the JMH benchmark
        Path tempInputFile = Files.createTempFile("jmh_input_", ".txt");
        Path resultFile = Files.createTempFile("jmh_result_", ".dat");
        try {
            Files.write(tempInputFile, input.getBytes());

            // Step 3: Run JMH in a separate process to allow forceful termination
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "main.core.CodeExecutor",
                    "JMH_RUN",
                    directory.getAbsolutePath(),
                    className,
                    tempInputFile.toAbsolutePath().toString(),
                    resultFile.toAbsolutePath().toString()
            );
            
            // Redirect output to avoid console clutter, or you can inheritIO
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            
            Process jmhProcess = pb.start();
            try {
                int jmhExit = jmhProcess.waitFor();
                if (jmhExit != 0) {
                    throw new Exception("JMH benchmarking process failed with exit code " + jmhExit);
                }
                
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(resultFile.toFile()))) {
                    return (PerformanceMetrics) ois.readObject();
                }
            } catch (InterruptedException e) {
                jmhProcess.destroyForcibly();
                throw e;
            }

        } finally {
            Files.deleteIfExists(tempInputFile);
            Files.deleteIfExists(resultFile);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 5 && args[0].equals("JMH_RUN")) {
            String classDir = args[1];
            String className = args[2];
            String inputFilePath = args[3];
            String resultFilePath = args[4];

            Options opt = new OptionsBuilder()
                    .include(".*" + UserCodeBenchmark.class.getSimpleName() + ".*")
                    .param("classDir", classDir)
                    .param("className", className)
                    .param("inputFilePath", inputFilePath)
                    .forks(1)
                    .warmupIterations(2)
                    .warmupTime(org.openjdk.jmh.runner.options.TimeValue.seconds(1))
                    .measurementIterations(3)
                    .measurementTime(org.openjdk.jmh.runner.options.TimeValue.seconds(1))
                    .addProfiler(GCProfiler.class)
                    .build();

            Collection<RunResult> results = new Runner(opt).run();
            if (results == null || results.isEmpty()) {
                System.exit(1);
            }

            double timeMs = 0;
            double memoryUsedBytes = 0;
            double throughputOpsPerSec = 0;
            double gcPauseTimeMs = 0;
            double heapAllocationRateMbPerSec = 0;
            double p50LatencyMs = 0;
            double p95LatencyMs = 0;
            double p99LatencyMs = 0;

            for (RunResult result : results) {
                String mode = result.getParams().getMode().name();
                if (mode.equals("AverageTime")) {
                    timeMs = result.getPrimaryResult().getScore();
                    if (result.getSecondaryResults().containsKey("gc.alloc.rate.norm")) {
                        memoryUsedBytes = result.getSecondaryResults().get("gc.alloc.rate.norm").getScore();
                    }
                    if (result.getSecondaryResults().containsKey("gc.alloc.rate")) {
                        heapAllocationRateMbPerSec = result.getSecondaryResults().get("gc.alloc.rate").getScore();
                    }
                    if (result.getSecondaryResults().containsKey("gc.time")) {
                        gcPauseTimeMs = result.getSecondaryResults().get("gc.time").getScore();
                    }
                } else if (mode.equals("Throughput")) {
                    throughputOpsPerSec = result.getPrimaryResult().getScore();
                } else if (mode.equals("SampleTime")) {
                    org.openjdk.jmh.util.Statistics stats = result.getPrimaryResult().getStatistics();
                    p50LatencyMs = stats.getPercentile(50.0);
                    p95LatencyMs = stats.getPercentile(95.0);
                    p99LatencyMs = stats.getPercentile(99.0);
                }
            }

            PerformanceMetrics metrics = new PerformanceMetrics(timeMs, memoryUsedBytes, throughputOpsPerSec, gcPauseTimeMs,
                    heapAllocationRateMbPerSec, p50LatencyMs, p95LatencyMs, p99LatencyMs);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resultFilePath))) {
                oos.writeObject(metrics);
            }
            System.exit(0);
        }
    }
}
