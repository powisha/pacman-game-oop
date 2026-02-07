import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Ghost {
    protected Pacman pacman;
    protected Map map;
    BufferedImage image;
    BufferedImage scaryImage;
    BufferedImage normalImage;

    protected int targetX, targetY;
    int gridX, gridY;
    float speedGhost;
    float currentX, currentY;
    float nextX, nextY;
    final int SIZE = 22;
    public long wait;
    public long scaryModeCounter=5000;


    private class Node {
        int x, y;
        Direction firstMove;

        Node(int x, int y, Direction firstMove) {
            this.x = x;
            this.y = y;
            this.firstMove = firstMove;
        }
    }


    public abstract void targetTile(Map map);

    public void setSpeedGhost() {
        speedGhost = 1.5f;
    }

    public abstract void setWait();

}






