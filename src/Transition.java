package src;

public class Transition {
    public State originState;
    public State finalState;
    public char symbol;
    public String condition;
    public String stackOperations;

    /**
     * Constructor for creating a basic transition
     * @param originState The starting state
     * @param finalState The destination state
     * @param symbol The triggering symbol
     */
    public Transition(State originState, State finalState, char symbol) {
        this.originState = originState;
        this.finalState = finalState;
        this.symbol = symbol;
    }

    /**
     * Get the origin state of the transition
     * @return The origin state
     */
    public State getOriginState() {
        return originState;
    }

    /**
     * Set the origin state of the transition
     * @param originState The new origin state
     */
    public void setOriginState(State originState) {
        this.originState = originState;
    }

    /**
     * Get the final state of the transition
     * @return The final state
     */
    public State getFinalState() {
        return finalState;
    }

    /**
     * Set the final state of the transition
     * @param finalState The new final state
     */
    public void setFinalState(State finalState) {
        this.finalState = finalState;
    }

    /**
     * Get the symbol that triggers this transition
     * @return The transition symbol
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Set the symbol that triggers this transition
     * @param symbol The new transition symbol
     */
    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Get the condition for this transition
     * @return The condition string
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Set the condition for this transition
     * @param condition The new condition string
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Get the stack operations for this transition
     * @return The stack operations string
     */
    public String getStackOperation() {
        return stackOperations;
    }

    /**
     * Set the stack operations for this transition
     * @param stackOperations The new stack operations string
     */
    public void setStackOperation(String stackOperations) {
        this.stackOperations = stackOperations;
    }

    /**
     * Generate a string representation of this transition
     * @return A string describing the transition's properties
     */
    @Override
    public String toString() {
        return "src.Transition{" +
                "originState=" + originState.getName() +
                ", finalState=" + finalState.getName() +
                ", symbol=" + symbol +
                ", condition=" + condition +
                ", stackOperations=" + stackOperations +
                '}';
    }
}
