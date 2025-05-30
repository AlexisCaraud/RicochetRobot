package ricochetRobot;

public class Constants {

    // public static final int NBR_ROBOT = 4;

    public static final int NBR_COIN = 1;

    public static final int NBR_BARRE = 1;

    // Liste des directions
    public static final String[] DIRECTION = {"HAUT", "BAS", "GAUCHE", "DROITE"};

    // Liste des robots générée dynamiquement de 0 à NBR_ROBOT-1
    public static String[] ROBOT;

    // Méthode pour générer la liste des robots
    public static String[] generateRobotList(int nbrRobot) {
        String[] robotList = new String[nbrRobot];
        for (int i = 0; i < nbrRobot; i++) {
            robotList[i] = Integer.toString(i); // Convertit l'entier i en chaîne de caractères
        }
        return robotList;
    }
}

