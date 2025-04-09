package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {

    // --- Tunable Parameters (Hyperparameters) ---
    /** Max number of structures (rooms + hallways) to generate. */
    private static final int MAX_STRUCTURES = 50;
    /** Min width/height of a room. */
    private static final int MIN_ROOM_SIZE = 4;
    /** Max width/height of a room. */
    private static final int MAX_ROOM_SIZE = 9; // Adjusted max size slightly
    /** Min length of a hallway. */
    private static final int MIN_HALLWAY_LENGTH = 3;
    /** Max length of a hallway. */
    private static final int MAX_HALLWAY_LENGTH = 7; // Adjusted max size slightly
    /** Probability (0.0 to 1.0) to attempt placing a ROOM when expanding from a HALLWAY connection point. */
    private static final double PROB_HALLWAY_CONNECTS_ROOM = 0.55; // e.g., 55% chance for room

    // --- Internal Constants ---
    private static final int NORTH = 0;
    private static final int SOUTH = 1;
    private static final int EAST = 2;
    private static final int WEST = 3;

    // --- Member Variables ---
    private int worldWidth;
    private int worldHeight;
    private Random random;
    private TETile[][] world;
    private List<Rectangle> placedStructures;
    private List<ConnectionPoint> frontier;

    // --- Enums and Inner Classes ---
    private enum StructureType {
        ROOM, HALLWAY
    }

    private static class ConnectionPoint {
        int x, y;
        int direction;
        StructureType fromType;
        ConnectionPoint(int x, int y, int direction, StructureType fromType) { /* constructor */
            this.x = x; this.y = y; this.direction = direction; this.fromType = fromType;
        }
    }

    // --- Public Generate Method ---
    public TETile[][] generate(int width, int height, long seed) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.random = new Random(seed);
        this.world = new TETile[width][height];
        this.placedStructures = new ArrayList<>();
        this.frontier = new ArrayList<>();

        initializeWorld();
        if (!createStartingRoom()) {
            System.err.println("Error: Could not place starting room.");
            return world;
        }

        int structuresBuilt = 1;
        while (!frontier.isEmpty() && structuresBuilt < MAX_STRUCTURES) {
            int index = random.nextInt(frontier.size());
            ConnectionPoint cp = frontier.remove(index);
            if (tryExpandFrom(cp)) {
                structuresBuilt++;
            }
        }

        // ⭐ Final Cleanup Pass (Reverted to simpler N,S,E,W check around FLOOR only) ⭐
        finalCleanupPass();

        // Optional: Fill remaining background
        // fillRemainingNothing(Tileset.GRASS);

        return world;
    }

    // --- Initialization ---
    private void initializeWorld() { /* unchanged */
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    // --- Starting Room ---
    private boolean createStartingRoom() { /* unchanged */
        int attempts = 100;
        for (int i = 0; i < attempts; i++) {
            int roomWidth = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;
            int roomHeight = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;
            int x = random.nextInt(worldWidth - roomWidth - 2) + 1;
            int y = random.nextInt(worldHeight - roomHeight - 2) + 1;
            Rectangle roomRect = new Rectangle(x, y, roomWidth, roomHeight);
            if (isAreaClear(roomRect, null)) {
                drawRoom(x, y, roomWidth, roomHeight);
                placedStructures.add(roomRect);
                placeLockedDoor(x, y, roomWidth, roomHeight);
                addRoomConnectionPoints(x, y, roomWidth, roomHeight);
                return true;
            }
        }
        return false;
    }

    private void placeLockedDoor(int x, int y, int w, int h) { /* unchanged */
        List<int[]> possibleDoorLocations = new ArrayList<>();
        for (int dx = x + 1; dx < x + w - 1; dx++) {
            if (isWithinBounds(dx, y)) possibleDoorLocations.add(new int[]{dx, y});
            if (isWithinBounds(dx, y + h - 1)) possibleDoorLocations.add(new int[]{dx, y + h - 1});
        }
        for (int dy = y + 1; dy < y + h - 1; dy++) {
            if (isWithinBounds(x, dy)) possibleDoorLocations.add(new int[]{x, dy});
            if (isWithinBounds(x + w - 1, dy)) possibleDoorLocations.add(new int[]{x + w - 1, dy});
        }
        if (!possibleDoorLocations.isEmpty()) {
            int[] doorPos = possibleDoorLocations.get(random.nextInt(possibleDoorLocations.size()));
            if (world[doorPos[0]][doorPos[1]] == Tileset.WALL) {
                world[doorPos[0]][doorPos[1]] = Tileset.LOCKED_DOOR;
            } else {
                System.err.println("Warning: Could not place locked door on a wall tile at " + doorPos[0] + "," + doorPos[1] + ". Tile is " + world[doorPos[0]][doorPos[1]]);
            }
        } else {
            System.err.println("Warning: No valid wall locations found for locked door in room at " + x + "," + y);
        }
    }


    // --- Expansion Logic ---
    private boolean tryExpandFrom(ConnectionPoint cp) {
        // Basic validity checks
        if (!isWithinBounds(cp.x, cp.y) || world[cp.x][cp.y] == Tileset.NOTHING || world[cp.x][cp.y] == Tileset.FLOOR) return false;
        int targetX = cp.x, targetY = cp.y;
        switch(cp.direction) { /* calculate target */
            case NORTH: targetY++; break; case SOUTH: targetY--; break;
            case EAST: targetX++; break; case WEST: targetX--; break;
        }
        if (!isWithinBounds(targetX, targetY) || world[targetX][targetY] == Tileset.FLOOR) return false;

        // Decide what to build
        if (cp.fromType == StructureType.ROOM) {
            return tryAddHallway(cp); // Rooms must connect to hallways
        } else { // From HALLWAY
            // ⭐ Use Hyperparameter for decision ⭐
            if (random.nextDouble() < PROB_HALLWAY_CONNECTS_ROOM) {
                // Try Room first
                if (tryAddRoom(cp)) return true;
                else return tryAddHallway(cp); // Fallback: try hallway
            } else {
                // Try Hallway first
                if (tryAddHallway(cp)) return true;
                else return tryAddRoom(cp); // Fallback: try room
            }
        }
    }

    private boolean tryAddHallway(ConnectionPoint cp) { /* unchanged */
        int length = random.nextInt(MAX_HALLWAY_LENGTH - MIN_HALLWAY_LENGTH + 1) + MIN_HALLWAY_LENGTH;
        boolean horizontal;
        int drawX, drawY;
        Rectangle hallRect;
        switch (cp.direction) {
            case NORTH: horizontal=false; drawX=cp.x-1; drawY=cp.y+1; hallRect=new Rectangle(drawX,drawY,3,length); break;
            case SOUTH: horizontal=false; drawX=cp.x-1; drawY=cp.y-length; hallRect=new Rectangle(drawX,drawY,3,length); break;
            case EAST: horizontal=true; drawX=cp.x+1; drawY=cp.y-1; hallRect=new Rectangle(drawX,drawY,length,3); break;
            case WEST: horizontal=true; drawX=cp.x-length; drawY=cp.y-1; hallRect=new Rectangle(drawX,drawY,length,3); break;
            default: return false;
        }
        if (!isAreaClear(hallRect, cp)) return false;
        drawHallway(drawX, drawY, length, horizontal, cp);
        placedStructures.add(hallRect);
        addHallwayConnectionPoints(drawX, drawY, length, horizontal);
        return true;
    }


    private boolean tryAddRoom(ConnectionPoint cp) { /* unchanged */
        if (cp.fromType != StructureType.HALLWAY) return false;
        int roomWidth = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;
        int roomHeight = random.nextInt(MAX_ROOM_SIZE - MIN_ROOM_SIZE + 1) + MIN_ROOM_SIZE;
        int rx = 0, ry = 0;
        switch (cp.direction) {
            case NORTH: rx=cp.x-(random.nextInt(roomWidth-2)+1); ry=cp.y+1; break;
            case SOUTH: rx=cp.x-(random.nextInt(roomWidth-2)+1); ry=cp.y-roomHeight; break;
            case EAST: rx=cp.x+1; ry=cp.y-(random.nextInt(roomHeight-2)+1); break;
            case WEST: rx=cp.x-roomWidth; ry=cp.y-(random.nextInt(roomHeight-2)+1); break;
            default: return false;
        }
        Rectangle roomRect = new Rectangle(rx, ry, roomWidth, roomHeight);
        if (!isAreaClear(roomRect, cp)) return false;
        drawRoom(rx, ry, roomWidth, roomHeight);
        // Ensure connection points are floor
        if(isWithinBounds(cp.x, cp.y)) world[cp.x][cp.y]=Tileset.FLOOR;
        int targetX=cp.x, targetY=cp.y;
        switch(cp.direction){case NORTH:targetY++;break; case SOUTH:targetY--;break; case EAST:targetX++;break; case WEST:targetX--;break;}
        if(isWithinBounds(targetX,targetY)){if(world[targetX][targetY]==Tileset.WALL || world[targetX][targetY]==Tileset.NOTHING) world[targetX][targetY]=Tileset.FLOOR;}
        placedStructures.add(roomRect);
        addRoomConnectionPoints(rx, ry, roomWidth, roomHeight);
        return true;
    }

    // --- Drawing Methods ---
    private void drawRoom(int x, int y, int width, int height) { /* unchanged */
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (!isWithinBounds(i, j)) continue;
                if (i == x || i == x + width - 1 || j == y || j == y + height - 1) {
                    if (world[i][j] == Tileset.NOTHING) world[i][j] = Tileset.WALL;
                } else {
                    if (world[i][j] != Tileset.LOCKED_DOOR) world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    private void drawHallway(int x, int y, int length, boolean horizontal, ConnectionPoint cp) { /* unchanged */
        // Draw Floor
        if (horizontal) { for (int i=0;i<length;i++){int cX=x+i; if(isWithinBounds(cX,y+1) && world[cX][y+1]!=Tileset.LOCKED_DOOR) world[cX][y+1]=Tileset.FLOOR;}}
        else { for (int j=0;j<length;j++){int cY=y+j; if(isWithinBounds(x+1,cY) && world[x+1][cY]!=Tileset.LOCKED_DOOR) world[x+1][cY]=Tileset.FLOOR;}}
        // Draw Walls
        if (horizontal) { for (int i=0;i<length;i++){int cX=x+i; if(isWithinBounds(cX,y+2) && world[cX][y+2]==Tileset.NOTHING) world[cX][y+2]=Tileset.WALL; if(isWithinBounds(cX,y) && world[cX][y]==Tileset.NOTHING) world[cX][y]=Tileset.WALL;}}
        else { for (int j=0;j<length;j++){int cY=y+j; if(isWithinBounds(x,cY) && world[x][cY]==Tileset.NOTHING) world[x][cY]=Tileset.WALL; if(isWithinBounds(x+2,cY) && world[x+2][cY]==Tileset.NOTHING) world[x+2][cY]=Tileset.WALL;}}
        // Ensure connection is Floor
        if(isWithinBounds(cp.x,cp.y)) world[cp.x][cp.y]=Tileset.FLOOR;
        int targetX=cp.x, targetY=cp.y;
        switch(cp.direction){case NORTH:targetY++;break; case SOUTH:targetY--;break; case EAST:targetX++;break; case WEST:targetX--;break;}
        if(isWithinBounds(targetX,targetY)){if(world[targetX][targetY]==Tileset.WALL || world[targetX][targetY]==Tileset.NOTHING) world[targetX][targetY]=Tileset.FLOOR;}
    }


    // --- Add Connection Points ---
    private void addRoomConnectionPoints(int x, int y, int width, int height) { /* unchanged */
        for(int i=x+1;i<x+width-1;i++){if(isWithinBounds(i,y) && world[i][y]==Tileset.WALL) frontier.add(new ConnectionPoint(i,y,SOUTH,StructureType.ROOM));}
        for(int i=x+1;i<x+width-1;i++){if(isWithinBounds(i,y+height-1) && world[i][y+height-1]==Tileset.WALL) frontier.add(new ConnectionPoint(i,y+height-1,NORTH,StructureType.ROOM));}
        for(int j=y+1;j<y+height-1;j++){if(isWithinBounds(x,j) && world[x][j]==Tileset.WALL) frontier.add(new ConnectionPoint(x,j,WEST,StructureType.ROOM));}
        for(int j=y+1;j<y+height-1;j++){if(isWithinBounds(x+width-1,j) && world[x+width-1][j]==Tileset.WALL) frontier.add(new ConnectionPoint(x+width-1,j,EAST,StructureType.ROOM));}
    }

    private void addHallwayConnectionPoints(int x, int y, int length, boolean horizontal) { /* unchanged */
        if(horizontal){int fY=y+1; for(int i=0;i<length;i++){int cX=x+i; if(isWithinBounds(cX,y+2) && world[cX][y+2]==Tileset.WALL) frontier.add(new ConnectionPoint(cX,y+2,NORTH,StructureType.HALLWAY)); if(isWithinBounds(cX,y) && world[cX][y]==Tileset.WALL) frontier.add(new ConnectionPoint(cX,y,SOUTH,StructureType.HALLWAY));} if(isWithinBounds(x-1,fY) && world[x-1][fY]==Tileset.WALL) frontier.add(new ConnectionPoint(x-1,fY,WEST,StructureType.HALLWAY)); if(isWithinBounds(x+length,fY) && world[x+length][fY]==Tileset.WALL) frontier.add(new ConnectionPoint(x+length,fY,EAST,StructureType.HALLWAY));}
        else{int fX=x+1; for(int j=0;j<length;j++){int cY=y+j; if(isWithinBounds(x,cY) && world[x][cY]==Tileset.WALL) frontier.add(new ConnectionPoint(x,cY,WEST,StructureType.HALLWAY)); if(isWithinBounds(x+2,cY) && world[x+2][cY]==Tileset.WALL) frontier.add(new ConnectionPoint(x+2,cY,EAST,StructureType.HALLWAY));} if(isWithinBounds(fX,y-1) && world[fX][y-1]==Tileset.WALL) frontier.add(new ConnectionPoint(fX,y-1,SOUTH,StructureType.HALLWAY)); if(isWithinBounds(fX,y+length) && world[fX][y+length]==Tileset.WALL) frontier.add(new ConnectionPoint(fX,y+length,NORTH,StructureType.HALLWAY));}
    }


    // --- Utility Methods ---
    private boolean isWithinBounds(int x, int y) { /* unchanged */
        return x >= 0 && x < worldWidth && y >= 0 && y < worldHeight;
    }

    // Check if area is clear (all NOTHING except allowed contact points)
    // Includes previous bug fix for rect.height
    private boolean isAreaClear(Rectangle rect, ConnectionPoint cp) { /* unchanged, includes previous fix */
        int targetX = -1, targetY = -1;
        if (cp != null) {
            targetX = cp.x; targetY = cp.y;
            switch(cp.direction) {
                case NORTH: targetY++; break; case SOUTH: targetY--; break;
                case EAST: targetX++; break; case WEST: targetX--; break;
            }
        }
        if (rect.x < 0 || rect.y < 0 || rect.x + rect.width > worldWidth || rect.y + rect.height > worldHeight) return false;
        for (int x = rect.x; x < rect.x + rect.width; x++) {
            for (int y = rect.y; y < rect.y + rect.height; y++) { // Ensure rect.height is used
                boolean isAllowedContactPoint = cp!=null && ((x==cp.x && y==cp.y)||(x==targetX && y==targetY));
                if (!isAllowedContactPoint && world[x][y] != Tileset.NOTHING) return false;
            }
        }
        return true;
    }

    // ⭐ Final Cleanup Pass (REVERTED to simpler logic) ⭐
    // Fills NOTHING tiles directly adjacent (N, S, E, W) to FLOOR tiles.
    private void finalCleanupPass() {
        // Create a copy to read from, modify the original world grid
        TETile[][] originalWorld = new TETile[worldWidth][worldHeight];
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                originalWorld[x][y] = world[x][y];
            }
        }

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                // Only check around FLOOR tiles from the original state
                if (originalWorld[x][y] == Tileset.FLOOR) {
                    // Check only N, S, E, W neighbors
                    int[] dx = {0, 0, 1, -1};
                    int[] dy = {1, -1, 0, 0}; // N, S, E, W

                    for (int i = 0; i < 4; i++) {
                        int nx = x + dx[i];
                        int ny = y + dy[i];

                        if (isWithinBounds(nx, ny)) {
                            // If neighbor in *original* state was NOTHING, fill it with WALL
                            if (originalWorld[nx][ny] == Tileset.NOTHING) {
                                world[nx][ny] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }


    // Optional: Fill remaining NOTHING tiles
    private void fillRemainingNothing(TETile background) { /* unchanged */
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                if (world[x][y] == Tileset.NOTHING) world[x][y] = background;
            }
        }
    }

    // --- Main Method for Testing ---
    public static void main(String[] args) { /* unchanged */
        int width = 80;
        int height = 40;
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        WorldGenerator wg = new WorldGenerator();
        long seed = 12347;
        // long seed = System.currentTimeMillis();
        TETile[][] finalWorld = wg.generate(width, height, seed);
        ter.renderFrame(finalWorld);
    }

} // End of WorldGenerator class