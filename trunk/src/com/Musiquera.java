/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.graficos.Icones;
import com.musica.Musica;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

/**
 *
 * @author rudieri
 */
public class Musiquera implements BasicPlayerListener {

    //  private boolean jSliderBarPressed = false;
    private boolean tocando;
    private boolean paused;
    private boolean ajust;
    private int volume;
    private int balanco;
    private int tempo;
    private int tenteiTocar = 0;
    private int tenteiAbrir = 0;
    Long total = new Long(1);
    JPrincipal principal;
    JPlayList playList;
    JBiBlioteca biblioteca;
    JMini mini;
    Icones icones;
    BasicPlayer player;
    private Musica musica;
    private File in;
    private String tipo = "";

    public Musiquera(JPrincipal jpr, JPlayList jpl, JBiBlioteca jbl, JMini jmi, Icones ico) {
        principal = jpr;
        playList = jpl;
        biblioteca = jbl;
        mini = jmi;
        icones = ico;
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);
    }

    public void setVolume(int v) {
        volume = v;
        try {
            player.setGain(new Double(v) / 100);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getVolume() {
        return volume;
    }

    public void setBalanco(int b) {
        balanco = b;
        try {
            player.setPan(new Double(b) / 100);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getBalanco() {
        return balanco;
    }

    public Musica getMusica() {
        return this.musica;
    }

    public void setEstado(boolean estaTocando, boolean pausado) {
        tocando = estaTocando;
        paused = pausado;
    }

    public boolean isPlaying() {
        return tocando;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getTempo() {
        return tempo;
    }

    public void abrir(Musica m, int toc, boolean isPause, boolean tocar) {
        try {
            this.musica = m;
            if(m==null){
                System.out.println("Musica não existe, nullPointer");
                return ;
            }
            in = new File(m.getCaminho());
            if (in == null) {
                return;
            }
            player.open(in);

            if (toc > 0) {
                ajust = true;
                skipTo(toc);
                ajust = false;
                tocar();
                if (isPause) {
                    tocar();
                }
            } else {
                if (tocar) {
                    tocar();
                }
            }
            tenteiAbrir = 0;

        } catch (BasicPlayerException ex) {
            tenteiAbrir++;
            switch (tenteiAbrir) {
                case 1:
                case 2:
                    System.out.println("Falha ao abrir, tentando novamente!");
                    abrir(m, toc, isPause, tocar);
                    break;
                case 3:
                    System.out.println("Falha ao abrir, passando para a próxima música!");
                    abrir(playList.getProxima(), 0, false, true);
                    break;
                case 4:
                    System.out.println("Falha ao abrir (estágio 2), tentando novamente!");
                    abrir(m, toc, isPause, tocar);
                    break;
                default:
                    System.out.println("Todas as tentativas falharam... :(");
            }
            ex.printStackTrace();
        }

    }

    public void abrir(File f) {
        if (f == null) {
            return;
        }
        try {
            player.open(f);
            tocar();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean jSliderBarPressed() {
        return principal.ajust;
    }

    public void parar() {
        try {
            player.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tocar() {
        try {
            // tarefa = new Timer();
            int estado = player.getStatus();
            System.out.println(estado);

            if (estado == BasicPlayer.UNKNOWN) {
                System.out.println("Estado UNKNOWN");
                principal.atualizaIcone("jButton_Play", "PAUSE");
                player.open(in);
            }


            switch (estado) {
                case BasicPlayer.PLAYING:
                    principal.atualizaIcone("jButton_Play", "TOCAR");
                    player.pause();
                    setEstado(true, true);
                    break;
                case BasicPlayer.PAUSED:
                    principal.atualizaIcone("jButton_Play", "PAUSAR");
                    player.resume();
                    setEstado(true, false);
                    break;

                case BasicPlayer.STOPPED:
                    principal.atualizaIcone("jButton_Play", "PAUSAR");
                    player.play();
                    setEstado(true, true);
                    break;
                case BasicPlayer.OPENED:
                    principal.atualizaIcone("jButton_Play", "PAUSAR");
                    player.play();
                    setEstado(true, true);
                    break;
            }
            tenteiTocar = 0;


        } catch (Exception ex) {
            tenteiTocar++;
            switch (tenteiTocar) {
                case 1:
                case 2:
                    System.out.println("Falha ao tocar, tentando novamente!");
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    tocar();
                    break;
                case 3:
                    System.out.println("Falha ao tocar, passando para a próxima música!");
                    abrir(playList.getProxima(), 0, false, true);
                    break;
                case 4:
                    System.out.println("Falha ao tocar (estágio 2), tentando novamente!");
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    tocar();
                    break;
                default:
                    System.out.println("Todas as tentativas falharam... :(");
            }
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public synchronized boolean skipTo(long time) {
        System.out.println(time + "  " + total);
        long skipBytes = (time * total / 1000);
        System.out.println(skipBytes);
        try {
            player.seek(skipBytes);
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
        try {
            player.setPan(new Double(balanco) / 100);
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
        try {
            player.setGain(new Double(volume) / 100);
        } catch (BasicPlayerException ex) {
            ex.printStackTrace();
        }
        return false;


    }

    public String miliSegundosEmMinSeq(Long mili) {
        mili = mili / 1000000;
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        Date date = null;
        try {
            date = sdf.parse(mili.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
    }
    public String miliSegundosEmMinSeq(int mili) {
        mili = mili / 1000000;
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(mili));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
    }

    public void opened(Object stream, Map properties) {
        total = new Long((Integer) properties.get("audio.length.bytes"));
      
        Encoding enc;

        try {

            enc = AudioSystem.getAudioFileFormat((File) stream).getFormat().getEncoding();
            if (enc.toString().toLowerCase().indexOf("vorbis") != -1) {
                tipo = "ogg";
            }
            if (enc.toString().toLowerCase().indexOf("mpeg") != -1) {
                tipo = "mp3";
            }
            System.out.println("Tipo: "+ tipo);
            String info = properties.get("title") + " " + properties.get("author") + " " + properties.get("album");
            String duracao = miliSegundosEmMinSeq((Long) properties.get("duration"));
            int bits = (Integer) properties.get(tipo + ".bitrate.nominal.bps") / 1000;
            int freq = (Integer) properties.get(tipo + ".frequency.hz") / 1000;
            if (info.trim().equalsIgnoreCase("") || info.trim().equalsIgnoreCase("null null null")) {
                try {
                    info = getMusica().getNome();
                } catch (Exception ex) {
                    System.out.println("Erro em opened");
                }
            }
            principal.atualizaLabels(info, bits, duracao, freq);
            mini.setNomeMusica(info);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void progress(int i, long l, byte[] bytes, Map properties) {
        tempo = Integer.parseInt(String.valueOf((Long) properties.get(tipo + ".position.byte") * 1000 / total));
        if (!jSliderBarPressed()) {
            principal.atualizaTempo(tempo);
        }
        String hms = miliSegundosEmMinSeq(i);
        principal.atualizaTempo(hms);

    }

    public void stateUpdated(BasicPlayerEvent event) {
        switch (event.getCode()) {
            case BasicPlayerEvent.STOPPED:
                principal.atualizaIcone("jButton_Play", icones.playIcon);
                mini.setPlayIcon(icones.playIcon);

                tocando = false;
                paused = false;
                if (!ajust) {
                    principal.atualizaTempo(0);
                }
                break;
            case BasicPlayerEvent.PLAYING:

                tocando = true;
                paused = false;
                principal.atualizaIcone("jButton_Play", icones.pauseIcon);
                mini.setPlayIcon(icones.pauseIcon);

                break;
            case BasicPlayerEvent.RESUMED:
                principal.atualizaIcone("jButton_Play", icones.pauseIcon);
                mini.setPlayIcon(icones.pauseIcon);
                tocando = true;
                paused = false;

                break;
            case BasicPlayerEvent.PAUSED:
                tocando = true;
                paused = true;
                principal.atualizaIcone("jButton_Play", icones.playIcon);
                mini.setPlayIcon(icones.playIcon);

                break;

            case BasicPlayerEvent.GAIN:
                if (Math.abs(event.getValue() * 100 - volume) > 2) {
                    try {
                        player.setGain(new Double(volume) / 100);
                    } catch (BasicPlayerException ex) {
                        System.out.println("Erro ao mudar o volume!");
                    }
                }
                if (event.getValue() * 100 != balanco) {
                    try {
                        player.setPan(new Double(balanco) / 100);
                    } catch (BasicPlayerException ex) {
                        System.out.println("Erro ao mudar o balanço");
                    }
                }
                break;
            case BasicPlayerEvent.SEEKED:
                tocando = true;
                paused = false;
                break;
            case BasicPlayerEvent.SEEKING:
                tocando = true;
                paused = false;
                break;
            case BasicPlayerEvent.EOM:
                abrir(playList.getProxima(), 0, false, true);
                break;

        }

    }

    public void setController(BasicController bc) {
        System.out.println("Controller= " + bc.toString());
    }
}
