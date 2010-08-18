/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.Aguarde;
import com.JBiBlioteca;
import com.JMini;
import com.JPlayList;
import com.JPrincipal;
import com.conexao.BD;
import com.conexao.Transacao;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.playlist.JPlaylists;
import java.awt.Point;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author -moNGe_
 */
public class GerenciadorConfig {

    private JPrincipal pr;
    private JPlayList pl;
    private JBiBlioteca lib;
    private JMini mini;
    HashMap list = null;

    public GerenciadorConfig(JPrincipal principal, JPlayList playList, JBiBlioteca biblioteca, JMini jmini) {
        pr = principal;
        pl = playList;
        lib = biblioteca;
        mini = jmini;
    }

    /**Grava as todas configurações que estão sendo usadas pelo Player*/
    public void setAllValores() {
        // tela de aguarde (passa muito rápido, nem da tempo de ver)
        final Aguarde ag = new Aguarde();
        new Thread(new Runnable() {

            public void run() {
                pr.setVisible(false);
                ag.setVisible(true);
                ag.setAlwaysOnTop(true);
                ag.fechar();
            }
        }).start();
        //Cria uma transação com o banco
        Transacao t = new Transacao();
        try {
            //Inicia a transação
            t.begin();
            //ATENÇÃO! APENAS O PRIMEIRO incluir DEVE MANDAR O PARAMETRO reset COMO true
            ConfigBD.incluir("tocando", String.valueOf(pr.getTocando()), true, t);
            ConfigBD.incluir("pause", String.valueOf(pr.getPause()), false, t);
            if (pr.getMusica() != null) {
                ConfigBD.incluir("musica", pr.getMusica().getId().toString(), false, t);
            } else {
                ConfigBD.incluir("musica", "", false, t);
            }
            ConfigBD.incluir("tempo", String.valueOf(pr.getTempo()), false, t);
            ConfigBD.incluir("volume", String.valueOf(pr.getVolume()), false, t);
            ConfigBD.incluir("pan", String.valueOf(pr.getBalanco()), false, t);
            if (pl.getId() != -1) {
                ConfigBD.incluir("playList", String.valueOf(pl.getId()), false, t);
            } else {
                ConfigBD.incluir("playList", "", false, t);
            }
            ConfigBD.incluir("random", String.valueOf(pr.isRandom()), false, t);
            ConfigBD.incluir("repeat", String.valueOf(pr.getRepetir()), false, t);
            ConfigBD.incluir("bandeja", String.valueOf(pr.isBandeija()), false, t);
            ConfigBD.incluir("miniPosicao", String.valueOf((int) mini.getLocal().getX() + "X" + (int) mini.getLocal().getY()), false, t);
            ConfigBD.incluir("minitop", String.valueOf(mini.getTop()), false, t);
           //salva o que foi mudado
            t.commit();

        } catch (Exception ex) {
            t.rollback();
            Logger.getLogger(GerenciadorConfig.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            BD.fecharBD();
            ag.setVisible(false);
        }

    }

    /**Aplica todas as configurações que foram gravadas quando o player foi fechado pela ultima vez.*/
    public void getAllValores() {

        try {
            //lê a tabela de configurações que está no banco, list é um HashMap
            list = ConfigBD.listar(null);
            //aplica as configurações.
             if (list.get("playList") != null && !list.get("playList").toString().trim().equals("")) {
                setPlayList(Integer.parseInt((String) list.get("playList")));
            }
            if (list.get("repeat") != null) {
                setRepetir(Boolean.parseBoolean((String) list.get("repeat")));
            }
            if (list.get("random") != null) {
                setRandom(Boolean.parseBoolean((String) list.get("random")));
            }
            if (list.get("musica") != null && !((String) list.get("musica")).trim().equals("")) {
                String musica = (String) list.get("musica");
                String tempo = (String) list.get("tempo");
                if (tempo == null || tempo.trim().equals("") || tempo.equalsIgnoreCase("null")) {
                    tempo = "0";
                }
                String pause = (String) list.get("pause");
                String tocando = (String) list.get("tocando");
                if (Boolean.parseBoolean(tocando)) {
                    setMusica(Integer.valueOf(musica), Long.valueOf(tempo), Boolean.parseBoolean(pause));
                } else {
                    setMusica(Integer.valueOf(musica));
                }
            }

            if (list.get("volume") != null) {
                setVolume(Integer.parseInt((String) list.get("volume")));
            }
            if (list.get("pan") != null) {
                setPan(Integer.parseInt((String) list.get("pan")));
            }

            if (list.get("bandeja") != null) {
                setBandeja(Boolean.parseBoolean((String) list.get("bandeja")));
            }
            if (list.get("miniPosicao") != null) {
                setPosicao(String.valueOf(list.get("miniPosicao")));
            }
            if (list.get("minitop") != null) {
                setTop(Boolean.parseBoolean((String) list.get("minitop")));
            }

        } catch (Exception ex) {
            Logger.getLogger(GerenciadorConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getValue(String v) {
        return String.valueOf(list.get(v));
    }

    private void setMusica(int id, Long t, boolean toc) {
        Musica m = new Musica();
        m.setId(id);
        try {
            MusicaBD.carregar(m);
            pr.setMusica(m);
            if (!toc) {
                pr.abrir(m, t);
            } else {
                pr.apenasAbrir(m, t);
            }
            System.out.println("Qual é a tua!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setMusica(int id) {
        Musica m = new Musica();
        m.setId(id);
        try {
            MusicaBD.carregar(m);
            pr.setMusica(m);
            pr.apenasAbrir(m, 0l);
            System.out.println("Qual é a tua!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPlayList(int id) {
        System.out.println("id: " + id);
        pl.tocar(JPlaylists.getPlayList(id), false);
    }

    private void setRandom(boolean b) {
        pr.setRandom(b);
    }

    private void setRepetir(boolean b) {
        pr.setRepetir(b);
    }

    private void setVolume(int v) {
        pr.setVolume(v);
    }

    private void setPan(int p) {
        pr.setBalaco(p);
    }

    private void setBandeja(boolean b) {
        pr.setBandeija(b);
    }

    private void setPosicao(String s) {
        System.out.println(s);
        String ar[] = s.split("X");
        Point p = new Point(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]));
        mini.setLocal((int) p.getX(), (int) p.getY());
    }

    private void setTop(boolean b) {
        mini.setTop(b);
    }
}
