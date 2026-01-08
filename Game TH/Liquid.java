abstract class Liquid {
    abstract boolean canPlayerEnter(Player player);

    double x, y, w, h;
    int r, g, b;

    Liquid(double x, double y, double w, double h, int r, int g, int b){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    void draw(){
        Draw.setColor(r, g, b);
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }

     // intercection liquid player check
    public static boolean intersects(Player p, Liquid liquid){
        return p.x < liquid.x + liquid.w &&
               p.x + p.w > liquid.x &&
               p.y < liquid.y + liquid.h &&
               p.y + p.h > liquid.y;
    }

    // Handle player interaction with this liquid: if player is inside
    // and not allowed, teleport them back to their start position.
    public void handlePlayer(Player player){
        if (!Liquid.intersects(player, this)) return;

        if (canPlayerEnter(player)){
            // player is allowed in this liquid — no action here
            return;
        }

        // teleport player back to their start
        player.resetToStart();
    }
    
}
