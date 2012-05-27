/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.config;

import com.main.gui.JBiBlioteca;
import com.main.gui.JMini;
import com.main.gui.JPrincipal;
import com.musica.CacheDeMusica;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.playlist.JPlayList;
import com.playlist.JSelectPlaylists;
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

    public GerenciadorConfig(JPrincipal pri, JPlayList playList, JBiBlioteca biblioteca, JMini jmini) {
        principal = pri;
        pl = playList;
        lib = biblioteca;
        mini = jmini;
    }

    public void setAllValores() {
        principal.setVisible(false);

//        try {
//
//            Musiquera mq = principal.getMusiquera();
//
//            cf.incluir("tocando", String.valueOf(mq.isPlaying()));
//            cf.incluir("pause", String.valueOf(mq.isPaused()));
//
//            if (mq.getMusica() != null) {
//                cf.incluir("musica", String.valueOf(mq.getMusica().getId()));
//            } else {
//                cf.incluir("musica", "");
//            }
//
//            cf.incluir("tempo", String.valueOf(mq.getTempoAtual()));
//            cf.incluir("volume", String.valueOf(mq.getVolume()));
//            principal.setVolume(0);
//            cf.incluir("pan", String.valueOf(mq.getBalanco()));
//            if (pl.getId() != -1) {
//                cf.incluir("playList", String.valueOf(pl.getId()));
//            } else {
//                cf.incluir("playList", "");
//            }
//            cf.incluir("random", String.valueOf(pl.isRandom()));
//            cf.incluir("repeat", String.valueOf(pl.isRepeat()));
//            //       cf.incluir("bandeja", String.valueOf(principal.isBandeija()));
//            cf.incluir("miniPosicao", String.valueOf((int) mini.getLocal().getX() + "X" + (int) mini.getLocal().getY()));
//            cf.incluir("minitop", String.valueOf(mini.getTop()));
//            cf.incluir("downloadcapas", String.valueOf(MusicaGerencia.downLoadCapas));
//            cf.incluir("organizador", String.valueOf(MusicaGerencia.organizarPastas));
//            cf.incluir("destino", MusicaGerencia.destino);
//            cf.incluir("tempo_monitorar", String.valueOf(Scan.getTempo()));
//            ArrayList<String> pastas = Scan.getPastas();
//            String pastasT = "";
//            for (int i = 0; i < pastas.size(); i++) {
//                pastasT += pastas.get(i) + ";";
//            }
//
//            cf.incluir("monitorar", pastasT);
//            cf.gravar();
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

    }

    /**Aplica todas as configurações que foram gravadas quando o player foi fechado pela ultima vez.*/
    public void getAllValores() {
//        if (!cf.read()) {
//            return;
//        }
//        try {
//
//            if (cf.recuperar("playList") != null && !cf.recuperar("playList").trim().isEmpty()) {
//                setPlayList(Integer.parseInt(cf.recuperar("playList")));
//            }
//            if (cf.recuperar("repeat") != null) {
//                setRepetir(Boolean.parseBoolean(cf.recuperar("repeat")));
//            }
//            if (cf.recuperar("random") != null) {
//                setRandom(Boolean.parseBoolean(cf.recuperar("random")));
//            }
//            if (cf.recuperar("musica") != null && !cf.recuperar("musica").trim().isEmpty()) {
//                String musica = cf.recuperar("musica");
//                String tempo = cf.recuperar("tempo");
//                if (tempo == null || tempo.trim().isEmpty() || tempo.equalsIgnoreCase("null")) {
//                    tempo = "0";
//                }
//                String pause = cf.recuperar("pause");
//                String tocando = cf.recuperar("tocando");
//                if (Boolean.parseBoolean(tocando)) {
//                    setMusica(Integer.valueOf(musica), Integer.valueOf(tempo), Boolean.parseBoolean(pause));
//                } else {
//                    setMusica(Integer.valueOf(musica));
//                }
//            }
//
//            if (cf.recuperar("volume") != null) {
//                setVolume(Integer.parseInt(cf.recuperar("volume")));
//
//            }
//            if (cf.recuperar("pan") != null) {
//                setPan(Integer.parseInt(cf.recuperar("pan")));
//            }
//
//            if (cf.recuperar("bandeja") != null) {
//                setBandeja(Boolean.parseBoolean(cf.recuperar("bandeja")));
//            }
//            if (cf.recuperar("miniPosicao") != null) {
//                setPosicao(String.valueOf(cf.recuperar("miniPosicao")));
//            }
//            if (cf.recuperar("minitop") != null) {
//                setTop(Boolean.parseBoolean(cf.recuperar("minitop")));
//            }
//            if (cf.recuperar("downloadcapas") != null) {
//                MusicaGerencia.downLoadCapas = Boolean.parseBoolean(cf.recuperar("downloadcapas"));
//            }
//            if (cf.recuperar("organizador") != null) {
//                MusicaGerencia.organizarPastas = Boolean.parseBoolean(cf.recuperar("organizador"));
//            }
//            if (cf.recuperar("destino") != null) {
//                MusicaGerencia.destino = cf.recuperar("destino");
//                if (!new File(MusicaGerencia.destino).exists()) {
//                    MusicaGerencia.organizarPastas = false;
//                }
//            }
//            if (cf.recuperar("tempo_monitorar") != null) {
//                Scan.setTempo(Integer.valueOf(cf.recuperar("tempo_monitorar")));
//                //  Scan.setTempo(1);
//            }
//            if (cf.recuperar("monitorar") != null) {
//                String pastasT[] = cf.recuperar("monitorar").split(";");
//                ArrayList<String> pastas = new ArrayList<String>();
//                pastas.addAll(Arrays.asList(pastasT));
//                Scan.setPastas(pastas);
//            }
//
//
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public String getValue(String v) {
        return String.valueOf(list.get(v));
    }

    private void setMusica(int id, int t, boolean pause) {
        Musica m = CacheDeMusica.get(id);
        try {
            MusicaBD.carregar(m);
            (principal.getMusiquera()).abrir(m, t, pause);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setMusica(int id) {
        Musica m = CacheDeMusica.get(id);
        m.setId(id);
        try {
            MusicaBD.carregar(m);
            //principal.setMusica(m);
            principal.getMusiquera().abrir(m, 0, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPlayList(int id) {
        pl.tocar(JSelectPlaylists.getPlayList(id), false);
    }

    private void setRandom(boolean b) {
        pl.setAleatorio(b);
    }

    private void setRepetir(boolean b) {
        pl.setRepetir(b);
    }

    private void setVolume(int v) {
        principal.getMusiquera().setVolume((byte) v);
        principal.setVolume(v);
        mini.atualizaVolume(v);
    }

    private void setPan(int p) {
        principal.getMusiquera().setBalanco((byte) p);
    }

    private void setBandeja(boolean b) {
        //      principal.setBandeija(b);
    }

    private void setPosicao(String s) {
        String ar[] = s.split("X");
        Point p = new Point(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]));
        mini.setLocal((int) p.getX(), (int) p.getY());
    }

    private void setTop(boolean b) {
        mini.setTop(b);
    }
}
