/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.config.Configuracoes;
import com.musica.MusicaGerencia;
import com.serial.PortaCDs;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Scan implements Runnable {

    private static int tempoSegudos;
    private Thread thMonitor;
    private final int ESCALA_TEMPO = 1000;
    private HashMap<String, Long> cacheModArquivos;
    private boolean atualizarChache;

    public Scan() {
        this(10);
    }

    public Scan(int t) {
        try {
            cacheModArquivos = new HashMap<String, Long>(PortaCDs.getMusicas().size());
        } catch (Exception ex) {
            cacheModArquivos = new HashMap<String, Long>(100);
        }
        tempoSegudos = t;
        this.start();
    }

    public static Integer getTempo() {
        return tempoSegudos;
    }

    private void start() {
        thMonitor = new Thread(this);
        thMonitor.setPriority(Thread.MIN_PRIORITY);
        thMonitor.start();
    }

    @SuppressWarnings("SleepWhileInLoop")
    @Override
    public void run() {
        int contadorAtualizar = 0;
//        int contadorLimpar = 0;
//        Transacao t = new Transacao();
        while (true) {
            try {
                Thread.sleep(tempoSegudos * ESCALA_TEMPO);
                if (getPastas().isEmpty()) {
                    continue;
                } else {
                    System.out.println("Tenho " + getPastas().size() + " configuradas...");
                }
//                t.begin();
                for (int i = 0; i < getPastas().size(); i++) {
                    verificarModicicacoes(new File(getPastas().get(i)));
                }
                atualizarChache = false;
//                t.commit();
            } catch (Exception ex) {
                Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
//                t.rollback();
            }
//            if (contadorLimpar++ == 600) {
//                //TODO fazer isso
//                limparArquivosExcluido();
//            }
            if (contadorAtualizar++ == 15) {
                atualizarChache = true;
                contadorAtualizar = 0;
            }

        }

    }

    private ArrayList<String> getPastas() {
        return Configuracoes.PASTAS_SCANER.getValor();
    }

    private void verificarModicicacoes(File path) {
        try {
            boolean ehDiretorio = path.isDirectory();
            long ultimaModificacao = path.lastModified();
            if (ehDiretorio) {
                File[] files = path.listFiles();
                boolean contemDiretorio = false;
                for (int i = 0; !contemDiretorio && i < files.length; i++) {
                    contemDiretorio |= files[i].isDirectory();

                }
                if (contemDiretorio) {
                    for (File file : files) {
                        verificarModicicacoes(file);
                    }
                } else {
                    String nomeArq = path.getAbsolutePath();
                    Long maxDtModArquivo;
                    if (atualizarChache || (maxDtModArquivo = cacheModArquivos.get(nomeArq)) == null) {
                        maxDtModArquivo = PortaCDs.getMaxDtModArquivo(nomeArq, ehDiretorio);
                        cacheModArquivos.put(nomeArq, maxDtModArquivo);
                    }
                    if (ultimaModificacao > maxDtModArquivo) {
                        for (File file : files) {
                            verificarModicicacoes(file);
                        }
                    }
                }
            } else {
                if (MusicaGerencia.ehValido(path)) {
                    String nomeArq = path.getAbsolutePath();
                    Long maxDtModArquivo;
                    if (atualizarChache || (maxDtModArquivo = cacheModArquivos.get(nomeArq)) == null) {
                        maxDtModArquivo = PortaCDs.getMaxDtModArquivo(nomeArq, ehDiretorio);
                        cacheModArquivos.put(nomeArq, maxDtModArquivo);
                    }
                    if (ultimaModificacao > maxDtModArquivo) {
                        atualizarChache = true;
                        MusicaGerencia.addOneFile(path);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Scan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
