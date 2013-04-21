package com.hotkey.linux;

import com.config.Configuracaoes;
import com.main.Carregador;
import com.main.GerenciadorTelas;
import com.musica.Musiquera;
import com.playlist.JPlayList;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author rudieri
 */
public class Ouvinte implements Runnable {

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
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            initSocketRun();
        } catch (IOException ex) {
            Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                    if (linha.isEmpty()) {
                        continue;
                    }
                    System.out.println("Recebi o comando: " + linha);
                    int idxComando = linha.indexOf(' ');
                    // só 1 por enquanto
                    String[] arquivos = new String[]{linha.substring(idxComando + 1, linha.length())};

                    TipoComando porComando = TipoComando.getPorComando(linha.substring(0, idxComando));
                    for (int i = 0; i < arquivos.length; i++) {
                        String arq = arquivos[i];
                        System.out.println((porComando == null ? "null" : porComando.getComando()) + " " + arq);
                    }
                    switch (porComando) {
                        case ABRIR_CREPZ:

                            break;
                        case ADICIONAR_LISTA:
                            JPlayList playList = GerenciadorTelas.getPlayList(null);
//                            String[] musicas = new String[arquivos.length - 1];
//                            System.arraycopy(arquivos, 1, musicas, 0, arquivos.length - 1);
                            playList.importarMusicasParaPlayList(arquivos);
                            break;
                        case AVANCAR_MUSICA:
                            musiquera.tocarProxima();
                            break;
                        case VOLTAR_MUSICA:
                            musiquera.tocarAnterior();
                            break;
                        case OBTER_LISTA:
                            throw new UnsupportedOperationException("Não implementado ainda...");
                        case PARAR_MUSICA:
                            musiquera.parar();
                            break;
                        case PAUSAR_MUSICA:
                        case REPRODUZIR_MUSICA:
                            musiquera.tocarPausar();
                            break;

                        default:
                            throw new AssertionError();
                    }

                }
                bin.close();
                inputStreamReader.close();


            } catch (IOException ex) {
                Logger.getLogger(Ouvinte.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void retornarMsg(Socket socket, Serializable object) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();
        outputStream.close();
    }
}
