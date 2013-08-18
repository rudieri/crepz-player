package com.conexao;

import com.utils.ComandosSO;
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
        s.setDatabasePath(0, ComandosSO.getLocalCrepzPath() + bd);
        s.start();
    }

    public void stop() {
        ativo = false;
        s.shutdown();
        s.stop();
    }

    public boolean isAtivo(){
        return ativo;
    }
}
