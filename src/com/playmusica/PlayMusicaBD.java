package com.playmusica;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.CacheDeMusica;
import java.sql.*;
import java.util.*;
import com.musica.Musica;
import com.playlist.Playlist;

/**
 *
 * @author -moNGe_
 */
public class PlayMusicaBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "playmusica";

    /**
     * M�todo que consiste a BK de Musica.
     */
    public static void consistirBK(PlayMusica playMusica) throws Exception {
        if (playMusica == null) {
            throw new Exception(" - PlayList n�o informado.");
        }

        if (playMusica.getPlaylist() == null) {
            throw new Exception(" - Playlsit n�o informado.");
        }

        if (playMusica.getMusica() == null) {
            throw new Exception(" - Musica n�o informada.");
        }



    }

    /**
     * M�todo que consiste os dados de Musica.
     */
    public static void consistir(PlayMusica playMusica) throws Exception {
        consistirBK(playMusica);

        if (playMusica.getSeq() < 0 ) {
            throw new Exception(" - Sequ�ncia n�o informado.");
        }

    }

    /**
     * M�todo que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(PlayMusica playMusica, Transacao t) throws Exception {
        consistir(playMusica);
        System.out.println(playMusica.getId());
        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, playlist, musica, seq) ");
        sql.add("VALUES ");
        sql.add(" (:id, :playlist, :musica , :seq)");

        sql.setParam("id", null);
        sql.setParam("playlist", playMusica.getPlaylist().getId());
        sql.setParam("musica", playMusica.getMusica().getId());
      //  sql.setParam("musica", playMusica.getMusica().getId());
        sql.setParam("seq", playMusica.getSeq());

        return t.executeUpdate(sql.getSql());
    }
 public static int incluir(PlayMusica playMusica, int id, Transacao t) throws Exception {
        consistir(playMusica);
        System.out.println(playMusica.getId());
        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, playlist, musica, seq) ");
        sql.add("VALUES ");
        sql.add(" (:id, :playlist, :musica , :seq)");

        sql.setParam("id", null);
        sql.setParam("playlist", playMusica.getPlaylist().getId());
        sql.setParam("musica",id);
      //  sql.setParam("musica", playMusica.getMusica().getId());
        sql.setParam("seq", playMusica.getSeq());

        return t.executeUpdate(sql.getSql());
    }
    /**
     * M�todo que tenta alterar um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int alterar(PlayMusica playMusica, Transacao t) throws Exception {
        consistir(playMusica);

        SQL sql = new SQL();
        sql.add("UPDATE " + TBL);
        sql.add("SET playlist = :playlist, id_musica = :id_musica, seq = :seq,              ");
        sql.add("WHERE id = :id ");

        sql.setParam("id", playMusica.getId());
        sql.setParam("playlist", playMusica.getPlaylist().getId());
        sql.setParam("musica", playMusica.getMusica().getId());
        sql.setParam("seq", playMusica.getSeq());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * M�todo que tenta excluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int excluir(PlayMusica playMusica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", playMusica.getMusica().getId());

        return t.executeUpdate(sql.getSql());
    }

      public static int excluirMusica(Playlist Playlist, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE playlist = :playlist ");

        sql.setParam("playlist", Playlist.getId());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * M�todo que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
     */
    public static boolean carregar(PlayMusica playMusica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", playMusica.getId());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }

            playMusica.setId(rs.getInt("id"));
            Musica m = CacheDeMusica.get(rs.getInt("musica"));
            playMusica.setMusica(m);

            Playlist p = new Playlist();
            p.setId(rs.getInt("playlist"));

            playMusica.setPlaylist(p);


            return true;
        } finally {
            rs.close();
        }
    }

//    /** M�todo que retorna uma lista de Musicas de acordo com o filtro.
//     * @param filtro Contendo o filtro.
//     * @param t Contendo a transa��o.
//     * @return ArrayList Contendo uma lista de Musicas. */
    public static ArrayList listar(PlayMusicaSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro n�o informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1=1");

        if (filtro.getPlaylist() != null) {
            sql.add("AND playlist = :playlist");
            sql.setParam("playlist", filtro.getPlaylist().getId());
        }

        sql.add("ORDER BY seq ");

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList();
            while (rs.next()) {
                PlayMusica playMusica = new PlayMusica();
                playMusica.setId(rs.getInt("id"));
                Musica m = CacheDeMusica.get(rs.getInt("musica"));
                playMusica.setMusica(m);

                Playlist p = new Playlist();
                p.setId(rs.getInt("playlist"));

                playMusica.setPlaylist(p);
                lista.add(playMusica);
            }
            return lista;
        } finally {
            rs.close();
        }
    }

    /*#########################################
    METODOS SEM TRANSACAO
     *#########################################*/
    /**
     * M�todo que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(PlayMusica playMusica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(playMusica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * M�todo que tenta alterar um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int alterar(PlayMusica playMusica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = alterar(playMusica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * M�todo que tenta excluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int excluir(PlayMusica playMusica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluir(playMusica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }


    /**
     * M�todo que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
     */
    public static boolean carregar(PlayMusica playMusica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = carregar(playMusica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }
    /** M�todo que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
    //     * @return ArrayList Contendo uma lista de Musicas. */
//    public static ArrayList listar(MusicaSC filtro) throws Exception {
//        Transacao t = new Transacao();
//        try {
//            t.begin();
//            ArrayList r = listar(filtro, t);
//            t.commit();
//            return r;
//        } catch (Exception ex) {
//            t.rollback();
//            throw ex;
//        }
//    }
}
