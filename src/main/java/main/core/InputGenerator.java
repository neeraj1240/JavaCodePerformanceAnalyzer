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
        return generateInput(code, size, "random");
    }

    public String generateInput(String code, int size, String arrayType) {
        StringBuilder input = new StringBuilder();
        String dataType = detectDataType(code);

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

    private String detectDataType(String code) {
        if (code.contains("char[]") || code.contains("Character[]")) {
            return "char";
        } else if (code.contains("String[]")) {
            return "string";
        } else {
            return "int"; // Default to int if no specific type is found
        }
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
        String[] parts = inputType.split(":");
        String baseType = parts[0];
        String arrayType = parts.length > 1 ? parts[1] : "random";

        if ("array".equals(baseType)) {
            sb.append(size).append("\n");
            int[] array = new int[size];
            
            // Generate initial array based on type
            switch (arrayType) {
                case "sorted":
                    for (int i = 0; i < size; i++) {
                        array[i] = i;
                    }
                    break;
                case "nearly-sorted":
                    for (int i = 0; i < size; i++) {
                        array[i] = i;
                    }
                    // Swap some elements to make it nearly sorted
                    int swaps = Math.max(1, size / 10); // Swap 10% of elements
                    for (int i = 0; i < swaps; i++) {
                        int pos1 = random.nextInt(size);
                        int pos2 = Math.min(size - 1, pos1 + random.nextInt(3) + 1);
                        int temp = array[pos1];
                        array[pos1] = array[pos2];
                        array[pos2] = temp;
                    }
                    break;
                case "random":
                default:
                    for (int i = 0; i < size; i++) {
                        array[i] = random.nextInt(100);
                    }
                    break;
            }
            
            // Convert array to string
            for (int value : array) {
                sb.append(value).append(" ");
            }
            sb.append("\n");
        } else if ("matrix".equals(baseType)) {
            sb.append(size).append(" ").append(size).append("\n");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    sb.append(random.nextInt(100)).append(" ");
                }
                sb.append("\n");
            }
        } else if ("string".equals(baseType)) {
            String chars = "abcdefghijklmnopqrstuvwxyz";
            for (int i = 0; i < size; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            sb.append("\n");
        } else {
            throw new IllegalArgumentException("Unsupported input_type: " + baseType);
        }
        return sb.toString();
    }

    public void setGeneratedInput(String input) {
        this.generatedInput = input;
    }
}
