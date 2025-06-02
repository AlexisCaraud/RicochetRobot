package ricochetRobot.Etats.EncodeurEtat;

import ricochetRobot.Constants;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Coordonnees;

import java.math.BigInteger;
import java.util.*;

 /**
     Classe qui encode chaque état en lui associant un BigInteger à partir des coordonnées de ses robots
     Fonctionne pour toute taille de grille et nombre de robot
     Ne prend pas en compte l'ordre des robots secondaires
     */

public class EncodeurEtatFlexFast {

    private static final int BOARD_BITS = 6; // 64x64 => 6 bits pour x et y
    private static final int POS_BITS = BOARD_BITS * 2; // 12 bits par robot
    // private static final int BOARD_SIZE = 1 << BOARD_BITS; // 64

    public static BigInteger encoderEtatPlateau(EtatPlateau etat, String couleur) {
        Map<String, Coordonnees> robots = etat.getRobots();

        List<Integer> secondaryPositions = new ArrayList<>();
        int posPrincipal = encodeCoord(robots.get(couleur));

        for (String robot : Constants.ROBOT) {
            if (!robot.equals(couleur)) {
                secondaryPositions.add(encodeCoord(robots.get(robot)));
            }
        }

        Collections.sort(secondaryPositions);

        BigInteger code = BigInteger.ZERO;

        // Encode les secondaires en poids forts
        for (int pos : secondaryPositions) {
            code = code.shiftLeft(POS_BITS).or(BigInteger.valueOf(pos));
        }

        // Ajoute le robot principal en dernier
        code = code.shiftLeft(POS_BITS).or(BigInteger.valueOf(posPrincipal));

        return code;
    }

    private static int encodeCoord(Coordonnees c) {
        return (c.getX() << BOARD_BITS) | c.getY(); // x * 64 + y, mais en bitwise
    }
}
