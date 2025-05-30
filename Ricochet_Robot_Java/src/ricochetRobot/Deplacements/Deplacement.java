package ricochetRobot.Deplacements;
public class Deplacement {
    String robot;
    String direction;

    public Deplacement(String robot, String direction){
        this.direction = direction;
        this.robot = robot;

    }

    public String getDirection() {
        return direction;
    }

    public String getRobot() {
        return robot;
    }
}
