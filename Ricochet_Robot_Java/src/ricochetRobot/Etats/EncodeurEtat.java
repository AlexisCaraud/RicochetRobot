package ricochetRobot.Etats;

import ricochetRobot.Constants;
import ricochetRobot.ObjetsDuJeu.Coordonnees;

import java.util.Map;

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
