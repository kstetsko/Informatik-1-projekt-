



public class Collectible { 
    
    double x, y, size;
    int ownerId;       // 1 oder 2
    boolean collected = false;

    public Collectible(double x, double y, double size, int ownerId) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.ownerId = ownerId;

    }

    public void draw() {
    if (collected) return;

    if (ownerId == 1)
        Draw.setColor(0, 255, 0);   // Spieler 1
    else
        Draw.setColor(0, 0, 255); // Spieler 2

    Draw.filledRect(
    (int) x,
    (int) y,
    (int) size,
    (int) size
);
}
    public void checkCollect(Player p) {
        if (collected) return;
        if (p.id == ownerId) return; // ❗ falscher Spieler

        boolean hit =
            p.x < x + size &&
            p.x + p.w > x &&
            p.y < y + size &&
            p.y + p.h > y;

        if (hit) {
            collected = true;
            p.score++; 
        }
    }
    
    }


