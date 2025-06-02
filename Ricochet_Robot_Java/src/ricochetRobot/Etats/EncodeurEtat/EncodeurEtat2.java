package ricochetRobot.Etats.EncodeurEtat;

import ricochetRobot.Constants;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Coordonnees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

 /**
     * Classe qui encode les états sans prendre en compte l'ordre des robots secondaires
     */

public class EncodeurEtat2 {

    public static long encoderEtatPlateau(EtatPlateau etat, String couleur) {
    Map<String, Coordonnees> robots = etat.getRobots();
    long code = 0;

    String[] ROBOT = Constants.ROBOT;
    ArrayList<String> list = new ArrayList<>(Arrays.asList(ROBOT));
    // Retire le robot principal
    list.remove(couleur);
    String[] ROBOT_SECONDAIRE = list.toArray(new String[0]);

    
    // 1. Encoder le robot principal
    Coordonnees principal = robots.get(couleur);
    int posPrincipal = principal.getX() * 16 + principal.getY();
    code |= ((long) posPrincipal); // bits 0-7

    // 2. Encoder les robots secondaires (ordre non significatif)
    int[] secondaryPositions = new int[Constants.ROBOT.length-1];
    int pos = 0;
    for (String rob_sec: ROBOT_SECONDAIRE) {
        Coordonnees c = robots.get(rob_sec);
        secondaryPositions[pos] = c.getX() * 16 + c.getY();
        pos++;
    }

    // Tri des positions pour que l'ordre n'ait pas d'importance
    Arrays.sort(secondaryPositions);


    for (int i = 0; i < secondaryPositions.length; i++) {
        code |= ((long) secondaryPositions[i] << ((i + 1) * 8)); // bits 8-... selon n
    }

    return code;
}
}
