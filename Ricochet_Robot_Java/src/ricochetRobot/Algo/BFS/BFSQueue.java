package ricochetRobot.Algo.BFS;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.GestionEtat.GestionEtatLongSet;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class BFSQueue {     // faire un BFS random
    Plateau plateau;
    GestionEtatLongSet listEtatsVisites;
    Chemin meilleurChemin;

    public BFSQueue(Plateau plateau) {
        this.plateau = plateau;
    }

    public void reset_memory(){
        listEtatsVisites = new GestionEtatLongSet();
    }

    public Chemin solveOpt() {
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

        Queue<EtatPlateau> queue = new ArrayDeque<>();
        queue.offer(etatInit);

        while (!queue.isEmpty()) {
            EtatPlateau courant = queue.poll();

            for (String robot : Constants.ROBOT) {
                for (String direction : Constants.DIRECTION) {
                    plateau.replacerRobot(courant.getRobots());
                    boolean moved = plateau.deplacer_robot_dicho_bool(robot, direction);
                    if (!moved) continue;

                    EtatPlateau voisin = new EtatPlateau(plateau);
                    if (listEtatsVisites.estDejaRencontre(voisin)) continue;

                    // Chemin = copie du chemin courant + ce déplacement
                    Chemin chemin = courant.getChemin().copy();
                    chemin.addDepFin(new Deplacement(robot, direction));
                    voisin.setChemin(chemin);

                    listEtatsVisites.ajouterEtat(voisin);
                    queue.offer(voisin);

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
        return null; // Aucun chemin trouvé
    }

    public Chemin solve2() {
        reset_memory();
        plateau.replacerRobot(plateau.getCoordInit());

        String couleur = plateau.getObjective().getCouleur();
        int objX = plateau.getObjective().getPosX();
        int objY = plateau.getObjective().getPosY();
        Robot robotPrincipal = plateau.getRobot(couleur);

        String[] ROBOT = Constants.ROBOT;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(ROBOT));
        // Retire le robot principal
        list.remove(couleur);
        String[] ROBOT_SECONDAIRE = list.toArray(new String[0]);

        Chemin initChemin = plateau.cheminBFS(robotPrincipal.getPosX(), robotPrincipal.getPosY(), objX, objY, couleur);
        if (initChemin.getLongueur() <= 2) {
            return initChemin;
        }

        EtatPlateau etatInit = new EtatPlateau(plateau);
        listEtatsVisites.ajouterEtat(etatInit);

        Queue<EtatPlateau> queue = new ArrayDeque<>();
        queue.offer(etatInit);

        while (!queue.isEmpty()) {
            EtatPlateau courant = queue.poll();

            for (String robot : Constants.ROBOT) {
                for (String direction : Constants.DIRECTION) {
                    plateau.replacerRobot(courant.getRobots());
                    boolean moved = plateau.deplacer_robot_dicho_bool(robot, direction);
                    if (!moved) continue;

                    EtatPlateau voisin = new EtatPlateau(plateau);
                    if (listEtatsVisites.estDejaRencontre(voisin)) continue;

                    // Chemin = copie du chemin courant + ce déplacement
                    Chemin chemin = courant.getChemin().copy();
                    chemin.addDepFin(new Deplacement(robot, direction));
                    voisin.setChemin(chemin);

                    listEtatsVisites.ajouterEtat(voisin);
                    queue.offer(voisin);

                    // Vérifie si robot principal proche de l'objectif
                    int rx = voisin.getRobots().get(couleur).getX();
                    int ry = voisin.getRobots().get(couleur).getY();
                    Chemin versObjectif = plateau.cheminBFS(rx, ry, objX, objY, couleur);

                    if (versObjectif.getLongueur() <= 2) {
                        chemin.ajoutCheminFin(versObjectif);
                        return chemin;
                    }

                    if (versObjectif.getLongueur() == 3) {
                            for (String dir: Constants.DIRECTION) {
                                for (String rob_sec: ROBOT_SECONDAIRE) {
                                    plateau.replacerRobot(voisin.getRobots());
                                    boolean moved2 = plateau.deplacer_robot_dicho_bool(rob_sec, dir);
                                    if (!moved2) continue;
                                    Chemin cheminPourFinirNew = plateau.cheminBFS(rx, ry, objX, objY, couleur);
                                    if (cheminPourFinirNew.getLongueur() <= 2) {
                                        chemin.addDepFin(new Deplacement(rob_sec, dir));
                                        chemin.ajoutCheminFin(versObjectif);
                                        System.out.println("là " + listEtatsVisites.size());
                                        return chemin;
                                    }
                                }
                            }
                            chemin.ajoutCheminFin(versObjectif);
                            System.out.println("lolo " + listEtatsVisites.size());
                            return chemin;
                        }
                }
            }
        }

        return null; // Aucun chemin trouvé
    }
}