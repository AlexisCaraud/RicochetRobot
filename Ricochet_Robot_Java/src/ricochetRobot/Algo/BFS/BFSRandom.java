package ricochetRobot.Algo.BFS;

import java.util.*;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.GestionEtat.GestionEtatLongSet;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class BFSRandom {
    Plateau plateau;
    GestionEtatLongSet listEtatsVisites;
    double p;
    Random rand = new Random();

    public BFSRandom(Plateau plateau, double p) {
        this.plateau = plateau;
        this.p = p;
    }

    public void reset_memory() {
        listEtatsVisites = new GestionEtatLongSet();
    }

    public Chemin solve() {
        reset_memory();
        plateau.replacerRobot(plateau.getCoordInit());

        String couleur = plateau.getObjective().getCouleur();
        int objX = plateau.getObjective().getPosX();
        int objY = plateau.getObjective().getPosY();
        Robot robotPrincipal = plateau.getRobot(couleur);

        Chemin initChemin = plateau.cheminBFS(robotPrincipal.getPosX(), robotPrincipal.getPosY(), objX, objY, couleur);
        if (initChemin.getLongueur() <= 2) {
            return initChemin;
        }

        EtatPlateau etatInit = new EtatPlateau(plateau);
        listEtatsVisites.ajouterEtat(etatInit);
        List<EtatPlateau> listEtatsATraiter = new ArrayList<>();
        listEtatsATraiter.add(etatInit);

        while (true) {
            // Sélection aléatoire de l'état
            int taille = listEtatsATraiter.size();
            // int limite = Math.min(taille, Math.max(5, taille / 5));
            int index = rand.nextInt(taille);
            EtatPlateau etatAleatoire = listEtatsATraiter.get(index);

            // Sélection aléatoire du robot
            String robotChoisi;
            if (rand.nextDouble() < p) {
                robotChoisi = couleur;
            } else {
                List<String> secondaires = new ArrayList<>(Arrays.asList(Constants.ROBOT));
                secondaires.remove(couleur);
                robotChoisi = secondaires.get(rand.nextInt(secondaires.size()));
            }
            // Sélection aléatoire de direction
            String directionChoisie = Constants.DIRECTION[rand.nextInt(Constants.DIRECTION.length)];
            plateau.replacerRobot(etatAleatoire.getRobots());
            boolean moved = plateau.deplacementAvecRobot(robotChoisi, directionChoisie);
            if (!moved) continue;

            EtatPlateau voisin = new EtatPlateau(plateau);
            if (listEtatsVisites.estDejaRencontre(voisin)) continue;

            // Chemin = copie du chemin courant + ce déplacement
            Chemin chemin = etatAleatoire.getChemin().copy();
            chemin.addDepFin(new Deplacement(robotChoisi, directionChoisie));
            voisin.setChemin(chemin);

            listEtatsVisites.ajouterEtat(voisin);
            listEtatsATraiter.add(voisin);

            for (String dir : Constants.DIRECTION) {
                plateau.replacerRobot(voisin.getRobots());
                boolean moved2 = plateau.deplacer_robot_dicho_bool(couleur, dir);
                if (!moved2) continue;
                int robX = plateau.getCoordRobot().get(couleur).getX();
                int robY = plateau.getCoordRobot().get(couleur).getY();
                if (robX == objX && robY == objY) {
                    chemin.addDepFin(new Deplacement(couleur, dir));
                    System.out.println("lolo " + listEtatsVisites.size());
                    return chemin;
                }
            }
        }
    }
}