# Java Code Performance Analyzer Tool

A sophisticated desktop application that analyzes Java code performance metrics, including execution time, memory usage, and algorithmic complexity. Features automated test data generation with customizable input sizes up to 100,000 elements, eliminating the need for manual test data creation.

## Features

- **Real-time Code Analysis**: Analyze Java code performance with detailed metrics
- **Multiple Input Options**:
  - Manual Input
  - Random Input Generation
  - Hardcoded Input Support
- **Performance Metrics**:
  - Execution Time Analysis
  - Memory Usage Tracking
  - Time Complexity Analysis
  - Space Complexity Analysis
- **Visualization**:
  - Time Performance Graphs
  - Memory Usage Graphs
- **Flexible Unit Display**:
  - Time Units (Milliseconds, Seconds, Minutes)
  - Memory Units (Bytes, Kilobytes, Megabytes)
- **Input/Output Data Visualization**
- **User Manual Integration**

## Requirements

- [Java Development Kit (JDK) 8 or higher](https://openjdk.org/projects/jdk/21/)
- [JavaFX](https://openjfx.io/)
- Sufficient system memory for code execution and analysis

## Installation

1. Clone the repository:
git clone https://github.com/neeraj1240/JavaCodePerformanceAnalyzer.git

2. Navigate to the project directory:
cd JavaCodePerformanceAnalyzer

3. Compile the project:
javac main/ui/CodeAnalyzerUI.java

4. Run the application:
java main.ui.CodeAnalyzerUI


## Usage

1. **Input Code**:
   - Enter your Java code in the left panel
   - Choose input type (Manual/Random/Hardcoded)
   - Set input parameters if required

2. **Analysis**:
   - Click "Analyze" to start the performance analysis
   - Wait for the results to appear in the right panel

3. **View Results**:
   - Execution time with adjustable units
   - Memory usage with adjustable units
   - Time complexity analysis
   - Space complexity analysis
   - Performance graphs
   - Input/Output data visualization

## Features in Detail

### Code Analysis
- Automated detection of algorithm types
- Complex pattern recognition for various data structures
- Multiple runs for accurate performance measurement
- Warm-up cycles for [JVM optimization](https://en.wikipedia.org/wiki/Just-in-time_compilation)

### Complexity Analysis
- Detection of common algorithm patterns like:
  - Iterative algorithms
  - Recursive algorithms
  - Divide and conquer
  - Dynamic programming
  - Backtracking

### Performance Metrics
- Average execution time
- Memory consumption
- Time complexity notation
- Space complexity notation

## Contributing

Contributions are welcome! Please feel free to submit a [Pull Request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests).

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](https://choosealicense.com/licenses/mit/) file for details.

## Support

For support, please open an issue in the GitHub repository or contact the maintainers.

## Future Enhancements

- Extended algorithm pattern recognition
- Additional visualization options
- Support for more complex input patterns
- Integration with popular [IDEs](https://en.wikipedia.org/wiki/Integrated_development_environment)
- Batch analysis capabilities

---
**Note**: This project is continuously under development. Feedback and contributions are always welcome!
