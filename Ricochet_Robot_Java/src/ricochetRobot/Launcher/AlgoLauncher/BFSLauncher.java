package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.BFS.BFS;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class BFSLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        BFS algoBFS = new BFS(plateau);
        return algoBFS.solveOpt();
    }

    @Override
    protected String getDescription() {
        return "solution BFS";
    }

    public static void main(String[] args) {
        new BFSLauncher().run();
    }
}
