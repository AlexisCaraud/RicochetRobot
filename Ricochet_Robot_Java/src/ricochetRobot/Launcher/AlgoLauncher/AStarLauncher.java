package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.AStar.Astar;
import ricochetRobot.Algo.AStar.Heuristique.Heuristique;
import ricochetRobot.Algo.AStar.Heuristique.HeuristiqueManhattan;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class AStarLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        Heuristique heuristiqueManhattan = new HeuristiqueManhattan();
        Astar algo = new Astar(plateau, heuristiqueManhattan);
        return algo.aStarSearch();
    }

    @Override
    protected String getDescription() {
        return "solution A* Manhattan";
    }

    public static void main(String[] args) {
        new AStarLauncher().run();
    }
}
