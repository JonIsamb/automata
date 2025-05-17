package src;

public class Transition {
    public Etat etatOrigine;

    public Etat etatFinal;

    public char symbole;

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

    @Override
    public String toString() {
        return "src.Transition{" +
                "etatOrigine=" + etatOrigine.getName() +
                ", etatFinal=" + etatFinal.getName() +
                ", symbole=" + symbole +
                '}';
    }

}
