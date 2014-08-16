/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import gsearch.Client;
import gsearch.Result;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author manchini
 */
public class TesteImagem {

    public static void main(String[] args) {

        Client c = new Client();

        List<Result> lolcats = c.searchImages("eu nao sei");
        for (int i = 0; i < lolcats.size(); i++) {
            try {
                Result rs = lolcats.get(i);
                URL link = new URL(rs.getUrl());

                File file = new File("/home/manchini/nomeAlbum" + i + ".jpg");
//Serve the file
                InputStream in = link.openStream();
                FileOutputStream out = new FileOutputStream(file);
                byte[] buf = new byte[4 * 1024]; // 4K buffer
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }
                out.flush();
                out.close();

            } catch (Exception ex) {
                Logger.getLogger(TesteImagem.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }
}
