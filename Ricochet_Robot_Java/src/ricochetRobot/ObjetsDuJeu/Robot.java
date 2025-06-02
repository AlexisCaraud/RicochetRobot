package ricochetRobot.ObjetsDuJeu;

public class Robot implements Cloneable {
    String couleur; // "ROUGE", "BLEU", "VERT", "VIOLET"
    int posX;
    int posY;

    public Robot(String couleur, int posX, int posY) {
        this.couleur = couleur;
        this.posX = posX;
        this.posY = posY;
    }

    public String getCouleur() {
        return couleur;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    @Override
    public String toString() {
        return String.format("Robot[couleur=%s, posX=%d, posY=%d]", couleur, posX, posY);
    }


    @Override
    public Robot clone() {
        return new Robot(this.couleur, this.posX, this.posY);
    }
}
