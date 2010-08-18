package com.conexao;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hsqldb.server.Server;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author manchini
 */
public class BancoServer {

    Server s;
    boolean ativo = true;

    public BancoServer(String bd) {
        s = new Server();
        s.setDatabaseName(0, "BD");
        s.setDatabasePath(0, new File("").getAbsolutePath() + "/" + bd);
        s.start();
        new Thread(new Runnable() {

            public void run() {
                while (ativo) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                s.shutdown();
                s.stop();
            }
        }).start();

    }

    public void stop() {
        ativo = false;
    }

    public boolean isAtivo(){
        return ativo;
    }
}
