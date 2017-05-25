package com.ripcm.microbiomeres.gui; /**
 this class describes properties and functions common to all humans
 */

import java.awt.*;

public abstract class Human {

    //Constructor
    public Human(double mb_resistance, int x, int y ) {
        this.mb_resistance = mb_resistance;
        this.x = x;
        this.y = y;
        setColor(208, 11, 26);
    }

    private int x, y;
    private double mb_resistance;
    int color_r, color_g, color_b;

    protected void set_mb_resistance(double mb_resistance) {
        this.mb_resistance = mb_resistance;
    }

    //methods to get/set value of variable
    protected void setX(int x) { this.x = x; }
    protected int getX() { return this.x; }
    protected void setY(int y) { this.y = y; }
    protected int getY() { return this.y; }
    protected void setColor(int r, int g, int b) {
        this.color_r = r;
        this.color_g = g;
        this.color_b = b;
    }
    protected int getColor_r() { return this.color_r; }
    protected int getColor_g() { return this.color_g; }
    protected int getColor_b() { return this.color_b; }
    protected double get_mb_resistance() { return this.mb_resistance; }


    //draw object; visual version
    public static void draw(Graphics c) {
        Graphics2D c2d = (Graphics2D) c;
        c2d.fillOval(50, 50, 10, 10);
    }

}





    /*
    //Constructor
    public Human(int x, int y) {
        this.x = x; this.y =y;

    }

    //coordinate[mkm]
    private int x, y;
    //color variable
    int color_r, color_g, color_b;
*/