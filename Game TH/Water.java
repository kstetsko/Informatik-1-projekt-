public class Water extends Liquid {
    public Water(double x, double y, double w, double h){
        super(x, y, w, h , 0, 0, 255);
    }

    @Override
    void draw(){
        Draw.setColor(0, 0, 255);
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }

    boolean canPlayerEnter(Player player){
        return player.isPlayer1();
    }
}
