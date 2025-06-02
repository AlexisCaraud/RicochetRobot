package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.BFS.BFSQueue;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class BFSQueueLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        BFSQueue algoBFS = new BFSQueue(plateau);
        return algoBFS.solveOpt();
    }

    @Override
    protected String getDescription() {
        return "solution BFSQueue";
    }

    public static void main(String[] args) {
        new BFSLauncher().run();
    }
}
