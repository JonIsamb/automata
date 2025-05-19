package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Automaton {

    private State initialState;
    private State currentState;
    private List<State> states;
    private List<State> finalStates;
    private List<Transition> transitions;
    private Set<Character> alphabet;

    /**
     * Basic constructor
     */
    public Automaton() {
        this.states = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.alphabet = new HashSet<>();
    }

    /**
     * Constructor for the Automaton class that loads from a file
     * @param fileName Name of the file to load the automaton from
     */
    public Automaton(String fileName) {
        this();
        try {
            loadFromFile(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads an automaton from a file with a specific defined syntax
     * @param fileName Name of the file
     * @throws IOException If file read operations fail
     */
    private void loadFromFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        Map<String, State> stateMap = new HashMap<>();

        // Phase 1: Collect all state names
        boolean statesSection = true;
        List<String> stateNames = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Ignore empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Detect transition between states section and transitions section
            if (line.contains("->")) {
                statesSection = false;
                break; // Exit to process this line in phase 2
            }

            if (statesSection) {
                // This is a state name
                stateNames.add(line);
            }
        }

        // Create State objects
        for (String stateName : stateNames) {
            State state = new State(stateName, false); // All states are non-final by default
            states.add(state);
            stateMap.put(stateName, state);
        }

        // If we have an initial state defined by "-"
        initialState = null;

        // Phase 2: Process transitions
        // Process the first transition that was read earlier
        if (line != null && line.contains("->")) {
            processTransition(line, stateMap);
        }

        // Process the remaining transitions
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.contains("->")) {
                processTransition(line, stateMap);
            }
        }

        reader.close();
    }

    /**
     * Processes a transition line in the defined format
     * @param line Transition line
     * @param stateMap Mapping of state names to State objects
     */
    private void processTransition(String line, Map<String, State> stateMap) {
        // Format: <origin_state> -> <destination_state> : <condition>; <symbol>; <stack_operations>
        String[] parts = line.split("->");
        if (parts.length != 2) return;

        String originStateStr = parts[0].trim();

        // Extract the part after the arrow
        String secondPart = parts[1].trim();
        String[] subParts = secondPart.split(":");
        if (subParts.length != 2) return;

        String destinationStateStr = subParts[0].trim();
        String remainingParts = subParts[1].trim();

        // Divide the rest into condition, symbol, and stack operations
        String[] operationParts = remainingParts.split(";");
        if (operationParts.length < 2) return;

        String conditionStr = operationParts[0].trim();
        String symbolStr = operationParts[1].trim();
        String stackOperationsStr = operationParts.length > 2 ? operationParts[2].trim() : "";

        // Process the origin state
        State originState;
        if (originStateStr.equals("-")) {
            // This is the initial state
            if (initialState == null) {
                // If the initial state hasn't been defined, create a special state
                initialState = new State(destinationStateStr, false);
                stateMap.put(destinationStateStr, initialState);
                originState = initialState;
                currentState = initialState; // At the start, the current state is the initial state
            } else {
                originState = initialState;
            }

            return;
        } else {
            originState = stateMap.get(originStateStr);
            // If this is the first transition of the automaton and we don't have an initial state yet
            if (initialState == null && originState != null) {
                initialState = originState;
                currentState = initialState;
            }
        }

        // Process the destination state
        State destinationState = stateMap.get(destinationStateStr);
        if (destinationStateStr.equals("-")) {
            originState.setFinal(true);
            finalStates.add(originState);
        }

        // Check if states exist
        if (originState == null || destinationState == null) {
            System.out.println("State not found: " + originStateStr + " or " + destinationStateStr);
            return;
        }

        // Process the symbol
        char symbol;
        if (symbolStr.length() == 1) {
            symbol = symbolStr.charAt(0);
        } else {
            // Handle special symbols like "ε" (epsilon)
            if (symbolStr.equals("ε")) {
                symbol = 'ε';
            } else if (symbolStr.equals("⊥")) {
                symbol = '⊥';
            } else {
                // For other multi-character symbols like "P5", "P10", etc.
                symbol = symbolStr.charAt(0); // Take the first character by default
            }
        }

        // Add the transition
        Transition transition = new Transition(originState, destinationState, symbol);

        // Add additional information to the transition if necessary
        if (!conditionStr.equals("-")) {
            transition.setCondition(conditionStr);
        }

        if (!stackOperationsStr.isEmpty()) {
            transition.setStackOperation(stackOperationsStr);
        }

        this.transitions.add(transition);
        this.alphabet.add(symbol);
        originState.addTransition(destinationState, symbol);
    }

    /**
     * Adds a state to the automaton
     * @param state The state to add
     */
    public void addState(State state) {
        this.states.add(state);
        if (state.isFinal) {
            this.finalStates.add(state);
        }
    }

    /**
     * Adds a transition to the automaton
     * @param originState The origin state of the transition
     * @param finalState The destination state of the transition
     * @param symbol The symbol that triggers the transition
     */
    public void addTransition(State originState, State finalState, char symbol) {
        Transition transition = new Transition(originState, finalState, symbol);
        this.transitions.add(transition);
        this.alphabet.add(symbol);
        originState.addTransition(finalState, symbol);
    }

    /**
     * Checks if the automaton is deterministic
     * @return true if the automaton is deterministic, false otherwise
     */
    public boolean isDeterministic() {
        if (initialState == null) {
            return false;
        }

        for (State state : states) {
            if (!state.hasDeterministicTransitions()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a word belongs to the language defined by this automaton
     * @param word The word to check
     * @return true if the word belongs to the automaton's language, false otherwise
     */
    boolean belongs(String word) {
        if (initialState == null) {
            System.out.println("The automaton does not have a defined initial state.");
            return false;
        }

        State currentState = initialState;

        for (int i = 0; i < word.length(); i++) {
            char symbol = word.charAt(i);

            if (!alphabet.contains(symbol)) {
                System.out.println("The symbol '" + symbol + "' does not belong to the automaton's alphabet.");
                return false;
            }

            State nextState = currentState.getNextState(symbol);

            if (nextState == null) {
                System.out.println("Error: No transition from state '" +
                        currentState.getName() + "' with symbol '" + symbol + "'.");
                return false;
            }
            currentState = nextState;
        }

        if (!currentState.isFinal()) {
            System.out.println("Error: The final state '" + currentState.getName() + "' is not an accepting state.");
            return false;
        }

        return true;
    }

    /**
     * Resets the automaton to its initial state
     */
    public void reset() {
        this.currentState = initialState;
    }

    /**
     * Gets the initial state of the automaton
     * @return The initial state
     */
    public State getInitialState() {
        return initialState;
    }

    /**
     * Gets the list of final/accepting states
     * @return List of final states
     */
    public List<State> getFinalStates() {
        return finalStates;
    }

    /**
     * Gets the list of all states
     * @return List of all states
     */
    public List<State> getStates() {
        return states;
    }

    /**
     * Gets the list of all transitions
     * @return List of all transitions
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Gets the set of symbols in the alphabet
     * @return Set of alphabet symbols
     */
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    /**
     * Generate a string representation of this automaton
     * @return A string describing the automaton's properties
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automaton:\n");
        sb.append("States: ").append(states).append("\n");
        sb.append("Initial state: ").append(initialState).append("\n");
        sb.append("Final states: ").append(finalStates).append("\n");
        sb.append("Alphabet: ").append(alphabet).append("\n");
        sb.append("Transitions:\n");
        for (Transition t : transitions) {
            sb.append("  ").append(t).append("\n");
        }
        return sb.toString();
    }
}