package ricochetRobot.Launcher.AlgoLauncher;

import ricochetRobot.Algo.SolutionRapide;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Launcher.AbstractLauncher;
import ricochetRobot.ObjetsDuJeu.Plateau;

public class FastLauncher extends AbstractLauncher {

    @Override
    protected Chemin runAlgorithm(Plateau plateau) {
        SolutionRapide algoRapide = new SolutionRapide(plateau);
        return algoRapide.sol_fast(100);
    }

    @Override
    protected String getDescription() {
        return "solution rapide";
    }

    public static void main(String[] args) {
        new FastLauncher().run();
    }
}
