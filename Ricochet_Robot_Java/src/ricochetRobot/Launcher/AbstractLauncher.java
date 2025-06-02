package ricochetRobot.Launcher;

import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.GestionMap.FichierLecteur;
import ricochetRobot.ObjetsDuJeu.Plateau;

public abstract class AbstractLauncher {

    protected static final String CHEMIN_FICHIER = "C:/Users/alexi/OneDrive/Documents/Ricochet Robot/DossierRicochetRobot/Map/mapActuelle.txt";

    // méthode principale qui lance le process
    public void run() {
        Plateau plateau = new Plateau();
        FichierLecteur.lireFichier(CHEMIN_FICHIER, plateau);

        long debut = System.currentTimeMillis();

        Chemin chemin = runAlgorithm(plateau);

        long fin = System.currentTimeMillis();
        long tempsEcoule = (fin - debut) / 1000;

        System.out.println(getDescription() + ": " + chemin + " time: " + tempsEcoule + " secondes");
    }

    protected abstract Chemin runAlgorithm(Plateau plateau);

    protected abstract String getDescription();
}
