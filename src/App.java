package src;

import java.io.BufferedReader;
import java.io.FileReader;

public class App {
    public static void main(String[] args) throws Exception {
        Automate a = new Automate("lib/automate.txt");

        System.out.println(a.toString());

        Automate automateFacile = new Automate("lib/automate-facile.txt");

        System.out.println(automateFacile.toString());

        testerAutomate(automateFacile, "lib/tests_automate.txt");
    }

    private static void testerAutomate(Automate a, String testFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        String line;

        System.out.println("\nExécution des tests:");
        System.out.println("====================");

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Ignorer les lignes vides et les commentaires
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // Séparer la séquence du résultat attendu
            String[] parts = line.split("->");
            if (parts.length != 2) continue;

            String sequence = parts[0].trim();
            String expected = parts[1].trim().split(" ")[0];
            String comment = parts[1].contains("[") ?
                    parts[1].substring(parts[1].indexOf("[") + 1, parts[1].indexOf("]")) : "";

            // Exécuter le test
            boolean result = a.belong(sequence);
            String status = (result && expected.equals("true")) || (!result && expected.equals("false"))
                    ? "SUCCÈS" : "ÉCHEC";

            // Afficher le résultat
            System.out.printf("Test '%s': %s (Attendu: %s, Obtenu: %s) %s%n",
                    sequence, status, expected, result, comment);
        }

        reader.close();
    }
}
