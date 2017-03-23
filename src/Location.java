/**
 * Created by vera on 20.04.16.
 */

import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.*;
//import java.util.List;

public class Location
{
    //Constructor
    public Location(int missX, int missY, int width, int heigth, Color color) {
        this.width = width;
        this.heigth = heigth;
        this.missX = missX;
        this.missY = missY;
        this.color = color;
    }

    //variables
    public static int number_of_people = 1;
    private static int width;
    private static int heigth;
    private static int missX;
    private static int missY;
    private static Color color;

    public int nHealthy;
    public int nIncPer;
    public int nAntTreated;

    public void setnHealthy(int n){this.nHealthy =n;}
    public int getnHealthy(){return this.nHealthy;}
    public void setnIncPer(int n){this.nIncPer =n;}
    public int getnIncPer(){return this.nIncPer;}
    public void setnAntTreated(int n){this.nAntTreated =n;}
    public int getnAntTreated(){return this.nAntTreated;}


    //draw objects and environment
    public void drawLocation(Graphics g) {
        g.setColor(color);
        g.drawRect ( missX, missY, width, heigth);
        for (int i = 0; i < nHealthy + nIncPer + nAntTreated; i++) {
            if (i<nHealthy){ g.setColor(new Color(240, 50, 100));}
            else {
                if (i < nHealthy + nIncPer) {
                    g.setColor(new Color(60, 60, 240));
                } else {
                    g.setColor(new Color(40, 200, 80));
                }
            }
            int w = (10 + i * 20) % width;
            int k = (10 + 20 * i - w) / heigth;
            int h = k * 20 + 10;
            g.fillOval(missX + w, missY + 5 + h, 5, 5);
        }
    }


}
