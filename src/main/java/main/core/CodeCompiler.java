package main.core;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeCompiler {

    public String extractClassName(String code) {
        Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public File createTempDirectory() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "codeanalysis_" + System.currentTimeMillis());
        if (!tempDir.mkdir()) {
            throw new IOException("Failed to create temporary directory");
        }
        return tempDir;
    }

    public void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public String compileCode(File sourceFile) throws IOException {
        ProcessBuilder compileBuilder = new ProcessBuilder(
                "javac",
                sourceFile.getAbsolutePath()
        );
        compileBuilder.redirectErrorStream(true);
        Process compileProcess = compileBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        try {
            int exitCode = compileProcess.waitFor();
            if (exitCode != 0) {
                return output.toString();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Compilation interrupted", e);
        }

        return null;
    }
}
