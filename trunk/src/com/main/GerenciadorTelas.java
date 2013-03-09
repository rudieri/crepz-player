/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.config.Configuracaoes;
import com.fila.JFilaReproducao;
import com.main.gui.JBiBlioteca;
import com.main.gui.JMini;
import com.main.gui.JPrincipal;
import com.playlist.JPlayList;
import com.utils.Warning;
import com.utils.pele.ColorUtils;
import com.utils.pele.JPele;
import java.awt.Rectangle;

/**
 *
 * @author rudieri
 */
public class GerenciadorTelas {

    private static JPrincipal principal;
    private static JMini mini;
    private static Scan scan;
    private static JFilaReproducao filaReproducao;
    private static JPlayList playList;
    private static JBiBlioteca biblioteca;
    private static JPele pele;
    private static CrepzTray crepzTray;

    public static JPrincipal getPrincipal() {
        if (principal == null) {
            carregandoDebug("Principal");
            try {
                principal = new JPrincipal(Carregador.getMe());
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                principal = new JPrincipal(Carregador.getMe());
            }
            Rectangle rectangle = Configuracaoes.getRectangle(Configuracaoes.CONF_LOCAL_PRINCIPAL);
            if (rectangle != null) {
                principal.setBounds(rectangle);
            }
            ColorUtils.registrar(principal);
            ColorUtils.aplicarTema();
        }
        return principal;
    }

    public static JMini getMini() {
        if (mini == null) {
            carregandoDebug("Mini");
            mini = new JMini(Carregador.getMe());
            Rectangle rectangle = Configuracaoes.getRectangle(Configuracaoes.CONF_LOCAL_MINI);
            if (rectangle != null) {
                mini.setBounds(rectangle);
            }
            ColorUtils.registrar(mini);
            ColorUtils.aplicarTema();
        }
        return mini;
    }

    public static Scan getScan() {
        if (scan == null) {
            carregandoDebug("Scan");
            scan = new Scan();
        }
        return scan;
    }

    public static JFilaReproducao getFilaReproducao() {
        if (filaReproducao == null) {
            carregandoDebug("Fila de Reprodução");
            filaReproducao = new JFilaReproducao(Carregador.getMe());
            Rectangle rectangle = Configuracaoes.getRectangle(Configuracaoes.CONF_LOCAL_FILA);
            if (rectangle != null) {
                filaReproducao.setBounds(rectangle);
            }
            ColorUtils.registrar(filaReproducao);
            ColorUtils.aplicarTema();

        }
        return filaReproducao;
    }

    public static JPlayList getPlayList() {
        if (playList == null) {
            carregandoDebug("Play List");
            playList = new JPlayList(Carregador.getMe());
            playList.setPlayListAberta(Configuracaoes.getInteger(Configuracaoes.CONF_LISTA_ABERTA));
            Rectangle rectangle = Configuracaoes.getRectangle(Configuracaoes.CONF_LOCAL_PLAYLIST);
            if (rectangle != null) {
                playList.setBounds(rectangle);
            } else {
                playList.setLocation(Carregador.getMe().getWindowPrincipal().getX()
                        - GerenciadorTelas.getPlayList().getWidth() - 5, Carregador.getMe().getWindowPrincipal().getY());
            }
            ColorUtils.registrar(playList);
            ColorUtils.aplicarTema();
        }
        return playList;
    }

    public static JBiBlioteca getBiblioteca() {
        if (biblioteca == null) {
            carregandoDebug("Biblioteca");
            biblioteca = new JBiBlioteca(Carregador.getMe());
            Rectangle rectangle = Configuracaoes.getRectangle(Configuracaoes.CONF_LOCAL_BIBLIOTECA);
            if (rectangle != null) {
                biblioteca.setBounds(rectangle);
            } else {
                biblioteca.setLocation(Carregador.getMe().getWindowPrincipal().getX()
                        - GerenciadorTelas.getBiblioteca().getWidth() - 5,
                        Carregador.getMe().getWindowPrincipal().getY());
            }
        }
        return biblioteca;
    }

    public static JPele getPele() {
        if (pele == null) {
            carregandoDebug("Pele");
            pele = new JPele(Carregador.getMe());
            ColorUtils.registrar(pele);
            ColorUtils.aplicarTema();
        }
        return pele;
    }

    public static CrepzTray getCrepzTray() {
        if (crepzTray == null) {
            try {
                carregandoDebug("Crepz Tray");
                crepzTray = new CrepzTray(Carregador.getMe());
            } catch (Exception ex) {
                Warning.print("System tray não supostado.");
                ex.printStackTrace(System.err);
                crepzTray = null;
            }

        }
        return crepzTray;
    }

    private static void carregandoDebug(String nomeDoObjetoCarregado) {
        StringBuilder saida = new StringBuilder(300);
        saida.append("Carregando: ").append(nomeDoObjetoCarregado).append(" ==>\n");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            saida.append(stackTraceElement.toString()).append('\n');
        }
        System.out.println(saida);
    }

    public static boolean isFilaReproducaoCarregada() {
        return filaReproducao != null;
    }

    public static boolean isMiniCarregado() {
        return mini != null;
    }

    public static boolean isPrincipalCarregado() {
        return principal != null;
    }

    public static boolean isPlayListCarregado() {
        return playList != null;
    }

    public static boolean isBibliotecaCarregado() {
        return biblioteca != null;
    }

    public static boolean isCrepzTrayCarregado() {
        return crepzTray != null;
    }

    private GerenciadorTelas() {
    }
}
