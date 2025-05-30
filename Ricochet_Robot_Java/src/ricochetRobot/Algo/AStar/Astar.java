package ricochetRobot.Algo.AStar;
import java.util.*;

import ricochetRobot.Algo.AStar.Heuristique.Heuristique;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;
import ricochetRobot.Etats.EtatPlateau;
import ricochetRobot.ObjetsDuJeu.Objective;
import ricochetRobot.ObjetsDuJeu.Plateau;
public class Astar {

    private Plateau plateau;
    private String robot;
    private EtatPlateau start; // Etat de départ
    private Objective goal; // Objectif à atteindre
    private Heuristique heuristicFunction; // Métaheuristique avec trois paramètres

    public Astar(Plateau plateau, Heuristique heuristicFunction) {
        this.plateau = plateau;
        this.robot = plateau.getObjective().getCouleur();
        this.start = new EtatPlateau(plateau);
        this.goal = plateau.getObjective();
        this.heuristicFunction = heuristicFunction; // Métaheuristique passée en paramètre
    }

    // Fonction A* pour trouver le meilleur chemin
    public Chemin aStarSearch() {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.getfCost()));
        Set<EtatPlateau> closedList = new HashSet<>();
        Map<EtatPlateau, Integer> gScores = new HashMap<>();

        // Initialiser le point de départ
        Node startNode = new Node(start, null, 0, heuristicFunction.calculer(start, goal, plateau));
        openList.add(startNode);
        gScores.put(start, 0);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            EtatPlateau currentPosition = currentNode.getPosition();

            // Si on atteint l'objectif, reconstruire le chemin
            if (currentPosition.getRobots().get(robot).getX() == goal.getPosX() &&
                currentPosition.getRobots().get(robot).getY() == goal.getPosY()) {
                return reconstruireChemin(currentNode);
            }

            closedList.add(currentPosition);
            plateau.replacerRobot(currentPosition.getRobots());

            // Explorer les voisins
            for (EtatPlateau voisin : currentPosition.getVoisins()) {
                if (closedList.contains(voisin)) {
                    continue;
                }

                int tentativeGScore = currentNode.getgCost() + 1; // Chaque déplacement a un coût de 1

                if (!gScores.containsKey(voisin) || tentativeGScore < gScores.get(voisin)) {
                    Node voisinNode = new Node(
                        voisin,
                        currentNode,
                        tentativeGScore,
                        heuristicFunction.calculer(voisin, goal, plateau)
                    );
                    openList.add(voisinNode);
                    gScores.put(voisin, tentativeGScore);
                }
            }
        }

        // Aucun chemin trouvé
        throw new RuntimeException("Aucun chemin trouvé !");
    }

    // Reconstruire le chemin depuis le noeud final
    private Chemin reconstruireChemin(Node goalNode) {
        Deque<Deplacement> deps = new ArrayDeque<>();
        Node currentNode = goalNode;
        while (currentNode.getParent() != null) {
            deps.addFirst(currentNode.getParent().getPosition().depEntreDeuxEtat(currentNode.getPosition()));
            currentNode = currentNode.getParent();
        }
        return new Chemin(deps);
    }
}
