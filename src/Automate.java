package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Charge un automate depuis un fichier avec la syntaxe spécifique définie
     * @param fileName Nom du fichier
     * @throws FileNotFoundException
     */
    private void chargerFichier(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        Map<String, Etat> mapEtats = new HashMap<>();

        // Phase 1: Collecter tous les noms d'états
        boolean etatsSection = true;
        List<String> nomEtats = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Ignorer les lignes vides
            if (line.isEmpty()) {
                continue;
            }

            // Détecter la transition entre la section des états et la section des transitions
            if (line.contains("->")) {
                etatsSection = false;
                break; // Sortir pour traiter cette ligne dans la phase 2
            }

            if (etatsSection) {
                // C'est un nom d'état
                nomEtats.add(line);
            }
        }

        // Créer les objets Etat
        for (String nomEtat : nomEtats) {
            Etat etat = new Etat(nomEtat, false); // Tous les états sont non finaux par défaut
            etats.add(etat);
            mapEtats.put(nomEtat, etat);
        }

        // Si on a un état initial défini par "-"
        etatInitial = null;

        // Phase 2: Traiter les transitions
        // Traiter la première transition qui a été lue plus tôt
        if (line != null && line.contains("->")) {
            traiterTransition(line, mapEtats);
        }

        // Traiter le reste des transitions
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.contains("->")) {
                traiterTransition(line, mapEtats);
            }
        }

        reader.close();
    }

    /**
     * Traite une ligne de transition au format défini
     * @param line Ligne de transition
     * @param mapEtats Mapping des noms d'états vers les objets Etat
     */
    private void traiterTransition(String line, Map<String, Etat> mapEtats) {
        // Format: <etat_origine> -> <etat_arrivee> : <condition>; <symbole>; <operations_sur_pile>
        String[] parts = line.split("->");
        if (parts.length != 2) return;

        String etatOrigineStr = parts[0].trim();

        // Extraire la partie après la flèche
        String secondPart = parts[1].trim();
        String[] subParts = secondPart.split(":");
        if (subParts.length != 2) return;

        String etatArriveeStr = subParts[0].trim();
        String resteParts = subParts[1].trim();

        // Diviser le reste en condition, symbole et opérations sur pile
        String[] operationParts = resteParts.split(";");
        if (operationParts.length < 2) return;

        String conditionStr = operationParts[0].trim();
        String symboleStr = operationParts[1].trim();
        String operationsPileStr = operationParts.length > 2 ? operationParts[2].trim() : "";

        // Traiter l'état d'origine
        Etat etatOrigine;
        if (etatOrigineStr.equals("-")) {
            // C'est l'état initial
            if (etatInitial == null) {
                // Si l'état initial n'a pas été défini, créer un état spécial
                etatInitial = new Etat(etatArriveeStr, false);
                etats.add(etatInitial);
                mapEtats.put(etatArriveeStr, etatInitial);
                etatOrigine = etatInitial;
                etatCourant = etatInitial; // Au départ, l'état courant est l'état initial
            } else {
                etatOrigine = etatInitial;
            }
        } else {
            etatOrigine = mapEtats.get(etatOrigineStr);
            // Si c'est la première transition de l'automate et qu'on n'a pas encore d'état initial
            if (etatInitial == null && etatOrigine != null) {
                etatInitial = etatOrigine;
                etatCourant = etatInitial;
            }
        }

        // Traiter l'état d'arrivée
        Etat etatArrivee = mapEtats.get(etatArriveeStr);
        if (etatArriveeStr.equals("-")) {
            etatsFinaux.add(etatOrigine);
        }

        // Vérifier si les états existent
        if (etatOrigine == null || etatArrivee == null) {
            System.out.println("État non trouvé: " + etatOrigineStr + " ou " + etatArriveeStr);
            return;
        }

        // Traiter le symbole
        char symbole;
        if (symboleStr.length() == 1) {
            symbole = symboleStr.charAt(0);
        } else {
            // Gérer les symboles spéciaux comme "ε" (epsilon)
            if (symboleStr.equals("ε")) {
                symbole = 'ε';
            } else if (symboleStr.equals("⊥")) {
                symbole = '⊥';
            } else {
                // Pour les autres symboles multi-caractères comme "P5", "P10", etc.
                symbole = symboleStr.charAt(0); // Prendre le premier caractère par défaut
            }
        }

        // Ajouter la transition
        Transition transition = new Transition(etatOrigine, etatArrivee, symbole);

        // Ajouter des informations supplémentaires à la transition si nécessaire
        if (!conditionStr.equals("-")) {
            transition.setCondition(conditionStr);
        }

        if (!operationsPileStr.isEmpty()) {
            transition.setOperationPile(operationsPileStr);
        }

        this.transitions.add(transition);
        this.alphabet.add(symbole);
        etatOrigine.ajouterTransition(etatArrivee, symbole);
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