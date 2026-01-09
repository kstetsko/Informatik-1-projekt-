public class Door {
    double x, y;
    double w = 100;
    double h = 60;

    public Door(double x, double y, double w, double h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void draw(){    
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }
}
