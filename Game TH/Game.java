import java.awt.event.KeyEvent;

public class Game {

    // interection platform player check
    public static boolean intersects(Player p, Platform pl){
        return p.x < pl.x + pl.w &&
               p.x + p.w > pl.x &&
               p.y < pl.y + pl.h &&
               p.y + p.h > pl.y;
    }

   

    public static boolean victoryCondition(Player p, Door d) {
         return p.x >= d.x &&
             p.x + p.w <= d.x + d.w &&
             p.y >= d.y &&
             p.y + p.h <= d.y + d.h;
    }


    public static void main(String[] args){

        int width = 1000;
        int height = 1000;

        Draw.init(width, height);

        // players
        Player player1 = new Player(100, 800, 40, 40, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, true);
        Player player2 = new Player(300, 800, 40, 40, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, false);

        // Platforms
        Platform platform1 = new Platform(50, 900, 900, 50);   
        Platform platform2 = new Platform(50, 800, 450, 50);  
        Platform platform3 = new Platform(550, 700, 200, 50);
        Platform platform4 = new Platform(300, 600, 400, 50);
        Platform platform5 = new Platform(750, 500, 200, 50);

        // Doors
        Door door1 = new Door(850, 840, 60, 60);
        Door door2 = new Door(700, 640, 60, 60);

        Acid acid1 = new Acid(800, 500, 200, 25);

        Water water1 = new Water(200, 600, 200, 25);

        Lava lava1 = new Lava(100, 800, 200, 25, 255, 0, 0);

        // Lists of platforms and players
        Player[] players = new Player[] { player1, player2 };
        Platform[] platforms = new Platform[] { platform1, platform2, platform3, platform4, platform5 };
       // Door[] doors = new Door[] { door1, door2 };
        double lastTime = System.nanoTime() / 1e9;

        while (true) {
            double currentTime = System.nanoTime() / 1e9;
            double dt = currentTime - lastTime;
            lastTime = currentTime;


            for (Player p : players) {
                p.handleInput();
                p.update(dt);
            }
Liquid[] liquids = new Liquid[] { acid1, water1, lava1 };
            for (Player p : players) {
                for (Liquid li : liquids) {
                    li.handlePlayer(p);
                }
            }
            // Allow players to interact with platforms
            for (Player p : players) {
                p.onGround = false; // in the air by default

                for (Platform pl : platforms) { // check collision with each platform
                    if (!intersects(p, pl)) continue;

                    double overlapX = Math.min(p.x + p.w, pl.x + pl.w) - Math.max(p.x, pl.x);
                    double overlapY = Math.min(p.y + p.h, pl.y + pl.h) - Math.max(p.y, pl.y);

                    if (overlapX <= 0 || overlapY <= 0) continue;

                    if (overlapX < overlapY) {
                        // allow horizontal movement
                        if (p.x < pl.x) {
                            p.x -= overlapX;
                        } else {
                            p.x += overlapX;
                        }
                        p.vx = 0;
                    } else {
                        // allow vertical movement
                        if (p.y < pl.y) {
                            // Player is above the platform — landing on it     
                            p.y = pl.y - p.h;
                            p.vy = 0;
                            p.onGround = true;
                        } else {
                            // Player is below the platform — push down
                            p.y = pl.y + pl.h;
                            p.vy = 0;
                        }
                    }
                }
                if(victoryCondition(player2, door2) == true && victoryCondition(player1, door1) == true){
                    Draw.setColor(0, 0, 0);
                    Draw.text(100, 100, "Victory!", 50 );
                    return;
                }
            }

            Draw.clearScreen();

            Draw.setColor(150, 150, 150);
            for (Platform pl : platforms) pl.draw();

            Draw.setColor(144, 213, 255);
            player1.draw();

            Draw.setColor(255, 165, 0);
            player2.draw();

            Draw.setColor(255, 165, 0);
            door2.draw();

            Draw.setColor(144, 213, 255);
            door1.draw();

            acid1.draw();
            water1.draw();
            lava1.draw();

            // Check liquid interactions: teleport or block players when necessary
            

            Draw.syncToFrameRate(); 

            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }
}