package ricochetRobot;

import ricochetRobot.Algo.SolutionRapide;
import ricochetRobot.Algo.AStar.Astar;
import ricochetRobot.Algo.AStar.Heuristique.HeuristiqueManhattan;
import ricochetRobot.Algo.BFS.BFS;
import ricochetRobot.Algo.BFS.BFSQueue;
import ricochetRobot.Algo.BFS.BFSRandom;
import ricochetRobot.Algo.BranchAndBound.BranchAndBound;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.GestionMap.FichierLecteur;
import ricochetRobot.ObjetsDuJeu.Plateau;



public class Main {
    public static void main(String[] args) {

        Plateau plateau = new Plateau();

        // Nom du fichier
        // String nomFichier = "8rob31sec.txt";
        // String nomFichier = "taille128.txt";
        String nomFichier = "Map1.txt";

        // Dossier de base où sont stockés les cartes
        String dossierBase = "C:/Users/alexi/OneDrive/Documents/Ricochet Robot/DossierRicochetRobot/Map/";

        // Construction du chemin complet
        String cheminFichier = dossierBase + nomFichier;

        // Lire le fichier et peupler le plateau
        FichierLecteur.lireFichier(cheminFichier, plateau);

        // Choix des algorithmes à exécuter
        boolean runFastSolution = false;
        boolean runBranchAndBound = false;
        boolean runBFS = true;
        boolean runBFSQueue = false;
        boolean runBFSRandom = false;
        boolean runAStar = true;

        if (runFastSolution) lancerSolutionRapide(plateau);
        if (runBranchAndBound) lancerBranchAndBound(plateau);
        if (runBFS) lancerBFS(plateau);
        if (runBFSQueue) lancerBFSQueue(plateau);
        if (runBFSRandom) lancerBFSRandom(plateau);
        if (runAStar) lancerAStar(plateau);
    }

    private static void lancerAStar(Plateau plateau) {
        Astar astar = new Astar(plateau, new HeuristiqueManhattan());
        long debut = System.currentTimeMillis();
        Chemin chemin = astar.aStarSearch();
        long fin = System.currentTimeMillis();
        afficherResultat("A*", chemin, debut, fin);
    }

    private static void lancerSolutionRapide(Plateau plateau) {
        SolutionRapide algoRapide = new SolutionRapide(plateau);
        long debut = System.currentTimeMillis();
        Chemin chemin = algoRapide.sol_fast(100);
        long fin = System.currentTimeMillis();
        afficherResultat("Solution rapide", chemin, debut, fin);
    }

    private static void lancerBranchAndBound(Plateau plateau) {
        BranchAndBound algo = new BranchAndBound(plateau);
        long debut = System.currentTimeMillis();
        Chemin chemin = algo.BranchAndBoundAlgo(20, false);
        long fin = System.currentTimeMillis();
        afficherResultat("Branch and Bound", chemin, debut, fin);
    }

    private static void lancerBFS(Plateau plateau) {
        BFS algo = new BFS(plateau);
        long debut = System.currentTimeMillis();
        Chemin chemin = algo.solveOpt();
        long fin = System.currentTimeMillis();
        afficherResultat("BFS", chemin, debut, fin);
    }

    private static void lancerBFSQueue(Plateau plateau) {
        BFSQueue algo = new BFSQueue(plateau);
        long debut = System.currentTimeMillis();
        Chemin chemin = algo.solveOpt();
        long fin = System.currentTimeMillis();
        afficherResultat("BFSQueue", chemin, debut, fin);
    }

    private static void lancerBFSRandom(Plateau plateau) {
        BFSRandom algo = new BFSRandom(plateau, 0.5);
        long debut = System.currentTimeMillis();
        Chemin chemin = algo.solve();
        long fin = System.currentTimeMillis();
        afficherResultat("BFSRandom", chemin, debut, fin);
    }

    private static void afficherResultat(String nomAlgo, Chemin chemin, long debut, long fin) {
        long temps = (fin - debut) / 1000;
        System.out.println(nomAlgo + " : " + chemin + " | Temps : " + temps + " secondes");
    }
}