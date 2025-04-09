package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    private TETile[][] world;
    private boolean[][] visited;
    private Queue<Position> queue = new LinkedList<>();
    private Random random;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        initializeWorld();
        long seed = Long.parseLong(input);
        WorldGenerator generator = new WorldGenerator();
        return generator.generate(80,30,seed);
    }

    private void initializeWorld() {
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.WALL;
            }
        }
    }
//
//    // 生成起始房间（对应图像中的黄色焦点块）
//    private void createSeedRoom(int x, int y) {
//        int roomW = random.nextInt(3) + 3; // 3~5 宽度
//        int roomH = random.nextInt(3) + 3;
//        for (int i = x; i < x + roomW; i++) {
//            for (int j = y; j < y + roomH; j++) {
//                if (i < WIDTH && j < HEIGHT) {
//                    world[i][j] = Tileset.FLOOR; // 房间地板
//                    visited[i][j] = true;
//                }
//            }
//        }
//        queue.add(new Position(x + roomW/2, y + roomH/2)); // 房间中心作为 BFS 起点
//    }
//    private Position getRandomPosition() {
//        return new Position(random.nextInt(WIDTH),random.nextInt(HEIGHT));
//    }
}
