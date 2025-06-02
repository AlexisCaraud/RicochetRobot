package ricochetRobot.GestionMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ricochetRobot.Constants;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class FichierLecteur {

    public static void lireFichier(String cheminFichier, Plateau plateau) {
        String section = "";
        int tailleGrille = 0;
        int tailleCaseCentral = 0;
        List<Robot> robots = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty()) continue;

                // Gestion des sections
                if (ligne.equals("OBJECTIF") ||
                ligne.startsWith("NOMBRE_ROBOTS") || 
                ligne.equals("COIN_HAUT_DROIT") ||
                ligne.equals("COIN_HAUT_GAUCHE") ||
                ligne.equals("COIN_BAS_DROIT") ||
                ligne.equals("COIN_BAS_GAUCHE") ||
                ligne.equals("BARRE_VERTICALE") ||
                ligne.equals("BARRE_HORIZONTALE")) {
                // Si ligne commence par "NOMBRE_ROBOTS", on garde juste "NOMBRE_ROBOTS" comme section
                    if (ligne.startsWith("NOMBRE_ROBOTS")) {
                        section = "NOMBRE_ROBOTS";
                    } else {
                        section = ligne;
                    }
                continue;
                }

                // Lecture taille de la grille
                if (ligne.startsWith("TAILLE_DE_LA_GRILLE")) {
                    String[] parts = ligne.split("\\s+");
                    if (parts.length == 2) {
                        tailleGrille = Integer.parseInt(parts[1]);
                        plateau.initialiseCase(tailleGrille);
                        plateau.ajoutBordure();
                    }
                    continue;
                }

                if (ligne.startsWith("TAILLE_CASE_CENTRALE")) {
                    String[] parts = ligne.split("\\s+");
                    if (parts.length == 2) {
                        tailleCaseCentral = Integer.parseInt(parts[1]);
                        plateau.setCentral(tailleCaseCentral);
                        plateau.ajoutBlockCentral();
                    }
                    continue;
                }

                // Traitement selon section
                switch (section) {
                    case "OBJECTIF":
                        traiterObjectif(ligne, plateau);
                        break;

                    case "NOMBRE_ROBOTS":
                        // Ici on attend une ligne avec le nombre de robots, ou les robots eux-mêmes ?
                        // Par ex, si c’est juste un nombre, on peut l’ignorer ou le stocker
                        // sinon, si la ligne commence par "rob" on la traite en robots
                        if (ligne.matches("\\d+")) {
                            // nombre de robots, on peut ignorer ou stocker
                            // int nbrRobots = Integer.parseInt(ligne);
                        } else if (ligne.startsWith("rob")) {
                            traiterRobot(ligne, robots);
                        }
                        break;

                    case "COIN_HAUT_DROIT":
                        traiterCoin(ligne, plateau, "haut_droite");
                        break;

                    case "COIN_HAUT_GAUCHE":
                        traiterCoin(ligne, plateau, "haut_gauche");
                        break;

                    case "COIN_BAS_DROIT":
                        traiterCoin(ligne, plateau, "bas_droite");
                        break;

                    case "COIN_BAS_GAUCHE":
                        traiterCoin(ligne, plateau, "bas_gauche");
                        break;

                    case "BARRE_VERTICALE":
                        traiterBarre(ligne, plateau, true);
                        break;

                    case "BARRE_HORIZONTALE":
                        traiterBarre(ligne, plateau, false);
                        break;

                    default:
                        // aucune action
                }
            }

            // Ajout robots dans le plateau après lecture complète
            for (Robot robot : robots) {
                plateau.add_robot(robot);
            }

            plateau.InitCoord();
            plateau.crée_list_murs();
            plateau.calcule_deplacement();

            // Mise à jour constante robots si nécessaire
            int nbrRobot = plateau.getNbrRobot();
            Constants.ROBOT = Constants.generateRobotList(nbrRobot);

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }

    private static void traiterObjectif(String ligne, Plateau plateau) {
        String[] parts = ligne.split("\\s+");
        if (parts.length == 3 && parts[0].startsWith("obj")) {
            String objId = parts[0].substring(3); // récupère le numéro après "obj"
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            plateau.add_objective(new Objective(objId, x, y));
        } else {
            System.err.println("Ligne OBJECTIF invalide : " + ligne);
        }
    }

    private static void traiterRobot(String ligne, List<Robot> robots) {
        String[] parts = ligne.split("\\s+");
        if (parts.length == 3 && parts[0].startsWith("rob")) {
            String robotId = parts[0].substring(3); // récupère le numéro après "rob"
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            Robot robot = new Robot(robotId, x, y);
            robots.add(robot);
        } else {
            System.err.println("Ligne ROBOT invalide : " + ligne);
        }
    }

    private static void traiterCoin(String ligne, Plateau plateau, String type) {
        String[] parts = ligne.split("\\s+");
        if (parts.length == 2) {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            switch (type) {
                case "haut_droite":
                    plateau.add_coin_h_d(x, y);
                    break;
                case "haut_gauche":
                    plateau.add_coin_h_g(x, y);
                    break;
                case "bas_droite":
                    plateau.add_coin_b_d(x, y);
                    break;
                case "bas_gauche":
                    plateau.add_coin_b_g(x, y);
                    break;
            }
        } else {
            System.err.println("Ligne COIN invalide : " + ligne);
        }
    }

    private static void traiterBarre(String ligne, Plateau plateau, boolean verticale) {
        String[] parts = ligne.split("\\s+");
        if (parts.length == 2) {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (verticale) {
                plateau.add_barrev(x, y);
            } else {
                plateau.add_barreh(x, y);
            }
        } else {
            System.err.println("Ligne BARRE invalide : " + ligne);
        }
    }
}
