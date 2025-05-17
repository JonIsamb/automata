package src;

import java.util.ArrayList;
import java.util.List;

public class Etat {
    public String name;
    public boolean isFinal;
    private final List<Transition> transitions;

    public Etat(String name, boolean isFinal) {
        this.name = name;
        this.isFinal = isFinal;
        this.transitions = new ArrayList<>();
    }

    /**
     * Ajoute une transition sortante à l'état
     * @param etatFinal État de destination
     * @param symbole Symbole déclenchant la transition
     * @return La transition créée
     */
    public Transition ajouterTransition(Etat etatFinal, char symbole) {
        Transition transition = new Transition(this, etatFinal, symbole);
        transitions.add(transition);
        return transition;
    }

    /**
     * Récupère l'état suivant pour un symbole donné
     * @param symbole Symbole de la transition
     * @return État suivant ou null si aucune transition n'existe pour ce symbole
     */
    public Etat getEtatSuivant(char symbole) {
        for (Transition t : transitions) {
            if (t.getSymbole() == symbole) {
                return t.getEtatFinal();
            }
        }
        return null;
    }

    /**
     * Récupère toutes les transitions sortantes avec un symbole donné
     * @param symbole Symbole de la transition
     * @return Liste des états suivants possibles
     */
    public List<Etat> getEtatsSuivants(char symbole) {
        List<Etat> etats = new ArrayList<>();
        for (Transition t : transitions) {
            if (t.getSymbole() == symbole) {
                etats.add(t.getEtatFinal());
            }
        }
        return etats;
    }

    /**
     * Récupère la liste des transitions sortantes
     * @return Liste des transitions
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * Vérifie si l'état a des transitions déterministes (pas plus d'une pour un même symbole)
     * @return true si les transitions sont déterministes, false sinon
     */
    public boolean transitionDeteministes(){
        for (int i = 0; i < transitions.size(); i++) {
            for (int j = i + 1; j < transitions.size(); j++) {
                if (transitions.get(i).getSymbole() == transitions.get(j).getSymbole()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        this.isFinal = aFinal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + (isFinal ? " (Final)" : "");
    }
}
