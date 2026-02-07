import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Blinky extends Ghost {

    private Pacman pacman;
    private Direction currentDirection;
    private final int TILE_SIZE = 24;
    public boolean blinkyScared;
    public long blinkyScaredTime=0;

    private class Node {
        int x, y;
        Direction firstMove;

        Node(int x, int y, Direction firstMove){
            this.x = x;
            this.y = y;
            this.firstMove = firstMove;
        }
    }

    public Blinky(Pacman pacman, GamePanel panel){
        this.pacman = pacman;

        gridX = 13;
        gridY = 13;
        currentX = gridX * TILE_SIZE + 12;
        currentY = gridY * TILE_SIZE + 12;
        blinkyScared=false;

        setSpeedGhost();
        setWait();
        currentDirection = Direction.UP;
        loadImage(panel);
    }

    private void loadImage(GamePanel panel){
        try{
            scaryImage = ImageIO.read(getClass().getResourceAsStream("/pacman-art/ghosts/blue_ghost.png"));
            normalImage = ImageIO.read(getClass().getResourceAsStream("/pacman-art/ghosts/blinky.png"));
        } catch(Exception e){
            System.out.println("Blinky image yüklenemedi!");
        }
    }

    @Override
    public void targetTile(Map map){
        targetX = pacman.gridX;
        targetY = pacman.gridY;
    }

    public void targetScary(Map map){
        targetX = 26;
        targetY = 2;
    }

    private Direction findPath(Map map){
        Queue<Node> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        Point start = new Point(gridX, gridY);
        visited.add(start);

        addNeighbor(queue, visited, gridX, gridY - 1, Direction.UP, map);
        addNeighbor(queue, visited, gridX, gridY + 1, Direction.DOWN, map);
        addNeighbor(queue, visited, gridX - 1, gridY, Direction.LEFT, map);
        addNeighbor(queue, visited, gridX + 1, gridY, Direction.RIGHT, map);

        while(!queue.isEmpty()){
            Node current = queue.poll();

            if(current.x == targetX && current.y == targetY){
                return current.firstMove;
            }

            addNeighbor(queue, visited, current.x, current.y - 1, current.firstMove, map);
            addNeighbor(queue, visited, current.x, current.y + 1, current.firstMove, map);
            addNeighbor(queue, visited, current.x - 1, current.y, current.firstMove, map);
            addNeighbor(queue, visited, current.x + 1, current.y, current.firstMove, map);
        }

        return currentDirection;
    }

    private void addNeighbor(Queue<Node> queue, Set<Point> visited,
                             int x, int y, Direction firstMove, Map map){
        if(x < 0 || x >= 28 || y < 0 || y >= 32) return;

        Point p = new Point(x, y);

        if(!map.isWall(p) && !visited.contains(p)){
            queue.add(new Node(x, y, firstMove));
            visited.add(p);
        }
    }

    private void move(Direction dir, Map map){
        float nextX = currentX;
        float nextY = currentY;

        if(dir == Direction.UP) nextY = currentY - speedGhost;
        else if(dir == Direction.DOWN) nextY = currentY + speedGhost;
        else if(dir == Direction.LEFT) nextX = currentX - speedGhost;
        else if(dir == Direction.RIGHT) nextX = currentX + speedGhost;

        int newGridX = Math.round((nextX - 12) / TILE_SIZE);
        int newGridY = Math.round((nextY - 12) / TILE_SIZE);

        if(newGridX < 0 || newGridX >= 28 || newGridY < 0 || newGridY >= 32) return;

        Point newPos = new Point(newGridX, newGridY);
        if(!map.isWall(newPos)){
            currentX = nextX;
            currentY = nextY;
            gridX = newGridX;
            gridY = newGridY;
            currentDirection = dir;
        }
    }

    public void update(Map map, GamePanel panel){

        if (System.currentTimeMillis() - panel.pelletTime < panel.scaryMode) {
            targetScary(map);
            image = scaryImage;
        } else {
            targetTile(map);
            image = normalImage;
        }

        Direction dir = findPath(map);
        move(dir, map);
    }

    public void draw(Graphics2D pen){
        if(image != null){
            pen.drawImage(image, (int)currentX - 12, (int)currentY - 12, TILE_SIZE, TILE_SIZE, null);
        } else {
            pen.setColor(Color.PINK);
            pen.fillOval((int)currentX - 11, (int)currentY - 11, 22, 22);
        }

    }

    public void setWait(){
        wait = 0;
    }
}
