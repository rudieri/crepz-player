package com.playlist;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.playlist.listainteligente.condicao.CondicaoBD;
import java.sql.*;
import java.util.*;

/**
 *
 * @author -moNGe_
 */
public class PlaylistBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "playlist";

    /**
     * Método que consiste a BK de Musica.
     * @param playlist
     * @throws Exception  
     */
    public static void consistirBK(Playlist playlist) throws Exception {
        if (playlist == null) {
            throw new Exception(" - Playlist não informado.");
        }

        if (playlist.getNome() == null || playlist.getNome().isEmpty()) {
            throw new Exception(" - Nome da playlist não informado.");
        }


    }

    /**
     * Método que consiste os dados de Musica.
     * @param playlist
     * @throws Exception  
     */
    public static void consistir(Playlist playlist) throws Exception {
        consistirBK(playlist);

    }

    /**
     * Método que tenta incluir um objeto Musica.
     *
     *
     * @param playlist 
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int incluir(Playlist playlist, Transacao t) throws Exception {
        consistir(playlist);

        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, nome, nrMus, tipo) ");
        sql.add("VALUES ");
        sql.add(" (:id, :nome, :nrMus, :tipo)");

        sql.setParam("id", null);
        sql.setParam("nome", playlist.getNome());
        sql.setParam("nrMus", playlist.getNrMusicas());
        sql.setParam("tipo", playlist.getTipoPlayList().ordinal());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que tenta alterar um objeto Musica.
     *
     *
     * @param playlist 
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int alterar(Playlist playlist, Transacao t) throws Exception {
        consistir(playlist);

        SQL sql = new SQL();
        sql.add("UPDATE " + TBL);
        sql.add("SET nome = :nome, nrMus = :nrMus, tipo = :tipo");
        sql.add("WHERE id = :id");

        sql.setParam("id", playlist.getId());
        sql.setParam("nome", playlist.getNome());
        sql.setParam("nrMus", playlist.getNrMusicas());
        sql.setParam("tipo", playlist.getTipoPlayList().ordinal());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que tenta excluir um objeto Musica.
     *
     *
     * @param playlist 
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int excluir(Playlist playlist, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id");
        CondicaoBD.excluir(playlist, t);

        sql.setParam("id", playlist.getId());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param playlist 
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception  
     */
    public static boolean existe(Playlist playlist, Transacao t) throws Exception {
        consistirBK(playlist);

        SQL sql = new SQL();
        sql.add("SELECT id FROM " + TBL);
        sql.add("WHERE nome = :nome");

        sql.setParam("nome", playlist.getNome());

        ResultSet rs = t.executeQuery(sql.getSql());

        try {
            if (!rs.next()) {
                return false;
        }
            playlist.setId(rs.getInt("id"));
            return true;
        } finally {
            rs.close();
    }
    }

    /**
     * Método que carrega o objeto Musica pelo ID.
     *
     *
     * @param playlist 
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception  
     */
    public static boolean carregar(Playlist playlist, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", playlist.getId());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
    }

            playlist.setId(rs.getInt("id"));
            playlist.setNome(rs.getString("nome"));
            playlist.setNrMusicas(rs.getInt("nrMus"));
            playlist.setTipoPlayList(TipoPlayList.values()[rs.getInt("tipo")]);

            return true;
        } finally {
            rs.close();
        }
    }

    /** Método que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @param t Contendo a transação.
     * @return ArrayList Contendo uma lista de Musicas.
     * @throws Exception 
     */
    public static ArrayList listar(PlaylistSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
    }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1=1");


        if (filtro.getId() >= 0) {
            sql.add("AND id = :id");
            sql.setParam("id", filtro.getId());
            ResultSet rs = t.executeQuery(sql.getSql());
            try {
                ArrayList lista = new ArrayList(13);
                while (rs.next()) {
                    Playlist playlist = new Playlist();

                    playlist.setId(rs.getInt("id"));
                    playlist.setNome(rs.getString("nome"));
                    playlist.setNrMusicas(rs.getInt("nrMus"));
                    playlist.setTipoPlayList(TipoPlayList.values()[rs.getInt("tipo")]);
                    lista.add(playlist);

                }
                return lista;
            } finally {
                rs.close();
            }
        } else {
            if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
                sql.add("AND UCASE (nome) like :nome");
                sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            }

            sql.add("ORDER BY nome ");



            ResultSet rs = t.executeQuery(sql.getSql());
            try {
                ArrayList lista = new ArrayList(13);
                while (rs.next()) {
                    Playlist playlist = new Playlist();

                    playlist.setId(rs.getInt("id"));
                    playlist.setNome(rs.getString("nome"));
                    playlist.setNrMusicas(rs.getInt("nrMus"));
                    playlist.setTipoPlayList(TipoPlayList.values()[rs.getInt("tipo")]);
                    lista.add(playlist);
                }
                return lista;
            } finally {
                rs.close();
            }
        }
    }

    /*#########################################
    METODOS SEM TRANSACAO
     *#########################################*/
    /**
     * Método que tenta incluir um objeto Musica.
     *
     *
     * @param playlist 
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int incluir(Playlist playlist) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(playlist, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que tenta alterar um objeto Musica.
     *
     *
     * @param playlist 
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int alterar(Playlist playlist) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = alterar(playlist, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que tenta excluir um objeto Musica.
     *
     *
     * @param playlist 
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception  
     */
    public static int excluir(Playlist playlist) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluir(playlist, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param playlist 
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception  
     */
    public static boolean existe(Playlist playlist) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = existe(playlist, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que carrega o objeto Musica pelo ID.
     *
     *
     * @param playlist 
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception  
     */
    public static boolean carregar(Playlist playlist) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = carregar(playlist, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /** Método que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @return ArrayList Contendo uma lista de Musicas.
     * @throws Exception 
     */
    public static ArrayList listar(PlaylistSC filtro) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            ArrayList r = listar(filtro, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    private PlaylistBD() {
    }
}
