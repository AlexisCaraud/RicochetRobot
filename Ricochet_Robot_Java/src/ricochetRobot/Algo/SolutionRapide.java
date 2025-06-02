package ricochetRobot.Algo;
import java.util.ArrayList;
import java.util.List;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.GestionEtat.GestionEtat;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class SolutionRapide {
    Plateau plateau;
    GestionEtat listEtatsVisites;
    List<EtatPlateau> listEtatsATraiter;
    Chemin meilleurChemin;

    public SolutionRapide(Plateau plateau){
        this.plateau = plateau;
    }

    public void reset_memory(){
        listEtatsVisites = new GestionEtat();
        listEtatsATraiter = new ArrayList<>();
    }

    public Chemin sol_fast(int max_dep_sec){
        reset_memory();
        plateau.replacerRobot(plateau.getCoordInit());
        String[] DIRECTION = Constants.DIRECTION;
        String[] ROBOT = Constants.ROBOT;
        int xobj = plateau.getObjective().getPosX();
        int yobj = plateau.getObjective().getPosY();
        String couleur = plateau.getObjective().getCouleur();
        
        EtatPlateau etatInit = new EtatPlateau(plateau);
        listEtatsVisites.ajouterEtat(etatInit);
        listEtatsATraiter.add(etatInit);
        int count = 0;

        int xrobot = etatInit.getRobots().get(couleur).getX();
        int yrobot = etatInit.getRobots().get(couleur).getY();
        
        Chemin cheminTestInit = plateau.cheminBFS(xrobot, yrobot, xobj, yobj, couleur);
        if (cheminTestInit.getLongueur() < Integer.MAX_VALUE){
            reset_memory();
            return cheminTestInit;
        }

        while (count < max_dep_sec){
            List<EtatPlateau> newlistEtat = new ArrayList<>();
            for (EtatPlateau etatPrecedents:listEtatsATraiter){
                for (String direction: DIRECTION){
                    for (String robot: ROBOT){
                        plateau.replacerRobot(etatPrecedents.getRobots());
                        boolean changed = plateau.deplacer_robot_bool(robot, direction);
                        if (changed) {
                            EtatPlateau nouvelEtat = new EtatPlateau(plateau);
                            if (!listEtatsVisites.estDejaRencontre(nouvelEtat)){
                                Chemin copieChemin = etatPrecedents.getChemin().copy(); // Crée une copie indépendante du chemin
                                nouvelEtat.setChemin(copieChemin);
                                nouvelEtat.getChemin().addDepFin(new Deplacement(robot, direction));
                                listEtatsVisites.ajouterEtat(nouvelEtat);
                                newlistEtat.add(nouvelEtat);
                                int xrob = nouvelEtat.getRobots().get(couleur).getX();
                                int yrob = nouvelEtat.getRobots().get(couleur).getY();
                                Chemin cheminPourFinir = plateau.cheminBFS(xrob, yrob, xobj, yobj, couleur);
                                if (cheminPourFinir.getLongueur() < Integer.MAX_VALUE){
                                    nouvelEtat.getChemin().ajoutCheminFin(cheminPourFinir);
                                    reset_memory();
                                    return nouvelEtat.getChemin();
                                }
                            }
                        }
                    }
                }
            }
            listEtatsATraiter = newlistEtat;
            count += 1;
        }
        // Aucun chemin trouvé
        throw new RuntimeException("Aucun chemin trouvé !");
    }
}
