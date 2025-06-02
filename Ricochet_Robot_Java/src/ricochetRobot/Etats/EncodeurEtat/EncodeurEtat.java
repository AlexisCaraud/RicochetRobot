package ricochetRobot.Etats.EncodeurEtat;

import ricochetRobot.Constants;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Coordonnees;

import java.util.Map;

 /**
     Classe qui encode chaque état en lui associant un long à partir des coordonnées de ses robots
     Pour une grille 16*16, fonctionne pour maximum 8 robots
     */

public class EncodeurEtat {

    public static long encoderEtatPlateau(EtatPlateau etat) {
    Map<String, Coordonnees> robots = etat.getRobots();
    long code = 0;

    for (int i = 0; i < Constants.ROBOT.length; i++) {
        Coordonnees c = robots.get(Constants.ROBOT[i]);
        int pos = c.getX() * 16 + c.getY(); // valeur entre 0 et 255
        code |= ((long) pos << (i * 8));    // Cast explicite pour éviter débordement
    }

    return code;
    }
}
