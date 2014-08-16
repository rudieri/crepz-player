package com.hotkey.linux;

import java.awt.HeadlessException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author rudieri
 */
public class DisparaComando {

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[]{"--open"};
        }
        disparar(args[0]);
        System.exit(0);
    }

    /**
     * Dispara um camando...
     *
     * @param meuComando
     * @return true se conseguiu enviar o comando.
     */
    public static boolean disparar(Comando meuComando) throws HeadlessException {
        return disparar(meuComando.toString());
    }

    /**
     * Dispara um camando...
     *
     * @return true se conseguiu enviar o comando.
     */
    private static boolean disparar(String meuComando) throws HeadlessException {
        return disparar(meuComando, 0);
    }

    private static boolean disparar(String meuComando, int tentativas) throws HeadlessException {

        try {

            if (meuComando.equals("--open")) {
                String path = DisparaComando.class.getResource("/").getFile();
                String comando = "sh " + path + "open_crepz.sh";
                System.out.println("Abrir com o comando: " + comando);
                Runtime.getRuntime().exec(comando);
                return true;
            }
            Socket socket = new Socket("127.0.0.1", 3586);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(meuComando);
            printWriter.close();
            socket.close();
            return true;
        } catch (Exception ex) {
            if (tentativas < 3) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(DisparaComando.class.getName()).log(Level.SEVERE, null, ex1);
                }
                disparar(meuComando, tentativas + 1);
            } else {
                if (!meuComando.equals(TipoComando.PING.getComando())) {
                    Logger.getLogger(DisparaComando.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, ex);
                    
                }
            }
        }
        return false;
    }
}
