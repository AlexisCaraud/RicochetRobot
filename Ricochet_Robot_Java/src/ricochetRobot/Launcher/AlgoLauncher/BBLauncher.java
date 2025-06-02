package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.BranchAndBound.BranchAndBound;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class BBLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        BranchAndBound algoBB = new BranchAndBound(plateau);
        return algoBB.BranchAndBoundAlgo(100);
    }

    @Override
    protected String getDescription() {
        return "solution BB";
    }

    public static void main(String[] args) {
        new BFSLauncher().run();
    }
}
