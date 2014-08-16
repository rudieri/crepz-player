/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.gtk;

/**
 *
 * @author c90
 */
public class NativeGtkSystemTray {

    public native void addMenuItem(String text, int id);

    public static void menuAction(int id) {
        System.out.println("Teste " + id);
    }

    static {
        System.load("/home/c90/NetBeansProjects/Versionados/CrepzPlayer/src/com/utils/gtk/teste.o");
    }

    public static void main(String[] args) {
        NativeGtkSystemTray tray = new NativeGtkSystemTray();
        tray.addMenuItem("teste", 123);
    }
}
