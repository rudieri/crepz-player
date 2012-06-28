/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.config.constantes.AcaoPadraoFila;
import com.config.constantes.AcoesFilaVazia;
import com.config.constantes.AdicionarNaFilaVazia;
import com.config.constantes.TelaPadrao;
import com.musica.CacheDeMusica;
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
    public static final Byte NOME_DA_CONFIG = ordinal_da_config; // é sequencial, deve ser o indice da config no array
    private static final Tipo nomeDaConfig = new Tipo();
     *
     */
    // Config 0
    public static final Byte CONF_PASTAS_SCANER = 0;
    private static final ArrayList<String> pastasScaner = new ArrayList<String>(10);
    // Config 1
    public static final Byte CONF_ACAO_PADRAO_FILA = 1;
    private static AcaoPadraoFila acaoPadraoFila = AcaoPadraoFila.ADICIONAR_FILA;
    // Config 2
    public static final Byte CONF_ACOES_FILA_VAZIA = 2;
    private static AcoesFilaVazia acoesFilaVazia = AcoesFilaVazia.TOCAR_RANDOM;
    // Config 3
    public static final Byte CONF_ADICIONAR_NA_FILA_VAZIA = 3;
    private static AdicionarNaFilaVazia adicionarNaFilaVazia = AdicionarNaFilaVazia.REPRODUZIR_MUSICA;
    // Config 4
    public static final Byte CONF_TELA_PADRAO = 4;
    private static TelaPadrao telaPadrao = TelaPadrao.J_FILA;
    // Config 5
    public static final Byte CONF_ATALHOS_GLOBAIS_ATIVOS = 5;
    private static Boolean atalhosGlobaisAtivos = false;
    
    // lista de todas as configs
    private static final Object[] configs;
    private static final String ARQUIVO = "etc/conf";
    private static final HashMap<Byte, Acao> acoes;

    static {
        // inicializa a lista das configs
        configs = new Object[]{
            pastasScaner,
            acaoPadraoFila, 
            acoesFilaVazia,
            adicionarNaFilaVazia,
            telaPadrao,
            atalhosGlobaisAtivos
        
        };
//        configs[CONF_PASTAS_SCANER] = pastasScaner;
        acoes = new HashMap<Byte, Acao>(configs.length);
        ler();
    }

    public static void set(Byte indexConf, Enum valor) {
        configs[indexConf] = valor;
        gravar();
    }

    public static void set(Byte indexConf, String valor) {
        configs[indexConf] = valor;
        gravar();
    }

    public static void set(Byte indexConf, Integer valor) {
        configs[indexConf] = valor;
        gravar();
    }
    public static void set(Byte indexConf, boolean valor) {
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

    public static Enum getEnum(Byte index) {
        return (Enum) configs[index];
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
    public static boolean getBoolean(Byte index) {
        return (Boolean)configs[index];
    }

    private static void ler() {
        try {
            if (!new File(ARQUIVO).exists()) {
                return;
            }
            final StringBuilder conteudoBruto = FileUtils.leArquivo(new File(ARQUIVO));
            for (int i = conteudoBruto.length() - 1; i >= 0; i--) {
                if (conteudoBruto.charAt(i) == '\r') {
                    conteudoBruto.deleteCharAt(i);
                }
            }
            String conteudo = conteudoBruto.toString();
            String[] linhas = conteudo.split("\n");
            for (int i = 0; i < linhas.length; i++) {
                String linha = linhas[i];
                String[] tokens = linha.split(" ", 2);
                Object myConfig = configs[Integer.parseInt(tokens[0])];
                if (myConfig instanceof String) {
                    configs[Integer.parseInt(tokens[0])] = tokens[1];
                } else if (myConfig instanceof ArrayList) {
                    String[] valores = tokens[1].split(";");
                    ((ArrayList) myConfig).clear();
                    ((ArrayList) myConfig).addAll(Arrays.asList(valores));
                } else if (myConfig instanceof Musica) {
                    Musica musica = CacheDeMusica.get(Integer.parseInt(tokens[1].trim()));
                    configs[Integer.parseInt(tokens[0])] = musica;
                } else if (myConfig instanceof Integer) {
                    configs[Integer.parseInt(tokens[0])] = Integer.valueOf(tokens[1].trim());
                } else if (myConfig instanceof Enum) {
                    configs[Integer.parseInt(tokens[0])] = Enum.valueOf(((Enum) myConfig).getClass(), tokens[1]);
                } else if(myConfig instanceof Boolean){
                    configs[Integer.parseInt(tokens[0])] = Boolean.parseBoolean(tokens[1]);
                }else{
                    configs[Integer.parseInt(tokens[0])] = tokens[1];
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
            } else if (myConfig instanceof Enum) {
                textFile.append(((Enum) myConfig).name());
            } else if (myConfig instanceof String) {
                textFile.append(myConfig.toString());
            } else if(myConfig instanceof Boolean){
                textFile.append(myConfig.toString());
            }else{
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
