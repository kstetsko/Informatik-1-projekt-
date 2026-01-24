public class Platform {
    double x, y, w, h;


    public Platform(double x, double y, double w, double h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void draw(){
        Draw.filledRect((int)x, (int)y, (int)w, (int)h);
    }
    public void update(double deltaTime) {
        // normale Plattform: tut nichts
    }
}  
