package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static  int WIDTH = 60;
    private static  int HEIGHT = 60;
    private static final long SEED = 114514;
    private static final Random RANDOM = new Random(SEED);

    public static void fileWithRandomHextile(TETile[][] randomTiles) {
        int n = RANDOM.nextInt();
        //int length = RANDOM.nextInt(3) + 2;
        int length = 4;
        System.out.println(length);
        for(int i=0; i<100;i++) {
            Hextile a = getRandomHextile(length);
            a.draw(randomTiles);
        }
    }
    public static void fileWithHextile(TETile[][] randomtiles,int length) {
        int x = 5*length -2  -1;
        int y = 10*length  -1;
        drawline(randomtiles,length,x-2*length+1-2*length+1,y-length-length,3);
        drawline(randomtiles,length,x-2*length+1,y-length,4);
        drawline(randomtiles,length,x,y,5);
        drawline(randomtiles,length,x+2*length-1,y-length,4);
        drawline(randomtiles,length,x+2*length-1+2*length-1,y-length-length,3);
    }
    public static void drawline(TETile[][] randomtiles,int length,int x,int yy,int num){
        int y = yy;
        for(int i=1;i<=num;i++){
            if(num == 5){
                System.out.println(x);
                System.out.println(y);

            }
            TETile texture = randomTile();
            Hextile h = new Hextile(x,y,length,texture);
            h.draw(randomtiles);
            if(y == 3) {
                System.out.println(h.candraw(randomtiles));
            }
            y-=2*length;
        }
    }
    public static Hextile getRandomHextile(int length) {
        int x = RANDOM.nextInt(HEIGHT);
        int y = RANDOM.nextInt(WIDTH);
        //System.out.println(x);
        //System.out.println(y);
        //System.out.println(length);
        TETile texture = randomTile();
        return new Hextile(x,y,length,texture);
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0: return Tileset.TREE;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.MOUNTAIN;
            default: return Tileset.SAND;
        }
    }
    public static void main(String[] args) {
        int length = 2 ;
        TERenderer ter = new TERenderer();
        WIDTH = 11*length -6  ;
        HEIGHT = 10*length   ;
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                randomTiles[x][y] = Tileset.NOTHING;
            }
        }

//        fileWithRandomHextile(randomTiles);
//        ter.renderFrame(randomTiles);
        fileWithHextile(randomTiles,length);
        ter.renderFrame(randomTiles);
    }
}
