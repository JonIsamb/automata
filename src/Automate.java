package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Automate {

    private Etat etatInitial;
    private Etat etatCourant;
    private List<Etat> etats;
    private List<Etat> etatsFinaux;
    private List<Transition> transitions;
    private Set<Character> alphabet;


    /**
     * Constructeur de base
     */
    public Automate() {
        this.etats = new ArrayList<>();
        this.etatsFinaux = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.alphabet = new HashSet<>();
    }

    /**
     * Constructeur de la classe Automate
     * @param fileName
     */
    public Automate(String fileName)  {
        this();
        try{
            chargerFichier(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Charge un automate depuis un fichier
     * @param fileName Nom du fichier
     * @throws FileNotFoundException
     */
    private void chargerFichier(String fileName) throws IOException {
        BufferedReader reader= new BufferedReader(new FileReader(fileName));
        String line;
        Map<String, Etat> mapEtats = new HashMap<>();

        //TODO finir la lecture du fichier

        //lecture des etats

        //lecture de l'état initial

        //lecture des états finaux

        //lecture de l'alphabet

        //lecture des transitions

    }

    /**
     * Ajoute un état à l'automate
     * @param etat
     */
    public void ajouterEtat(Etat etat) {
        this.etats.add(etat);
        if (etat.isFinal) {
            this.etatsFinaux.add(etat);
        }
    }

    /**
     * Ajoute une transition à l'automate
     * @param etatOrigine
     * @param etatFinal
     * @param symbole
     */
    public void ajouterTransition(Etat etatOrigine, Etat etatFinal, char symbole) {
        Transition transition = new Transition(etatOrigine, etatFinal, symbole);
        this.transitions.add(transition);
        this.alphabet.add(symbole);
        etatOrigine.ajouterTransition(etatFinal, symbole);
    }

    /**
     *
     * @return true if the automate is determinist, false otherwise
     */
    public boolean isDeterminist() {

        if (etatInitial == null) {
            return false;
        }

        for (Etat etat : etats) {
            if (!etat.transitionDeteministes()){
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param word
     * @return true if the word belong to automate, false otherwise
     */
    boolean belong(String word){
        if (etatInitial == null) {
            System.out.println("L'automate n'a pas d'état initial défini.");
            return false;
        }

        Etat etatCourant = etatInitial;

        for (int i = 0; i < word.length(); i++) {
            char symbole = word.charAt(i);

            if (!alphabet.contains(symbole)){
                System.out.println("Le symbole '" + symbole + "' n'appartient pas à l'alphabet de l'automate.");
                return false;
            }

            Etat etatSuivant = etatCourant.getEtatSuivant(symbole);

            if (etatSuivant == null){
                System.out.println("Erreur: Absence de transition à partir de l'état '" +
                        etatCourant.getName() + "' avec le symbole '" + symbole + "'.");
                return false;
            }
            etatCourant = etatSuivant;
        }

        if (!etatCourant.isFinal()){
            System.out.println("Erreur: L'état final '" + etatCourant.getName() + "' n'est pas un état final.");
            return false;
        }

        return true;
    }

    /**
     * Réinitialise l'automate à son état initial
     */
    public void reinitialiser() {
        this.etatCourant = etatInitial;
    }

    /**
     * Récupère l'état initial de l'automate
     * @return L'état initial
     */
    public Etat getEtatInitial() {
        return etatInitial;
    }

    /**
     * @return liste des états finaux
     */
    public List<Etat> getEtatsFinaux(){
        return etatsFinaux;
    }

    /**
     * @return liste des états
     */
    public List<Etat> getEtats() {
        return etats;
    }

    /**
     * @return Liste de toutes les transitions
     */
    public List<Transition> getTransitions() {
        return transitions;
    }

    /**
     * @return Ensemble des symboles de l'alphabet
     */
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automate:\n");
        sb.append("États: ").append(etats).append("\n");
        sb.append("État initial: ").append(etatInitial).append("\n");
        sb.append("États finaux: ").append(etatsFinaux).append("\n");
        sb.append("Alphabet: ").append(alphabet).append("\n");
        sb.append("Transitions:\n");
        for (Transition t : transitions) {
            sb.append("  ").append(t).append("\n");
        }
        return sb.toString();
    }
}
