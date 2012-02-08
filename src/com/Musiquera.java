/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.graficos.Icones;
import com.musica.Musica;
import com.musica.MusicaBD;
import com.musica.Tempo;
import com.utils.Warning;
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
public abstract class Musiquera implements BasicPlayerListener {

    //  private boolean jSliderBarPressed = false;
    private boolean tocando;
    private boolean paused;
    private boolean ajust;
    private byte volume;
    private byte balanco;
    private int tempoAtual;
    private int tenteiTocar = 0;
    private int tenteiAbrir = 0;
    private long totalTempo = 1;
    private int totalBytes = 1;
    Icones icones;
    BasicPlayer player;
    private Musica musica;
    private File in;
    private String tipo = "";

    @SuppressWarnings("LeakingThisInConstructor")
    public Musiquera() {
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);
        player.setSleepTime(15);
        volume = 50;
        balanco = 50;
    }

    public abstract void numberTempoChange(double s);

    public abstract void stringTempoChange(String hms);

//    public abstract void stringTempoTotalChange(String hms);
    public abstract void eventoOcorreuNaMusica(int evt);

    public abstract Musica getNextMusica();

    public abstract Musica getPreviousMusica();

    public abstract void setPropriedadesMusica(PropriedadesMusica propriedadesMusica);

    public abstract void atualizaLabels(String nome, int bits, String tempo, int freq);

    public void setVolume(byte v) {
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

    public void setBalanco(byte b) {
        balanco = b;
        try {
            player.setPan(new Double(b) / 100);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getTempoAtual() {
        return tempoAtual;
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

    public boolean apenasAbrir(Musica m) throws BasicPlayerException {
        try {
            this.musica = m;
            if (m == null) {
                System.out.println("Musica não existe, nullPointer");
                return false;
            }
            try {
                MusicaBD.carregar(musica);
            } catch (Exception ex) {
                Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
            }
            in = new File(m.getCaminho());
            if (in == null) {
                return false;
            }
            player.open(in);
            return true;
        } catch (BasicPlayerException ex) {
            throw ex;
        }
    }

    public void abrirETocar() {
        abrir(getNextMusica(), 0, false);
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public void abrir(Musica m, int posicao, boolean abrirComPausa) {
        try {
            boolean abriu = apenasAbrir(m);
            if (!abriu) {
                return;
            }

            if (posicao > 0) {
                ajust = true;
                skipTo(posicao);
                ajust = false;
                tocarPausar();
                if (abrirComPausa) {
                    tocarPausar();
                }
            } else {
                tocarPausar();
            }
            tenteiAbrir = 0;


        } catch (Exception ex) {
            tenteiAbrir++;
            System.out.println(ex.getMessage());
            if (ex.getMessage().toString().indexOf("FileNotFoundException") != -1) {
                Warning.print(ex.getMessage());
                try {
                    MusicaBD.excluir(m);
                    //                Operacoes.moverMusicaParaEstragadas(m);
                } catch (Exception ex1) {
                    Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            switch (tenteiAbrir) {
                case 1:
                case 2:
                    System.out.println("Falha ao abrir, tentando novamente!");
                    abrir(m, posicao, abrirComPausa);
                    break;
                case 3:
                    System.out.println("Falha ao abrir, passando para a próxima música!");
                    abrir(getNextMusica(), 0, false);
                    break;
                case 4:
                    System.out.println("Falha ao abrir (estágio 2), tentando novamente!");
                    abrir(m, posicao, abrirComPausa);
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
            tocarPausar();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void parar() {
        try {
            player.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tocarPausar() {
        try {
            int estado = player.getStatus();
            System.out.println(estado);

            if (estado == BasicPlayer.UNKNOWN) {
                System.out.println("Estado UNKNOWN");
                player.open(in);
            }


            switch (estado) {
                case BasicPlayer.PLAYING:
                    player.pause();
                    setEstado(true, true);
                    break;
                case BasicPlayer.PAUSED:
                    player.resume();
                    setEstado(true, false);
                    break;

                case BasicPlayer.STOPPED:
                    player.play();
                    setEstado(true, true);
                    break;
                case BasicPlayer.OPENED:
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
                    tocarPausar();
                    break;
                case 3:
                    System.out.println("Falha ao tocar, passando para a próxima música!");
                    break;
                case 4:
                    System.out.println("Falha ao tocar (estágio 2), tentando novamente!");
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    tocarPausar();
                    break;
                default:
                    System.out.println("Todas as tentativas falharam... :(");
            }
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public synchronized void skipTo(int segundos) {
        //DoIt
        double micro = segundos * 1000000;
        skipTo(micro / (double) totalTempo);
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public synchronized void skipTo(double timePorcent) {
        System.out.println("Skip to: " + timePorcent + "%");
        long skipBytes = (long) (timePorcent * totalBytes);
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

    }

    @SuppressWarnings("CallToThreadDumpStack")
    public String microSegundosEmMinSeq(long micro) {
        try {
            micro = micro / 1000000;
            SimpleDateFormat sdf = new SimpleDateFormat("ss");
            Date date = sdf.parse(String.valueOf(micro));
            return new SimpleDateFormat("HH:mm:ss").format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public String miliSegundosEmMinSeq(int mili) {
        mili = mili / 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("ss");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(mili));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new java.text.SimpleDateFormat("HH:mm:ss").format(date);
    }

    @Override
    public void opened(Object stream, Map properties) {
        PropriedadesMusica propriedadesMusica = new PropriedadesMusica();
        totalTempo = (Long) properties.get("duration");
        totalBytes = (Integer) properties.get("audio.length.bytes");
//        stringTempoTotalChange(microSegundosEmMinSeq(totalTempo));
        Encoding enc;

        try {

            enc = AudioSystem.getAudioFileFormat((File) stream).getFormat().getEncoding();
            if (enc.toString().toLowerCase().indexOf("vorbis") != -1) {
                tipo = "ogg";
            }
            if (enc.toString().toLowerCase().indexOf("mpeg") != -1) {
                tipo = "mp3";
            }
            System.out.println("Tipo: " + tipo);
            String info = properties.get("title") + " " + properties.get("author") + " " + properties.get("album");
//            String duracao = microSegundosEmMinSeq((Long) properties.get("duration"));
            int bits = (Integer) properties.get(tipo + ".bitrate.nominal.bps") / 1000;
            int freq = (Integer) properties.get(tipo + ".frequency.hz") / 1000;
            if (info.trim().isEmpty() || info.trim().equalsIgnoreCase("null null null")) {
                try {
                    info = getMusica().getNome();
                } catch (Exception ex) {
                    System.out.println("Erro em opened, " + ex.toString());
                }
            }
            atualizaLabels(info, bits, microSegundosEmMinSeq(totalTempo), freq);
            propriedadesMusica.setBytesTotal(totalBytes);
            propriedadesMusica.setTempoTotal(totalTempo);
            propriedadesMusica.setNome((String)properties.get("title"));
            propriedadesMusica.setArtista((String)properties.get("author"));
            propriedadesMusica.setAlbum((String)properties.get("album"));
            if (propriedadesMusica.getNome()==null || propriedadesMusica.getNome().isEmpty()) {
                propriedadesMusica.setNome(musica.getNome());
            }
            setPropriedadesMusica(propriedadesMusica);
            // principal.atualizaLabels(info, bits, duracao, freq);
            // mini.setNomeMusica(info);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map properties) {
//        numberTempoChange(new Double(l)/totalTempo);
        double prop = new Double(i) / totalBytes;
        numberTempoChange(prop);
        String hms = microSegundosEmMinSeq((long) (totalTempo * prop));
        stringTempoChange(hms);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        switch (event.getCode()) {
            case BasicPlayerEvent.STOPPED:
                tocando = false;
                paused = false;
                if (!ajust) {
                    numberTempoChange(0);
                }
                break;
            case BasicPlayerEvent.PLAYING:

                tocando = true;
                paused = false;
                break;
            case BasicPlayerEvent.RESUMED:
                tocando = true;
                paused = false;

                break;
            case BasicPlayerEvent.PAUSED:
                tocando = true;
                paused = true;
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
                abrir(getNextMusica(), 0, false);
                break;

        }

    }

    @Override
    public void setController(BasicController bc) {
        System.out.println("Controller= " + bc.toString());
    }

    public class PropriedadesMusica {

        private Tempo tempoTotal;
        private long bytesTotal;
        private String nome;
        private String artista;
        private String album;

        public void setTempoTotal(long tempoTotalMicrossegundos) {
            this.tempoTotal = new Tempo(tempoTotalMicrossegundos);
        }

        /**Retorna o tempo total em microssegundos*/
        public Tempo getTempoTotal() {
            return tempoTotal;
        }

        public void setTempoTotal(Tempo tempoTotal) {
            this.tempoTotal = tempoTotal;
        }

        public long getBytesTotal() {
            return bytesTotal;
        }

        public void setBytesTotal(long bytesTotal) {
            this.bytesTotal = bytesTotal;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getArtista() {
            return artista;
        }

        public void setArtista(String artista) {
            this.artista = artista;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

    }
}
