package main.core;

public class EmpiricalComplexityAnalyzer {

    public String inferComplexity(double[] sizes, double[] values) {
        String[] complexities = {"O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n^2)"};
        double minCv = Double.POSITIVE_INFINITY;
        String bestComplexity = "O(n)"; // Default fallback

        for (String complexity : complexities) {
            double[] expected = new double[sizes.length];
            for (int i = 0; i < sizes.length; i++) {
                double n = sizes[i];
                switch (complexity) {
                    case "O(1)":
                        expected[i] = 1;
                        break;
                    case "O(log n)":
                        expected[i] = Math.log(n) / Math.log(2);
                        break;
                    case "O(n)":
                        expected[i] = n;
                        break;
                    case "O(n log n)":
                        expected[i] = n * Math.log(n) / Math.log(2);
                        break;
                    case "O(n^2)":
                        expected[i] = n * n;
                        break;
                }
            }

            double[] normalizedValues = normalize(values);
            double[] normalizedExpected = normalize(expected);

            double mse = 0;
            for (int i = 0; i < values.length; i++) {
                mse += Math.pow(normalizedValues[i] - normalizedExpected[i], 2);
            }
            mse /= values.length;

            double mean = 0;
            for (double val : normalizedValues) {
                mean += val;
            }
            mean /= normalizedValues.length;
            double variance = 0;
            for (double val : normalizedValues) {
                variance += Math.pow(val - mean, 2);
            }
            variance /= normalizedValues.length;
            double stdDev = Math.sqrt(variance);
            double cv = mean != 0 ? stdDev / mean : Double.POSITIVE_INFINITY;

            double score = mse + cv;
            if (score < minCv) {
                minCv = score;
                bestComplexity = complexity;
            }
        }

        return bestComplexity;
    }

    private double[] normalize(double[] values) {
        double max = 0;
        for (double val : values) {
            if (val > max) max = val;
        }
        if (max == 0) return values;
        double[] normalized = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            normalized[i] = values[i] / max;
        }
        return normalized;
    }
}
