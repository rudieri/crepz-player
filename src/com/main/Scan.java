/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.conexao.Transacao;
import com.config.Configuracaoes;
import com.musica.MusicaBD;
import com.musica.MusicaGerencia;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Scan {

    private static int tempoSegudos;
    Thread thMonitor;
    private final int ESCALA_TEMPO = 1000;
    private HashMap<String, Long> cacheModArquivos;
    private boolean atualizarChache;

    public Scan() {
        this(10);
    }

    public Scan(int t) {
        try {
            cacheModArquivos = new HashMap<String, Long>(MusicaBD.contarMusicas());
        } catch (Exception ex) {
            cacheModArquivos = new HashMap<String, Long>(100);
        }
        tempoSegudos = t;
//        pastas.add(teste.getAbsolutePath());
        this.start();
    }

//    public static void setPastas(ArrayList<String> dirs) {
//        System.out.println("Novs locais definidos para o scan:");
//        for (int i = 0; i < dirs.size(); i++) {
//            System.out.println("L" + i + ": " + dirs.get(i));
//        }
//        pastas = dirs;
//    }
//    public static ArrayList<String> getPastas() {
//        return pastas;
//    }
//    public static void setTempo(int t) {
//
//        tempoSegudos = t;
//        System.out.println("Novo tepo: " + tempoSegudos);
//    }
    public static Integer getTempo() {
        return tempoSegudos;
    }

    private void start() {
//        t = new Transacao();
//        try {
//            t.begin();
//        } catch (Exception ex) {
//            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
//        }

        thMonitor = new Thread(new Runnable() {

            int count = 0;

            @SuppressWarnings("SleepWhileInLoop")
            @Override
            public void run() {
                Transacao t = new Transacao();
                while (true) {
                    try {
                        Thread.sleep(tempoSegudos * ESCALA_TEMPO);
                        if (getPastas().isEmpty()) {
                            continue;
                        }else{
                            System.out.println("Tenho " + getPastas().size() + " configuradas...");
                        }
                        t.begin();
                        for (int i = 0; i < getPastas().size(); i++) {
                            verificarModicicacoes(new File(getPastas().get(i)), t);
                        }
                        atualizarChache = false;
                        t.commit();
                    } catch (Exception ex) {
                        Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
                        t.rollback();
                    }
                    if (count == 5) {
                        //TODO fazer isso
//                        limparNaoExistentes();
                    }
                    if (count++ == 10) {
                        atualizarChache = true;
                        count = 0;
                    }

                }

            }

            private ArrayList<String> getPastas() {
                return Configuracaoes.getList(Configuracaoes.CONF_PASTAS_SCANER);
            }
        });
        thMonitor.setPriority(Thread.MIN_PRIORITY);
        thMonitor.start();
    }

    private void verificarModicicacoes(File path, Transacao t) {
        try {
            boolean ehDiretorio = path.isDirectory();
            //TODO verificar datade modificação
            if (ehDiretorio) {
                long ultimaModificacao = path.lastModified();
                File[] files = path.listFiles();
                boolean contemDiretorio = false;
                for (int i = 0; !contemDiretorio && i < files.length; i++) {
                    contemDiretorio |= files[i].isDirectory();

                }
                if (contemDiretorio) {
                    for (File file : files) {
                        verificarModicicacoes(file, t);
                    }
                } else {
                    String nomeArq = path.getAbsolutePath();
                    Long maxDtModArquivo;
                    if (atualizarChache || (maxDtModArquivo = cacheModArquivos.get(nomeArq)) == null) {
                        maxDtModArquivo = MusicaBD.getMaxDtModArquivo(nomeArq, ehDiretorio, t);
                        cacheModArquivos.put(nomeArq, maxDtModArquivo);
                    }
                    if (ultimaModificacao > maxDtModArquivo) {
                        for (File file : files) {
                            verificarModicicacoes(file, t);
                        }
                    }
                }
            } else {
                long ultimaModificacao = path.lastModified();
                if (MusicaGerencia.ehValido(path)) {
                    String nomeArq = path.getAbsolutePath();
                    Long maxDtModArquivo;
                    if (atualizarChache || (maxDtModArquivo = cacheModArquivos.get(nomeArq)) == null) {
                        maxDtModArquivo = MusicaBD.getMaxDtModArquivo(nomeArq, ehDiretorio, t);
                        cacheModArquivos.put(nomeArq, maxDtModArquivo);
                    }
                    if (ultimaModificacao > maxDtModArquivo) {
                        atualizarChache = true;
                        MusicaGerencia.addFiles(path, t);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
