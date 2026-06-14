package main.core;

import java.io.*;
import java.util.concurrent.TimeUnit;

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

    public PerformanceMetrics executeAndMeasure(File directory, String className, String input) throws Exception {
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

        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(50);
        }

        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        Thread.sleep(100);

        long startMemory = Math.max(0, runtime.totalMemory() - runtime.freeMemory());
        long startTime = System.nanoTime();

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

        long endTime = System.nanoTime();
        runtime.gc();
        Thread.sleep(100);
        long endMemory = Math.max(0, runtime.totalMemory() - runtime.freeMemory());
        long memoryUsed = Math.max(0, endMemory - startMemory);

        return new PerformanceMetrics(
                (endTime - startTime) / 1_000_000.0,
                memoryUsed
        );
    }
}
