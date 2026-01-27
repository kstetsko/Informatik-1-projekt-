public class Platform {
        // gemeinsame Textur für alle Plattformen
    private static int[][] texture;

    // statischer Block: wird einmal beim Laden der Klasse ausgeführt
    static {
        texture = Draw.loadImage("assets/wooden_tiles00.png");
        if (texture == null) {
            System.out.println("Konnte platformtextur nicht laden!");
        }
    }

    double x, y, w, h;


    public Platform(double x, double y, double w, double h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

         public Platform(int i, int j, int k, int l, String string) {
        //TODO Auto-generated constructor stub
    }

         void draw() {

        // Wenn eine Textur gesetzt ist → kacheln
        if (texture != null) {
            int texH = texture.length;
            int texW = texture[0].length;

            int startX = (int)x;
            int startY = (int)y;

            // Wie viele ganze Tiles passen rein?
            int tilesX = (int)(w / texW);
            int tilesY = (int)(h / texH);

            if (tilesX < 1) tilesX = 1;
            if (tilesY < 1) tilesY = 1;

            for (int iy = 0; iy < tilesY; iy++) {
                for (int ix = 0; ix < tilesX; ix++) {
                    int tx = startX + ix * texW;
                    int ty = startY + iy * texH;
                    Draw.blendImage(tx, ty, texture, false);
                }
            }
        } else {
            // Fallback: einfarbige Plattform
            Draw.filledRect((int)x, (int)y, (int)w, (int)h);
        }
    }

}  
