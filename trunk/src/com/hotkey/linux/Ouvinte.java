package com.hotkey.linux;

import com.config.Configuracoes;
import com.main.Carregador;
import com.main.GerenciadorTelas;
import com.musica.Musiquera;
import com.playlist.JPlayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Ouvinte implements Runnable {

    private final Musiquera musiquera;

    public Ouvinte(Musiquera musiquera) {
        this.musiquera = musiquera;
        if (Configuracoes.ATALHOS_GLOBAIS_ATIVOS.getValor()) {
            exportPath();
        }
        initSocket();
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
        new Thread(this, "Ouvinte - Comandos").start();
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
            Socket accept = server.accept();
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
                    TipoComando porComando;
                    if (idxComando == -1) {
                        porComando = TipoComando.getPorComando(linha);
                    } else {
                        porComando = TipoComando.getPorComando(linha.substring(0, idxComando));
                   }
                    switch (porComando) {
                        case ABRIR_CREPZ:

                            break;
                        case ADICIONAR_LISTA:
                            JPlayList playList = GerenciadorTelas.getPlayList(null);
//                            String[] musicas = new String[arquivos.length - 1];
//                            System.arraycopy(arquivos, 1, musicas, 0, arquivos.length - 1);
                            String[] arquivos = new String[]{linha.substring(idxComando + 1, linha.length())};
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
                        case PING:
                            break;
                        case TO_FRONT:
                            Carregador.getMe().getWindowPrincipal().toFront();
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
