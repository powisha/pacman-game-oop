import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Pacman {
     BufferedImage upImage1,upImage2,upImage3;
     BufferedImage downImage1,downImage2,downImage3;
     BufferedImage rightImage1,rightImage2,rightImage3;
    BufferedImage leftImage1,leftImage2,leftImage3;
    private float speedPacman;
    Map map;
    private Direction currentDirection;
    private Direction nextDirection;
    public float currentX, currentY;
    float nextX,nextY;
    int gridX,gridY;
    int counter=0;
    private final int SIZE = 22;
    private int health= 3;



    public Pacman(){
        currentX = 13 * 24;
        currentY = 18 * 24;
        gridX = 13;
        gridY = 18;
        speedPacman = 2.0f;
        currentDirection = Direction.RIGHT;
        nextDirection = Direction.NONE;
        loadImage();

    }

    public void loadImage(){
        try{
            upImage1= ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-up/1.png"));
            upImage2= ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-up/2.png"));
            upImage3= ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-up/3.png"));

            downImage1=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-down/1.png"));
            downImage2=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-down/2.png"));
            downImage3=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-down/3.png"));

            leftImage1=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-left/1.png"));
            leftImage2=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-left/2.png"));
            leftImage3=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-left/3.png"));

            rightImage1=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-right/1.png"));
            rightImage2=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-right/2.png"));
            rightImage3=ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman-right/3.png"));
        }

        catch(Exception e){
            System.out.println("Images cannot load!");

        }
    }

    public boolean moveUp(Map map){
        nextY = currentY - speedPacman;
        nextX = currentX;

        int tempGridX1 = (int)(nextX / 24);
        int tempGridX2 = (int)((nextX + SIZE) / 24);
        int tempGridY = (int)(nextY /24);

        Point p1 = new Point(tempGridX1, tempGridY);
        Point p2 = new Point(tempGridX2, tempGridY);

        if(!map.isWall(p1) && !map.isWall(p2)){
            currentY = nextY;
            currentX = nextX;
            return true;
        }
        return false;
    }


    public boolean moveDown(Map map){
        nextY = currentY + speedPacman;
        nextX = currentX;


        int tempGridX1 = (int)(nextX / 24);
        int tempGridX2 = (int)((nextX + SIZE) / 24);
        int tempGridY = (int)((nextY + SIZE) / 24);

        Point p1 = new Point(tempGridX1, tempGridY);
        Point p2 = new Point(tempGridX2, tempGridY);

        if(!map.isWall(p1) && !map.isWall(p2)){
            currentY = nextY;
            currentX = nextX;
            return true;
        }
        return false;
    }


    public boolean moveRight(Map map){
        nextY = currentY;
        nextX = currentX + speedPacman;


        int tempGridX = (int)((nextX + SIZE) /24);
        int tempGridY1 = (int)(nextY / 24);
        int tempGridY2 = (int)((nextY + SIZE) / 24);

        Point p1 = new Point(tempGridX, tempGridY1);
        Point p2 = new Point(tempGridX, tempGridY2);

        if(!map.isWall(p1) && !map.isWall(p2)){
            currentY = nextY;
            currentX = nextX;
            return true;
        }
        return false;
    }

    public boolean moveLeft(Map map){
        nextY = currentY;
        nextX = currentX - speedPacman;


        int tempGridX = (int)(nextX / 24);
        int tempGridY1 = (int)(nextY / 24);
        int tempGridY2 = (int)((nextY + SIZE) / 24);

        Point p1 = new Point(tempGridX, tempGridY1);
        Point p2 = new Point(tempGridX, tempGridY2);

        if(!map.isWall(p1) && !map.isWall(p2)){
            currentY = nextY;
            currentX = nextX;
            return true;
        }
        return false;
    }

    public void update(Map map){

        boolean moved=false;

        counter++;

        if(counter>15){
            counter=0;
        }

        if(nextDirection!=Direction.NONE){

            if(nextDirection==Direction.UP){
                moved=moveUp(map);
            }
            else if(nextDirection==Direction.LEFT){
                moved=moveLeft(map);
            }
            else if(nextDirection==Direction.RIGHT){
                moved=moveRight(map);
            }
            else if(nextDirection==Direction.DOWN){
                moved=moveDown(map);
            }

            if(moved){
                currentDirection=nextDirection;
                nextDirection=Direction.NONE;
            }

        }

        if(!moved){

            if(currentDirection==Direction.UP){
                moveUp(map);
            }
            else if(currentDirection==Direction.LEFT){
                moveLeft(map);
            }
            else if(currentDirection==Direction.RIGHT){
                moveRight(map);
            }
            else if(currentDirection==Direction.DOWN){
                moveDown(map);
            }
        }

        gridX=(int)(currentX/24);
        gridY=(int)(currentY/24);

    }

    public void draw(Graphics2D pen){

        BufferedImage currentImage=null;

        int frame;

        if(counter<5){
            frame=1;
        }
        else if(counter<10){
            frame=2;
        }
        else{
            frame=3;
        }


        if(currentDirection==Direction.UP){
            if(frame==1) currentImage=upImage1;
            else if(frame==2) currentImage=upImage2;
            else currentImage=upImage3;

        }

        else if(currentDirection==Direction.DOWN) {
            if(frame==1) currentImage=downImage1;
            else if(frame==2) currentImage=downImage2;
            else currentImage=downImage3;
        }

        else if(currentDirection==Direction.LEFT){
            if(frame==1) currentImage=leftImage1;
            else if(frame==2) currentImage=leftImage2;
            else currentImage=leftImage3;
        }

        else if(currentDirection==Direction.RIGHT){
            if(frame==1) currentImage=rightImage1;
            else if(frame==2) currentImage=rightImage2;
            else currentImage=rightImage3;
        }


        if(currentImage!=null){
            pen.drawImage(currentImage,(int)currentX,(int)currentY,22,22,null);
        }
        else{
            pen.setColor(Color.YELLOW);
            pen.fillOval((int)currentX, (int)currentY, 22, 22);
        }


    }

    public void setNextDirection(Direction dir){
        nextDirection=dir;
    }

    public Direction getCurrentDirection(){
        return currentDirection;
    }

    public int getHealth(){
        return health;
    }

    public void setHealth(int h){
        health=h;
    }

}