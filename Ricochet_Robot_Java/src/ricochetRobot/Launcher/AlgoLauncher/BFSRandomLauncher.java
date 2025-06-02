package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.BFS.BFSRandom;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class BFSRandomLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        BFSRandom algoBFSRand = new BFSRandom(plateau, 0.5);
        return algoBFSRand.solve();
    }

    @Override
    protected String getDescription() {
        return "solution BFS Random";
    }

    public static void main(String[] args) {
        new BFSLauncher().run();
    }
}
