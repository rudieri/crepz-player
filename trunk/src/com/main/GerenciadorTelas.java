/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.main;

import com.fila.JFilaReproducao;
import com.main.gui.JBiBlioteca;
import com.main.gui.JMini;
import com.main.gui.JPrincipal;
import com.playlist.JPlayList;
import com.utils.Warning;
import com.utils.pele.ColorUtils;
import com.utils.pele.JPele;

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
            principal = new JPrincipal(Carregador.getMe().getMusiquera(), Carregador.getMe());
            ColorUtils.registrar(principal);
            ColorUtils.aplicarTema();
        }
        return principal;
    }

    public static JMini getMini() {
        if (mini == null) {
            mini = new JMini(Carregador.getMe().getMusiquera(), Carregador.getMe());
            ColorUtils.registrar(mini);
            ColorUtils.aplicarTema();
        }
        return mini;
    }

    public static Scan getScan() {
        if (scan == null) {
            scan = new Scan();
        }
        return scan;
    }

    public static JFilaReproducao getFilaReproducao() {
        if (filaReproducao == null) {
            filaReproducao = new JFilaReproducao(Carregador.getMe().getMusiquera(), Carregador.getMe());
            ColorUtils.registrar(filaReproducao);
            ColorUtils.aplicarTema();
        }
        return filaReproducao;
    }

    public static JPlayList getPlayList() {
        if (playList == null) {
            playList = new JPlayList(Carregador.getMe().getMusiquera(), Carregador.getMe());
            ColorUtils.registrar(playList);
            ColorUtils.aplicarTema();
        }
        return playList;
    }

    public static JBiBlioteca getBiblioteca() {
        if (biblioteca == null) {
            biblioteca = new JBiBlioteca(Carregador.getMe().getMusiquera(), Carregador.getMe());
        }
        return biblioteca;
    }

    public static JPele getPele() {
        if (pele == null) {
            pele = new JPele(Carregador.getMe());
            ColorUtils.registrar(pele);
            ColorUtils.aplicarTema();
        }
        return pele;
    }

    public static CrepzTray getCrepzTray() {
        if (crepzTray == null) {
            try {
                crepzTray = new CrepzTray(Carregador.getMe().getMusiquera(), Carregador.getMe());
            } catch (Exception ex) {
                Warning.print("System tray não supostado.");
                ex.printStackTrace(System.err);
                crepzTray = null;
            }

        }
        return crepzTray;
    }
}
