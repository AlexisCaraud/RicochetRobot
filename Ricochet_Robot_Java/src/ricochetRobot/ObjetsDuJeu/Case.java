package ricochetRobot.ObjetsDuJeu;
public class Case implements Cloneable {
    boolean murHaut;
    boolean murBas;
    boolean murGauche;
    boolean murDroite;
    String objective;
    String robot; // "ROUGE", "BLEU", "VERT", "VIOLET" ou " " pour aucune couleur

    public Case(boolean murHaut, boolean murBas, boolean murGauche, boolean murDroite) {
        this.murHaut = murHaut;
        this.murBas = murBas;
        this.murGauche = murGauche;
        this.murDroite = murDroite;
        this.robot = " ";
        this.objective = " ";
    }

    public String getObjective() {
        return objective;
    }

    public String getRobot() {
        return robot;
    }

    public boolean isMurBas() {
        return murBas;
    }

    public boolean isMurDroite() {
        return murDroite;
    }

    public boolean isMurGauche() {
        return murGauche;
    }

    public boolean isMurHaut() {
        return murHaut;
    }

    public void setRobot(String robot) {
        this.robot = robot;
    }

    public void setMurBas(boolean murBas) {
        this.murBas = murBas;
    }

    public void setMurDroite(boolean murDroite) {
        this.murDroite = murDroite;
    }

    public void setMurGauche(boolean murGauche) {
        this.murGauche = murGauche;
    }

    public void setMurHaut(boolean murHaut) {
        this.murHaut = murHaut;
    }


    public boolean getMur(String direction) {
        switch (direction) {
            case "DROITE":
                return murDroite;
            case "GAUCHE":
                return murGauche;
            case "BAS":
                return murBas;
            case "HAUT":
                return murHaut;
            default:
                throw new IllegalArgumentException("Direction invalide : " + direction);
        }
    }
    

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();

        // Ajouter les murs si présents
        if (murHaut) {
            string.append("murHaut ");
        }
        if (murBas) {
            string.append("murBas ");
        }
        if (murGauche) {
            string.append("murGauche ");
        }
        if (murDroite) {
            string.append("murDroite ");
        }

        // Ajouter le robot s'il y en a un
        if (!robot.equals(" ")) {
            string.append("robot(").append(robot).append(") ");
        }

        // Ajouter l'objectif s'il y en a un
        if (!objective.equals(" ")) {
            string.append("objective(").append(objective).append(") ");
        }

        // Si la case est vide
        if (string.length() == 0) {
            string.append("vide");
        }

        return string.toString().trim(); // Supprime l'espace final si présent
    }

    @Override
    public Case clone() {
        try {
            return (Case) super.clone(); // suffisant ici car tous les champs sont immuables ou primitifs
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // ne devrait jamais arriver
        }
    }

}

