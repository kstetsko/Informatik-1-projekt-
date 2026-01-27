public class MovingPlattform extends Platform{
    private double startY;
    private double endY;
    private double speed;
    private boolean movingDown = true;

    private double triggerX;
    private double triggerY;
    private double triggerW;
    private double triggerH;
    public boolean active = false;

    public MovingPlattform(double x, double y, double w, double h,
                          double endY, double speed, double triggerX, double triggerY,
                          double triggerW, double triggerH) {
        super(x, y, w, h);
        this.startY = y;
        this.endY = endY;
        this.speed = speed;
        this.triggerX = triggerX;
        this.triggerY = triggerY;
        this.triggerW = triggerW;
        this.triggerH = triggerH;
    }
    public void checkTrigger(Player p) {
    boolean inside =
        p.x < triggerX + triggerW &&
        p.x + p.w > triggerX &&
        p.y < triggerY + triggerH &&
        p.y + p.h > triggerY;

    if (inside) {
        active = true;
    }
}

@Override
    void draw() {
        super.draw();   // Plattform normal zeichnen

        // DEBUG: Trigger anzeigen
        Draw.setColor(255, 0, 100);
        Draw.filledRect(
            (int) triggerX,
            (int) triggerY,
            (int) triggerW,
            (int) triggerH
        );
    }

    public void update(double deltaTime) {
        if (!active) return;
        
        if (movingDown) {
            y += speed * deltaTime;
            if (y >= endY) {
                y = endY;
                movingDown = false;
            }
        } else {
            y -= speed * deltaTime;
            if (y <= startY) {
                y = startY;
                movingDown = true;
            }
        }
    }
}

