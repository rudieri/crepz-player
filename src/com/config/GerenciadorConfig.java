/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.JBiBlioteca;
import com.JMini;
import com.JPlayList;
import com.JPrincipal;
import com.Musiquera;
import com.conexao.BD;
import com.conexao.Transacao;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.playlist.JPlaylists;
import java.awt.Point;
import java.util.HashMap;

/**
 *
 * @author -moNGe_
 */
public class GerenciadorConfig {

    private JPrincipal principal;
    private JPlayList pl;
    private JBiBlioteca lib;
    private JMini mini;
    HashMap list = null;
    ConfigFile cf;

    public GerenciadorConfig(JPrincipal pri, JPlayList playList, JBiBlioteca biblioteca, JMini jmini) {
        principal = pri;
        pl = playList;
        lib = biblioteca;
        mini = jmini;
        cf = new ConfigFile();
    }

    /**Grava as todas configura��es que est�o sendo usadas pelo Player*/
    /* public void setAllValores() {
    principal.setVisible(false);
    //Cria uma transa��o com o banco
    Transacao t = new Transacao();
    try {
    //Inicia a transa��o
    t.begin();
    //ATEN��O! APENAS O PRIMEIRO incluir DEVE MANDAR O PARAMETRO reset COMO true
    ConfigBD.incluir("tocando", String.valueOf(principal.tocando2), true, t);
    ConfigBD.incluir("pause", String.valueOf(principal.paused2), false, t);
    if (principal.getMusica() != null) {
    ConfigBD.incluir("musica", principal.getMusica().getId().toString(), false, t);
    } else {
    ConfigBD.incluir("musica", "", false, t);
    }

    ConfigBD.incluir("tempo", String.valueOf(principal.getTempo()), false, t);
    ConfigBD.incluir("volume", String.valueOf(principal.getVolume()), false, t);
    principal.setVolume(0);
    ConfigBD.incluir("pan", String.valueOf(principal.getBalanco()), false, t);
    if (pl.getId() != -1) {
    ConfigBD.incluir("playList", String.valueOf(pl.getId()), false, t);
    } else {
    ConfigBD.incluir("playList", "", false, t);
    }
    ConfigBD.incluir("random", String.valueOf(principal.isRandom()), false, t);
    ConfigBD.incluir("repeat", String.valueOf(principal.getRepetir()), false, t);
    ConfigBD.incluir("bandeja", String.valueOf(principal.isBandeija()), false, t);
    ConfigBD.incluir("miniPosicao", String.valueOf((int) mini.getLocal().getX() + "X" + (int) mini.getLocal().getY()), false, t);
    ConfigBD.incluir("minitop", String.valueOf(mini.getTop()), false, t);

    //salva o que foi mudado
    t.commit();
    BD.fecharBD();


    } catch (Exception ex) {
    t.rollback();
    ex.printStackTrace();
    } finally {
    BD.fecharBD();
    //            ag.setVisible(false);
    }

    }*/
    public void setAllValores() {
        principal.setVisible(false);
        //Cria uma transa��o com o banco
//        Transacao t = new Transacao();

        try {
            //Inicia a transa��o
//            t.begin();

            Musiquera mq= principal.getMusiquera();

            cf.incluir("tocando", String.valueOf(mq.isPlaying()));
            cf.incluir("pause", String.valueOf(mq.isPaused()));
            
            if (mq.getMusica() != null) {
                cf.incluir("musica", mq.getMusica().getId().toString());
            } else {
                cf.incluir("musica", "");
            }

            cf.incluir("tempo", String.valueOf(mq.getTempo()));
            cf.incluir("volume", String.valueOf(mq.getVolume()));
            principal.setVolume(0);
            cf.incluir("pan", String.valueOf(mq.getBalanco()));
            if (pl.getId() != -1) {
                cf.incluir("playList", String.valueOf(pl.getId()));
            } else {
                cf.incluir("playList", "");
            }
            cf.incluir("random", String.valueOf(principal.isRandom()));
            cf.incluir("repeat", String.valueOf(principal.getRepetir()));
            cf.incluir("bandeja", String.valueOf(principal.isBandeija()));
            cf.incluir("miniPosicao", String.valueOf((int) mini.getLocal().getX() + "X" + (int) mini.getLocal().getY()));
            cf.incluir("minitop", String.valueOf(mini.getTop()));

            //salva o que foi mudado
            //   t.commit();
            // BD.fecharBD();
            cf.gravar();


        } catch (Exception ex) {
            //    t.rollback();
            ex.printStackTrace();
        } finally {
         //   BD.fecharBD();
//            ag.setVisible(false);
        }

    }

    /**Aplica todas as configura��es que foram gravadas quando o player foi fechado pela ultima vez.*/
    public void getAllValores() {
        if (!cf.read()) {
            return;
        }
        try {
            //l� a tabela de configura��es que est� no banco, list � um HashMap
            // list = ConfigBD.listar(null);
            //aplica as configura��es.
            if (cf.recuperar("playList") != null && !cf.recuperar("playList").toString().trim().equals("")) {
                setPlayList(Integer.parseInt((String) cf.recuperar("playList")));
            }
            if (cf.recuperar("repeat") != null) {
                setRepetir(Boolean.parseBoolean((String) cf.recuperar("repeat")));
            }
            if (cf.recuperar("random") != null) {
                setRandom(Boolean.parseBoolean((String) cf.recuperar("random")));
            }
            if (cf.recuperar("musica") != null && !((String) cf.recuperar("musica")).trim().equals("")) {
                String musica = (String) cf.recuperar("musica");
                String tempo = (String) cf.recuperar("tempo");
                if (tempo == null || tempo.trim().equals("") || tempo.equalsIgnoreCase("null")) {
                    tempo = "0";
                }
                String pause = (String) cf.recuperar("pause");
                String tocando = (String) cf.recuperar("tocando");
                if (Boolean.parseBoolean(tocando)) {
                    setMusica(Integer.valueOf(musica), Long.valueOf(tempo), Boolean.parseBoolean(pause));
                } else {
                    setMusica(Integer.valueOf(musica));
                }
            }

            if (cf.recuperar("volume") != null) {
                setVolume(Integer.parseInt((String) cf.recuperar("volume")));

            }
            if (cf.recuperar("pan") != null) {
                setPan(Integer.parseInt((String) cf.recuperar("pan")));
            }

            if (cf.recuperar("bandeja") != null) {
                setBandeja(Boolean.parseBoolean((String) cf.recuperar("bandeja")));
            }
            if (cf.recuperar("miniPosicao") != null) {
                setPosicao(String.valueOf(cf.recuperar("miniPosicao")));
            }
            if (cf.recuperar("minitop") != null) {
                setTop(Boolean.parseBoolean((String) cf.recuperar("minitop")));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public String getValue(String v) {
        return String.valueOf(list.get(v));
    }

    private void setMusica(int id, Long t, boolean pause) {
        Musica m = new Musica();
        m.setId(id);
        try {
            MusicaBD.carregar(m);
            //principal.setMusica(m);
            
            
             (principal.getMusiquera()).abrir(m, t.intValue(), pause, true);
            // principal.getMusiquera().tocar();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setMusica(int id) {
        Musica m = new Musica();
        m.setId(id);
        try {
            MusicaBD.carregar(m);
            //principal.setMusica(m);
            principal.getMusiquera().abrir(m, 0, false, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPlayList(int id) {
        System.out.println("id: " + id);
        pl.tocar(JPlaylists.getPlayList(id), false);
    }

    private void setRandom(boolean b) {
        principal.setRandom(b);
    }

    private void setRepetir(boolean b) {
        principal.setRepetir(b);
    }

    private void setVolume(int v) {
        principal.getMusiquera().setVolume(v);
    }

    private void setPan(int p) {
        principal.getMusiquera().setBalanco(p);
    }

    private void setBandeja(boolean b) {
        principal.setBandeija(b);
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
