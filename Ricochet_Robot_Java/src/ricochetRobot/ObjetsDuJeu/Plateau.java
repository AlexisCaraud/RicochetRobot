package ricochetRobot.ObjetsDuJeu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ricochetRobot.Constants;
import ricochetRobot.Deplacements.Chemin;
import ricochetRobot.Deplacements.Deplacement;

public class Plateau implements Cloneable {
    Case[][] plateau;
    int taille;
    int central;
    ArrayList<Robot> robots = new ArrayList<>();
    Map<String, Robot> robotsMap = new HashMap<>();
    Objective objective;
    Map<String, Coordonnees> coordInit = new HashMap<>();
    List<List<Integer>> murs_verticaux = new ArrayList<>();
    List<List<Integer>> murs_horizontaux = new ArrayList<>();
    CaseDep[][] tableauDeplacement;


    
    public Plateau() {
    }

    public Plateau(int taille) {
        this.taille = taille;
    
        // Initialisation des cases
        initialiseCase(taille);
    
        // Ajouter les murs extérieurs
        ajoutBordure();

        // Ajouter le carré central 2x2
        int centre = taille / 2;
        plateau[centre - 1][centre - 2].setMurDroite(true);
        plateau[centre][centre - 2].setMurDroite(true);
        plateau[centre - 1][centre+1].setMurGauche(true);
        plateau[centre][centre+1].setMurGauche(true);
        plateau[centre+1][centre - 1].setMurHaut(true);
        plateau[centre+1][centre].setMurHaut(true);
        plateau[centre-2][centre - 1].setMurBas(true);
        plateau[centre-2][centre].setMurBas(true);
    }

    public Plateau(int taille, int central) {
        this.taille = taille;
        this.central = central;
    
        // Initialisation des cases
        initialiseCase(taille);
    
        // Ajouter les murs extérieurs
        ajoutBordure();

        // Ajouter le carré central central * central
        ajoutBlockCentral();
    }

    public void initialiseCase(int size) {
        // Initialisation des cases
        setTaille(size);
        plateau = new Case[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                plateau[i][j] = new Case(false, false, false, false);
            }
        }
    }

    public void ajoutBordure() {
        for (int i = 0; i < taille; i++) {
            plateau[0][i].setMurHaut(true);
            plateau[taille - 1][i].setMurBas(true);
            plateau[i][0].setMurGauche(true);
            plateau[i][taille - 1].setMurDroite(true);
        }
    }

    public void ajoutBlockCentral() {
        int centre = taille / 2;
        int dcentre = central / 2;
        for (int i = centre - dcentre; i < centre + dcentre; i++) {
            plateau[i][centre + dcentre - 1].setMurDroite(true);
            plateau[i][centre - dcentre].setMurGauche(true);
        }
        for (int j = centre - dcentre; j < centre + dcentre; j++) {
            plateau[centre + dcentre - 1][j].setMurBas(true);
            plateau[centre - dcentre][j].setMurGauche(true);
        }
    }

    public Map<String, Coordonnees> getCoordInit() {
        return coordInit;
    }
    
    public Objective getObjective() {
        return objective;
    }

    public Case[][] getPlateau() {
        return plateau;
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public void setCentral(int central) {
        this.central = central;
    }

    public int getNbrRobot() {
        return robots.size();
    }

    public List<List<Integer>> getMurs_verticaux() {
        return murs_verticaux;
    }

    public List<List<Integer>> getMurs_horizontaux() {
        return murs_horizontaux;
    }

    public void add_coin_h_d(int x, int y) {    // Mur en haut et à droite
        addCoin(x, y, true, false, false, true);
    }
    
    public void add_coin_b_d(int x, int y) {    // Mur en bas et à droite
        addCoin(x, y, false, true, false, true);
    }
    
    public void add_coin_b_g(int x, int y) {    // Mur en bas et à gauche
        addCoin(x, y, false, true, true, false);
    }
    
    public void add_coin_h_g(int x, int y) {    // Mur en haut et à gauche
        addCoin(x, y, true, false, true, false);
    }

    public void addCoin(int x, int y, boolean haut, boolean bas, boolean gauche, boolean droite) {
        if (haut) {
            this.plateau[x-1][y].setMurBas(true); // Mur au-dessus de la case courante
            this.plateau[x][y].setMurHaut(true);
        }
        if (bas) {
            this.plateau[x+1][y].setMurHaut(true); // Mur en dessous de la case courante
            this.plateau[x][y].setMurBas(true);
        }
        if (gauche) {
            this.plateau[x][y-1].setMurDroite(true); // Mur à gauche de la case courante
            this.plateau[x][y].setMurGauche(true);
        }
        if (droite) {
            this.plateau[x][y+1].setMurGauche(true); // Mur à droite de la case courante
            this.plateau[x][y].setMurDroite(true);
        }
    }
    

    public void add_barrev(int x, int y){   // barre verticale à droite
        this.plateau[x][y].setMurDroite(true);
        this.plateau[x][y+1].setMurGauche(true);
    }

    public void add_barreh(int x, int y){   // barre horizontale en bas
        this.plateau[x][y].setMurBas(true);
        this.plateau[x+1][y].setMurHaut(true);
    }

    public void add_robot(Robot robot){
        this.plateau[robot.getPosX()][robot.getPosY()].robot = robot.getCouleur();
        this.robots.add(robot);
        this.robotsMap.put(robot.getCouleur(), robot);
    }

    public void add_objective(Objective objective){
        this.plateau[objective.getPosX()][objective.getPosY()].objective = objective.getCouleur();
        this.objective = objective;
    }

    public Robot getRobot(String color){
        return robotsMap.get(color);
    }

    /**
     * Methode qui sauvegarde les coordonnées initiales des robots
     */

    public void InitCoord(){
        for(Robot robot : this.robots){
            coordInit.put(robot.getCouleur(), new Coordonnees(robot.getPosX(), robot.getPosY()));
        }
    }

    /**
     * Methode qui crée les listes de murs verticaux et horizontaux
     */

    public void crée_list_murs() {
        for (int i = 0; i <= taille; i++) {
            murs_verticaux.add(new ArrayList<>());
            murs_horizontaux.add(new ArrayList<>());
        }
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                Case c = plateau[i][j];
                if (c.isMurGauche()) {
                    murs_verticaux.get(i).add(j);
                } 
                if (c.isMurHaut()) {
                    murs_horizontaux.get(j).add(i);
                }
            }
        }
        for (int i = 0; i <= taille; i++) {
            murs_verticaux.get(i).add(taille);
            murs_horizontaux.get(i).add(taille);
        }
    }


    /**
     * Methode qui renvoie les coordonnées actuelles des robots
     */

    public Map<String, Coordonnees> getCoordRobot(){
        Map<String, Coordonnees> coordRobot = new HashMap<>();
        for(Robot robot : this.robots){
            coordRobot.put(robot.getCouleur(), new Coordonnees(robot.getPosX(), robot.getPosY()));
        }
        return coordRobot;
    }

    /**
     * Methode qui replace les robots du plateau aux coordonnées 'coordRobot'
     */

    public void replacerRobot(Map<String, Coordonnees> coordRobot){
        for(Robot robot : this.robots){
            change_pos_robot(robot.getCouleur(), coordRobot.get(robot.getCouleur()).getX(), coordRobot.get(robot.getCouleur()).getY());
        }
    }
    
    /**
     * Methode qui déplace un robot dans une direction
     * @param color_robot couleur du robot à déplacer
     */

    public void deplacer_robot(String color_robot, String direction) {
        Robot robot = getRobot(color_robot);

        // Supprimer le robot de sa position actuelle sur le plateau
        plateau[robot.getPosX()][robot.getPosY()].robot = " ";

        switch (direction) {
            case "HAUT":
                while (robot.getPosX() > 0 && !plateau[robot.getPosX()][robot.getPosY()].isMurHaut() 
                       && plateau[robot.getPosX() - 1][robot.getPosY()].robot.equals(" ")) {
                        robot.setPosX(robot.getPosX() - 1);
                }
                break;
            case "BAS":
                while (robot.getPosX() < taille - 1 && !plateau[robot.getPosX()][robot.getPosY()].isMurBas() 
                       && plateau[robot.getPosX() + 1][robot.getPosY()].robot.equals(" ")) {
                        robot.setPosX(robot.getPosX() + 1);
                }
                break;
            case "GAUCHE":
                while (robot.getPosY() > 0 && !plateau[robot.getPosX()][robot.getPosY()].isMurGauche() 
                       && plateau[robot.getPosX()][robot.getPosY() - 1].robot.equals(" ")) {
                        robot.setPosY(robot.getPosY() - 1);
                }
                break;
            case "DROITE":
                while (robot.getPosY() < taille - 1 && !plateau[robot.getPosX()][robot.getPosY()].isMurDroite() 
                       && plateau[robot.getPosX()][robot.getPosY() + 1].robot.equals(" ")) {
                        robot.setPosY(robot.getPosY() + 1);
                }
                break;
            default:
                System.err.println("Direction deplacement invalide : " + direction);
                return;
        }

        // Mettre à jour la nouvelle position du robot sur le plateau
        plateau[robot.getPosX()][robot.getPosY()].setRobot(robot.getCouleur());
    }

    public boolean deplacer_robot_bool(String color_robot, String direction) {
        Robot robot = getRobot(color_robot);

        // Supprimer le robot de sa position actuelle sur le plateau
        int xInit = robot.getPosX();
        int yInit = robot.getPosY();
        plateau[robot.getPosX()][robot.getPosY()].robot = " ";

        switch (direction) {
            case "HAUT":
                while (robot.getPosX() > 0 && !plateau[robot.getPosX()][robot.getPosY()].isMurHaut() 
                       && plateau[robot.getPosX() - 1][robot.getPosY()].robot.equals(" ")) {
                        robot.setPosX(robot.getPosX() - 1);
                }
                break;
            case "BAS":
                while (robot.getPosX() < taille - 1 && !plateau[robot.getPosX()][robot.getPosY()].isMurBas() 
                       && plateau[robot.getPosX() + 1][robot.getPosY()].robot.equals(" ")) {
                        robot.setPosX(robot.getPosX() + 1);
                }
                break;
            case "GAUCHE":
                while (robot.getPosY() > 0 && !plateau[robot.getPosX()][robot.getPosY()].isMurGauche() 
                       && plateau[robot.getPosX()][robot.getPosY() - 1].robot.equals(" ")) {
                        robot.setPosY(robot.getPosY() - 1);
                }
                break;
            case "DROITE":
                while (robot.getPosY() < taille - 1 && !plateau[robot.getPosX()][robot.getPosY()].isMurDroite() 
                       && plateau[robot.getPosX()][robot.getPosY() + 1].robot.equals(" ")) {
                        robot.setPosY(robot.getPosY() + 1);
                }
                break;
            default:
                System.err.println("Direction deplacement invalide : " + direction);
        }

        // Mettre à jour la nouvelle position du robot sur le plateau
        plateau[robot.getPosX()][robot.getPosY()].setRobot(robot.getCouleur());
        return (xInit != robot.getPosX() || yInit != robot.getPosY());
    }

    public void change_pos_robot(String colorRobot, int xFinal, int yFinal){
        int xInit = getRobot(colorRobot).getPosX();
        int yInit = getRobot(colorRobot).getPosY();
        plateau[xInit][yInit].setRobot(" "); // on enlève le robot de sa position initiale
        plateau[xFinal][yFinal].setRobot(colorRobot);  // on ajoute le robot de à position finale
        Robot rob = robotsMap.get(colorRobot);
        rob.setPosX(xFinal);
        rob.setPosY(yFinal);
    }

    /**
     * Methode qui renvoie la distance entre une case(x,y) et le premier obstacle rencontré dans la direction 'direction'
     */

    public int distanceObstacle(int x, int y, String direction) {
        int i = 0;
        switch (direction) {
            case "DROITE":
                while (!plateau[x][y + i].isMurDroite() && plateau[x][y + i + 1].getRobot().equals(" ")) {
                    i++;
                }
                break;
            case "GAUCHE":
                while (!plateau[x][y - i].isMurGauche() && plateau[x][y - i - 1].getRobot().equals(" ")) {
                    i++;
                }
                break;
            case "HAUT":
                while (!plateau[x - i][y].isMurHaut() && plateau[x - i - 1][y].getRobot().equals(" ")) {
                    i++;
                }
                break;
            case "BAS":
                while (!plateau[x + i][y].isMurBas() && plateau[x + i + 1][y].getRobot().equals(" ")) {
                    i++;
                }
                break;
            default:
                throw new IllegalArgumentException("Direction obstacle invalide : " + direction);
        }
        return i;
    }
    
    /**
     * Methode qui renvoie un tableau de chemin entre une case et la case(x,y)
     * @param minimum si minimum: on renvoie les meilleurs minimaux (on suppose que le robot peut s'arrêter même si pas d'obstacle devant lui)
     */

    public Chemin[][] tableauDistanceBFS(int x, int y, String couleur, boolean minimum) {
        
        Robot robot = getRobot(couleur);
        int xRobot = robot.getPosX();
        int yRobot = robot.getPosY();
        plateau[xRobot][yRobot].setRobot(" ");; // Enlève le robot temporairement

        if (minimum){
            for (Robot rob : robots){
                plateau[rob.getPosX()][rob.getPosY()].setRobot(" ");; // Enlève les robots temporairement
                }
        }

        int rows = plateau.length;
        int cols = plateau[0].length;

        // Initialisation de la matrice des chemins
        Chemin[][] chemins = new Chemin[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                chemins[i][j] = new Chemin(); // Par défaut, chemins non définis
            }
        }

        // Initialisation de la file pour BFS
        Queue<Coordonnees> queue = new LinkedList<>();
        Coordonnees start = new Coordonnees(x, y);
        queue.add(start);
        chemins[x][y] = new Chemin(); // Case cible : longueur 0
        chemins[x][y].setLongueur(0);

        // Déplacements possibles (haut, bas, gauche, droite)
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        String[] directions = {"HAUT", "BAS", "GAUCHE", "DROITE"};
        String[] directionsOpposees = {"BAS", "HAUT", "DROITE", "GAUCHE"};

        // BFS
        while (!queue.isEmpty()) {
            Coordonnees current = queue.poll();
            int currentX = current.getX();
            int currentY = current.getY();
            Chemin currentChemin = chemins[currentX][currentY];

            for (int i = 0; i < 4; i++) {

                // Distance obstacle dans la direction directions[i]
                int dist = distanceObstacle(currentX, currentY, directionsOpposees[i]);
                if (minimum ||this.plateau[currentX][currentY].getMur(directions[i]) || 
                    !this.plateau[currentX + dx[i]][currentY + dy[i]].robot.equals(" ")) {
                    for (int t = 1; t <= dist; t++) {
                        int newX = currentX - t * dx[i];
                        int newY = currentY - t * dy[i];
                        Chemin neighborChemin = chemins[newX][newY];

                        // Construire un nouveau chemin vers le voisin
                        Chemin newChemin = currentChemin.copy();
                        newChemin.addDepDebut(new Deplacement(couleur, directions[i]));

                        // Mettre à jour le chemin si une meilleure solution est trouvée
                        if (neighborChemin.getLongueur() > newChemin.getLongueur()) {
                            chemins[newX][newY] = newChemin;
                            queue.add(new Coordonnees(newX, newY));
                            }
                        }
                    }
                }
            }
            plateau[xRobot][yRobot].setRobot(couleur);;  // Remet le robot à sa place d'origine

            if (minimum){
                for (Robot rob : robots){
                    plateau[rob.getPosX()][rob.getPosY()].robot = rob.getCouleur(); // Remet les robots
                    }
                }

            return chemins;
        }

        public Chemin[][] tableauDistanceBFS(int x, int y, String couleur) {
            return tableauDistanceBFS(x, y, couleur, false);
        }

    /**
     * Methode qui renvoie le plus court chemin entre la case (xDepart, yDepart) et (xArrivee, yArrivee)
     * Renvoie un chemin de longueur infini si aucun n'existe
     * @param minimum si minimum: on renvoie le chemin minimal (on suppose que le robot peut s'arrêter même si pas d'obstacle devant lui)
     */

        public Chemin cheminBFS(int xDepart, int yDepart, int xArrivee, int yArrivee, String couleur, boolean minimum) {
            Robot robot = getRobot(couleur);
            int xRobot = robot.getPosX();
            int yRobot = robot.getPosY();
            plateau[xRobot][yRobot].setRobot(" ");; // Enlève le robot temporairement
    
            if (minimum){
                for (Robot rob : robots){
                    plateau[rob.getPosX()][rob.getPosY()].setRobot(" ");; // Enlève les robots temporairement
                    }
            } else if (!plateau[xArrivee][yArrivee].getRobot().equals(" ")) {   // si la case objectif n'est pas vide
                return new Chemin();
            }
    
            int rows = plateau.length;
            int cols = plateau[0].length;
    
            // Initialisation de la matrice des chemins
            Chemin[][] chemins = new Chemin[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    chemins[i][j] = new Chemin(); // Par défaut, chemins non définis
                }
            }
    
            // Initialisation de la file pour BFS
            Queue<Coordonnees> queue = new LinkedList<>();
            Coordonnees start = new Coordonnees(xArrivee, yArrivee);
            queue.add(start);
            chemins[xArrivee][yArrivee] = new Chemin(); // Case cible : longueur 0
            chemins[xArrivee][yArrivee].setLongueur(0);
            if (xArrivee==xDepart && yArrivee==yDepart){
                return chemins[xArrivee][yArrivee];
            }
    
            // Déplacements possibles (haut, bas, gauche, droite)
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};
            String[] directions = {"HAUT", "BAS", "GAUCHE", "DROITE"};
            String[] directionsOpposees = {"BAS", "HAUT", "DROITE", "GAUCHE"};
    
            // BFS
            while (!queue.isEmpty()) {
                Coordonnees current = queue.poll();
                int currentX = current.getX();
                int currentY = current.getY();
                Chemin currentChemin = chemins[currentX][currentY];
    
                for (int i = 0; i < 4; i++) {
    
                    // Distance obstacle dans la direction directions[i]
                    int dist = distanceObstacle(currentX, currentY, directionsOpposees[i]);
                    if (minimum ||this.plateau[currentX][currentY].getMur(directions[i]) || 
                        !this.plateau[currentX + dx[i]][currentY + dy[i]].robot.equals(" ")) {
                        for (int t = 1; t <= dist; t++) {
                            int newX = currentX - t * dx[i];
                            int newY = currentY - t * dy[i];
                            Chemin neighborChemin = chemins[newX][newY];
    
                            // Construire un nouveau chemin vers le voisin
                            Chemin newChemin = currentChemin.copy();
                            newChemin.addDepDebut(new Deplacement(couleur, directions[i]));

                            // Si on est remonté jusqu'au point de départ:
                            if (newX == xDepart && newY == yDepart){
                                plateau[xRobot][yRobot].setRobot(couleur);;  // Remet le robot à sa place d'origine
                                if (minimum){
                                    for (Robot rob : robots){
                                        plateau[rob.getPosX()][rob.getPosY()].robot = rob.getCouleur(); // Remet les robots
                                    }
                                }
                                return newChemin;
                            }
    
                            // Mettre à jour le chemin si une meilleure solution est trouvée
                            if (neighborChemin.getLongueur() > newChemin.getLongueur()) {
                                chemins[newX][newY] = newChemin;
                                queue.add(new Coordonnees(newX, newY));
                            }
                        }
                    }
                }
            }
            //plateau[xRobot][yRobot].setRobot(couleur);;  // Remet le robot à sa place d'origine

            if (minimum){
                for (Robot rob : robots){
                    plateau[rob.getPosX()][rob.getPosY()].robot = rob.getCouleur(); // Remet les robots
                }
            }

            return new Chemin();
        }

        public Chemin cheminBFS(int xDepart, int yDepart, int xArrivee, int yArrivee, String couleur) {
            return cheminBFS(xDepart, yDepart, xArrivee, yArrivee, couleur, false);
        }

        public int indiceEncadrant(List<Integer> t, int a) {
            int gauche = 0;
            int droite = t.size() - 2; // car on cherche i tel que t[i] <= a < t[i+1]
        
            while (gauche <= droite) {
                int milieu = (gauche + droite) / 2;
                int val = t.get(milieu);
                int valSuivant = t.get(milieu + 1);
        
                if (val <= a && a < valSuivant) {
                    return milieu;
                } else if (a < val) {
                    droite = milieu - 1;
                } else {
                    gauche = milieu + 1;
                }
            }
        
            // Si on est ici, les préconditions sont violées
            throw new IllegalArgumentException("a ne vérifie pas : t[0] <= a < t[t.size()-1]");
        }
    

        public void deplacer_robot_dicho(String color_robot, String direction) {
            Robot robot = getRobot(color_robot);

            // Supprimer le robot de sa position actuelle sur le plateau
            plateau[robot.getPosX()][robot.getPosY()].robot = " ";

            int posXInit = robot.getPosX();
            int posYInit = robot.getPosY();
            int indice;
            int newPosX = posXInit;
            int newPosY = posYInit;

            switch (direction) {
                case "HAUT":
                    indice = indiceEncadrant(murs_horizontaux.get(robot.getPosY()), robot.getPosX());
                    newPosX = murs_horizontaux.get(robot.getPosY()).get(indice);
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit > rob.getPosX()) {
                            newPosX = Math.max(newPosX, rob.getPosX() + 1);
                        }
                    }
                    break;
                case "BAS":
                    indice = indiceEncadrant(murs_horizontaux.get(robot.getPosY()), robot.getPosX());
                    newPosX = murs_horizontaux.get(robot.getPosY()).get(indice + 1) - 1;
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit < rob.getPosX()) {
                            newPosX = Math.min(newPosX, rob.getPosX() - 1);
                        }
                    }
                    break;
                case "GAUCHE":
                    indice = indiceEncadrant(murs_verticaux.get(robot.getPosX()), robot.getPosY());
                    newPosY = murs_verticaux.get(robot.getPosX()).get(indice);
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit > rob.getPosY()) {
                            newPosY = Math.max(newPosY, rob.getPosY() + 1);
                        }
                    }
                    break;
                case "DROITE":
                    indice = indiceEncadrant(murs_verticaux.get(robot.getPosX()), robot.getPosY());
                    newPosY = murs_verticaux.get(robot.getPosX()).get(indice + 1) - 1;
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit < rob.getPosY()) {
                            newPosY = Math.min(newPosY, rob.getPosY() - 1);
                        }
                    }
                    break;
                default:
                    System.err.println("Direction deplacement invalide : " + direction);
                    return;
            }

            robot.setPosX(newPosX);
            robot.setPosY(newPosY);

            // Mettre à jour la nouvelle position du robot sur le plateau
            plateau[robot.getPosX()][robot.getPosY()].setRobot(robot.getCouleur());
        }

        public boolean deplacer_robot_dicho_bool(String color_robot, String direction) {
            Robot robot = getRobot(color_robot);

            // Supprimer le robot de sa position actuelle sur le plateau
            plateau[robot.getPosX()][robot.getPosY()].robot = " ";

            int posXInit = robot.getPosX();
            int posYInit = robot.getPosY();
            int indice;
            int newPosX = posXInit;
            int newPosY = posYInit;

            switch (direction) {
                case "HAUT":
                    indice = indiceEncadrant(murs_horizontaux.get(robot.getPosY()), robot.getPosX());
                    newPosX = murs_horizontaux.get(robot.getPosY()).get(indice);
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit > rob.getPosX()) {
                            newPosX = Math.max(newPosX, rob.getPosX() + 1);
                        }
                    }
                    break;
                case "BAS":
                    indice = indiceEncadrant(murs_horizontaux.get(robot.getPosY()), robot.getPosX());
                    newPosX = murs_horizontaux.get(robot.getPosY()).get(indice + 1) - 1;
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit < rob.getPosX()) {
                            newPosX = Math.min(newPosX, rob.getPosX() - 1);
                        }
                    }
                    break;
                case "GAUCHE":
                    indice = indiceEncadrant(murs_verticaux.get(robot.getPosX()), robot.getPosY());
                    newPosY = murs_verticaux.get(robot.getPosX()).get(indice);
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit > rob.getPosY()) {
                            newPosY = Math.max(newPosY, rob.getPosY() + 1);
                        }
                    }
                    break;
                case "DROITE":
                    indice = indiceEncadrant(murs_verticaux.get(robot.getPosX()), robot.getPosY());
                    newPosY = murs_verticaux.get(robot.getPosX()).get(indice + 1) - 1;
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit < rob.getPosY()) {
                            newPosY = Math.min(newPosY, rob.getPosY() - 1);
                        }
                    }
                    break;
                default:
                    System.err.println("Direction deplacement invalide : " + direction);
            }

            robot.setPosX(newPosX);
            robot.setPosY(newPosY);

            // Mettre à jour la nouvelle position du robot sur le plateau
            plateau[robot.getPosX()][robot.getPosY()].setRobot(robot.getCouleur());
            return (posXInit != robot.getPosX() || posYInit != robot.getPosY());
        }

        public Coordonnees deplacer_case(int x, int y, String direction) {
            int posXInit = x;
            int posYInit = y;
            int indice;
            int newPosX = posXInit;
            int newPosY = posYInit;

            switch (direction) {
                case "HAUT":
                    indice = indiceEncadrant(murs_horizontaux.get(y), x);
                    newPosX = murs_horizontaux.get(y).get(indice);
                    break;
                case "BAS":
                    indice = indiceEncadrant(murs_horizontaux.get(y), x);
                    newPosX = murs_horizontaux.get(y).get(indice + 1) - 1;
                    break;
                case "GAUCHE":
                    indice = indiceEncadrant(murs_verticaux.get(x), y);
                    newPosY = murs_verticaux.get(x).get(indice);
                    break;
                case "DROITE":
                    indice = indiceEncadrant(murs_verticaux.get(x), y);
                    newPosY = murs_verticaux.get(x).get(indice + 1) - 1;
                    break;
                default:
                    System.err.println("Direction deplacement invalide : " + direction);
            }
            return new Coordonnees(newPosX, newPosY);
        }

        public void calcule_deplacement() {
            CaseDep[][] resultat = new CaseDep[taille][taille];
            String[] DIRECTION = Constants.DIRECTION;
            for (int i=0; i<taille; i++) {
                for (int j=0; j<taille; j++) {
                    resultat[i][j] = new CaseDep();
                    for (String direction: DIRECTION) {
                        resultat[i][j].setDeplacement(direction, deplacer_case(i, j, direction));
                    }
                }
            }
            tableauDeplacement = resultat;
        }

        public boolean deplacementAvecRobot(String colorRobot, String direction) {
            Robot robot = getRobot(colorRobot);
            int posXInit = robot.getPosX();
            int posYInit = robot.getPosY();
            Coordonnees coord = tableauDeplacement[posXInit][posYInit].getDeplacement(direction);
            int newPosX = coord.getX();
            int newPosY = coord.getY();
            
            switch (direction) {
                case "HAUT":
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit > rob.getPosX()) {
                            newPosX = Math.max(newPosX, rob.getPosX() + 1);
                        }
                    }
                    break;
                case "BAS":
                    for(Robot rob : this.robots) {
                        if (posYInit == rob.getPosY() && posXInit < rob.getPosX()) {
                            newPosX = Math.min(newPosX, rob.getPosX() - 1);
                        }
                    }
                    break;
                case "GAUCHE":
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit > rob.getPosY()) {
                            newPosY = Math.max(newPosY, rob.getPosY() + 1);
                        }
                    }
                    break;
                case "DROITE":
                    for(Robot rob : this.robots) {
                        if (posXInit == rob.getPosX() && posYInit < rob.getPosY()) {
                            newPosY = Math.min(newPosY, rob.getPosY() - 1);
                        }
                    }
                    break;
                default:
                    System.err.println("Direction deplacement invalide : " + direction);
            }

            robot.setPosX(newPosX);
            robot.setPosY(newPosY);
            plateau[posXInit][posYInit].setRobot(" ");
            plateau[newPosX][newPosY].setRobot(robot.getCouleur());
            return (posXInit != robot.getPosX() || posYInit != robot.getPosY());
        }
        
        


    @Override
    public String toString() {
        return getCoordRobot().toString();
    }

    @Override
    public Plateau clone() {
        try {
            Plateau clone = (Plateau) super.clone();

            // Cloner plateau (Case[][])
            if (this.plateau != null) {
                clone.plateau = new Case[taille][taille];
                for (int i = 0; i < taille; i++) {
                    for (int j = 0; j < taille; j++) {
                        clone.plateau[i][j] = this.plateau[i][j].clone(); // Assure-toi que Case implémente Cloneable
                    }
                }
            }

            // Cloner robots
            clone.robots = new ArrayList<>();
            for (Robot r : this.robots) {
                clone.robots.add(r.clone());
            }

            // Cloner objective
            clone.objective = this.objective.clone();

            // Cloner coordInit
            clone.coordInit = new HashMap<>();
            for (Map.Entry<String, Coordonnees> entry : this.coordInit.entrySet()) {
                clone.coordInit.put(entry.getKey(), entry.getValue().clone());
            }

            // Cloner murs_verticaux / murs_horizontaux
            clone.murs_verticaux = deepCopyListOfLists(this.murs_verticaux);
            clone.murs_horizontaux = deepCopyListOfLists(this.murs_horizontaux);

            // Cloner tableauDeplacement si nécessaire
            if (this.tableauDeplacement != null) {
                clone.tableauDeplacement = new CaseDep[taille][taille];
                for (int i = 0; i < taille; i++) {
                    for (int j = 0; j < taille; j++) {
                        clone.tableauDeplacement[i][j] = this.tableauDeplacement[i][j].clone(); // si mutable
                    }
                }
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private List<List<Integer>> deepCopyListOfLists(List<List<Integer>> original) {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> inner : original) {
            copy.add(new ArrayList<>(inner));
        }
        return copy;
    }
}