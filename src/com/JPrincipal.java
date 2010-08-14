package com;

import com.conexao.BD;
import com.config.GerenciadorConfig;
import com.help.JHelp;
import com.help.JSobre;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;
import com.musica.Musica;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on 29/05/2010, 08:38:25
 */
/**
 *
 * @author Crepz Player
 */
public class JPrincipal extends javax.swing.JFrame implements BasicPlayerListener {

    public static Aguarde aguarde = new Aguarde();
    /** Creates new form NewJFrame */
    BasicPlayer player = new BasicPlayer();
    // BasicPlayer is a BasicController.
    BasicController tocador = (BasicController) player;
    //Configura��es salvas
    private Musica musica;
    File in = null;
    Long total = new Long(1);
    private int estado=0;
    private JFileChooser jFileChooser = new JFileChooser();
    private SystemTray tray;
    private TrayIcon trayIcon;
    private JBiBlioteca biblioteca = new JBiBlioteca(this);
    private JPlayList playList = new JPlayList(this, false, this);
    final JMini jmini = new JMini(this, false, this, playList, biblioteca);
    GerenciadorConfig _conf = new GerenciadorConfig(this, playList, biblioteca, jmini);

    public JPrincipal() {
        initComponents();
        loadIcons("tipo2");
        player.addBasicPlayerListener(this);
        try {
            aguarde.intro();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _conf.getAllValores();

//        in = new File("D:/Musicas/David_Guetta_-_One_Love-2009-MOD/David Guetta _ Chris Willis ft Fergie _ LMFAO - Gettin_ Over.mp3");

        playList.setAleatorio(random);
        playList.setRepetir(repeat);
        this.setIconImage(new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage());
        playList.posicionar();

        inicializaIcones();

        trays();
    }

    public void trays() {
        if (isVisible()) {
            if (bandeija) {
                iconeTray();
            }
        } else {
            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    trays();
                }
            }, 20);
        }
    }

    public BasicPlayer getPlayer() {
        return player;
    }

    public void setMusica(File in) {
        this.in = in;
    }

    public void setMusica(Musica m) {
        this.musica = m;
    }

    public Musica getMusica() throws Exception {
        return musica;
    }

    public boolean getTocando() {
        return tocando;
    }

    public boolean getPause() {
        return paused;
    }

    public Long getTempo() {
        return tempo;
    }

    public boolean isRandom() {
        return random;
    }

    public void setRandom(boolean b) {
        random = b;
    }

    public boolean getRepetir() {
        return repeat;
    }

    public void setRepetir(boolean b) {
        repeat = b;
    }

    public void setVolume(int v) {
        jSlider_vol.setValue(v);
    }

    public int getVolume() {
        return jSlider_vol.getValue();
    }

    public void setBalaco(int b) {
        jSlider_Balanco.setValue(b);
    }

    public int getBalanco() {
        return jSlider_Balanco.getValue();
    }

    public void setBandeija(boolean b) {
        bandeija = b;
    }

    public boolean isBandeija() {
        return bandeija;
    }

    /*   public void setTocando(boolean b) {
    tocando = b;
    }
     */
    public void parar() {
        try {
            jButton_Play.setToolTipText("PLAY");

            jCIMenuPlay.setText("Tocar");
            tocador.stop();
            jSlider_Tempo.setValue(0);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void abrir(Musica m) {
        try {
            this.musica = m;
            in = new File(m.getCaminho());
            tocador.open(in);
            tocar();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void apenasAbrir(Musica m, Long toc) {
        try {
            this.musica = m;
            in = new File(m.getCaminho());
            tocador.open(in);
            if (toc > 0) {
                tempo = toc;
                ajust = true;
                jSlider_Tempo.setValue(tempo.intValue());
                skipTo();
                ajust = false;
            }
            paused = true;
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void abrir(Musica m, Long t) {
        try {
            this.musica = m;
            in = new File(m.getCaminho());
            tocador.open(in);
            player.open(in);
            player.play();
            tempo = t;
            ajust = true;
            jSlider_Tempo.setValue(tempo.intValue());
            skipTo();
            ajust = false;
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tocar() {
        try {
            tarefa = new Timer();
            if (player.getStatus() == BasicPlayer.UNKNOWN) {
                jButton_Play.setToolTipText("PAUSE");
                jCIMenuPlay.setText("Pausar");
                tarefa.schedule(new tarefaPlay(), 50);

            }

            switch (player.getStatus()) {
                case BasicPlayer.PAUSED:
                    jButton_Play.setToolTipText("PAUSE");
                    jCIMenuPlay.setText("Pausar");
                    tarefa.schedule(new tarefaPlay(), 5);
                    break;
                case BasicPlayer.PLAYING:
                    jButton_Play.setToolTipText("PLAY");
                    jCIMenuPlay.setText("Tocar");
                    tarefa.schedule(new tarefaPlay(), 5);

                    break;
                case BasicPlayer.STOPPED:
                    jButton_Play.setToolTipText("PAUSE");
                    jCIMenuPlay.setText("Pausar");
                    tarefa.schedule(new tarefaPlay(), 40);
                    break;
                case BasicPlayer.OPENED:
                    jButton_Play.setToolTipText("PAUSE");
                    jCIMenuPlay.setText("Pausar");
                    tarefa.schedule(new tarefaPlay(), 50);
            }



        } catch (Exception ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void skipTo() {
        try {

            long skipBytes = (jSlider_Tempo.getValue() * total / 1000);
            System.out.println(skipBytes);
            try {
                tocador.seek(skipBytes);

            } catch (Exception ex) {
                tocador.seek(skipBytes);
            }
            player.setPan(new Double(jSlider_Balanco.getValue()) / 100);


        } catch (Exception ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void skipTo(Long t) {
        try {
            jSlider_Tempo.setValue(tempo.intValue());
            long skipBytes = (jSlider_Tempo.getValue() * total / 1000);
            tocador.seek(skipBytes);
            player.setPan(new Double(jSlider_Balanco.getValue()) / 100);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class tarefaPlay extends TimerTask {

        @Override
        public void run() {
            try {
                if (player.getStatus() == BasicPlayer.UNKNOWN) {
                    player.open(in);
                }

                switch (player.getStatus()) {
                    case BasicPlayer.PAUSED:
                        try {
                            player.resume();
                        } catch (Exception e) {
                            player.resume();
                        }
                        break;
                    case BasicPlayer.PLAYING:

                        try {
                            player.pause();
                        } catch (Exception ex) {
                            player.pause();
                        }
                        break;
                    case BasicPlayer.STOPPED:
                        try {
                            player.play();
                        } catch (Exception ex) {
                            player.play();
                        }
                        break;
                    case BasicPlayer.OPENED:
                        try {
                            player.play();
                        } catch (Exception e) {
                            player.play();
                        }
                        break;
                }
//                player.setGain(new Double(jSlider_vol.getValue()) / 100);
                Thread.sleep(1000);


            } catch (Exception ex) {
                Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public executaProxima getExecutaProxima() {
        return new executaProxima();
    }

    public executaAnterior getExecutaAnterior() {
        return new executaAnterior();
    }

    public class executaProxima extends TimerTask {

        @Override
        public void run() {
            playList.getProxima();
        }
    }

    public class executaAnterior extends TimerTask {

        @Override
        public void run() {
            playList.getAnterior();
        }
    }

    public void atualizaTempo(int t) {
        jSlider_Tempo.setValue(t);
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

    /**
     * Open callback, stream is ready to play.
     *
     * properties map includes audio format dependant features such as
     * bitrate, duration, frequency, channels, number of frames, vbr flag, ...
     *
     * @param stream could be File, URL or InputStream
     * @param properties audio stream properties.s
     */
    public void opened(Object stream, Map properties) {
// Pay attention to properties. It's useful to get duration,
// bitrate, channels, even tag such as ID3v2.
        display("opened : " + properties.toString());
        total = new Long((Integer) properties.get("audio.length.bytes"));
        jLabel_tempoTotal.setText(miliSegundosEmMinSeq((Long) properties.get("duration")));
        jLabel_bit.setText(String.valueOf((Integer) properties.get("mp3.bitrate.nominal.bps") / 1000) + " Kbps");
        jLabel_freq.setText(String.valueOf((Integer) properties.get("mp3.frequency.hz") / 1000) + " Mhz");
        jLabel_Musica.setText(properties.get("title") + " " + properties.get("author") + " " + properties.get("album"));


    }

    /** * Progress callback while playing.
     *
     * This method is called severals time per seconds while playing.
     * properties map includes audio format features such as
     * instant bitrate, microseconds position, current frame number, ...
     *
     * @param bytesread from encoded stream.
     * @param microseconds elapsed (<b>reseted after a seek !</b>).
     * @param pcmdata PCM samples.
     * @param properties audio stream parameters.
     */
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
// Pay attention to properties. It depends on underlying JavaSound SPI
// MP3SPI provides mp3.equalizer.

        if (!ajust) {
            jSlider_Tempo.setValue(Integer.valueOf(String.valueOf((Long) properties.get("mp3.position.byte") * 1000 / total)));
        }
//        display("progress : " + properties.toString());

        jLabel_tempo.setText(miliSegundosEmMinSeq((Long) properties.get("mp3.position.microseconds")));

        jSlider_Tempo.setToolTipText(miliSegundosEmMinSeq((Long) properties.get("mp3.position.microseconds")));
        tempo = (Long) properties.get("mp3.position.byte") * 1000 / total;
        //  display(tempo + "  " + total);

    }

    /**
     * Notification callback for basicplayer events such as opened, eom ...
     *
     * @param event
     */
    public void stateUpdated(BasicPlayerEvent event) {
// Notification of BasicPlayer states (opened, playing, end of media, ...)
        display("stateUpdated : " + event.toString());
        switch (event.getCode()) {
            case BasicPlayerEvent.STOPPED:
                jButton_Play.setIcon(playIcon);
                jmini.setPlayIcon(resizeIcons(bf_playIcon));
                jCIMenuPlay.setText("Tocar");

                tocando = false;
                paused = false;
                if (!ajust) {
                    jSlider_Tempo.setValue(0);
                }
                break;
            case BasicPlayerEvent.PLAYING:
                if (trayIcon != null && !tocando) {
                    trayIcon.displayMessage("Tocando \n", jLabel_Musica.getText(), TrayIcon.MessageType.INFO);
                }
                tocando = true;
                paused = false;
                jButton_Play.setIcon(pauseIcon);
                jmini.setPlayIcon(resizeIcons(bf_pauseIcon));
                jCIMenuPlay.setText("Pausar");
                break;
            case BasicPlayerEvent.RESUMED:
                jButton_Play.setIcon(pauseIcon);
                jmini.setPlayIcon(resizeIcons(bf_pauseIcon));
                tocando = true;
                paused = false;
                jCIMenuPlay.setText("Pausar");
                break;
            case BasicPlayerEvent.PAUSED:
                tocando = true;
                paused = true;
                jButton_Play.setIcon(playIcon);
                jmini.setPlayIcon(resizeIcons(bf_playIcon));
                jCIMenuPlay.setText("Tocar");
                break;

            case BasicPlayerEvent.GAIN:
                if (Math.abs(event.getValue() * 100 - jSlider_vol.getValue()) > 2) {
                    try {
                        player.setGain(new Double(jSlider_vol.getValue()) / 100);
                    } catch (BasicPlayerException ex) {
                        Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (event.getValue() * 100 != jSlider_Balanco.getValue()) {
                    try {
                        player.setPan(new Double(jSlider_Balanco.getValue()) / 100);
                    } catch (BasicPlayerException ex) {
                        Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
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
                playList.getProxima();
                break;

        }

    }

    /**
     * A handle to the BasicPlayer, plugins may control the player through
     * the controller (play, stop, ...)
     * @param controller : a handle to the player
     */
    public void setController(BasicController controller) {
        display("setController : " + controller);
    }

    public void display(Object msg) {
        System.out.println(msg);

    }

    public File telaAbrirArquivo() throws Exception {

        // restringe a amostra a diretorios apenas
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogTitle("Abrir Arquivo");

        int res = jFileChooser.showOpenDialog(null);

        if (res == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        }
        return null;
//        else {
//            throw new Exception("Voce nao selecionou nenhum diretorio.");
//        }
    }

    public void someTray(int x, int y) {
        tray.remove(trayIcon);
        setVisible(true);
        setLocation(x - this.getWidth() / 2, y - this.getHeight() / 2);
        jmini.dispose();
        setBandeija(false);
    }

    public void someTray() {
        tray.remove(trayIcon);
        setVisible(true);
        jmini.dispose();
        setBandeija(false);
    }

    private void iconeTray() {
        try {
            if (SystemTray.isSupported()) {
                tray = SystemTray.getSystemTray();
                setBandeija(true);
                Image image = new ImageIcon(getClass().getResource("/com/img/icon.png")).getImage();
                ActionListener listener1 = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        someTray();
                    }
                };
                ActionListener listener2 = new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        sair();

                    }
                };

                PopupMenu popup = new PopupMenu();
                MenuItem item1 = new MenuItem("Restaurar");
                MenuItem item2 = new MenuItem("Sair");
                item1.addActionListener(listener1);
                item2.addActionListener(listener2);
                popup.add(item1);
                popup.add(item2);


                trayIcon = new TrayIcon(image, "Jar jar Player", popup);
                trayIcon.setImageAutoSize(true);


                trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getButton() == MouseEvent.BUTTON1) {
                            if (evt.getClickCount() == 2) {
                                tocar();
                                display("Event: 2 cliques");
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        initX = e.getXOnScreen();
                        initY = e.getYOnScreen();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (Math.abs(initX - e.getXOnScreen()) > 1 || Math.abs(initY - e.getYOnScreen()) > 1) {
                                someTray(e.getXOnScreen(), e.getYOnScreen());
                            } else {
                                jmini.setVisible(true, e);
                                display("Event: release");
                            }
                        }
                        if (e.getButton() == MouseEvent.BUTTON2) {
                            someTray();
                        }
                    }
                });

                try {
                    tray.add(trayIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.setVisible(false);
                if(this.getState() == JFrame.ICONIFIED)
                    this.setState(JFrame.NORMAL);
                this.dispose();
                jmini.pack();
                jmini.setVisible(true);

                new Thread(new Runnable() {

                    public void run() {
                        try {
                            Thread.sleep(500);
                            trayIcon.displayMessage("Tocancdo \n", jLabel_Musica.getText(), TrayIcon.MessageType.INFO);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JPlayList getJPlaylist() {
        return playList;
    }

    public void loadIcons(String tipo) {
        try {
            bf_playIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/play.png"));
            bf_stopIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/stop.png"));
            bf_voltaIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/tras.png"));
            bf_frenteIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/frente.png"));
            bf_pauseIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/pause.png"));
            bf_randomOnIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/trueRandom.png"));
            bf_randomOffIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/falseRandom.png"));
            bf_repeatOnIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/repeatOn.png"));
            bf_repeatOffIcon = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/repeatOff.png"));
            bf_save = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/save.png"));
            bf_saveAs = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/saveAs.png"));
            bf_topOn = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_top_on.png"));
            bf_topOff = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_top_off.png"));
            bf_pl = ImageIO.read(getClass().getResource("/com/img/playlist.gif"));
            bf_lib = ImageIO.read(getClass().getResource("/com/img/biblioteca.png"));
            bf_xis = ImageIO.read(getClass().getResource("/com/img/x.png"));
            bf_menu = ImageIO.read(getClass().getResource("/com/img/icons/" + tipo + "/c_menu.png"));

            playIcon = new ImageIcon(bf_playIcon);
            stopIcon = new ImageIcon(bf_stopIcon);
            frenteIcon = new ImageIcon(bf_frenteIcon);
            voltaIcon = new ImageIcon(bf_voltaIcon);
            pauseIcon = new ImageIcon(bf_pauseIcon);
            randomOnIcon = new ImageIcon(bf_randomOnIcon);
            randomOffIcon = new ImageIcon(bf_randomOffIcon);
            repeatOnIcon = new ImageIcon(bf_repeatOnIcon);
            repeatOffIcon = new ImageIcon(bf_repeatOffIcon);
            save = new ImageIcon(bf_save);
            saveAs = new ImageIcon(bf_saveAs);
            topOn = resizeIcons(bf_topOn, 13, 13);
            topOff = resizeIcons(bf_topOff, 13, 13);
            lib = resizeIcons(bf_lib, 13, 13);
            pl = resizeIcons(bf_pl, 13, 13);
            menu = resizeIcons(bf_menu, 13, 13);
            xis = resizeIcons(bf_xis, 13, 13);

            inicializaIcones();
        } catch (IOException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inicializaIcones() {
        if (getPause() && getTocando()) {
            jButton_Play.setIcon(pauseIcon); 
        } else {
           jButton_Play.setIcon(playIcon);
        }
        jButton_Stop.setIcon(stopIcon);
        jButton_Next.setIcon(frenteIcon);
        jButton_Ant.setIcon(voltaIcon);
        if (!(jmini == null)) {
            jmini.inicializaIcones();
        }
        if (random) {
            jToggleButton1.setIcon(randomOnIcon);
        } else {
            jToggleButton1.setIcon(randomOffIcon);
        }
        if (repeat) {
            jToggle_Repeat.setIcon(repeatOnIcon);
        } else {
            jToggle_Repeat.setIcon(repeatOffIcon);
        }
        playList.atualizaIcons();
    }

    @Override
    public synchronized void setState(int state) {
        super.setState(state);
    }

    public void sair() {
        setConf();
        System.exit(0);
    }

    public ImageIcon resizeIcons(BufferedImage im) {
//        int minimo = 17;
//        int maximo = 18;
//        if (im != null) {
//            minimo = Math.min(im.getWidth(), im.getHeight());
//            maximo = Math.max(im.getWidth(), im.getHeight());
//        }
//        int prop = maximo / minimo;
//        if (minimo < 17 && maximo < 20) {
//            System.out.println("padr�o");
//            return new ImageIcon(im.getScaledInstance(im.getWidth(), im.getHeight(), Image.SCALE_SMOOTH));
//        } else {
//            if (im.getWidth() == minimo) {
//                System.out.println("minimo= width");
//                return new ImageIcon(im.getScaledInstance(17, 17 * prop, Image.SCALE_SMOOTH));
//            } else {
//                System.out.println("minimo = height");
        return new ImageIcon(im.getScaledInstance(17, 17, Image.SCALE_SMOOTH));
//            }
//        }

    }

    public ImageIcon resizeIcons(BufferedImage im, int l, int a) {
        return new ImageIcon(im.getScaledInstance(l, a, Image.SCALE_SMOOTH));
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        jmini.setVisible(!b);
        if (random) {
            jToggleButton1.setIcon(randomOnIcon);
        } else {
            jToggleButton1.setIcon(randomOffIcon);
        }
        if (repeat) {
            jToggle_Repeat.setIcon(repeatOnIcon);
        } else {
            jToggle_Repeat.setIcon(repeatOffIcon);
        }
    }

    private void vouParaOnde(MouseEvent e) {
        estado++;
        if(estado==5){
            this.setLocation(e.getXOnScreen() - initX, e.getYOnScreen() - initY);
            estado=0;
        }
    }

    private void ondeEstou(MouseEvent e) {
        initX = e.getXOnScreen() - this.getX();
        initY = e.getYOnScreen() - this.getY();
        thisX = this.getX();
        thisY = this.getY();
    }

    public void mexeVolume(int quanto) {
        jSlider_vol.setValue(quanto);
    }

    public int getSliderValue() {
        return jSlider_vol.getValue();
    }

    public void setConf() {
        playList.salvarPlaylistAtual();
        _conf.setAllValores();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuDeContexto = new javax.swing.JPopupMenu();
        jCMenuReproduz = new javax.swing.JMenu();
        jCIMenuPlay = new javax.swing.JMenuItem();
        jCIMenuStop = new javax.swing.JMenuItem();
        jCMenuVisual = new javax.swing.JMenu();
        jCCheckBarraTitulos = new javax.swing.JCheckBoxMenuItem();
        jCCheckBarraDeMenus = new javax.swing.JCheckBoxMenuItem();
        jCIMenuMinimizar = new javax.swing.JMenuItem();
        jCIMenuFechar = new javax.swing.JMenuItem();
        GrupoSpiner = new javax.swing.ButtonGroup();
        jPanel17 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel_bib = new javax.swing.JLabel();
        jLabel_Playlist = new javax.swing.JLabel();
        jLabel_Edit = new javax.swing.JLabel();
        jLabel_Minimizar = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel_Musica = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel_bit = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel_freq = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel_tempoTotal = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel_tempo = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jSlider_Tempo = new javax.swing.JSlider();
        jPanel2 = new javax.swing.JPanel();
        jButton_Play = new javax.swing.JLabel();
        jButton_Stop = new javax.swing.JLabel();
        jButton_Ant = new javax.swing.JLabel();
        jButton_Next = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JLabel();
        jToggle_Repeat = new javax.swing.JLabel();
        jSlider_vol = new javax.swing.JSlider();
        jPanel16 = new javax.swing.JPanel();
        jSlider_Balanco = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem_Arquivo = new javax.swing.JMenuItem();
        jMenuItem_Arquivo1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        jCMenuReproduz.setText("Reprodução");

        jCIMenuPlay.setText("Tocar");
        jCIMenuPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCIMenuPlayActionPerformed(evt);
            }
        });
        jCMenuReproduz.add(jCIMenuPlay);

        jCIMenuStop.setText("Parar");
        jCIMenuStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCIMenuStopActionPerformed(evt);
            }
        });
        jCMenuReproduz.add(jCIMenuStop);

        jMenuDeContexto.add(jCMenuReproduz);

        jCMenuVisual.setText("Visualização");

        jCCheckBarraTitulos.setSelected(true);
        jCCheckBarraTitulos.setText("Mostrar barra de titulos");
        jCCheckBarraTitulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCCheckBarraTitulosActionPerformed(evt);
            }
        });
        jCMenuVisual.add(jCCheckBarraTitulos);

        jCCheckBarraDeMenus.setSelected(true);
        jCCheckBarraDeMenus.setText("Mostrar barra de menus");
        jCCheckBarraDeMenus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCCheckBarraDeMenusActionPerformed(evt);
            }
        });
        jCMenuVisual.add(jCCheckBarraDeMenus);

        jMenuDeContexto.add(jCMenuVisual);

        jCIMenuMinimizar.setText("Minimizar");
        jCIMenuMinimizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCIMenuMinimizarActionPerformed(evt);
            }
        });
        jMenuDeContexto.add(jCIMenuMinimizar);

        jCIMenuFechar.setText("Sair");
        jCIMenuFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCIMenuFecharActionPerformed(evt);
            }
        });
        jMenuDeContexto.add(jCIMenuFechar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Crepz Player 1.0");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/help/img/help.PNG"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        jPanel17.add(jLabel1);

        jLabel_bib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/biblioteca.png"))); // NOI18N
        jLabel_bib.setToolTipText("Biblioteca");
        jLabel_bib.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_bibMouseClicked(evt);
            }
        });
        jPanel17.add(jLabel_bib);

        jLabel_Playlist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/playlist.gif"))); // NOI18N
        jLabel_Playlist.setToolTipText("Playlist");
        jLabel_Playlist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_PlaylistMouseClicked(evt);
            }
        });
        jPanel17.add(jLabel_Playlist);

        jLabel_Edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/edit.png"))); // NOI18N
        jLabel_Edit.setToolTipText("Edit Propriedades MP3");
        jLabel_Edit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_EditMouseClicked(evt);
            }
        });
        jPanel17.add(jLabel_Edit);

        jLabel_Minimizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icon1616.png"))); // NOI18N
        jLabel_Minimizar.setToolTipText("Minimizar");
        jLabel_Minimizar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_MinimizarMouseClicked(evt);
            }
        });
        jPanel17.add(jLabel_Minimizar);

        getContentPane().add(jPanel17, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(375, 130));
        jPanel1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanel1MouseWheelMoved(evt);
            }
        });
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setPreferredSize(new java.awt.Dimension(397, 30));
        jPanel15.add(jLabel_Musica);

        jPanel3.add(jPanel15);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setLayout(new javax.swing.BoxLayout(jPanel14, javax.swing.BoxLayout.X_AXIS));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(132, 100));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.add(jLabel_bit);

        jPanel5.add(jPanel11);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.add(jLabel_freq);

        jPanel5.add(jPanel12);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(new java.awt.BorderLayout());
        jPanel5.add(jPanel13);

        jPanel14.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(132, 120));
        jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        jPanel14.add(jPanel6);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.Y_AXIS));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_tempoTotal.setText("0:00");
        jPanel10.add(jLabel_tempoTotal);

        jPanel7.add(jPanel10);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jLabel_tempo.setText("0:00");
        jPanel8.add(jLabel_tempo);

        jPanel7.add(jPanel8);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new java.awt.BorderLayout());
        jPanel7.add(jPanel9);

        jPanel14.add(jPanel7);

        jPanel3.add(jPanel14);

        jPanel1.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jSlider_Tempo.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Tempo.setMaximum(1000);
        jSlider_Tempo.setToolTipText("0:00");
        jSlider_Tempo.setValue(0);
        jSlider_Tempo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider_TempoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_TempoMouseReleased(evt);
            }
        });
        jSlider_Tempo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_TempoStateChanged(evt);
            }
        });
        jPanel4.add(jSlider_Tempo, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel4);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMinimumSize(new java.awt.Dimension(248, 35));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
        });
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jButton_Play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/play.png"))); // NOI18N
        jButton_Play.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_PlayMouseEntered(evt);
            }
        });
        jPanel2.add(jButton_Play);

        jButton_Stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/stop.png"))); // NOI18N
        jButton_Stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_StopMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_StopMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_StopMouseExited(evt);
            }
        });
        jPanel2.add(jButton_Stop);

        jButton_Ant.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/tras.png"))); // NOI18N
        jButton_Ant.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_AntMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_AntMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_AntMouseExited(evt);
            }
        });
        jPanel2.add(jButton_Ant);

        jButton_Next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/frente.png"))); // NOI18N
        jButton_Next.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_NextMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton_NextMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton_NextMouseExited(evt);
            }
        });
        jPanel2.add(jButton_Next);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/falseRandom.png"))); // NOI18N
        jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseExited(evt);
            }
        });
        jPanel2.add(jToggleButton1);

        jToggle_Repeat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/img/icons/tipo2/repeatOff.png"))); // NOI18N
        jToggle_Repeat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggle_RepeatMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jToggle_RepeatMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jToggle_RepeatMouseExited(evt);
            }
        });
        jPanel2.add(jToggle_Repeat);

        jSlider_vol.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_vol.setToolTipText("Volume");
        jSlider_vol.setPreferredSize(new java.awt.Dimension(100, 23));
        jSlider_vol.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_volMouseWheelMoved(evt);
            }
        });
        jSlider_vol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider_volMousePressed(evt);
            }
        });
        jSlider_vol.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_volStateChanged(evt);
            }
        });
        jPanel2.add(jSlider_vol);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setMaximumSize(new java.awt.Dimension(50, 2147483647));
        jPanel16.setMinimumSize(new java.awt.Dimension(20, 24));
        jPanel16.setPreferredSize(new java.awt.Dimension(50, 20));
        jPanel16.setLayout(new java.awt.BorderLayout());

        jSlider_Balanco.setBackground(new java.awt.Color(255, 255, 255));
        jSlider_Balanco.setMinimum(-100);
        jSlider_Balanco.setToolTipText("balanço");
        jSlider_Balanco.setValue(0);
        jSlider_Balanco.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jSlider_BalancoMouseWheelMoved(evt);
            }
        });
        jSlider_Balanco.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_BalancoStateChanged(evt);
            }
        });
        jPanel16.add(jSlider_Balanco, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel16);

        jPanel1.add(jPanel2);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenuBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuBar1MousePressed(evt);
            }
        });
        jMenuBar1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jMenuBar1MouseDragged(evt);
            }
        });

        jMenu1.setText("Arquivo");

        jMenuItem_Arquivo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        jMenuItem_Arquivo.setText("Arquivo");
        jMenuItem_Arquivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_ArquivoActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem_Arquivo);

        jMenuItem_Arquivo1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        jMenuItem_Arquivo1.setText("Biblioteca");
        jMenuItem_Arquivo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_Arquivo1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem_Arquivo1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        jMenuItem3.setText("Minimizar");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Editar");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 0));
        jMenuItem1.setText("Propriedades");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, 0));
        jMenuItem4.setText("PlayList");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
        jMenuItem2.setText("Play");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Sobre");

        jMenuItem6.setText("Sobre");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem5.setText("Help");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem7.setText("Restaurar Configuração Original");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-398)/2, (screenSize.height-240)/2, 398, 240);
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider_volStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_volStateChanged
        try {
            player.setGain(new Double(jSlider_vol.getValue()) / 100);
            jSlider_vol.setToolTipText(jSlider_vol.getValue() + "%");
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jSlider_volStateChanged

    private void jSlider_TempoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMouseReleased
        skipTo();
        ajust = false;
    }//GEN-LAST:event_jSlider_TempoMouseReleased

    private void jMenuItem_ArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_ArquivoActionPerformed
        try {
            in = telaAbrirArquivo();
            player.open(in);
            tocar();
        } catch (Exception ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMenuItem_ArquivoActionPerformed

    private void jSlider_volMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_volMouseWheelMoved
        // TODO add your handling code here:
        mexeVolume(jSlider_vol.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jSlider_volMouseWheelMoved

    private void jSlider_volMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_volMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider_volMousePressed

    private void jSlider_TempoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TempoMousePressed
        // TODO add your handling code here:
        ajust = true;
    }//GEN-LAST:event_jSlider_TempoMousePressed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        // TODO add your handling code here:
        ondeEstou(evt);
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        // TODO add your handling code here:
        vouParaOnde(evt);
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jMenuBar1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MousePressed
        // TODO add your handling code here:
        ondeEstou(evt);
    }//GEN-LAST:event_jMenuBar1MousePressed

    private void jMenuBar1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuBar1MouseDragged
        // TODO add your handling code here:
        vouParaOnde(evt);
    }//GEN-LAST:event_jMenuBar1MouseDragged

    private void jCIMenuFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuFecharActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jCIMenuFecharActionPerformed

    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        // TODO add your handling code here:
        ondeEstou(evt);
    }//GEN-LAST:event_jPanel2MousePressed

    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        // TODO add your handling code here:
        vouParaOnde(evt);
    }//GEN-LAST:event_jPanel2MouseDragged

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        // TODO add your handling code here:

        if (evt.getButton() == MouseEvent.BUTTON3 && thisX == this.getX() && thisY == this.getY()) {
            jMenuDeContexto.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanel1MouseReleased

    private void jCCheckBarraTitulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCCheckBarraTitulosActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        this.dispose();
        this.setUndecorated(!jCCheckBarraTitulos.getState());
        this.setVisible(true);
    }//GEN-LAST:event_jCCheckBarraTitulosActionPerformed

    private void jCIMenuPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuPlayActionPerformed
        // TODO add your handling code here:
        tocar();
    }//GEN-LAST:event_jCIMenuPlayActionPerformed

    private void jCIMenuStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuStopActionPerformed
        // TODO add your handling code here:
        parar();
    }//GEN-LAST:event_jCIMenuStopActionPerformed

    private void jCCheckBarraDeMenusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCCheckBarraDeMenusActionPerformed
        // TODO add your handling code here:
        this.dispose();
        jMenuBar1.setVisible(jCCheckBarraDeMenus.getState());
        this.setVisible(true);
    }//GEN-LAST:event_jCCheckBarraDeMenusActionPerformed

    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseReleased
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON3 && thisX == this.getX() && thisY == this.getY()) {
            jMenuDeContexto.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanel2MouseReleased

    private void jSlider_BalancoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_BalancoStateChanged
        try {
            player.setPan(new Double(jSlider_Balanco.getValue()) / 100);
            jSlider_Balanco.setToolTipText(String.valueOf(jSlider_Balanco.getValue() / 100));
        } catch (BasicPlayerException ex) {
            Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jSlider_BalancoStateChanged

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        tocar();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            new JMP3Propriedades(this, true, in).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Abrir Propriedades.\n" + ex);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        iconeTray();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem_Arquivo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_Arquivo1ActionPerformed
        biblioteca.setVisible(true);

        // biblioteca.setModal(true);
    }//GEN-LAST:event_jMenuItem_Arquivo1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        playList.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        new JHelp().setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton_PlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            tocar();
        }
    }//GEN-LAST:event_jButton_PlayMouseClicked

    private void jButton_StopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            parar();
        }
    }//GEN-LAST:event_jButton_StopMouseClicked

    private void jButton_AntMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            tarefa.cancel();
            tarefa = new Timer();
            tarefa.schedule(new executaAnterior(), 10);
        }
    }//GEN-LAST:event_jButton_AntMouseClicked

    private void jButton_NextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            if (!(tarefa == null)) {
                tarefa.cancel();
            }
            tarefa = new Timer();
            tarefa.schedule(new executaProxima(), 10);
        }
    }//GEN-LAST:event_jButton_NextMouseClicked

    private void jToggleButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseClicked
        // TODO add your handling code here:
        random = !random;
        playList.setAleatorio(random);
        if (random) {
            jToggleButton1.setIcon(randomOnIcon);
        } else {
            jToggleButton1.setIcon(randomOffIcon);
        }
        //jToggleButton1.setIcon(new ImageIcon(getClass().getResource("/com/img/icons/tipo2/"+random+"Random.png")));
    }//GEN-LAST:event_jToggleButton1MouseClicked

    private void jSlider_TempoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_TempoStateChanged
        // TODO add your handling code here:
        jmini.atualizaTempo(jSlider_Tempo.getValue());
    }//GEN-LAST:event_jSlider_TempoStateChanged

    private void jSlider_BalancoMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jSlider_BalancoMouseWheelMoved
        // TODO add your handling code here:
        jSlider_Balanco.setValue(jSlider_Balanco.getValue() - evt.getWheelRotation());
    }//GEN-LAST:event_jSlider_BalancoMouseWheelMoved

    private void jPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanel1MouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1MouseWheelMoved

    private void jButton_PlayMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseEntered
        // TODO add your handling code here:
        jmini.objetoRollOver(jButton_Play);
    }//GEN-LAST:event_jButton_PlayMouseEntered

    private void jButton_PlayMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_PlayMouseExited
        // TODO add your handling code here:
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_PlayMouseExited

    private void jButton_StopMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseEntered
        // TODO add your handling code here:
        jmini.objetoRollOver(jButton_Stop);
    }//GEN-LAST:event_jButton_StopMouseEntered

    private void jButton_StopMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_StopMouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_StopMouseExited

    private void jButton_AntMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseEntered
        // TODO add your handling code here:
        jmini.objetoRollOver(jButton_Ant);
    }//GEN-LAST:event_jButton_AntMouseEntered

    private void jButton_AntMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_AntMouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_AntMouseExited

    private void jButton_NextMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseEntered
        // TODO add your handling code here:
        jmini.objetoRollOver(jButton_Next);
    }//GEN-LAST:event_jButton_NextMouseEntered

    private void jButton_NextMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_NextMouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jButton_NextMouseExited

    private void jToggleButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseEntered
        jmini.objetoRollOver(jToggleButton1);
    }//GEN-LAST:event_jToggleButton1MouseEntered

    private void jToggleButton1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jToggleButton1MouseExited

    private void jToggle_RepeatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeatMouseClicked
        // TODO add your handling code here:
        repeat = !repeat;
        playList.setRepetir(repeat);
        if (repeat) {
            jToggle_Repeat.setIcon(repeatOnIcon);
        } else {
            jToggle_Repeat.setIcon(repeatOffIcon);
        }
    }//GEN-LAST:event_jToggle_RepeatMouseClicked

    private void jToggle_RepeatMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeatMouseEntered
        // TODO add your handling code here:
        jmini.objetoRollOver(jToggle_Repeat);
    }//GEN-LAST:event_jToggle_RepeatMouseEntered

    private void jToggle_RepeatMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggle_RepeatMouseExited
        evt.getComponent().repaint();
    }//GEN-LAST:event_jToggle_RepeatMouseExited

    private void jCIMenuMinimizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCIMenuMinimizarActionPerformed
        // TODO add your handling code here:
        iconeTray();
    }//GEN-LAST:event_jCIMenuMinimizarActionPerformed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        // TODO add your handling code here:
//        if (evt.getNewState() == 1) {
            iconeTray();
//            this.setState(0);
//
//        }
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        setConf();
    }//GEN-LAST:event_formWindowClosing

    private void jLabel_bibMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_bibMouseClicked
//        if (evt.getClickCount() == 2) {
        biblioteca.setVisible(true);
//        }
    }//GEN-LAST:event_jLabel_bibMouseClicked

    private void jLabel_PlaylistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_PlaylistMouseClicked
//        if (evt.getClickCount() == 2) {
        playList.setVisible(true);
//        }
    }//GEN-LAST:event_jLabel_PlaylistMouseClicked

    private void jLabel_EditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_EditMouseClicked
//        if (evt.getClickCount() == 2) {
        try {
            new JMP3Propriedades(this, true, in).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Abrir Propriedades.\n" + ex);
            ex.printStackTrace();
        }
//        }
    }//GEN-LAST:event_jLabel_EditMouseClicked

    private void jLabel_MinimizarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_MinimizarMouseClicked
        iconeTray();
    }//GEN-LAST:event_jLabel_MinimizarMouseClicked

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        new JSobre().setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON1) {
            new JHelp().setVisible(true);
        }
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Isso limpar� a biblioteca e a playlist.\nO Crepz Player ser� fechado.\n Est� certo disso ??") == JOptionPane.YES_OPTION) {
            try {
                BD.hadukem();
                super.setVisible(false);
                aguarde.setVisible(true);
                aguarde.fechar();
                aguarde.setAlwaysOnTop(true);
                tarefa = new Timer();
                tarefa.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        System.exit(0);

                    }
                }, 3000);

            } catch (Exception ex) {
                Logger.getLogger(JPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ParseException {


        aguarde.setVisible(true);
        aguarde.standBy();
        File mk = new File("nbproject");
        if (!mk.exists()) {
            mk = new File("log");
            if (!mk.exists()) {
                mk.mkdir();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

            File f = new File(mk.getAbsolutePath() + "/" + format.format(new Date().getTime()) + ".txt");
            if (!f.exists()) {
                try {

                    f.createNewFile();
                    PrintStream saida = new PrintStream(f);
                    System.setOut(saida);
                    System.setErr(saida);
                } catch (IOException ex) {
                    Logger.getLogger(JMini.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        System.setProperty("Quaqua.tabLayoutPolicy", "wrap");


        try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
        new JPrincipal().setVisible(true);

        aguarde.setVisible(false);
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup GrupoSpiner;
    private javax.swing.JLabel jButton_Ant;
    private javax.swing.JLabel jButton_Next;
    private javax.swing.JLabel jButton_Play;
    private javax.swing.JLabel jButton_Stop;
    private javax.swing.JCheckBoxMenuItem jCCheckBarraDeMenus;
    private javax.swing.JCheckBoxMenuItem jCCheckBarraTitulos;
    private javax.swing.JMenuItem jCIMenuFechar;
    private javax.swing.JMenuItem jCIMenuMinimizar;
    private javax.swing.JMenuItem jCIMenuPlay;
    private javax.swing.JMenuItem jCIMenuStop;
    private javax.swing.JMenu jCMenuReproduz;
    private javax.swing.JMenu jCMenuVisual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel_Edit;
    private javax.swing.JLabel jLabel_Minimizar;
    private javax.swing.JLabel jLabel_Musica;
    private javax.swing.JLabel jLabel_Playlist;
    private javax.swing.JLabel jLabel_bib;
    private javax.swing.JLabel jLabel_bit;
    private javax.swing.JLabel jLabel_freq;
    private javax.swing.JLabel jLabel_tempo;
    private javax.swing.JLabel jLabel_tempoTotal;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu jMenuDeContexto;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem_Arquivo;
    private javax.swing.JMenuItem jMenuItem_Arquivo1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSlider jSlider_Balanco;
    private javax.swing.JSlider jSlider_Tempo;
    private javax.swing.JSlider jSlider_vol;
    private javax.swing.JLabel jToggleButton1;
    private javax.swing.JLabel jToggle_Repeat;
    // End of variables declaration//GEN-END:variables
    DefaultBoundedRangeModel md = new DefaultBoundedRangeModel();
    Timer tarefa;
    boolean tocando = false;
    boolean paused = false;
    boolean ajust = false;
    boolean random = false;
    boolean repeat = false;
    boolean trayEvent = true;
    boolean bandeija = false;
    int initX;
    int initY;
    int thisX;
    int thisY;
    Long tempo;
    ImageIcon playIcon;
    ImageIcon pauseIcon;
    ImageIcon stopIcon;
    ImageIcon randomOnIcon;
    ImageIcon randomOffIcon;
    ImageIcon frenteIcon;
    ImageIcon voltaIcon;
    ImageIcon repeatOnIcon;
    ImageIcon repeatOffIcon;
    ImageIcon save;
    ImageIcon saveAs;
    ImageIcon topOn;
    ImageIcon topOff;
    ImageIcon lib;
    ImageIcon pl;
    ImageIcon menu;
    ImageIcon xis;
    BufferedImage bf_playIcon;
    BufferedImage bf_pauseIcon;
    BufferedImage bf_stopIcon;
    BufferedImage bf_randomOnIcon;
    BufferedImage bf_randomOffIcon;
    BufferedImage bf_frenteIcon;
    BufferedImage bf_voltaIcon;
    BufferedImage bf_repeatOnIcon;
    BufferedImage bf_repeatOffIcon;
    BufferedImage bf_save;
    BufferedImage bf_saveAs;
    BufferedImage bf_topOn;
    BufferedImage bf_topOff;
    BufferedImage bf_lib;
    BufferedImage bf_pl;
    BufferedImage bf_menu;
    BufferedImage bf_xis;
}
