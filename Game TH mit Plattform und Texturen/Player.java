import java.awt.event.KeyEvent;

public class Player {
  
    double x, y; // place 
    double w = 32, h = 48; // size 

    double vx = 0; 
    double vy = 0; // x, y speed

    double speed = 300; // px pro sec
    double g = 900;     // px pro sec*sec (positive -> pulls down)

    boolean onGround = false;
    boolean facingLeft = false; // Blickrichtung: false = rechts, true = links

    int keyLeft;
    int keyRight;
    int keyUp;

    // Ordner mit den Animationsframes
    String framesFolder;

    // ──────────────── Animation ────────────────
    private int[][][] frames;      // Bild-Frames
    private int currentFrame = 6;  // aktueller Frame
    private long lastFrameTime = 0;
    private int frameDelay = 25;   // ms pro Frame (~28 FPS, schön flüssig)

    // Standard-Konstruktor
    public Player(double x, double y, double w, double h , int keyLeft, int keyRight, int keyUp) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;

        // Standard-Ordner für Spieler
        this.framesFolder = "boy_running_frames";

        loadFrames();
    }

    // Konstruktor mit Sprite-Ordner
    public Player(double x, double y, double w, double h ,
                  int keyLeft, int keyRight, int keyUp,
                  String framesFolder) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;

        this.framesFolder = "girl_running_frames";

        loadFrames();
    }

    // ───────────────────────────────────────────
    // ANIMATIONSFRAMES LADEN
    
    private void loadFrames() {
        java.io.File folder = new java.io.File(framesFolder);
        System.out.println("Suche Bilder in: " + folder.getAbsolutePath());

        java.io.File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (files == null || files.length == 0) {
            System.out.println("Keine PNG-Bilder im Ordner gefunden!");
            return;
        }

        java.util.Arrays.sort(files); // alphabetisch sortieren

        frames = new int[files.length][][];

        for (int i = 0; i < files.length; i++) {
            frames[i] = Draw.loadImage(folder.getPath() + "/" + files[i].getName());
            System.out.println("Geladen: " + files[i].getName());
        }
    }

    // ───────────────────────────────────────────
    // Physik + Animation

    void update(double dt){
        // 1. Schwerkraft
        vy += g * dt;

        // 2. Position updaten
        x += vx * dt;
        y += vy * dt;

        // KEINE Boden-Kollision hier!
        // onGround und Kollision werden in Game.java gemacht

        // 3. Richtung bestimmen
        if (vx < 0) {
            facingLeft = true;   // läuft nach links
        } else if (vx > 0) {
            facingLeft = false;  // läuft nach rechts
        }

        // 4. Animation: bei Bewegung in irgendeine Richtung
        boolean movingHorizontally = vx != 0;

        if (movingHorizontally && frames != null && frames.length > 0) {
            long now = System.currentTimeMillis();
            if (now - lastFrameTime > frameDelay) {
                currentFrame = (currentFrame + 1) % frames.length;
                lastFrameTime = now;
            }
        } else if (frames != null && frames.length > 0) {
            currentFrame = 0; // Idle Frame, wenn nicht bewegt
        }
    }

    // ───────────────────────────────────────────
    // INPUT
    // ───────────────────────────────────────────
    void handleInput(){

        vx = 0;

        if (Draw.isKeyDown(keyLeft)){
            vx -= speed;
        }

        if (Draw.isKeyDown(keyRight)){
            vx += speed;
        }

        if (Draw.isKeyDown(keyUp) && onGround){
            // negative vy: nach oben springen
            vy = -450;
        }
    }

    // ───────────────────────────────────────────
    // DRAW
    // ───────────────────────────────────────────
    void draw(){

        if (frames == null || frames.length == 0) {
            // Fallback: kleines Debug-Rechteck, falls keine Bilder geladen
            Draw.setColor(255, 0, 0);
            Draw.filledRect((int)x, (int)y, (int)w, (int)h);
            return;
        }

        int sx = (int)x;
        int sy = (int)y;

        // Bildschirmgröße: 1000 x 1000 (aus Game.java)
        int screenW = 1000;
        int screenH = 1000;

        // Wenn komplett außerhalb des Screens → gar nicht zeichnen
        if (sx + w < 0 || sx >= screenW || sy + h < 0 || sy >= screenH) {
            return;
        }

        Draw.blendImage(sx, sy, frames[currentFrame], facingLeft);
    }
}
