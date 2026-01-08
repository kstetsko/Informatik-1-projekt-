public class Acid extends Liquid {
    public Acid(double x, double y, double w, double h){
        super(x, y, w, h , 0, 255, 0);
    }

    @Override
    void draw(){
        Draw.setColor(0, 255, 0);
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }

    boolean canPlayerEnter(Player player){
        // forbid both players to enter acid
        return false;
    }
    
}
