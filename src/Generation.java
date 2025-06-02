package src;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generation {

    /**
     * Génère le code NetLogo à partir d'un automate et l'écrit dans un fichier "automate.nls"
     * @param automate L'instance d'automate à convertir en NetLogo
     */
    public void genererNetLogo(Automaton automate) {
        StringBuilder netLogoCode = new StringBuilder();

        // En-tête du fichier NetLogo
        netLogoCode.append(";; Code NetLogo généré automatiquement\n");
        netLogoCode.append(";; Automate: ").append(automate.getStates().size()).append(" états\n\n");

        // Génération des procédures pour chaque état
        for (State state : automate.getStates()) {
            genererProcedureEtat(state, netLogoCode);
        }

        // Écriture dans le fichier
        ecrireFichier("automate.nls", netLogoCode.toString());
    }

    /**
     * Génère la procédure NetLogo pour un état donné
     * @param state L'état pour lequel générer la procédure
     * @param code Le StringBuilder contenant le code à construire
     */
    private void genererProcedureEtat(State state, StringBuilder code) {
        code.append("to ").append(state.name).append("\n");
        code.append("  ;; to be completed\n");
        code.append("  print \"").append(state.name).append("\"\n");

        // Récupération des transitions depuis cet état
        List<Transition> transitionsEtat = getTransitionsFromState(state);

        if (transitionsEtat.isEmpty()) {
            code.append("end\n\n");
            return;
        }

        // Génération des conditions et transitions
        genererConditionsTransitions(transitionsEtat, code);

        code.append("end\n\n");
    }

    /**
     * Génère les conditions et transitions pour un état
     * @param transitions Liste des transitions depuis l'état
     * @param code Le StringBuilder contenant le code à construire
     */
    private void genererConditionsTransitions(List<Transition> transitions, StringBuilder code) {
        // Grouper les transitions par symbole pour gérer les conditions multiples
        Map<Character, List<Transition>> transitionsBySymbol = new HashMap<>();

        for (Transition t : transitions) {
            transitionsBySymbol.computeIfAbsent(t.symbol, k -> new ArrayList<>()).add(t);
        }

        boolean premierSymbole = true;

        for (Map.Entry<Character, List<Transition>> entry : transitionsBySymbol.entrySet()) {
            char symbole = entry.getKey();
            List<Transition> transitionsSymbole = entry.getValue();

            // Ignorer les transitions epsilon (ε) pour la génération des conditions
            if (symbole == 'ε') {
                continue;
            }

            String conditionClavier = genererConditionClavier(symbole);

            if (premierSymbole) {
                code.append("  if (").append(conditionClavier);
                premierSymbole = false;
            } else {
                code.append("  ] [\n");
                code.append("    if (").append(conditionClavier);
            }

            // Gérer les conditions spécifiques
            for (Transition transition : transitionsSymbole) {
                if (transition.condition != null && !transition.condition.isEmpty()) {
                    code.append(" and ").append(convertirCondition(transition.condition));
                }
            }

            code.append(") [\n");

            // Générer l'action de transition
            if (transitionsSymbole.size() == 1) {
                code.append("    set next-activity [ -> ").append(transitionsSymbole.get(0).finalState.name).append(" ]\n");
            } else {
                // Gérer les transitions multiples avec priorité
                genererTransitionsMultiples(transitionsSymbole, code);
            }
        }

        // Fermer toutes les conditions ouvertes
        for (int i = 0; i < transitionsBySymbol.size() - 1; i++) {
            code.append("  ] ");
        }
        if (!transitionsBySymbol.isEmpty()) {
            code.append("]\n");
        }
    }

    /**
     * Génère le code pour les transitions multiples (avec priorité)
     * @param transitions Liste des transitions à gérer
     * @param code Le StringBuilder contenant le code à construire
     */
    private void genererTransitionsMultiples(List<Transition> transitions, StringBuilder code) {
        // Trier par priorité (les conditions les plus spécifiques en premier)
        transitions.sort((t1, t2) -> {
            if (t1.condition != null && t2.condition == null) return -1;
            if (t1.condition == null && t2.condition != null) return 1;
            return 0;
        });

        boolean premiereTransition = true;
        for (Transition t : transitions) {
            if (t.condition != null && !t.condition.isEmpty()) {
                if (premiereTransition) {
                    code.append("    ifelse (").append(convertirCondition(t.condition)).append(") [\n");
                    premiereTransition = false;
                } else {
                    code.append("    ] [\n");
                    code.append("      if (").append(convertirCondition(t.condition)).append(") [\n");
                }
                code.append("      set next-activity [ -> ").append(t.finalState.name).append(" ]\n");
            } else {
                // Transition par défaut
                code.append("    ] [\n");
                code.append("      set next-activity [ -> ").append(t.finalState.name).append(" ]\n");
            }
        }
        code.append("    ]\n");
    }

    /**
     * Génère la condition clavier correspondant au symbole
     * @param symbole Le symbole de transition
     * @return La condition NetLogo correspondante
     */
    private String genererConditionClavier(char symbole) {
        switch (symbole) {
            case 'Q': return "bouton-quitter = true";
            case 'P': return "bouton-ramasser = true";
            case 'D': return "bouton-deposer = true";
            case 'G': return "bouton-avancer = true";
            case 'A': return "bouton-arreter = true";
            case 'J': return "bouton-sauter = true";
            case 'N': return "bouton-nord = true";
            case 'S': return "bouton-sud = true";
            case 'E': return "bouton-est = true";
            case 'W': return "bouton-ouest = true";
            default: return "false";
        }
    }

    /**
     * Convertit une condition Java en condition NetLogo
     * @param condition La condition à convertir
     * @return La condition convertie en NetLogo
     */
    private String convertirCondition(String condition) {
        // Convertir les conditions courantes
        condition = condition.replace("[", "").replace("]", "");
        condition = condition.replace("object nearby", "objet-proche");
        condition = condition.replace("nbO < 10", "nb-objets < 10");
        condition = condition.replace("nbO > 0", "nb-objets > 0");
        condition = condition.replace(" and ", " and ");
        return condition;
    }

    /**
     * Récupère toutes les transitions depuis un état donné
     * @param state L'état source
     * @return Liste des transitions depuis cet état
     */
    private List<Transition> getTransitionsFromState(State state) {
        List<Transition> result = new ArrayList<>();
        for (Transition t : state.getTransitions()) {
            if (t.originState.equals(state)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Écrit le contenu dans un fichier
     * @param nomFichier Le nom du fichier à créer
     * @param contenu Le contenu à écrire
     */
    private void ecrireFichier(String nomFichier, String contenu) {
        try (FileWriter writer = new FileWriter(nomFichier)) {
            writer.write(contenu);
            System.out.println("Fichier " + nomFichier + " généré avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
}