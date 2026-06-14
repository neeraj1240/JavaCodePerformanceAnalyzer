package main.core;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputGenerator {
    private final Random random = new Random(42);
    private String generatedInput = "";

    public String getGeneratedInput() {
        return generatedInput;
    }

    private final String[] COMMON_WORDS = {"the","of","and","a","to","in","is","you","that",
            "it","he","was","for","on","are","as","with","his","they","I","at",
            "be","this","have","from","or","one","had","by","word","but","not",
            "what","all","were","we","when","your","can","said","there","use",
            "an","each","which","she","do","how","their","if","will","up","other",
            "about","out","many","then","them","these","so","some","her","would",
            "make","like","him","into","time","has","look","two","more","write","go",
            "see","number","no","way","could","people","my","than","first","water",
            "been","call","who","oil","its","now","find","long","down","day","did",
            "get","come","made","may","part"};

    public String generateInput(String code, int size) {
        StringBuilder input = new StringBuilder();

        if (hasSingleStringInput(code)) {
            StringBuilder sentence = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sentence.append(COMMON_WORDS[random.nextInt(COMMON_WORDS.length)]);
                if (i < size - 1) {
                    sentence.append(" ");
                }
            }
            input.append(sentence);
        } else if (code.contains("Scanner")) {
            if (code.contains("matrix") || code.contains("[][]")) {
                int matrixDim = Math.min(size, 100);

                input.append(matrixDim).append("\n");
                input.append(matrixDim).append("\n");

                for (int i = 0; i < matrixDim; i++) {
                    for (int j = 0; j < matrixDim; j++) {
                        input.append(random.nextInt(10)).append(" ");
                    }
                    input.append("\n");
                }
                for (int i = 0; i < matrixDim; i++) {
                    for (int j = 0; j < matrixDim; j++) {
                        input.append(random.nextInt(10)).append(" ");
                    }
                    input.append("\n");
                }
            } else {
                Pattern arrayPattern = Pattern.compile("(\\w+)\\s*=\\s*new\\s+(\\w+)\\s*\\[");
                Matcher arrayMatcher = arrayPattern.matcher(code);
                int arrayCount = 0;
                while (arrayMatcher.find()) {
                    arrayCount++;
                }

                if (arrayCount > 1) {
                    input.append(size).append("\n");

                    for (int i = 0; i < size; i++) {
                        input.append(random.nextInt(100)).append(" ");
                    }
                    input.append("\n");
                } else {
                    Pattern nextIntPattern = Pattern.compile("nextInt\\(\\)");
                    Matcher nextIntMatcher = nextIntPattern.matcher(code);
                    int nextIntCount = 0;
                    while (nextIntMatcher.find()) {
                        nextIntCount++;
                    }

                    Pattern nextDoublePattern = Pattern.compile("nextDouble\\(\\)");
                    Matcher nextDoubleMatcher = nextDoublePattern.matcher(code);
                    int nextDoubleCount = 0;
                    while (nextDoubleMatcher.find()) {
                        nextDoubleCount++;
                    }

                    if (nextIntCount > 0 || nextDoubleCount > 0) {
                        input.append(size).append("\n");
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < nextIntCount - 1; j++) {
                                input.append(random.nextInt(100)).append(" ");
                            }
                            for (int j = 0; j < nextDoubleCount; j++) {
                                input.append(random.nextDouble() * 100).append(" ");
                            }
                            input.append("\n");
                        }
                    }
                }
            }
        }

        this.generatedInput = input.toString();
        return input.toString();
    }

    public boolean hasSingleStringInput(String code) {
        return (code.contains("scanner.nextLine()") ||
                code.contains("Scanner") && code.contains("nextLine()")) &&
                !code.contains("nextInt()") &&
                !code.contains("nextDouble()") &&
                !code.contains("next()");
    }

    public boolean isNumericInputRequired(String code) {
        boolean hasStringIndicators = code.contains("String ") ||
                code.contains("nextLine()") ||
                code.contains("charAt(") ||
                code.contains("length()");

        if (hasStringIndicators) {
            return false;
        }

        return code.contains("parseInt") ||
                code.contains("Integer") ||
                code.contains("int ") ||
                code.contains("nextInt()");
    }

    public String generateInputForType(String inputType, int size) {
        StringBuilder sb = new StringBuilder();
        if ("array".equals(inputType)) {
            sb.append(size).append("\n");
            for (int i = 0; i < size; i++) {
                sb.append(random.nextInt(100)).append(" ");
            }
            sb.append("\n");
        } else if ("matrix".equals(inputType)) {
            sb.append(size).append(" ").append(size).append("\n");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sb.append(random.nextInt(100)).append(" ");
                }
                sb.append("\n");
            }
        } else if ("string".equals(inputType)) {
            String chars = "abcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i < size; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            sb.append("\n");
        } else {
            throw new IllegalArgumentException("Unsupported input_type: " + inputType);
        }
        return sb.toString();
    }

    public void setGeneratedInput(String input) {
        this.generatedInput = input;
    }
}
