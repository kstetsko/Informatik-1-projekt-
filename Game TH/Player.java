import java.awt.event.KeyEvent;

public class Player {

    double x, y; //place 
    double w = 40, h = 40; // size 

    double vx = 0 ; 
    double vy = 0; // x, y speed

    double speed = 300; // px pro sec
    double g = 900; // px pro sec*sec (positive -> pulls down)

    boolean onGround = false;

    int keyLeft;
    int keyRight;
    int keyUp;

    boolean player1; // true for player1, false for player2
    double startX, startY; // starting position for respawn

    public Player(double x, double y, double w, double h , int keyLeft, int keyRight, int keyUp, boolean isPlayer1) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.player1 = isPlayer1;
        this.startX = x;
        this.startY = y;
    }

    public boolean isPlayer1(){
        return player1;
    }

    public void resetToStart(){
        this.x = startX;
        this.y = startY;
        this.vx = 0;
        this.vy = 0;
        this.onGround = false;
    }

    void  update(double dt){
        //hor
        x += vx * dt;

        //ver
        vy += g * dt;
        y += vy * dt;
    }

    void handleInput(){

        vx = 0;

        if (Draw.isKeyDown(keyLeft)){
            vx -= speed;
        }

        if (Draw.isKeyDown(keyRight )){
            vx += speed;
        }

        if (Draw.isKeyDown(keyUp) && onGround){
            // negative vy to move up when jumping (y increases downward)
            vy = -450;
        }
    }

    void draw(){
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }
}
