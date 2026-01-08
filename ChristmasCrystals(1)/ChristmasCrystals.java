/**
 * MIT License
 *
 * Copyright (c) 2024 Arnulph Fuhrmann (TH Koeln)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.event.KeyEvent;
import javax.sound.sampled.Clip;

 public class ChristmasCrystals {
	
	private static int tileSize = 64;
	private static int tilesX   = 16;
	private static int tilesY   = 12;

    private static int[][][] idle;
    private static int[][][] walk;
    private static int[][][] attack;
    private static int[][] backGround;
    private static int[][] tree;
    private static int[][] crystal;
    private static int[][] sign;
    private static int[][][] tileImages;
    
    private static Clip clip;

    private static int width    = tilesX*tileSize;  // 1024
    private static int height   = tilesY*tileSize; //  768
    
    private static int[][] tiles = {{ 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 0,  0, 11, 12, 12, 13,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 11, 12},
                                    {13,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 11, 13,  0,  0,  0,  0},
                                    { 6,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
                                    { 8,  3,  3,  3,  6,  0,  0,  5,  3,  3,  3,  6,  0,  0,  5,  3},
                                    { 4,  4,  4,  4, 10,  1,  1,  9,  4,  4,  4, 10,  1,  1,  9,  4},
                                    { 4,  4,  4,  4, 10,  2,  2,  9,  4,  4,  4,  4,  4,  4,  4,  4},
                                    { 4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4}};
                                                
    // two consecutive array entries are the x and y positions of one crystal.
    private static int[] crystalPositions = {2, 2, 4, 7, 8, 7, 15, 3};
    private static boolean[] collectedCrystals = new boolean[crystalPositions.length/2];

    private static int current = 0;

    // starting position of the character in pixels
    private static int characterPosX = 145;
    private static int characterPosY = 8*tileSize - 1;
    
    // Pixel position of the center of the character in x-direction (see Lecture Slides for a diagramm).
    private static int characterCenterX = 116;

    // Pixel position of the lowest visible part of the character (needed, since the image file is larger).
    private static int characterBottomY = 145;

    // some character variables
    private static boolean mirror  = false;
    private static boolean jumping = false;
    private static boolean walking = false;        
    private static double velocity = 0;

    // game state variables
    private static int numCrystals = 0;
    private static boolean completed = false;
    private static boolean jumpCheat = false;

    private static int frame = 0;

    public static void main(String[] args) {
        
        initDraw();
        loadAssets();
        
        while(true) {
            
            frame++;
            int deltaX = handleInput();
            moveCharacterAndHandleCollisions(deltaX);
            collectCrystals();
			            
            drawBackgroundTileAndObjects();            
            drawLevelProgressDependentStuff();			
            drawSanta();
            drawTopObjects();

            Draw.syncToFrameRate();
            Draw.clearScreen();
        }
    }

    private static void initDraw() {
        Draw.init(width, height, "Christmas Crystals");
        
        // This font should be available on all platforms. Can be used as a fallback.
        //Draw.setFont("Impact");        
        Draw.registerFont("res_fonts/SuperCartoon-6R791.ttf");
        Draw.setFont("Super Cartoon");        
        Draw.enableDoubleBuffering(true);
    }

    private static void loadAssets() {
        String res = "res_christmas/";
        idle       = loadAnimation(res, "Idle", 16);
        walk       = loadAnimation(res, "Walk", 13);
        attack     = loadAnimation(res, "Jump", 16);        
        backGround = Draw.loadImage(res + "backGround1024.jpg");
        tree       = Draw.loadImage(res + "Tree_1.png");
        crystal    = Draw.loadImage(res + "Crystal.png");
        sign       = Draw.loadImage(res + "Sign_2.png");
        tileImages = loadAnimation(res, "Floor", 13);
        
        clip = Draw.loadSound("res_sound/hitFloor.wav");
    }
	
    private static int handleInput() {
        int deltaX = 0;
        int typedKey = Draw.getLastTypedKeyCode();
        if(typedKey == KeyEvent.VK_J) {
            jumpCheat = !jumpCheat;
        }
        if(typedKey == KeyEvent.VK_SPACE) {
            if(!jumping) {
                jumping = true;
                velocity = jumpCheat ? -70 : -60;
                current = 0;
            }
        }
        if(Draw.isKeyDown(KeyEvent.VK_RIGHT)) {
            mirror = false;
            deltaX = 2;
            if(jumping) deltaX = 3;
            if(!walking) current = 0;
            walking = true;
        }
        else if(Draw.isKeyDown(KeyEvent.VK_LEFT)) {
            mirror = true;
            deltaX = -2;
            if(jumping) deltaX = -3;
            if(!walking) current = 0;
            walking = true;
        }
        else {
            walking = false;
        }
        return deltaX;
    }

    private static void moveCharacterAndHandleCollisions(int deltaX) {

        int deltaY = 0;
        if(jumping) {                
            double delta_t = 0.2;
            velocity = velocity + 9.81 * delta_t;
            deltaY = (int)(velocity * delta_t); 
        }
        
        // check if we land in a solid tile, when applying deltaX
        if(isCollidingWithASolidTile(tiles, characterPosY, characterPosX + deltaX)) {
            deltaX = 0;
        }
        
        if(jumping) {
            // check for collisions when jumping up or down, i.e when applying deltaY
            if(isCollidingWithASolidTile(tiles, characterPosY+deltaY, characterPosX)) {
                deltaY =  tileSize - (characterPosY % tileSize) - 1;
                jumping = false;
                current = 0;    
                velocity = 0; 
                Draw.playSound(clip);          
            }
        }
        else {
            // check if character reached floor, by testing going one pixel down
            if(!isCollidingWithASolidTile(tiles, characterPosY+1, characterPosX)) {
                jumping = true;
                velocity = 0;
            }				
        }

        characterPosX += deltaX;
        characterPosY += deltaY;
        
        // Check collisions with screen borders (left, right and top). Down is handled by tiles implicitly.
        if(characterPosX < 35) characterPosX = 35;
        if(characterPosX > width-35) characterPosX = width-35;
        if(characterPosY < characterBottomY) characterPosY = characterBottomY;
    }


    /**
	 * Checks, if the given position is inside a solid tile.
	 */
	public static boolean isCollidingWithASolidTile(int[][] tiles, int posY, int posX) {
		int collisionTile = tiles[ posY / tileSize][ posX / tileSize];
		
		// Tiles 1 and 2 are water. All others are solid.
		return collisionTile > 2;
	}
       
    public static int[][][] loadAnimation(String dir, String name, int numFrames) {
        int[][][] images = new int[numFrames][][];
        for( int i = 0; i < numFrames; i++ ){
            images[i] = Draw.loadImage(dir + name + " " + (i+1) + ".png");
        }          
        return images;
    }  

       
    private static void collectCrystals() {
        for(int i=0; i<collectedCrystals.length; i++) {
            if(!collectedCrystals[i]) {
                // check if character is nearby a crystal.
                if(characterPosY/tileSize == crystalPositions[2*i+1] && Math.abs(characterPosX - crystalPositions[2*i] * tileSize - tileSize/2) < 10 ) {
                    collectedCrystals[i] = true;
                    numCrystals++;                        
                }                    
            }
        }            
    }

    private static void drawBackgroundTileAndObjects() {
        // background
        Draw.blendImage(0, 0, backGround, false);
        
        // draw level tiles
        for(int y=0; y<tilesY; y++) {
            for(int x=0; x<tilesX; x++) {
                int currentTile = tiles[y][x];
                if(currentTile != 0) {
                    Draw.blendImage(x*tileSize, y*tileSize, tileImages[currentTile-1], false);
                }
            }
        }
        
        // draw uncollected crystals
        for(int i=0; i<crystalPositions.length/2; i++) {
            if(!collectedCrystals[i]) {
                Draw.blendImage(crystalPositions[i*2]*tileSize, crystalPositions[i*2+1]*tileSize, crystal, false);
            }
        }
    }

    private static void drawLevelProgressDependentStuff() {
        // draw exit sign and handle level completion
        if(numCrystals == collectedCrystals.length) {
            Draw.blendImage(14*tileSize+20, 8*tileSize-sign.length, sign, false);
            if(characterPosX/tileSize == 15 && characterPosY/tileSize == 7 || completed) {
                Draw.setColor(55, 213, 97);
                Draw.text(0, height/2-40, "Level completed", 76, width);
                completed = true;
            }   
        }

        if(frame < 70) {
            Draw.setColor(55, 213, 97);
            Draw.text(0, height/2-30, "Welcome", 136, width);
        }
    }

    private static void drawSanta() {
        int imageDelta = mirror ? -27 : 0; // correct centering of sprite            
        int[][] currentImage;            
        current++; // advance key frames
        
        if(walking && !jumping)
            currentImage =   walk[(current/4) % walk.length];
        else if(jumping)
            currentImage = attack[(current/5) % attack.length];
        else
            currentImage =   idle[(current/3) % idle.length];
        Draw.blendImage(characterPosX-characterCenterX+imageDelta, characterPosY-characterBottomY, currentImage, mirror);
    }

    private static void drawTopObjects() {
        // draw tree
        Draw.blendImage(3*tileSize, 3*tileSize - tree.length , tree, false);

        if(jumpCheat) {
            Draw.setColor(55, 213, 97); 
            Draw.text(width-200, 25, "Jump cheat active", 20);                
        }
    }
}