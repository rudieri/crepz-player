package com.musica;

import com.config.Configuracoes;
import com.main.gui.JPrincipal;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jlgui.basicplayer.*;

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
    private BasicPlayer player;
    private MusicaS musica;
    private File in;
    /**
     * Variável usada para saber se o núermo de reproduções já foi alterado.
     */
    private boolean naoAlterouMusicaAinda;
    private int contaFalhas;

    @SuppressWarnings("LeakingThisInConstructor")
    public Musiquera() {
        player = new BasicPlayer();
        player.addBasicPlayerListener(this);
        player.setSleepTime(15);

        setBalanco(Configuracoes.BALANCO.getValor());
        setVolume(Configuracoes.VOLUME.getValor());
    }

    public abstract void numberTempoChange(double s);

    public abstract void stringTempoChange(String hms);

//    public abstract void stringTempoTotalChange(String hms);
    public abstract void eventoOcorreuNaMusica(int evt);

    public abstract MusicaS getNextMusica();

    public abstract MusicaS getPreviousMusica();

    public abstract void setPropriedadesMusica(PropriedadesMusica propriedadesMusica);

    public abstract void atualizaLabels(String nome, int bits, String tempo, int freq);
    
    public final void setVolume(byte v) {
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

    public final void setBalanco(byte b) {
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

    public MusicaS getMusica() {
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
    
    public List<String> getMixers(){
        return player.getMixers();
    }
    
    public void setMixerName(String mixer){
        player.setMixerName(mixer);
    }

    public boolean apenasAbrir(MusicaS m) throws BasicPlayerException {
        try {
            this.musica = m;
            if (m == null) {
                System.out.println("Musica não existe!");
                System.err.print(Arrays.toString(Thread.currentThread().getStackTrace()));
                return false;
            }
            in = new File(musica.getCaminho());
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

    public final void abrirArquivo(String f) {
        abrirArquivo(new File(f));
    }

    public void abrirArquivo(File f) {
        try {
            player.open(f);
            player.play();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void abrir(MusicaS m, int posicao, boolean abrirComPausa) {
        abrir(m, segundoToMicro(posicao), abrirComPausa);
    }

    public void abrir(MusicaS m, long posicao, boolean abrirComPausa) {
        boolean consegueiuAbrir = abrirForaLinhaTempo(m, posicao, abrirComPausa);
        if (consegueiuAbrir) {
            LinhaDoTempo.adicionarNaPosicaoAtual(musica);
        }
    }

    private boolean abrirForaLinhaTempo(MusicaS m, long posicao, boolean abrirComPausa) {
        try {
            boolean abriu = apenasAbrir(m);
            if (!abriu) {
                return false;
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

            return true;
        } catch (BasicPlayerException ex) {
            tenteiAbrir++;
            if (ex.getMessage() != null && ex.getMessage().toString().indexOf("FileNotFoundException") != -1) {
                try {
                    m.setPerdida(true);
//                    Cache.removerMusica(musica);
                    //                Operacoes.moverMusicaParaEstragadas(m);
                } catch (Exception ex1) {
                    Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            ex.printStackTrace(System.err);
            if (tenteiAbrir <= 2) {
                System.out.println("Falha ao abrir, tentando novamente!");
                return abrirForaLinhaTempo(m, posicao, abrirComPausa);
            } else if (tenteiAbrir == 3) {
                System.out.println("Falha ao abrir, passando para a próxima música!");
                return abrirForaLinhaTempo(getNextMusica(), 0, false);
            } else if (tenteiAbrir == 4) {
                System.out.println("Falha ao abrir (estágio 2), tentando novamente!");
                return abrirForaLinhaTempo(m, posicao, abrirComPausa);
            } else if (tenteiAbrir <= 30) {
                System.out.println("Falha ao abrir (está ficando \"danger\"), tentando novamente!");
                return abrirForaLinhaTempo(getNextMusica(), posicao, abrirComPausa);
            } else {
                System.out.println("Todas as tentativas falharam... :(");
                tenteiAbrir = 0;
                return false;
            }
        }

    }

    public void tocarProxima() {
        MusicaS proxima = LinhaDoTempo.getProxima();
        if (proxima != null) {
            boolean conseguiu = abrirForaLinhaTempo(proxima, 0, false);
            if (!conseguiu) {
                contaFalhas++;
                if (contaFalhas < 5) {
                    tocarProxima();
                }
            } else {
                contaFalhas = 0;
            }
        } else {
            abrir(getNextMusica(), 0, false);
        }
    }

    public void tocarAnterior() {
        MusicaS anterior = LinhaDoTempo.getAnterior();
        if (anterior != null) {
            abrirForaLinhaTempo(anterior, 0, false);
        } else {
            abrir(getPreviousMusica(), 0, false);
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


        } catch (BasicPlayerException ex) {
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

    public long segundoToMicro(int segundos) {
        return segundos * 1000000;
    }

    public synchronized void skipTo(int segundos) {
        skipTo(segundoToMicro(segundos));
    }

    public synchronized void skipTo(long microssegundos) {
        skipTo(((double) microssegundos) / (double) totalTempo);
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public synchronized void skipTo(double timePorcent) {
        long skipBytes = (long) (timePorcent * totalBytes);
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
            int segundos = (int) (micro / 1000000);
            int horas = segundos / 3600;
            segundos -= horas * 3600;
            int minutos = segundos / 60;
            segundos -= minutos * 60;
//            Date date = sdf.parse(String.valueOf(segundos));
//            return formatotempo.format(date);
            StringBuilder tempo = new StringBuilder(12);
            if (horas > 0) {
                if (horas < 10) {
                    tempo.append('0').append(horas).append(':');
                } else {
                    tempo.append(horas).append(':');
                }
            }
            if (minutos < 10) {
                tempo.append('0').append(minutos).append(':');
            } else {
                tempo.append(minutos).append(':');
            }
            if (segundos < 10) {
                tempo.append('0').append(segundos);
            } else {
                tempo.append(segundos);
            }
            return tempo.toString();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return "";
    }

    @Override
    public void opened(Object stream, Map properties) {
        naoAlterouMusicaAinda = true;
        PropriedadesMusica propriedadesMusica = new PropriedadesMusica();
        totalTempo = (Long) properties.get("duration");
        totalBytes = (Integer) properties.get("audio.length.bytes");
//        stringTempoTotalChange(microSegundosEmMinSeq(totalTempo));
        Encoding enc;

        String tipo = "";
        try {

            enc = AudioSystem.getAudioFileFormat((File) stream).getFormat().getEncoding();
            if (enc.toString().toLowerCase().indexOf("vorbis") != -1) {
                tipo = "ogg";
            }
            if (enc.toString().toLowerCase().indexOf("mpeg") != -1) {
                tipo = "mp3";
            }
            String info = properties.get("title") + " " + properties.get("author") + " " + properties.get("album");
            info = info.trim();
//            String duracao = microSegundosEmMinSeq((Long) properties.get("duration"));
            int bits = (Integer) properties.get(tipo + ".bitrate.nominal.bps") / 1000;
            int freq = (Integer) properties.get(tipo + ".frequency.hz") / 1000;
            if (info.isEmpty() || info.equalsIgnoreCase("null null null")) {
                try {
                    info = getMusica().getNome();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
            atualizaLabels(info, bits, microSegundosEmMinSeq(totalTempo), freq);
            propriedadesMusica.setBytesTotal(totalBytes);
            propriedadesMusica.setTempoTotal(totalTempo);
            propriedadesMusica.setNome((String) properties.get("title"));
            propriedadesMusica.setArtista((String) properties.get("author"));
            propriedadesMusica.setAlbum((String) properties.get("album"));
            if (propriedadesMusica.getNome() == null || propriedadesMusica.getNome().isEmpty()) {
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
        if (naoAlterouMusicaAinda && prop > 0.8) {
            try {
                naoAlterouMusicaAinda = false;
                musica.setNumeroReproducoes((short) (musica.getNumeroReproducoes() + 1));
            } catch (Exception ex) {
                Logger.getLogger(Musiquera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        numberTempoChange(prop);
        tempoAtual = (int) (totalTempo * prop);
        String hms = microSegundosEmMinSeq((long) (tempoAtual));
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
                reajustarVolumeEBalanco(event);
                break;
            case BasicPlayerEvent.RESUMED:
                tocando = true;
                paused = false;
                reajustarVolumeEBalanco(event);
                break;
            case BasicPlayerEvent.PAUSED:
                tocando = true;
                paused = true;
                break;
            case BasicPlayerEvent.GAIN:
            case BasicPlayerEvent.PAN:
                reajustarVolumeEBalanco(event);
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
                if (isRepeat()) {
                    abrirForaLinhaTempo(musica, 0, false);
                } else {
                    tocarProxima();
                }
                break;

        }

    }

    public boolean isRepeat() {
        return Configuracoes.REPEAT_ATIVO.getValor();
    }

    @Override
    public void setController(BasicController bc) {
        System.out.println("Controller= " + bc.toString());
    }

    private void reajustarVolumeEBalanco(BasicPlayerEvent event) {
        if (event.getCode() != BasicPlayerEvent.PAN && Math.abs(event.getValue() * 100 - volume) > 2) {
            try {
                player.setGain(new Double(volume) / 100);

            } catch (BasicPlayerException ex) {
                ex.printStackTrace(System.err);
            }
        }
        if (event.getCode() != BasicPlayerEvent.GAIN && (int)(event.getValue() * 100) != balanco) {
            try {
                player.setPan(new Double(balanco) / 100);
            } catch (BasicPlayerException ex) {
                ex.printStackTrace(System.err);
            }
        }
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

        /**
         * Retorna o tempo total em microssegundos
         * @return 
         */
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
            this.artista = artista == null ? "" : artista;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }
}
