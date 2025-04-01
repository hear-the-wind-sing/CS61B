package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Hextile {
    private int length;
    private TETile texture;
    private int x;
    private int y;
    public Hextile(int x,int y,int length,TETile texture){
        this.x = x;
        this.y = y;
        this.length = length;
        this.texture = texture;
    }
    public boolean draw(TETile[][] map) {
        if(candraw(map)) {
            drawup(map);
            drawdown(map);
            return true;
        }
        return false;
    }
    public void drawup(TETile[][] map) {
        int x=this.x;
        int y=this.y;
        int length =this.length;
        for(int i=0;i<this.length;i++){
            drawmline(map,x,y,length);
            x-=1;y-=1;length+=2;
        }
    }
    public void drawdown(TETile[][] map) {
        int x = this.x - this.length+1;
        int y = this.y -this.length;
        int length = 2 * (this.length-1) +this.length;
        for(int i=0;i<this.length;i++){
            drawmline(map,x,y,length);
            x+=1;y-=1;length-=2;
        }
    }
    public void drawmline(TETile[][] map,int x,int y,int length) {
        for(int i = x;i <= x+length-1;i++){
            //System.out.println(y);
            map[i][y] = texture;
        }
    }
    public boolean candraw(TETile[][] map) {
        if(candrawup(map)&&candrawdown(map)) {
            return true;
        }
        else return false;
    }
    public boolean candrawup(TETile[][] map) {
        int row = map.length-1;
        int col = map[0].length-1;
        int x=this.x;
        int y=this.y;
        int length =this.length;
        for(int i=0;i<this.length;i++){
            if(x<0||x>row) return false;
            if(y<0||y>col) return false;
            if(x+length-1>row) return false;
            if(!candrawline(map,x,y,length))return false;
            x-=1;y-=1;length+=2;
//            if(x<0||x>row) return false;
//            if(y<0||y>col) return false;
//            if(x+length-1>row) return false;
        }
        return true;
    }
    public boolean candrawdown(TETile[][] map) {
        int row = map.length-1;
        int col = map[0].length-1;
        int x = this.x - this.length+1;
        int y = this.y -this.length;
        int length = 2 * (this.length-1) +this.length;
        for(int i=0;i<this.length;i++){
            if(x<0||x>row) return false;
            if(y<0||y>col) return false;
            if(x+length-1>row) return false;
            if(!candrawline(map,x,y,length))return false;
            x+=1;y-=1;length-=2;
//            if(x<0||x>row) return false;
//            if(y<0||y>col) return false;
//            if(x+length-1>row) return false;
        }
        return true;
    }
    public boolean candrawline(TETile[][] map,int x,int y,int length){
        for(int i=x;i<=x+length-1;i++){
            if(map[i][y] != Tileset.NOTHING)return false;
        }
        return true;
    }
}
