package com.hotkey.linux;

import com.musica.Musiquera;
import com.config.Configuracaoes;
import com.main.Carregador;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
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
public class Ouvinte {

    private final Musiquera musiquera;
    private Carregador carregador;

    public Ouvinte(Musiquera musiquera) {
        this.musiquera = musiquera;
        if (Configuracaoes.getBoolean(Configuracaoes.CONF_ATALHOS_GLOBAIS_ATIVOS)) {
            exportPath();
            initSocket();
        } else {
        }
    }

    private void exportPath() {
        try {
            String local = getClass().getResource("/").getFile();
            if (local.charAt(local.length() - 1) != '/') {
                local += "/";
            }

            Runtime.getRuntime().exec("sh " + getClass().getResource("").getFile() + "/export_var.sh " + local);
        } catch (IOException ex) {
            Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initSocket() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    initSocketRun();
                } catch (IOException ex) {
                    Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private void initSocketRun() throws IOException {
        ServerSocket server = new ServerSocket(3586);
        while (true) {
            final Socket accept = server.accept();
            try {
                BufferedReader bin = new BufferedReader(new InputStreamReader(accept.getInputStream()));
                String linha;
                while ((linha = bin.readLine()) != null) {
                    if (linha.equals("--play") || linha.equals("--pause")) {
                        musiquera.tocarPausar();
                    }
                    if (linha.equals("--stop")) {
                        musiquera.parar();
                    }
                    if (linha.equals("--next")) {
                        musiquera.tocarProxima();
                    }
                    if (linha.equals("--prev")) {
                        musiquera.tocarAnterior();
                    }
                    if (linha.equals("--open-crepz")) {
                        if (carregador != null) {
                            int r = JOptionPane.showConfirmDialog(null, "O crepz aparentemente está aberto... Se estamos enganados, clique em \"Sim\" para abrí-lo.");
                            if (r != JOptionPane.YES_OPTION) {
                                return;
                            }
                        }
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                carregador = new Carregador();
                            }
                        }).start();
                    }
                }


            } catch (IOException ex) {
                Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Aceito...");
        }
    }
}
