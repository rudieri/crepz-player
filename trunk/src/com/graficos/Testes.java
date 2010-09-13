/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.graficos;

import java.awt.Component;
import java.awt.Point;
import javax.swing.JComponent;

/**
 *
 * @author rudieri
 */
public class Testes {

    public static boolean hitTest(JComponent g1, JComponent g2) {
        boolean b1 = g1.getX() < g2.getX() + g2.getWidth() && g1.getX() + g1.getWidth() > g2.getX();

        boolean b2 = g1.getY() < g2.getY() + g2.getHeight() && g1.getY() + g1.getHeight() > g2.getY();

        return b1 && b2;
    }

    public static boolean hitTest(JComponent g1, Point p) {

        return g1.getX() <= p.getX() && g1.getX() + g1.getWidth() >= p.getX() && g1.getY() <= p.getY() && g1.getY() + g1.getHeight() >= p.getY();
    }

    public static boolean hitTest(Component g1, Point p) {

        return g1.getX() <= p.getX() && g1.getX() + g1.getWidth() >= p.getX() && g1.getY() <= p.getY() && g1.getY() + g1.getHeight() >= p.getY();
    }
}
