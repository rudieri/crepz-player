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
public class GtkSystemTray{
     public static void main(String[] args) {
        NativeGtkSystemTray tray = new NativeGtkSystemTray();
        tray.addMenuItem("teste", 123);
    }
}
