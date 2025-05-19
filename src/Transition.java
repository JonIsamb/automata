package src;

public class Transition {
    public Etat etatOrigine;

    public Etat etatFinal;

    public char symbole;

    public String condition;

    public String operationsPile;

    public Transition(Etat etatOrigine, Etat etatFinal, char symbole) {
        this.etatOrigine = etatOrigine;
        this.etatFinal = etatFinal;
        this.symbole = symbole;
    }

    public Etat getEtatOrigine() {
        return etatOrigine;
    }

    public void setEtatOrigine(Etat etatOrigine) {
        this.etatOrigine = etatOrigine;
    }

    public Etat getEtatFinal() {
        return etatFinal;
    }

    public void setEtatFinal(Etat etatFinal) {
        this.etatFinal = etatFinal;
    }

    public char getSymbole() {
        return symbole;
    }

    public void setSymbole(char symbole) {
        this.symbole = symbole;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOperationPile() {
        return operationsPile;
    }

    public void setOperationPile(String operationsPile) {
        this.operationsPile = operationsPile;
    }

    @Override
    public String toString() {
        return "src.Transition{" +
                "etatOrigine=" + etatOrigine.getName() +
                ", etatFinal=" + etatFinal.getName() +
                ", symbole=" + symbole +
                ", condition=" + condition +
                ", operationsPile=" + operationsPile +
                '}';
    }

}
