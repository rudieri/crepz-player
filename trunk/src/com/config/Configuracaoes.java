/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.config.constantes.AcaoPadraoFila;
import com.config.constantes.AcoesFilaVazia;
import com.config.constantes.AdicionarNaFilaVazia;
import com.config.constantes.TelaPadrao;
import com.main.FonteReproducao;
import com.musica.CacheDeMusica;
import com.musica.Musica;
import com.utils.file.FileUtils;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
     private static  Tipo nomeDaConfig = new Tipo(); // Valor padrão
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
    // Config 6
    public static final Byte CONF_RANDOM_ATIVO = 6;
    private static Boolean randomAtivo = false;
    // Config 7
    public static final Byte CONF_REPEAT_ATIVO = 7;
    private static Boolean repeatAtivo = false;
    // Config 8
    public static final Byte CONF_VISIB_PRINCIPAL = 8;
    private static Boolean visibPrincipal = false;
    // Config 9
    public static final Byte CONF_VISIB_MINI = 9;
    private static Boolean visibMini = false;
    // Config 10
    public static final Byte CONF_VISIB_PLAYLIST = 10;
    private static Boolean visibPlayList = false;
    // Config 11
    public static final Byte CONF_VISIB_BIBLIOTECA = 11;
    private static Boolean visibBiblioteca = false;
    // Config 12
    public static final Byte CONF_VISIB_FILA = 12;
    private static Boolean visibFila = false;
    // Config 13
    public static final Byte CONF_LISTA_ABERTA = 13;
    private static Integer listaAberta = -1;
    // Config 14
    public static final Byte CONF_FONTE_REPRODUCAO = 14;
    private static FonteReproducao fonteReproducao = FonteReproducao.AVULSO;
    // Config 15
    public static final Byte CONF_PELES = 15;
    private static ArrayList<String> peles = new ArrayList<String>();
    // Config 16
    public static final Byte CONF_PELE_ATUAL = 16;
    private static String peleAtual = "";
    // Config 17
    public static final Byte CONF_MUSICA_CONTINUA_ONDE_PAROU = 17;
    private static Boolean musicaContinuaOndeParou = true;
    // Config 18
    public static final Byte CONF_MUSICA_REPRODUZINDO = 18;
    private static Integer musicaReproduzindo = -1;
    // Config 19
    public static final Byte CONF_MUSICA_REPRODUZINDO_TEMPO = 19;
    private static Long musicaReproduzindoTempo = -1l;
    // Config 20
    public static final Byte CONF_VOLUME = 20;
    private static Byte volume = 50;
    // Config 21
    public static final Byte CONF_BALANCO = 21;
    private static Byte balanco = 50;
    // Config 22
    public static final Byte CONF_LOCAL_PRINCIPAL = 22;
    private static Rectangle localPrincipal = new Rectangle();
    // Config 23
    public static final Byte CONF_LOCAL_MINI = 23;
    private static Rectangle localMini = new Rectangle();
    // Config 24
    public static final Byte CONF_LOCAL_FILA = 24;
    private static Rectangle localFila = new Rectangle();
    // Config 25
    public static final Byte CONF_LOCAL_PLAYLIST = 25;
    private static Rectangle localPlayList = new Rectangle();
    // Config 26
    public static final Byte CONF_LOCAL_BIBLIOTECA = 26;
    private static Rectangle localBiblioteca = new Rectangle();
    // lista de todas as configs
    private static final Object[] configs;
    private static final String ARQUIVO = "etc/conf";
    private static final HashMap<Byte, Acao> acoes;

    /*########### AQUI 2 ###############33
     Coloque as varáveis  em ordem no array
     */
    static {
        // inicializa a lista das configs
        configs = new Object[]{
            pastasScaner,
            acaoPadraoFila,
            acoesFilaVazia,
            adicionarNaFilaVazia,
            telaPadrao,
            atalhosGlobaisAtivos,
            randomAtivo,
            repeatAtivo,
            visibPrincipal,
            visibMini,
            visibPlayList,
            visibBiblioteca,
            visibFila,
            listaAberta,
            fonteReproducao,
            peles,
            peleAtual,
            musicaContinuaOndeParou,
            musicaReproduzindo,
            musicaReproduzindoTempo,
            volume,
            balanco,
            localPrincipal,
            localMini,
            localFila,
            localPlayList,
            localBiblioteca
        };
//        configs[CONF_PASTAS_SCANER] = pastasScaner;
        acoes = new HashMap<Byte, Acao>(configs.length);
        ler();
    }

    public static void set(Byte indexConf, Enum valor, boolean gravarAgora) {
        configs[indexConf] = valor;
        if (gravarAgora) {
            gravar();
        }
    }

    public static void set(Byte indexConf, String valor, boolean gravarAgora) {
        configs[indexConf] = valor;
        if (gravarAgora) {
            gravar();
        }
    }

    public static void set(Byte indexConf, Integer valor, boolean gravarAgora) {
        configs[indexConf] = valor;
        if (gravarAgora) {
            gravar();
        }
    }

    public static void set(Byte indexConf, boolean valor, boolean gravarAgora) {
        configs[indexConf] = valor;
        if (gravarAgora) {
            gravar();
        }
    }

    public static void set(Byte indexConf, Rectangle valor, boolean gravarAgora) {
        configs[indexConf] = valor;
        if (gravarAgora) {
            gravar();
        }
    }

    public static void set(Byte indexConf, ArrayList valor, boolean gravarAgora) {
        ((ArrayList) configs[indexConf]).clear();
        ((ArrayList) configs[indexConf]).addAll(valor);
        dispararAcao(indexConf, valor);
        if (gravarAgora) {
            gravar();
        }
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
            ex.printStackTrace(System.err);
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

    public static Byte getByte(Byte index) {
        return (Byte) configs[index];
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
        return (Boolean) configs[index];
    }

    public static Long getLong(Byte index) {
        return (Long) configs[index];
    }

    public static Double getDouble(Byte index) {
        return (Double) configs[index];
    }

    public static Rectangle getRectangle(Byte index) {
        if (((Rectangle)configs[index]).isEmpty()) {
            return null;
        }
        return (Rectangle) configs[index];
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
                if (myConfig instanceof  String) {
                    configs[Integer.parseInt(tokens[0])] = tokens[1];
                } else if (myConfig instanceof  ArrayList) {
                    if (tokens[1].trim().isEmpty()) {
                        continue;
                    }
                    String[] valores = tokens[1].split(",");
                    ((ArrayList) myConfig).clear();
                    ((ArrayList) myConfig).addAll(Arrays.asList(valores));
                } else if (myConfig instanceof  Musica) {
                    Musica musica = CacheDeMusica.get(Integer.parseInt(tokens[1].trim()));
                    configs[Integer.parseInt(tokens[0])] = musica;
                } else if (myConfig instanceof Integer) {
                    configs[Integer.parseInt(tokens[0])] = Integer.valueOf(tokens[1].trim());
                } else if (myConfig instanceof Enum) {
                    configs[Integer.parseInt(tokens[0])] = Enum.valueOf(((Enum) myConfig).getClass(), tokens[1]);
                } else if (myConfig instanceof Boolean) {
                    configs[Integer.parseInt(tokens[0])] = Boolean.parseBoolean(tokens[1]);
                } else if (myConfig instanceof Long) {
                    configs[Integer.parseInt(tokens[0])] = Long.parseLong(tokens[1]);
                } else if (myConfig instanceof Double) {
                    configs[Integer.parseInt(tokens[0])] = Double.parseDouble(tokens[1]);
                } else if (myConfig instanceof Byte) {
                    configs[Integer.parseInt(tokens[0])] = Byte.parseByte(tokens[1]);
                } else if (myConfig instanceof Rectangle) {
                    if (tokens[1].trim().isEmpty() || tokens[1].trim().equals("-")) {
                        configs[Integer.parseInt(tokens[0])] = (Rectangle)null;
                    } else {
                        String[] tk = tokens[1].replaceAll("[\\[\\] ]*", "").split(",");
                        configs[Integer.parseInt(tokens[0])] = new Rectangle(Integer.valueOf(tk[0]),
                                Integer.valueOf(tk[1]), Integer.valueOf(tk[2]), Integer.valueOf(tk[3]));
                    }
                } else {
                    configs[Integer.parseInt(tokens[0])] = tokens[1];
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void gravar() {
        StringBuilder textFile = new StringBuilder(1024);
        for (int i = 0; i < configs.length; i++) {
            Object myConfig = configs[i];
            textFile.append(i).append(' ');
            if (myConfig instanceof  ArrayList) {
                textFile.append(((ArrayList) myConfig).toString().replaceAll("[\\[\\]]", ""));
            } else if (myConfig instanceof  Musica) {
                textFile.append(((Musica) myConfig).getId());
            } else if (myConfig instanceof Enum) {
                textFile.append(((Enum) myConfig).name());
            } else if (myConfig instanceof Rectangle) {
                Rectangle rec = (Rectangle) myConfig;
                textFile.append('[').append(rec.x).append(',').append(rec.y).append(',').append(rec.width).append(',').append(rec.height).append(']');
            } else if (myConfig instanceof String
                    || myConfig instanceof Long
                    || myConfig instanceof Double
                    || myConfig instanceof Boolean) {
                textFile.append(myConfig.toString());
            } else {
                textFile.append(myConfig.toString());
            }
            textFile.append('\n');
        }
        try {
            FileUtils.gravaArquivo(textFile, ARQUIVO);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public static void salvar() {
        gravar();
    }
}
