public class Lava extends Liquid {
    public Lava(double x, double y, double w, double h, int r, int g, int b){
        super(x, y, w, h , 255, 0, 0);
    }

    @Override
    void draw(){
        Draw.setColor(r, g, b);
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }

    boolean canPlayerEnter(Player player){
        // allow only the second player (not player1) into lava
        return !player.isPlayer1();
    }
}
    