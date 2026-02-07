import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;



public class Map {
    private final int TILE_SIZE = 24;
    private int width=28;
    private int height=32;
    private HashMap<Point,TileType> gridMap;
    private BufferedImage mapImage;
    public int panelWidth= width*24;
    public int panelHeight=height*24;

    public Map(){
        loadMapImage();
        gridMap= new HashMap<Point,TileType>();
        loadFromFile("hashmap.txt");

    }

    private void loadFromFile(String fileName){
        try{

            InputStream is= getClass().getResourceAsStream("/hashmap.txt");
            Scanner s = new Scanner(is);
            int y=0;

            while(s.hasNextLine()){
                String line= s.nextLine();


                for(int x=0; x<28; x++){

                        char tile= line.charAt(x);
                        Point p =new Point(x,y);

                        switch(tile){
                            case 'R':
                                gridMap.put(p,TileType.ROAD);
                                break;

                            case 'W':
                                gridMap.put(p,TileType.WALL);
                                break;

                            case 'P':
                                gridMap.put(p,TileType.PELLET);
                                break;

                            case 'G' :
                                gridMap.put(p,TileType.GHOST);
                                break;

                            case 'B':
                                gridMap.put(p,TileType.PACKMAN);
                                break;


                        }

                    }
                y++;

            }
            s.close();
            System.out.println("Toplam satır: " + y);
            System.out.println("HashMap boyutu: " + gridMap.size());
        }

        catch(Exception e){
            System.out.println("File can not read!");
        }

    }

    private void loadMapImage(){
        try{

            mapImage= ImageIO.read(getClass().getResourceAsStream("/pacman-art/pacman_map_large.png"));

        }

        catch(Exception e ){
                System.out.println("Image can not load!");
        }

    }

    public boolean isWall(Point p){
        if(gridMap.get(p)==TileType.WALL){
            return true;
        }
        return false;
    }

    public void draw(Graphics2D pen){

        if(mapImage!=null){
            pen.drawImage(mapImage,0,0,panelWidth,panelHeight,null);
        }

        else {
            pen.setColor(Color.BLACK);
            pen.fillRect(0, 0, 896, 1024);
        }
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public HashMap<Point,TileType> getGridMap(){
        return gridMap;
    }
}



