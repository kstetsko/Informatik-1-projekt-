import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

    static ArrayList<Collectible> collectibles = new ArrayList<>();

    // Проверка столкновения игрока с платформой
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
        Player player1 = new Player(100, 800, 40, 40, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP);
        player1.id = 1;
        Player player2 = new Player(300, 800, 40, 40, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W);
        player2.id = 2;

        collectibles.add(new Collectible(200, 750, 20, 1));
        collectibles.add(new Collectible(500, 750, 20, 2));

        // Platforms
        Platform platform1 = new Platform(50, 900, 900, 50);   
        Platform platform2 = new Platform(50, 800, 450, 50);  
        Platform platform3 = new Platform(550, 700, 200, 50);
        MovingPlattform movingPlattform =new MovingPlattform(350.0, 550.0, 180.0, 40.0, 750.0, 150.0, 180.0, 770, 130, 30);


        //Doors (wider than player so player can fit fully)
        Door door1 = new Door(850, 840, 60, 60);
        Door door2 = new Door(700, 640, 60, 60);

        // Lists of platforms and players
        Player[] players = new Player[] { player1, player2 };
        Platform[] platforms = new Platform[] { platform1, platform2, platform3, movingPlattform };
        Door[] doors = new Door[] { door1, door2 };
        double lastTime = System.nanoTime() / 1e9;

        while (true) {
            double currentTime = System.nanoTime() / 1e9;
            double dt = currentTime - lastTime;
            lastTime = currentTime;


            for (Collectible c : collectibles) {
            c.checkCollect(player1);
            c.checkCollect(player2);
            }


            for (Player p : players) {
                p.handleInput();
                p.update(dt);
            }
            for (Platform pl : platforms) {
        if (pl instanceof MovingPlattform mp) {

        // 1. Trigger zurücksetzen
        mp.active = false;

        // 2. Alle Spieler prüfen
        for (Player p : players) {
            mp.checkTrigger(p);
        }

        // 3. Plattform updaten
        mp.update(dt);

    
    }
}

            // Разрешение столкновений игроков со всеми платформами
            for (Player p : players) {
                p.onGround = false; // по умолчанию в воздухе

                for (Platform pl : platforms) {
                    if (!intersects(p, pl)) continue;

                    double overlapX = Math.min(p.x + p.w, pl.x + pl.w) - Math.max(p.x, pl.x);
                    double overlapY = Math.min(p.y + p.h, pl.y + pl.h) - Math.max(p.y, pl.y);

                    if (overlapX <= 0 || overlapY <= 0) continue;

                    if (overlapX < overlapY) {
                        // Разрешаем по горизонтали
                        if (p.x < pl.x) {
                            p.x -= overlapX;
                        } else {
                            p.x += overlapX;
                        }
                        p.vx = 0;
                    } else {
                        // Разрешаем по вертикали
                        if (p.y < pl.y) {
                            // Игрок сверху платформы — приземление
                            p.y = pl.y - p.h;
                            p.vy = 0;
                            p.onGround = true;
                        } else {
                            // Игрок снизу платформы — отталкиваем вниз
                            p.y = pl.y + pl.h;
                            p.vy = 0;
                        }
                    }
                }
                if(victoryCondition(player2, door2) == true && victoryCondition(player1, door1) == true){
            
                    Draw.text(100, 100, "Victory!", 50 );
                    return;
                }
            }

            Draw.clearScreen();

            Draw.setColor(150, 150, 150);
            for (Platform pl : platforms) pl.draw();

            Draw.setColor(0, 0, 255);
            player1.draw();

            Draw.setColor(0, 255, 0);
            player2.draw();

            for (Collectible c : collectibles) {
            c.draw();
            }

            Draw.setColor(0, 255, 0);
            door2.draw();

            Draw.setColor(0, 0, 255);
            door1.draw();

            Draw.syncToFrameRate(); 

            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }
}