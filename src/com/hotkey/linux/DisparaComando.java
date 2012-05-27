package com.hotkey.linux;


import com.main.Carregador;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author rudieri
 */
public class DisparaComando {

    public static void main(String[] args) {
        try {
//            JOptionPane.showMessageDialog(null, "teste");
            if (args.length > 0) {
                final String meuComando = args[0];
//                Class.forName("")
                if (meuComando.equals("--open")) {
                    new Carregador();
//                    Runtime.getRuntime().exec(meuComando)
                }
                Socket socket = new Socket("127.0.0.1", 3586);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println(meuComando);
                printWriter.close();
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(DisparaComando.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DisparaComando.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }
}
