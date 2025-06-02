package ricochetRobot.Algo.BFS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.Etats.GestionEtat.GestionEtatLongSet;
import ricochetRobot.ObjetsDuJeu.Plateau;
import ricochetRobot.ObjetsDuJeu.Robot;

public class BFSMultiThread {
    Plateau plateau;
    GestionEtatLongSet listEtatsVisites;
    List<EtatPlateau> listEtatsATraiter;
    private AtomicReference<Chemin> meilleurChemin = new AtomicReference<>(null);

    public BFSMultiThread(Plateau plateau) {
        this.plateau = plateau;
    }

    public void reset_memory(){
        listEtatsVisites = new GestionEtatLongSet();
        listEtatsATraiter = new ArrayList<>();
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

    String[] ROBOT = Constants.ROBOT;
    ArrayList<String> list = new ArrayList<>(Arrays.asList(ROBOT));
    list.remove(couleur);

    EtatPlateau etatInit = new EtatPlateau(plateau);
    listEtatsVisites.ajouterEtat(etatInit);
    List<EtatPlateau> listEtatsATraiter = new ArrayList<>();
    listEtatsATraiter.add(etatInit);
    int count = 0;

    while (true) {
        List<EtatPlateau> newListEtat = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final int currentCount = count;  // <-- copie finale de count pour la lambda

        for (EtatPlateau courant : listEtatsATraiter) {
            if (meilleurChemin.get() != null) break;
            for (String robot : Constants.ROBOT) {
                if (meilleurChemin.get() != null) break;
                for (String direction : Constants.DIRECTION) {
                    if (meilleurChemin.get() != null) break;

                    final EtatPlateau etatFinal = courant;
                    final String robotFinal = robot;
                    final String directionFinal = direction;

                    executor.submit(() -> {
                        Plateau plateauLocal;
                        synchronized (plateau) {
                            plateauLocal = plateau.clone();
                        }
                        plateauLocal.replacerRobot(etatFinal.getRobots());

                        boolean moved = plateauLocal.deplacementAvecRobot(robotFinal, directionFinal);
                        if (!moved) {
                            System.out.println("Déplacement impossible pour robot " + robotFinal + " direction " + directionFinal);
                            return;
                        }

                        EtatPlateau voisin = new EtatPlateau(plateauLocal);

                        synchronized (listEtatsVisites) {
                            if (listEtatsVisites.estDejaRencontre(voisin)) {
                                System.out.println("État déjà rencontré : " + voisin);
                                return;
                            }
                            listEtatsVisites.ajouterEtat(voisin);
                            System.out.println("Nouvel état ajouté : " + voisin);
                        }

                        Chemin chemin = etatFinal.getChemin().copy();
                        chemin.addDepFin(new Deplacement(robotFinal, directionFinal));
                        voisin.setChemin(chemin);
                        newListEtat.add(voisin);

                        int rx = voisin.getRobots().get(couleur).getX();
                        int ry = voisin.getRobots().get(couleur).getY();
                        Chemin versObjectif = plateauLocal.cheminBFS(rx, ry, objX, objY, couleur);

                        if (versObjectif.getLongueur() <= 2) {
                            chemin.ajoutCheminFin(versObjectif);
                            if (meilleurChemin.compareAndSet(null, chemin)) {
                                System.out.println("[SUCCESS] Solution trouvée à la couche " + currentCount);
                            }
                        }
                    });
                }
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (meilleurChemin.get() != null) {
            return meilleurChemin.get();
        }

        listEtatsATraiter = newListEtat;
        count++;
        System.out.println("Couche BFS " + count + ", états à traiter : " + listEtatsATraiter.size());

        if (listEtatsATraiter.isEmpty()) {
            System.out.println("Plus d'états à traiter, fin de la recherche sans solution.");
            break;
        }
    }

    return null;
}


}