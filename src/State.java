package src;

import java.util.ArrayList;
import java.util.List;

/**
 * The State class represents a state in a finite state machine or automaton.
 * It maintains a list of outgoing transitions and provides methods to
 * add transitions and navigate the state machine.
 */
public class State {
    public String name;
    public boolean isFinal;
    private final List<Transition> transitions;

    /**
     * Constructor for creating a new state
     * @param name Name identifier for the state
     * @param isFinal Whether this state is a final/accepting state
     */
    public State(String name, boolean isFinal) {
        this.name = name;
        this.isFinal = isFinal;
        this.transitions = new ArrayList<>();
    }

    /**
     * Adds an outgoing transition from this state
     * @param finalState Destination state of the transition
     * @param symbol Symbol that triggers the transition
     * @return The created transition object
     */
    public Transition addTransition(State finalState, char symbol) {
        Transition transition = new Transition(this, finalState, symbol);
        transitions.add(transition);
        return transition;
    }

    /**
     * Gets the next state for a given symbol
     * @param symbol Symbol of the transition
     * @return Next state or null if no transition exists for this symbol
     */
    public State getNextState(char symbol) {
        for (Transition t : transitions) {
            if (t.getSymbol() == symbol || "ε".equals(String.valueOf(t.getSymbol()))) {
                return t.getFinalState();
            }
        }
        return null;
    }

    /**
     * Gets all possible next states for a given symbol
     * @param symbol Symbol of the transition
     * @return List of all possible next states
     */
    public List<State> getNextStates(char symbol) {
        List<State> states = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.getSymbol() == symbol) {
                states.add(t.getFinalState());
            }
        }
        return states;
    }

    /**
     * Gets the list of all outgoing transitions
     * @return List of transitions
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Checks if the state has deterministic transitions (no more than one for the same symbol)
     * @return true if transitions are deterministic, false otherwise
     */
    public boolean hasDeterministicTransitions() {
        for (int i = 0; i < transitions.size(); i++) {
            for (int j = i + 1; j < transitions.size(); j++) {
                if (transitions.get(i).getSymbol() == transitions.get(j).getSymbol()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if this state is a final/accepting state
     * @return true if this is a final state, false otherwise
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Sets whether this state is a final/accepting state
     * @param aFinal true to set as final state, false otherwise
     */
    public void setFinal(boolean aFinal) {
        this.isFinal = aFinal;
    }

    /**
     * Gets the name of this state
     * @return The state name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this state
     * @param name The new state name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Generate a string representation of this state
     * @return A string with the state name and (Final) if it's a final state
     */
    @Override
    public String toString() {
        return name + (isFinal ? " (Final)" : "");
    }
}
