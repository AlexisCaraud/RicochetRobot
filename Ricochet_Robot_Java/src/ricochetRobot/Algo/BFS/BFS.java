package ricochetRobot.Algo.BFS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.GestionEtat.GestionEtatLongSet;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class BFS {
    Plateau plateau;
    GestionEtatLongSet listEtatsVisites;
    List<EtatPlateau> listEtatsATraiter;

    public BFS(Plateau plateau) {
        this.plateau = plateau;
    }

    public void reset_memory(){
        listEtatsVisites = new GestionEtatLongSet();
        listEtatsATraiter = new ArrayList<>();
    }

    /**
     * Methode qui renvoie le plus court chemin pour atteindre l'objectif en faisant un BFS
     * Algorithme optimal car on s'arrête quand le robot principal est à 1 déplacement de l'objectif
     */

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
        List<EtatPlateau> listEtatsATraiter = new ArrayList<>();
        listEtatsATraiter.add(etatInit);

        int count = 0;

        while (true) {
            List<EtatPlateau> newlistEtat = new ArrayList<>();
            for (EtatPlateau courant : listEtatsATraiter) {
                for (String robot : Constants.ROBOT) {
                    for (String direction : Constants.DIRECTION) {
                        plateau.replacerRobot(courant.getRobots());
                        boolean moved = plateau.deplacementAvecRobot(robot, direction);
                        if (!moved) continue;

                        EtatPlateau voisin = new EtatPlateau(plateau);
                        if (listEtatsVisites.estDejaRencontre(voisin)) continue;

                        // Chemin = copie du chemin courant + ce déplacement
                        Chemin chemin = courant.getChemin().copy();
                        chemin.addDepFin(new Deplacement(robot, direction));
                        voisin.setChemin(chemin);

                        listEtatsVisites.ajouterEtat(voisin);
                        newlistEtat.add(voisin);

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
            count++;
            listEtatsATraiter = newlistEtat;
            System.out.println("Chemins parcourus à " + count + " coups");
        }
    }

    /**
     * Methode qui renvoie le plus court chemin pour atteindre l'objectif en faisant un BFS
     * Algorithme non optimal car on s'arrête quand le robot principal est à 3 déplacement de l'objectif ou moins
     */

    public Chemin solve2() {
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

        String[] ROBOT = Constants.ROBOT;
        ArrayList<String> list = new ArrayList<>(Arrays.asList(ROBOT));
        list.remove(couleur);
        String[] ROBOT_SECONDAIRE = list.toArray(new String[0]);

        EtatPlateau etatInit = new EtatPlateau(plateau);
        listEtatsVisites.ajouterEtat(etatInit);
        List<EtatPlateau> listEtatsATraiter = new ArrayList<>();
        listEtatsATraiter.add(etatInit);

        int count = 0;

        while (true) {
            List<EtatPlateau> newlistEtat = new ArrayList<>();
            for (EtatPlateau courant : listEtatsATraiter) {
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
                        newlistEtat.add(voisin);

                        // Vérifie si robot principal proche de l'objectif
                        int rx = voisin.getRobots().get(couleur).getX();
                        int ry = voisin.getRobots().get(couleur).getY();

                        Chemin versObjectif = plateau.cheminBFS(rx, ry, objX, objY, couleur);

                        if (versObjectif.getLongueur() <= 2) {
                            chemin.ajoutCheminFin(versObjectif);
                            System.out.println("làlà " + listEtatsVisites.size());
                            return chemin;
                        }

                        if (versObjectif.getLongueur() == 3) {
                            for (String dir: Constants.DIRECTION) {
                                for (String rob_sec: ROBOT_SECONDAIRE) {
                                    plateau.replacerRobot(voisin.getRobots());
                                    plateau.deplacer_robot(rob_sec, dir);
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
            count++;
            listEtatsATraiter = newlistEtat;
            System.out.println("Chemins parcourus à " + count + " coups");
        }
    }
}


 // Chemin versObjectifSansRobot = plateau.cheminBFS(rx, ry, objX, objY, couleur, true);
                        // if (versObjectifSansRobot.getLongueur() == 1) {
                        // if (robotPrincipal.getPosX() == objX) {
                        //     int indice = plateau.indiceEncadrant(plateau.getMurs_horizontaux().get(objX), robotPrincipal.getPosY());
                        //     if (plateau.getMurs_horizontaux().get(robotPrincipal.getPosY()).get(indice) == objX)
                        // }
                        //     System.out.println(("coucou"));
                        //     for (String robot_sec : ROBOT_SECONDAIRE) {
                        //         for (String dir : Constants.DIRECTION) {
                        //             boolean moved2 = plateau.deplacer_robot_bool(robot_sec, dir);
                        //             if (!moved2) continue;
                        //             Chemin versObjectif2 = plateau.cheminBFS(rx, ry, objX, objY, couleur);
                        //             if (versObjectif2.getLongueur() == 1) {
                        //                 chemin.addDepFin(new Deplacement(robot_sec, dir));
                        //                 chemin.ajoutCheminFin(versObjectif2);
                        //                 return chemin;
                        //             }
                        //         }
                        //     }
                        // }