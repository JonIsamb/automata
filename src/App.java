package src;

import java.io.BufferedReader;
import java.io.FileReader;

public class App {
    /**
     * Main method that runs the application
     * @param args Command line arguments (not used)
     * @throws Exception If file operations fail
     */
    public static void main(String[] args) throws Exception {
        // Load the first automaton and display its details
        Automaton a = new Automaton("lib/automate.txt");
        System.out.println(a.toString());

        // Run tests on the first automaton
        testAutomaton(a, "lib/tests_automate_autre.txt");

        // Load a simpler automaton and display its details
        Automaton simpleAutomaton = new Automaton("lib/automate-facile.txt");
        System.out.println(simpleAutomaton.toString());

        // Run tests on the simple automaton
        testAutomaton(simpleAutomaton, "lib/tests_automate.txt");
    }

    /**
     * Tests an automaton against a series of test cases from a file
     * @param a The automaton to test
     * @param testFile Path to the file containing test cases
     * @throws Exception If file operations fail
     */
    private static void testAutomaton(Automaton a, String testFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        String line;

        System.out.println("\nRunning tests:");
        System.out.println("====================");

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Ignore empty lines and comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // Separate the sequence from the expected result
            String[] parts = line.split("->");
            if (parts.length != 2) continue;

            String sequence = parts[0].trim();
            String expected = parts[1].trim().split(" ")[0];
            String comment = parts[1].contains("[") ?
                    parts[1].substring(parts[1].indexOf("[") + 1, parts[1].indexOf("]")) : "";

            // Run the test
            boolean result = a.belongs(sequence);
            String status = (result && expected.equals("true")) || (!result && expected.equals("false"))
                    ? "SUCCESS" : "FAILURE";

            // Display the result
            System.out.printf("Test '%s': %s (Expected: %s, Got: %s) %s%n",
                    sequence, status, expected, result, comment);
        }

        reader.close();
    }
}
