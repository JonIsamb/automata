public class Transition {
    public Etat etatOrigine;

    public Etat etatFinal;

    public char symbole;

    public Transition(Etat etatOrigine, Etat etatFinal, char symbole) {
        this.etatOrigine = etatOrigine;
        this.etatFinal = etatFinal;
        this.symbole = symbole;
    }
}
