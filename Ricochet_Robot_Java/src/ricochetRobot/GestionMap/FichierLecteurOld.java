package ricochetRobot.GestionMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ricochetRobot.Constants;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class FichierLecteurOld {

    public static void lireFichier(String cheminFichier, Plateau plateau) {
        int maxX = 0;
        int maxY = 0;

        // 🟢 Première passe : calculer la taille du plateau
        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] parties = ligne.split(",");
                if (parties.length != 3) continue;

                int x = Integer.parseInt(parties[1]);
                int y = Integer.parseInt(parties[2]);

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la première lecture du fichier : " + e.getMessage());
            return;
        }

        int taillePlateau = Math.max(maxX, maxY) + 1;
        plateau.initialiseCase(taillePlateau);
        plateau.ajoutBordure();
        plateau.ajoutBlockCentral();

        // 🟢 Deuxième passe : ajouter les éléments
        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                traiterLigne(ligne, plateau);
            }

            int nbrRobot = plateau.getNbrRobot();
            Constants.ROBOT = Constants.generateRobotList(nbrRobot);
            plateau.InitCoord();
            plateau.crée_list_murs();
            plateau.calcule_deplacement();

        } catch (IOException e) {
            System.err.println("Erreur lors de la deuxième lecture du fichier : " + e.getMessage());
        }
    }

    private static void traiterLigne(String ligne, Plateau plateau) {
        String[] parties = ligne.split(",");
        if (parties.length != 3) {
            System.err.println("Ligne invalide : " + ligne);
            return;
        }

        String type = parties[0];
        int x = Integer.parseInt(parties[1]);
        int y = Integer.parseInt(parties[2]);

        switch (type) {
            case "bv":
                plateau.add_barrev(x, y);
                break;
            case "bh":
                plateau.add_barreh(x, y);
                break;
            case "c_h_d":
            case "c1":
                plateau.add_coin_h_d(x, y);
                break;
            case "c_h_g":
            case "c4":
                plateau.add_coin_h_g(x, y);
                break;
            case "c_b_d":
            case "c2":
                plateau.add_coin_b_d(x, y);
                break;
            case "c_b_g":
            case "c3":
                plateau.add_coin_b_g(x, y);
                break;
            default:
                if (type.startsWith("r")) {
                    String robotId = type.substring(1);
                    Robot robot = new Robot(robotId, x, y);
                    plateau.add_robot(robot);
                } else if (type.startsWith("o")) {
                    String objId = type.substring(1);
                    Objective obj = new Objective(objId, x, y);
                    plateau.add_objective(obj);
                }
        }
    }
}

