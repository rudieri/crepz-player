/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.conexao;

import com.musica.MusicaGerencia;
import com.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Rudieri T. Colbek
 */
public class Construtor {
    String[] generos = ("Blues,Classic Rock,Country,Dance,Disco,Funk,Grunge,Hip-Hop,Jazz,Metal,New Age,Oldies,Other,"
            + "Pop,R&B,Rap,Reggae,Rock,Techno,Industrial,Alternative,Ska,Death Metal,Pranks,Soundtrack,Euro-Techno,Ambient,Trip-Hop,"
            + "Vocal,Jazz+Funk,Fusion,Trance,Classical,Instrumental,Acid,House,Game,Sound Clip,Gospel,Noise,Altern Rock,Bass,Soul,Punk,"
            + "Space,Meditative,Instrumental Pop,Instrumental Rock,Ethnic,Gothic,Darkwave,Techno-Industrial,Electronic,Pop-Folk,Eurodance,"
            + "Dream,Southern Rock,Comedy,Cult,Gangsta,Top 40,Christian Rap,Pop/Funk,Jungle,Native American,Cabaret,New Wave,Psychadelic,Rave,"
            + "Showtunes,Trailer,Lo-Fi,Tribal,Acid Punk,Acid Jazz,Polka,Retro,Musical,Rock & Roll,Hard Rock,Folk,Folk/Rock,National Folk," + ""
            + "Swing,Bebob,Latin,Revival,Celtic,Bluegrass,Avantgarde,Gothic Rock,Progressive Rock,Psychedelic Rock,Symphonic Rock,Slow Rock,"
            + "Big Band,Chorus,Easy Listening,Acoustic,Humor,Speech,Chanson,Opera,Chamber Music,Sonata,Symphony,Booty Bass,Primus,Porn Groove,"
            + "Satire,Slow Jam,Club,Tango,Samba,Folclore").split(",");

    public void construir(Connection conn){
         Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(FileUtils.leArquivo(new File(getClass().getResource("/com/conexao/All.sql").toURI())).toString());
        
        } catch (SQLException ex) {
            Logger.getLogger(Construtor.class.getName()).log(Level.SEVERE, null, ex);
            return ;
        }
         catch(IOException ex){
             JOptionPane.showMessageDialog(null, "Erro ao construir banco de dados, All.sql não foi encontrado!", "Problemas...", JOptionPane.ERROR_MESSAGE);
             return ;
         }
        catch(URISyntaxException ex){
            ex.printStackTrace();
            return ;
        }
        
        for (int i= 0; i < generos.length; i++) {
            MusicaGerencia.addGenero(generos[i]);
        }
    }
}
