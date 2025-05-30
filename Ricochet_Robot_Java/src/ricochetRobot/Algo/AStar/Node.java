package ricochetRobot.Algo.AStar;

import ricochetRobot.Etats.EtatPlateau;

public class Node {
    EtatPlateau position; // position du noeud dans le graphe des etats
    Node parent; // Parent du noeud dans l'arbre de recherche (pour reconstruire le chemin)
    int gCost; // Coût pour atteindre ce noeud depuis le départ (g(n))
    int hCost; // Heuristique estimée pour atteindre l'objectif (h(n))
    int fCost; // Coût total (f(n) = g(n) + h(n))

    // Constructeur
    public Node(EtatPlateau position, Node parent, int gCost, int hCost) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    public Node getParent() {
        return parent;
    }

    public EtatPlateau getPosition() {
        return position;
    }

    public int getgCost() {
        return gCost;
    }

    public int getfCost() {
        return fCost;
    }
}