package ricochetRobot.Launcher;

import ricochetRobot.Algo.BFS.BFS;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.GestionMap.FichierLecteur;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class BFSLauncher {

    public static void main(String[] args) {
        // Création du plateau de jeu
        Plateau plateau = new Plateau(16); // Plateau de 16x16

        // Chemin vers le fichier
        String cheminFichier = "C:/Users/alexi/OneDrive/Documents/Ricochet Robot/DossierRicochetRobot/Map/mapActuelle.txt";

        // Lire le fichier et peupler le plateau
        FichierLecteur.lireFichier(cheminFichier, plateau);

        // Initialisation de l'algorithme
        BFS algoBFS = new BFS(plateau);

        long debut2 = System.currentTimeMillis();

        // Calcul du chemin avec l'algorithme B&B
        Chemin chemin2 = algoBFS.solveOpt();

        long fin2 = System.currentTimeMillis();

        // Calcul du temps écoulé
        long tempsEcoule2 = (fin2 - debut2) / 1000;

        // Affichage de la solution et du temps d'exécution
        System.out.println("solution BFS: " + chemin2 + " time: " + tempsEcoule2 + "secondes");
    }
}
