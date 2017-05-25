package com.ripcm.microbiomeres.gui;

import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/*class Rectangle extends JComponent {

    public Rectangle(int missX, int missY, int width, int higth) {
        this.width = width;
        this.higth = higth;
        this.missX = missX;
        this.missY = missY;

    }
    private static int width;
    private static int higth;
    private static int missX;
    private static int missY;

    public void paint(Graphics g) {
        g.drawRect ( missX, missY, width, higth);
    }
}*/
public class Rectangle extends JPanel {

    private static final int D_HEIGHT = 500;
    private static final int D_WIDTH = 400;
    private static final int INCREMENT = 8;
    private Timer timer = null;
    int l = 0;

    public Rectangle() {
        timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                l +=1;
 //               repaint();
            }
        });
        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                timer.start();
            }
        });

        JPanel panel = new JPanel();
        panel.add(start);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.PAGE_START);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawRect(100,100+l,100,100);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(D_WIDTH, D_HEIGHT);
    }

}