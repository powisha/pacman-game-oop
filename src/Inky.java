import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Inky extends Ghost {
    private Ghost blinky;
    private Direction currentDirection;
    private final int TILE_SIZE = 24;
    private final int OFFSET = 12;
    public boolean inkyScared;
    public long inkyScaredTime=0;


    private class Node {
        int x, y;
        Direction firstMove;
        Node(int x, int y, Direction firstMove) {
            this.x = x;
            this.y = y;
            this.firstMove = firstMove;
        }
    }

    public Inky(Pacman pacman, Ghost blinky, GamePanel panel) {
        this.pacman = pacman;
        this.blinky = blinky;
        inkyScared=false;

        gridX = 11;
        gridY = 14;
        currentX = gridX * TILE_SIZE + OFFSET;
        currentY = gridY * TILE_SIZE + OFFSET;
        currentDirection = Direction.UP;
        setSpeedGhost();
        setWait();

        loadImage();
    }


    public void setWait(){
        wait=15000;

    }

    private void loadImage() {
        try {
            scaryImage = ImageIO.read(getClass().getResourceAsStream("/pacman-art/ghosts/blue_ghost.png"));
            normalImage = ImageIO.read(getClass().getResourceAsStream("/pacman-art/ghosts/inky.png"));
            image = normalImage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void targetTile(Map map) {

            int px = pacman.gridX;
            int py = pacman.gridY;

            int p2x = px, p2y = py;
            Direction pd = pacman.getCurrentDirection();

            if (pd == Direction.UP) {
                p2y = py - 2;
            } else if (pd == Direction.DOWN) {
                p2y = py + 2;
            } else if (pd == Direction.LEFT) {
                p2x = px - 2;
            } else if (pd == Direction.RIGHT) {
                p2x = px + 2;
            } else {
                p2x = px;
                p2y = py;
            }

            int bx = blinky.gridX;
            int by = blinky.gridY;

            // target = 2P - B
            targetX = 2 * p2x - bx;
            targetY = 2 * p2y - by;


    }

    public void targetScary(Map map){

        targetX=1;
        targetY=30;
    }


    private void sanitizeTarget(Map map) {
        targetX = Math.max(0, Math.min(27, targetX));
        targetY = Math.max(0, Math.min(31, targetY));

        Point t = new Point(targetX, targetY);
        if (map.isWall(t)) {
            targetX = pacman.gridX;
            targetY = pacman.gridY;

            targetX = Math.max(0, Math.min(27, targetX));
            targetY = Math.max(0, Math.min(31, targetY));
        }
    }

    private boolean isAtTileCenter() {
        return ((int)(currentX - OFFSET) % TILE_SIZE == 0) && ((int)(currentY - OFFSET) % TILE_SIZE == 0);
    }

    private Direction findPath(Map map) {
        if (gridX == targetX && gridY == targetY) return currentDirection;

        Queue<Node> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        Point start = new Point(gridX, gridY);
        visited.add(start);

        addNeighbor(queue, visited, gridX, gridY - 1, Direction.UP, map);
        addNeighbor(queue, visited, gridX, gridY + 1, Direction.DOWN, map);
        addNeighbor(queue, visited, gridX - 1, gridY, Direction.LEFT, map);
        addNeighbor(queue, visited, gridX + 1, gridY, Direction.RIGHT, map);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();

            if (cur.x == targetX && cur.y == targetY) {
                return cur.firstMove; // BFS: ilk ulaştığın an shortest => ilk hamle budur
            }

            addNeighbor(queue, visited, cur.x, cur.y - 1, cur.firstMove, map);
            addNeighbor(queue, visited, cur.x, cur.y + 1, cur.firstMove, map);
            addNeighbor(queue, visited, cur.x - 1, cur.y, cur.firstMove, map);
            addNeighbor(queue, visited, cur.x + 1, cur.y, cur.firstMove, map);
        }

        return fallbackDirection(map);
    }

    private void addNeighbor(Queue<Node> queue, Set<Point> visited,
                             int x, int y, Direction firstMove, Map map) {
        if (x < 0 || x >= 28 || y < 0 || y >= 32) return;

        Point p = new Point(x, y);

        if (!map.isWall(p) && !visited.contains(p)) {
            visited.add(p);
            queue.add(new Node(x, y, firstMove));
        }
    }

    private Direction fallbackDirection(Map map) {
        Direction[] dirs = {Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT};

        for (Direction d : dirs) {
            int nx = gridX + (d == Direction.LEFT ? -1 : d == Direction.RIGHT ? 1 : 0);
            int ny = gridY + (d == Direction.UP ? -1 : d == Direction.DOWN ? 1 : 0);

            if (nx < 0 || nx >= 28 || ny < 0 || ny >= 32) continue;

            if (!map.isWall(new Point(nx, ny))) {
                return d;
            }
        }
        return currentDirection;
    }

    private void move(Direction dir, Map map) {
        float nextX = currentX;
        float nextY = currentY;

        if (dir == Direction.UP) nextY -= speedGhost;
        else if (dir == Direction.DOWN) nextY += speedGhost;
        else if (dir == Direction.LEFT) nextX -= speedGhost;
        else if (dir == Direction.RIGHT) nextX += speedGhost;

        int newGridX = (int)Math.floor((nextX - OFFSET + TILE_SIZE / 2f) / TILE_SIZE);
        int newGridY = (int)Math.floor((nextY - OFFSET + TILE_SIZE / 2f) / TILE_SIZE);

        if (newGridX < 0 || newGridX >= 28 || newGridY < 0 || newGridY >= 32) return;

        Point newPos = new Point(newGridX, newGridY);
        if (!map.isWall(newPos)) {
            currentX = nextX;
            currentY = nextY;
            gridX = newGridX;
            gridY = newGridY;
            currentDirection = dir;
        }
    }

    public void update(Map map, GamePanel panel) {
        if(panel.pelletTime > 0 && System.currentTimeMillis() - panel.pelletTime < panel.scaryMode){
            targetScary(map);
            image = scaryImage;
        }
        else{
            targetTile(map);
            image = normalImage;
        }

        sanitizeTarget(map);

        currentDirection = findPath(map);

        move(currentDirection, map);
    }
    public void draw(Graphics2D pen) {
        if (image != null) {
            pen.drawImage(image, (int) currentX - OFFSET, (int) currentY - OFFSET, TILE_SIZE, TILE_SIZE, null);
        } else {
            pen.setColor(Color.CYAN);
            pen.fillOval((int) currentX - 11, (int) currentY - 11, 22, 22);
        }


    }
}
