package ricochetRobot.Deplacements;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Chemin {

    int longueur;
    Deque<Deplacement> depList;

    /**
     * Constructeur d'un Chemin
     * longueur du chemin infini par défaut
     */

    public Chemin(){
        this.depList = new ArrayDeque<>();
        this.longueur = Integer.MAX_VALUE;
    }

    public Chemin(Deque<Deplacement> depList){
        this.depList = depList;
        this.longueur = this.depList.size();
    }

    public Chemin copy() {
        Chemin copie = new Chemin();
        copie.longueur = this.longueur; // Copier la longueur
        copie.depList = new ArrayDeque<>(this.depList); // Copier la liste de déplacements
        return copie;
    }


    public int getLongueur() {
        return longueur;
    }

    public void setLongueur(int longueur) {
        this.longueur = longueur;
    }

    public Deque<Deplacement> getDepList() {
        return depList;
    }

    public void setDepList(Deque<Deplacement> depList) {
        this.depList = depList;
    }

    
    public void addDepDebut(Deplacement dep){
        depList.addFirst(dep);
        if (longueur == Integer.MAX_VALUE){ // si le chemin était vide, on met sa longueur sur 1 en ajoutant le déplacement
            longueur = 1;
        }
        else{
            longueur += 1;
        }
    }

    public void addDepFin(Deplacement dep){
        depList.add(dep);
        if (longueur == Integer.MAX_VALUE) {// si le chemin était vide, on met sa longueur sur 1 en ajoutant le déplacement
            longueur = 1;
        }
        else{
            longueur += 1;
        }
    }

    /**
     * Méthode qui concatène 2 chemins
     */

    public void ajoutCheminFin(Chemin chemin){
        for (Deplacement dep: chemin.getDepList()){
            this.addDepFin(dep);
        }
    }

    /**
     * Méthode qui renvoie le nombre de déplacement secondaire dans le chemin
     * Les déplacements secondaires sont tous les déplacements avant qu'on ne commence à bouger que le robot principal
     */

    public int nbr_dep_secondaire(String couleur){
        Iterator<Deplacement> descendingIterator = depList.descendingIterator();
        int count = 0;
        while (descendingIterator.hasNext()) {
            if (!descendingIterator.next().robot.equals(couleur)){
                return longueur - count;
            }
            count ++;
        }
        return 0;
    }

    @Override
    public String toString() {
        String deps = "";
        for (Deplacement dep:getDepList()){
            deps += " " + dep.getRobot() + " " + dep.getDirection();
        }
        return "nbr_coups:" + longueur + " deplacements:" + deps;
    }

}
