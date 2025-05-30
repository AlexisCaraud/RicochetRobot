package ricochetRobot.GestionMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ricochetRobot.ObjetsDuJeu.Case;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class FichierEcrivain {

    public static void ecrireFichier(String cheminFichier, Plateau plateau) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(cheminFichier))) {
            for (int i = 0; i < plateau.getTaille(); i++) {
                for (int j = 0; j < plateau.getTaille(); j++) {
                    Case c = plateau.getPlateau()[i][j];

                    // Écrire les murs
                    if ((i==0 || i == plateau.getTaille() - 1) && j != plateau.getTaille()-1 && c.isMurDroite() && plateau.getPlateau()[i][j+1].isMurGauche()) {
                        bw.write(String.format("bv,%d,%d\n", i, j));
                    }
                    else if ((j==0 || j == plateau.getTaille() - 1) && i != plateau.getTaille()-1 && c.isMurBas() && plateau.getPlateau()[i+1][j].isMurHaut()) {
                        bw.write(String.format("bh,%d,%d\n", i, j));
                    }
                    if (i!=0 && j!=0 && i!=plateau.getTaille()-1 && j!=plateau.getTaille()-1){
                        if(c.isMurHaut() && c.isMurDroite()){
                            bw.write(String.format("c_h_d,%d,%d\n", i, j));
                        }
                        else if(c.isMurBas() && c.isMurDroite()){
                            bw.write(String.format("c_b_d,%d,%d\n", i, j));
                        }
                        else if(c.isMurBas() && c.isMurGauche()){
                            bw.write(String.format("c_b_g,%d,%d\n", i, j));
                        }
                        else if(c.isMurHaut() && c.isMurGauche()){
                            bw.write(String.format("c_h_g,%d,%d\n", i, j));
                        }
                    }
                }
            }

            // Écrire les robots
            for (Robot robot : plateau.getRobots()) {
                bw.write(String.format("r%s,%d,%d\n", robot.getCouleur(), robot.getPosX(), robot.getPosY()));
            }

            // Écrire l'objectif
            bw.write(String.format("o%s,%d,%d\n", plateau.getObjective().getCouleur(), plateau.getObjective().getPosX(), plateau.getObjective().getPosY()));

        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }
}
