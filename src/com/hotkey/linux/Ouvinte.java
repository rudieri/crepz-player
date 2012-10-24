package com.hotkey.linux;

import com.config.Configuracaoes;
import com.main.Carregador;
import com.musica.Musiquera;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
                InputStreamReader inputStreamReader = new InputStreamReader(accept.getInputStream());
                BufferedReader bin = new BufferedReader(inputStreamReader);
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
                    if (linha.equals("--list-music")) {
                        throw new UnsupportedOperationException("Não implementado ainda...");
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
                bin.close();
                inputStreamReader.close();


            } catch (IOException ex) {
                Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Aceito...");
        }
    }
    
    public void retornarMsg(Socket socket, Serializable object) throws IOException{
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
        outputStream.close();
    }
}