import java.awt.event.KeyEvent;

public class Game {

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
        int[][] ground = Draw.loadImage("assets/dirt_floor.png");
if (ground == null) {
    System.out.println("Konnte dirt_floor.png nicht laden!");
}

        int[][] background = Draw.loadImage("assets/background.png");
        // players
    // Player 1
    Player player1 = new Player(
    100, 800, 32, 48,
    KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP
    );

    // Player 2 
    Player player2 = new Player(
    300, 800, 32, 48,
    KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W,
    "girl_running_frames"
    );

        // Platforms
        //Platform floor = new Platform(0, 968, 1000, 32); 
        Platform platform1 = new Platform(480, 900, 576, 32);   
        Platform platform2 = new Platform(50, 800, 448, 32);  
        Platform platform3 = new Platform(550, 700, 192, 32);
        MovingPlattform movingPlattform =new MovingPlattform(350.0, 550.0, 160.0, 32.0, 750.0, 150.0, 180.0, 770, 130, 30);

 
        //Doors 
        Door door1 = new Door(850, 840, 60, 60);
        Door door2 = new Door(700, 640, 60, 60);

        // Lists of platforms and players
        Player[] players = new Player[] { player1, player2 };
        Platform[] platforms = new Platform[] { platform1, platform2, platform3, movingPlattform };
        Door[] doors = new Door[] { door1, door2 };
        double lastTime = System.nanoTime() / 1e9;
        boolean debugHitboxes = true; // auf false setzen = aus

        while (true) {
            double currentTime = System.nanoTime() / 1e9;
            double dt = currentTime - lastTime;
            lastTime = currentTime;

            // 1. Spieler updaten (Input + Physik + Animation)
            for (Player p : players) {
                p.handleInput();  // Tasten → vx / vy
                p.update(dt);     // Physik + Animation
            }
            // 2. Moving Plattformen updaten
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

            // 3. Kollision Spieler vs. alle Plattformen
            for (Player p : players) {
                p.onGround = false; // standardmäßig in der Luft

                for (Platform pl : platforms) {
                    if (!intersects(p, pl)) continue;

                    double overlapX = Math.min(p.x + p.w, pl.x + pl.w) - Math.max(p.x, pl.x);
                    double overlapY = Math.min(p.y + p.h, pl.y + pl.h) - Math.max(p.y, pl.y);

                    if (overlapX <= 0 || overlapY <= 0) continue;

                    if (overlapX < overlapY) {
                        // horizontal auflösen
                        if (p.x < pl.x) {
                            p.x -= overlapX;
                        } else {
                            p.x += overlapX;
                        }
                        p.vx = 0;
                    } else {
                        // vertikal auflösen
                        if (p.y < pl.y) {
                            // Spieler steht auf Plattform
                            p.y = pl.y - p.h;
                            p.vy = 0;
                            p.onGround = true;
                        } else {
                            // Spieler unter der Plattform
                            p.y = pl.y + pl.h;
                            p.vy = 0;
                        }
                    }
                }
                int floorY = 962; // HÖHE DES BODENS → an deine Welt anpassen
    if (p.y + p.h > floorY) {
        p.y = floorY - p.h;   // auf den Boden stellen
        if (p.vy > 0) {       
            p.vy = 0;
        }
        p.onGround = true;
    }


                // Victory-Bedingung prüfen
                if (victoryCondition(player2, door2) && victoryCondition(player1, door1)) {
                    Draw.text(100, 100, "Victory!", 50);
                    return;
                }
            }

            // Zeichnen
Draw.clearScreen();

// Hintergrund
Draw.blendImage(0, 0, background, false);

// --- Erd-Boden Textur
if (ground != null) {
    int tileH = ground.length;
    int tileW = ground[0].length;

    int floorY = 968; // Y-Position des Bodens

    for (int x = 0; x < width; x += tileW) {
        Draw.blendImage(x, floorY, ground, false);
    }
}

// Plattformen
Draw.setColor(150, 150, 150);
for (Platform pl : platforms) {
    pl.draw();
}

// Spieler (benutzen jetzt ihre eigenen Sprites)
player1.draw();
player2.draw();

if (debugHitboxes) {
    Draw.setColor(255, 0, 0); // rot

    // Hitbox Player 1
    Draw.rect((int)player1.x, (int)player1.y, (int)player1.w, (int)player1.h);

    // Hitbox Player 2
    Draw.rect((int)player2.x, (int)player2.y, (int)player2.w, (int)player2.h);
}

// Türen
Draw.setColor(0, 255, 0);
door2.draw();

Draw.setColor(0, 0, 255);
door1.draw();

Draw.syncToFrameRate();


            try {
                Thread.sleep(16);
            } catch (Exception e) {}
        }

    }
}
