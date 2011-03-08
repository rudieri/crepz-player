/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author manchini
 */
public class Servidor implements Runnable {

    private ServerSocket ss;
    private boolean on = true;

    public Servidor() throws IOException {
        ss = new ServerSocket(4445);
        System.out.println("Servidor ouvindo na porta:" + 4444);

    }

    public void run() {
        try {
            while (on) {
                new TrataCliente(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }



    public Socket getAccept() throws IOException{
        return ss.accept();
    }

    public boolean isOn(){
        return on;
    }
}
