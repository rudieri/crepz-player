package com.hotkey.linux;

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
            if (args.length == 0) {
                args = new String[]{"--open"};
            }
            if (args.length > 0) {
                final String meuComando = args[0];
//                Class.forName("")
                if (meuComando.equals("--open")) {
                    String path = DisparaComando.class.getResource("/").getFile();
                    String comando = "sh " + path + "open_crepz.sh";
                    System.out.println("Abrir com o comando: " + comando);
                    Runtime.getRuntime().exec(comando);
                    return;
//                    Runtime.getRuntime().exec(meuComando)
                }
                Socket socket = new Socket("127.0.0.1", 3586);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                printWriter.println(meuComando);
                printWriter.close();
                socket.close();
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
