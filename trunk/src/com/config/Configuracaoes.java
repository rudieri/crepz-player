/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.musica.Musica;
import com.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class Configuracaoes {
    //Aqui!!!!!!!!

    /*
    Declare aqui todas as configurações com seus determinados tipos;
     * Fazer dessa forma:
    public static final Byte NOME_DA_CONFIG = ordinal_da_config;
    private static final Tipo nomeDaConfig = new Tipo();
     *
     */
    public static final Byte PASTAS_SCANER = 0;
    private static final ArrayList<String> pastasScaner = new ArrayList<String>(10);
    public static final Byte ACAO_PADRAO_FILA = 1;
    private static Integer acaoPadraoFila = 0;
    // lista de todas as configs
    private static final Object[] configs;
    private static final String ARQUIVO = "etc/conf";
    private static final HashMap<Byte, Acao> acoes;

    static {
        // inicializa a lista das configs
        configs = new Object[]{pastasScaner, acaoPadraoFila};
//        configs[PASTAS_SCANER] = pastasScaner;
        acoes = new HashMap<Byte, Acao>(configs.length);
        ler();
    }

    public static void set(Byte indexConf, String valor) {
        configs[indexConf] = valor;
        gravar();
    }

    public static void set(Byte indexConf, Integer valor) {
        configs[indexConf] = valor;
        gravar();
    }

    public static void set(Byte indexConf, ArrayList valor) {
        ((ArrayList) configs[indexConf]).clear();
        ((ArrayList) configs[indexConf]).addAll(valor);
        dispararAcao(indexConf, valor);
        gravar();
    }

    private static void dispararAcao(Byte config, ArrayList valor) {
        try {
            final Acao acao = acoes.get(config);
            if (acao == null) {
                return;
            }
            if (acao.isContemParametros()) {
                acao.getAcao().invoke(acao.getAlvo(), valor);
            } else {
                acao.getAcao().invoke(acao.getAlvo());
            }
        } catch (Exception ex) {
            Logger.getLogger(Configuracaoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Object getObject(Byte index) {
        return configs[index];
    }

    public static String getString(Byte index) {
        return (String) configs[index];
    }

    public static Integer getInteger(Byte index) {
        return (Integer) configs[index];
    }

    public static Musica getMusica(Byte index) {
        return (Musica) configs[index];
    }

    public static ArrayList<String> getList(Byte index) {
        return (ArrayList) configs[index];
    }

    private static void ler() {
        try {
            if (!new File(ARQUIVO).exists()) {
                return;
            }
            String conteudo = FileUtils.leArquivo(new File(ARQUIVO)).toString();
            String[] linhas = conteudo.split("\n");
            for (int i = 0; i < linhas.length; i++) {
                String linha = linhas[i];
                String[] tokens = linha.split(" ", 2);
                Object myConfig = configs[Integer.parseInt(tokens[0])];
                if (myConfig instanceof String) {
                    myConfig = tokens[1];
                } else if (myConfig instanceof ArrayList) {
                    String[] valores = tokens[1].split(";");
                    ((ArrayList) myConfig).clear();
                    ((ArrayList) myConfig).addAll(Arrays.asList(valores));
                } else if (myConfig instanceof Musica) {
                    Musica musica = new Musica();
                    musica.setId(Integer.parseInt(tokens[1].trim()));
                    myConfig = musica;
                } else if (myConfig instanceof Integer) {
                    myConfig = Integer.valueOf(tokens[1].trim());
                } else {
                    myConfig = tokens[1];
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(Configuracaoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void gravar() {
        StringBuilder textFile = new StringBuilder(1024);
        for (int i = 0; i < configs.length; i++) {
            Object myConfig = configs[i];
            textFile.append(i).append(' ');
            if (myConfig instanceof ArrayList) {
                textFile.append(((ArrayList) myConfig).toString().replaceAll("[\\[\\]]", ""));
            } else if (myConfig instanceof Musica) {
                textFile.append(((Musica) myConfig).getId());
            } else {
                textFile.append(myConfig.toString());
            }
            textFile.append('\n');
        }
        try {
            FileUtils.gravaArquivo(textFile, ARQUIVO);
        } catch (Exception ex) {
            Logger.getLogger(Configuracaoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
