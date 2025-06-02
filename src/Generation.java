package src;

import src.Automaton;
import src.State;
import src.Transition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Generation {

    /**
     * Generates NetLogo code from an automaton and writes it to "automate.nls" file
     * @param automaton The automaton instance to convert to NetLogo
     */
    public void generateNetLogo(Automaton automaton, String fileName) {
        StringBuilder netLogoCode = new StringBuilder();

        // Generate procedures for each state
        for (State state : automaton.getStates()) {
            generateStateProcedure(state, automaton, netLogoCode);
        }

        // Write to file
        writeFile(fileName, netLogoCode.toString());
    }

    /**
     * Generates the NetLogo procedure for a given state
     * @param state The state for which to generate the procedure
     * @param automaton The full automaton to get transitions
     * @param code The StringBuilder containing the code being built
     */
    private void generateStateProcedure(State state, Automaton automaton, StringBuilder code) {
        code.append("to ").append(state.name).append("\n");
        code.append("  ;; to be completed\n");
        code.append("  print \"").append(state.name).append("\"\n");

        // Get transitions from this state
        List<Transition> stateTransitions = getTransitionsFromState(state, automaton);

        if (!stateTransitions.isEmpty()) {
            generateTransitionConditions(stateTransitions, code);
        }

        code.append("end\n\n");
    }

    /**
     * Generates conditions and transitions for a state
     * @param transitions List of transitions from the state
     * @param code The StringBuilder containing the code being built
     */
    private void generateTransitionConditions(List<Transition> transitions, StringBuilder code) {
        // Filter out epsilon transitions
        List<Transition> nonEpsilonTransitions = transitions.stream()
                .filter(t -> t.symbol != 'ε')
                .collect(Collectors.toList());

        if (nonEpsilonTransitions.isEmpty()) {
            return;
        }

        // Generate simple if-else chain
        for (int i = 0; i < nonEpsilonTransitions.size(); i++) {
            Transition t = nonEpsilonTransitions.get(i);
            String keyboardCondition = generateKeyboardCondition(t.symbol);

            String indent = "  ";
            for (int j = 0; j < i; j++) {
                indent += "  ";
            }

            if (i == 0) {
                code.append("  if (").append(keyboardCondition);
            } else {
                code.append(" [\n").append(indent).append("if (").append(keyboardCondition);
            }

            // Add specific condition if present
            if (t.condition != null && !t.condition.isEmpty()) {
                code.append(" and ").append(convertCondition(t.condition));
            }

            code.append(") [\n");
            code.append(indent).append("  set next-activity [ -> ").append(t.finalState.name).append(" ]\n");
            code.append(indent).append("]");
        }

        // Close all remaining brackets
        for (int i = nonEpsilonTransitions.size() - 1; i > 0; i--) {
            code.append(" ]");
        }
        code.append("\n");
    }

    /**
     * Generates the keyboard condition corresponding to the symbol
     * @param symbol The transition symbol
     * @return The corresponding NetLogo condition
     */
    private String generateKeyboardCondition(char symbol) {
        switch (symbol) {
            case 'Q': return "button-quit = true";
            case 'P': return "button-pick = true";
            case 'D': return "button-drop = true";
            case 'G': return "button-forward = true";
            case 'A': return "button-stop = true";
            case 'J': return "button-jump = true";
            case 'N': return "button-north = true";
            case 'S': return "button-south = true";
            case 'E': return "button-east = true";
            case 'W': return "button-west = true";
            default: return "false";
        }
    }

    /**
     * Converts a Java condition to NetLogo condition
     * @param condition The condition to convert
     * @return The condition converted to NetLogo
     */
    private String convertCondition(String condition) {
        // Convert current conditions
        condition = condition.replace("[", "").replace("]", "");
        condition = condition.replace("object nearby", "object-nearby");
        condition = condition.replace("nbO < 10", "nb-objects < 10");
        condition = condition.replace("nbO > 0", "nb-objects > 0");
        return condition;
    }

    /**
     * Gets all transitions from a given state
     * @param state The source state
     * @param automaton The automaton containing all transitions
     * @return List of transitions from this state
     */
    private List<Transition> getTransitionsFromState(State state, Automaton automaton) {
        List<Transition> result = new ArrayList<>();
        for (Transition t : automaton.getTransitions()) {
            if (t.originState.equals(state)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Writes content to a file
     * @param fileName The name of the file to create
     * @param content The content to write
     */
    private void writeFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            System.out.println("File " + fileName + " generated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}