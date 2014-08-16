package com.config;

import com.config.constantes.AcaoPadraoFila;
import com.config.constantes.AcoesFilaVazia;
import com.config.constantes.AdicionarNaFilaVazia;
import com.config.constantes.TelaPadrao;
import com.main.FonteReproducao;
import com.musica.MusicaS;
import com.musica.album.AlbumS;
import com.musica.autor.AutorS;
import com.serial.PortaCDs;
import com.utils.ComandosSO;
import com.utils.file.FileUtils;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 *
 * @author rudieri
 */
public class Configuracoes {

    public static final Configuracao<ArrayList<String>> PASTAS_SCANER = new Configuracao<ArrayList<String>>(new ArrayList<String>());
    public static final Configuracao<AcaoPadraoFila> ACAO_PADRAO_FILA = new Configuracao<AcaoPadraoFila>(AcaoPadraoFila.ADICIONAR_FILA);
    public static final Configuracao<AcoesFilaVazia> ACOES_FILA_VAZIA = new Configuracao<AcoesFilaVazia>(AcoesFilaVazia.TOCAR_RANDOM);
    public static final Configuracao<AdicionarNaFilaVazia> ADICIONAR_NA_FILA_VAZIA = new Configuracao<AdicionarNaFilaVazia>(AdicionarNaFilaVazia.REPRODUZIR_MUSICA);
    public static final Configuracao<TelaPadrao> TELA_PADRAO = new Configuracao<TelaPadrao>(TelaPadrao.COMO_ESTAVA);
    public static final Configuracao<Boolean> ATALHOS_GLOBAIS_ATIVOS = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> RANDOM_ATIVO = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> REPEAT_ATIVO = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> VISIB_PRINCIPAL = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> VISIB_MINI = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> VISIB_PLAYLIST = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> VISIB_BIBLIOTECA = new Configuracao<Boolean>(false);
    public static final Configuracao<Boolean> VISIB_FILA = new Configuracao<Boolean>(false);
    public static final Configuracao<String> LISTA_ABERTA = new Configuracao<String>();
    public static final Configuracao<FonteReproducao> FONTE_REPRODUCAO = new Configuracao<FonteReproducao>(FonteReproducao.AVULSO);
    public static final Configuracao<ArrayList<String>> PELES = new Configuracao<ArrayList<String>>(new ArrayList<String>());
    public static final Configuracao<String> PELE_ATUAL = new Configuracao<String>("");
    public static final Configuracao<Boolean> MUSICA_CONTINUA_ONDE_PAROU = new Configuracao<Boolean>(true);
    public static final Configuracao<MusicaS> MUSICA_REPRODUZINDO = new Configuracao<MusicaS>();
    public static final Configuracao<Long> MUSICA_REPRODUZINDO_TEMPO = new Configuracao<Long>(-1l);
    public static final Configuracao<Byte> VOLUME = new Configuracao<Byte>((byte) 50);
    public static final Configuracao<Byte> BALANCO = new Configuracao<Byte>((byte) 50);
    public static final Configuracao<Rectangle> LOCAL_PRINCIPAL = new Configuracao<Rectangle>();
    public static final Configuracao<Rectangle> LOCAL_MINI = new Configuracao<Rectangle>();
    public static final Configuracao<Rectangle> LOCAL_FILA = new Configuracao<Rectangle>();
    public static final Configuracao<Rectangle> LOCAL_PLAYLIST = new Configuracao<Rectangle>();
    public static final Configuracao<Rectangle> LOCAL_BIBLIOTECA = new Configuracao<Rectangle>();
    public static final Configuracao<String> LOOK_AND_FEEL = new Configuracao<String>("");
    public static final Configuracao<String> MIXER = new Configuracao<String>("");
    public static final Configuracao<String> FILE_BD_MUSICAS = new Configuracao<String>(ComandosSO.getLocalCrepzPath() + "/etc/musicas.csf");
    public static final Configuracao<String> FILE_BD_PLAYLISTS = new Configuracao<String>(ComandosSO.getLocalCrepz() + "/etc/playlists.csf");
    private static final String ARQUIVO = ComandosSO.getLocalCrepzPath()+"/etc/crepz.conf";
    
    static {
        ler();
    }

    private static Class getTipoGenerico(Type type) {
        try {
            Field declaredField = type.getClass().getDeclaredField("actualTypeArguments");
            declaredField.setAccessible(true);
            Type[] tipo = (Type[]) declaredField.get(type);
            if (tipo.length == 0) {
                return null;
            }
            if (tipo[0].getClass().isAssignableFrom(Class.class)) {
                return (Class) tipo[0];
            } else {
                ParameterizedTypeImpl impl = (ParameterizedTypeImpl) tipo[0];
                return impl.getRawType();
            }
        } catch (Exception ex) {
            Logger.getLogger(Configuracoes.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static void ler() {
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
            for (String linha : linhas) {
                String[] tokens = linha.split(" ", 2);
                String chave = tokens[0];
                String valor = tokens[1];
                Field field = Configuracoes.class.getField(chave);
                Configuracao configuracao = (Configuracao) field.get(null);
                if (valor.equals("<null>")) {
                    configuracao.setValor(null);
                    continue;
                }
                Class<?> tipoGenerico = getTipoGenerico(field.getGenericType());
                if (tipoGenerico.isAssignableFrom(String.class)) {
                    configuracao.setValor(valor);
                } else if (tipoGenerico.isAssignableFrom(ArrayList.class)) {
                    ArrayList lista = new ArrayList();
                    configuracao.setValor(lista);
                    if (valor.trim().isEmpty()) {
                        continue;
                    }
                    String[] valores = valor.split(",");
                    lista.addAll(Arrays.asList(valores));
                } else if (tipoGenerico.isAssignableFrom(MusicaS.class)) {
                    String[] split = valor.trim().split("/");
                    AutorS autor = PortaCDs.getAutor(split[0], false);
                    if (autor != null) {
                        AlbumS album = autor.getAlbum(split[1], false);
                        if (album != null) {
                            MusicaS musica = album.getMusica(split[2]);
                            configuracao.setValor(musica);
                        }
                    }
                } else if (tipoGenerico.isAssignableFrom(Integer.class)) {
                    configuracao.setValor(Integer.valueOf(valor.trim()));
                } else if (tipoGenerico.isAssignableFrom(Enum.class) || (tipoGenerico.getSuperclass() != null && tipoGenerico.getSuperclass().equals(Enum.class))) {
                    configuracao.setValor(Enum.valueOf((Class) tipoGenerico, valor));
                } else if (tipoGenerico.isAssignableFrom(Boolean.class)) {
                    configuracao.setValor(Boolean.parseBoolean(valor));
                } else if (tipoGenerico.isAssignableFrom(Long.class)) {
                    configuracao.setValor(Long.parseLong(valor));
                } else if (tipoGenerico.isAssignableFrom(Double.class)) {
                    configuracao.setValor(Double.parseDouble(valor));
                } else if (tipoGenerico.isAssignableFrom(Byte.class)) {
                    configuracao.setValor(Byte.parseByte(valor));
                } else if (tipoGenerico.isAssignableFrom(Rectangle.class)) {
                    if (valor.trim().isEmpty() || valor.trim().equals("-")) {
                        configuracao.setValor(null);
                    } else {
                        String[] tk = valor.replaceAll("[\\[\\] ]*", "").split(",");
                        configuracao.setValor(new Rectangle(Integer.valueOf(tk[0]),
                                Integer.valueOf(tk[1]), Integer.valueOf(tk[2]), Integer.valueOf(tk[3])));
                    }
                } else {
                    configuracao.setValor(valor);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Configuracoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void gravar() {
        try {
            StringBuilder textFile = new StringBuilder(1024);
            Field[] fields = Configuracoes.class.getFields();
            for (Field field : fields) {
                if (field.getType() == Configuracao.class) {
                    Configuracao config = (Configuracao) field.get(null);
                    String strGravavel = config.converteValorParaGravavel();
                    textFile.append(field.getName()).append(' ').append(strGravavel == null ? "<null>" : strGravavel);
                    textFile.append('\n');
                }
            }
            FileUtils.gravaArquivo(textFile, ARQUIVO);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Configuracoes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Configuracoes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Configuracoes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void limpar() {
        new File(FILE_BD_MUSICAS.getValor()).deleteOnExit();
        new File(FILE_BD_PLAYLISTS.getValor()).deleteOnExit();
        new File(ARQUIVO).deleteOnExit();
    }
}
