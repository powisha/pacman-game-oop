import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel implements KeyListener, ActionListener {
    Blinky blinky;
    Pinky pinky;
    Inky inky;
    Clyde clyde;
    Pacman pacman;
    Map map;
    Timer timer;
    BufferedImage dot;
    ArrayList<Point2D.Float> dotList = new ArrayList<>();
    boolean isGameOver;
    long lastHitTime = 0;
    final long coolDown = 2000;
    private long gameStartTime;
    int score;

    long scaryMode = 10000;
    long pelletTime;

    Set<Point> pelletList;

    GamePanel() {
        isGameOver = false;
        gameStartTime = System.currentTimeMillis();
        SoundEffect.play("pacman_beginning.wav");
        map = new Map();

        pelletTime = 0;
        pelletList = new HashSet<>();

        loadDotImage();
        loadDots();

        pacman = new Pacman();
        pinky = new Pinky(pacman, this);
        blinky = new Blinky(pacman, this);
        inky = new Inky(pacman, blinky, this);
        clyde = new Clyde(pacman, this);

        setPreferredSize(new Dimension(map.panelWidth, map.panelHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(16, this);
        timer.start();
    }

    private void loadDots() {
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 28; x++) {
                Point gridP = new Point(x, y);
                TileType type = map.getGridMap().get(gridP);

                if (type == TileType.ROAD || type == TileType.PACKMAN) {
                    dotList.add(new Point2D.Float((x * 24) + 12, (y * 24) + 12));
                }

                if (type == TileType.PELLET) {
                    pelletList.add(gridP);
                }
            }
        }
    }

    private void loadDotImage() {
        try {
            dot = ImageIO.read(getClass().getResourceAsStream("/pacman-art/other/dot.png"));
        } catch (Exception e) {
            System.out.println("Dot image yüklenemedi!");
        }
    }

    private void drawDots(Graphics2D pen) {
        for (Point2D.Float p : dotList) {
            pen.drawImage(dot, (int) p.x - 10, (int) p.y - 10, 20, 20, null);
        }

        for (Point p : pelletList) {
            int centerX = p.x * 24 + 12;
            int centerY = p.y * 24 + 12;
            pen.drawImage(dot, centerX - 25, centerY - 25, 50, 50, null);
        }
    }

    public void checkPelletEaten() {
        Point pacGrid = new Point(pacman.gridX, pacman.gridY);

        if (pelletList.remove(pacGrid)) {
            score+=50;
            pelletTime = System.currentTimeMillis();
            SoundEffect.playLoop("pacman_intermission.wav");
        }
    }

    private void removeDots() {
        float pacX = (pacman.gridX * 24) + 12;
        float pacY = (pacman.gridY * 24) + 12;


        for (int i = dotList.size() - 1; i >= 0; i--) {
            Point2D.Float d = dotList.get(i);
            if (d.x == pacX && d.y == pacY) {
                SoundEffect.play("eat_dot_0.wav");
                score+=20;
                dotList.remove(i);
            }
        }
    }

    public void scaryMode() {
        if (pelletTime > 0 && System.currentTimeMillis() - pelletTime < scaryMode) {

            if (pacman.gridX == blinky.gridX && pacman.gridY == blinky.gridY && !blinky.blinkyScared) {

                SoundEffect.play("pacman_eatghost.wav");
                blinky.gridX = 13;
                blinky.gridY = 13;
                blinky.currentX = 13 * 24 + 12;
                blinky.currentY = 13 * 24 + 12;
                blinky.blinkyScared = true;
                score+=200;
                blinky.blinkyScaredTime = System.currentTimeMillis();
            }

            if (pacman.gridX == inky.gridX && pacman.gridY == inky.gridY && !inky.inkyScared) {

                SoundEffect.play("pacman_eatghost.wav");
                inky.gridX = 11;
                inky.gridY = 14;
                inky.currentX = 11 * 24 + 12;
                inky.currentY = 14 * 24 + 12;
                inky.inkyScared = true;
                score+=200;
                inky.inkyScaredTime = System.currentTimeMillis();
            }

            if (pacman.gridX == pinky.gridX && pacman.gridY == pinky.gridY && !pinky.pinkyScared) {

                SoundEffect.play("pacman_eatghost.wav");
                pinky.gridX = 13;
                pinky.gridY = 14;
                pinky.currentX = 13 * 24 + 12;
                pinky.currentY = 14 * 24 + 12;
                pinky.pinkyScared = true;
                score+=200;
                pinky.pinkyScaredTime = System.currentTimeMillis();
            }

            if (pacman.gridX == clyde.gridX && pacman.gridY == clyde.gridY && !clyde.clydeScared) {

                SoundEffect.play("pacman_eatghost.wav");
                clyde.gridX = 15;
                clyde.gridY = 14;
                clyde.currentX = 15 * 24 + 12;
                clyde.currentY = 14 * 24 + 12;
                clyde.clydeScared = true;
                score+=200;
                clyde.clydeScaredTime = System.currentTimeMillis();
            }
        }
    }

    public void decreasePacman() {
        int currentHealth = pacman.getHealth();

        boolean isScaryMode = (pelletTime > 0 && System.currentTimeMillis() - pelletTime < scaryMode);

        if (!isScaryMode &&
                ((pacman.gridX == blinky.gridX && pacman.gridY == blinky.gridY) ||
                        (pacman.gridX == pinky.gridX && pacman.gridY == pinky.gridY) ||
                        (pacman.gridX == inky.gridX && pacman.gridY == inky.gridY) ||
                        (pacman.gridX == clyde.gridX && pacman.gridY == clyde.gridY))) {

            if (System.currentTimeMillis() - lastHitTime >= coolDown) {
                if (pacman.getHealth() >= 1) {
                    pacman.setHealth(currentHealth - 1);
                    SoundEffect.play("fright_firstloop.wav");

                    score -= 100;
                    lastHitTime = System.currentTimeMillis();
                }
            }

            if (pacman.getHealth() <= 0) {
                gameOver();
            }
        }
    }
    private void checkWinCond() {
        if (dotList.size() <= 0 && pelletList.size() <= 0) {
            timer.stop();
            SoundEffect.play("pacman_AwvgsBv.wav");

            SoundEffect.stopLoop();
            isGameOver = true;
            repaint();
        }
    }

    public void gameOver() {
        timer.stop();

        SoundEffect.play("pacman_death.wav");
        isGameOver = true;
        repaint();
    }

    private void drawWinScreen(Graphics2D pen) {
        pen.setColor(new Color(0, 100, 0, 200));
        pen.fillRect(0, 0, getWidth(), getHeight());

        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("Arial", Font.BOLD, 80));
        String winText = "YOU WIN!";
        FontMetrics fm = pen.getFontMetrics();
        int winWidth = fm.stringWidth(winText);
        int winX = (getWidth() - winWidth) / 2;
        int winY = getHeight() / 2 - 100;
        pen.drawString(winText, winX, winY);

        pen.setColor(Color.YELLOW);
        int[] starX1 = {winX - 80, winX - 70, winX - 60};
        int[] starY1 = {winY - 20, winY - 50, winY - 20};
        pen.fillPolygon(starX1, starY1, 3);

        int[] starX2 = {winX + winWidth + 60, winX + winWidth + 70, winX + winWidth + 80};
        int[] starY2 = {winY - 20, winY - 50, winY - 20};
        pen.fillPolygon(starX2, starY2, 3);

        pen.setColor(Color.WHITE);
        pen.setFont(new Font("Arial", Font.BOLD, 50));
        String scoreText = "Final Score: " + score;
        FontMetrics fm2 = pen.getFontMetrics();
        int scoreWidth = fm2.stringWidth(scoreText);
        int scoreX = (getWidth() - scoreWidth) / 2;
        int scoreY = getHeight() / 2;
        pen.drawString(scoreText, scoreX, scoreY);

        for(int i = 0; i < 3; i++) {
            int pacX = (getWidth() / 2) - 120 + (i * 80);
            int pacY = getHeight() / 2 + 50;
            pen.drawImage(pacman.rightImage1, pacX, pacY, 60, 60, null);
        }

        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("Arial", Font.PLAIN, 24));
        String message = "Congratulations!";
        FontMetrics fm3 = pen.getFontMetrics();
        int msgWidth = fm3.stringWidth(message);
        int msgX = (getWidth() - msgWidth) / 2;
        int msgY = getHeight() / 2 + 150;
        pen.drawString(message, msgX, msgY);
    }

    public void update() {
        checkPelletEaten();
        scaryMode();

        if(System.currentTimeMillis()-pelletTime>scaryMode){
            SoundEffect.stopLoop();
        }

        if (blinky.blinkyScared && System.currentTimeMillis() - blinky.blinkyScaredTime > blinky.wait) {
            blinky.blinkyScared = false;
        }
        if (System.currentTimeMillis() - gameStartTime > blinky.wait && !blinky.blinkyScared) {
            blinky.update(map, this);
        }

        if (pinky.pinkyScared && System.currentTimeMillis() - pinky.pinkyScaredTime > pinky.wait) {
            pinky.pinkyScared = false;
        }
        if (System.currentTimeMillis() - gameStartTime > pinky.wait && !pinky.pinkyScared) {
            pinky.update(map, this);
        }

        pacman.update(map);


        if (inky.inkyScared && System.currentTimeMillis() - inky.inkyScaredTime > inky.wait) {
            inky.inkyScared = false;
        }
        if (System.currentTimeMillis() - gameStartTime > inky.wait && !inky.inkyScared) {
            inky.update(map, this);
        }

        if (clyde.clydeScared && System.currentTimeMillis() - clyde.clydeScaredTime > clyde.wait) {
            clyde.clydeScared = false;
        }
        if (System.currentTimeMillis() - gameStartTime > clyde.wait && !clyde.clydeScared) {
            clyde.update(map, this);
        }

        removeDots();
        decreasePacman();
        checkWinCond();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void drawGameOver(Graphics2D pen) {
        pen.setColor(new Color(0, 0, 0, 180));
        pen.fillRect(0, 0, getWidth(), getHeight());

        pen.setColor(Color.RED);
        pen.setFont(new Font("Arial", Font.BOLD, 60));

        String text = "GAME OVER";
        FontMetrics fm = pen.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = (getWidth() - textWidth) / 2;
        int textY = getHeight() / 2 - 50;

        pen.drawString(text, textX, textY);

        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("Arial", Font.BOLD, 40));
        String scoreText = "Score: " + score;
        FontMetrics fm2 = pen.getFontMetrics();
        int scoreWidth = fm2.stringWidth(scoreText);
        int scoreX = (getWidth() - scoreWidth) / 2;
        int scoreY = getHeight() / 2 + 30;

        pen.drawString(scoreText, scoreX, scoreY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D pen = (Graphics2D) g;

        map.draw(pen);

        for (int i = 0; i < pacman.getHealth(); i++) {
            g.drawImage(pacman.rightImage1, 24 * i, 0, 24, 24, null);
        }

        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreText = "Score: " + score;
        drawDots(pen);
        pen.drawString(scoreText, 72 + 470, 24);
        pacman.draw(pen);
        pinky.draw(pen);
        blinky.draw(pen);
        inky.draw(pen);
        clyde.draw(pen);

        if (isGameOver) {
            if (pacman.getHealth() <= 0) {
                drawGameOver(pen);
            } else {
                drawWinScreen(pen);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) pacman.setNextDirection(Direction.UP);
        else if (key == KeyEvent.VK_DOWN) pacman.setNextDirection(Direction.DOWN);
        else if (key == KeyEvent.VK_LEFT) pacman.setNextDirection(Direction.LEFT);
        else if (key == KeyEvent.VK_RIGHT) pacman.setNextDirection(Direction.RIGHT);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}