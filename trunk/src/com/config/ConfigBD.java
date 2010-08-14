package com.config;

import com.musica.*;
import com.conexao.SQL;
import com.conexao.Transacao;
import java.sql.*;
import java.util.*;

/**
 * Classe responsável pela persistência de objetos Musica.
 */
public class ConfigBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "config";

    /**
     * Método que consiste a BK de Musica.
     */
    public static void consistirBK(Musica musica) throws Exception {
        if (musica == null) {
            throw new Exception(" - Musica não informado.");
        }

        if (musica.getCaminho() == null || musica.getCaminho().equals("")) {
            throw new Exception(" - Caminho da Musica não informado.");
        }


    }

    /**
     * Método que consiste os dados de Musica.
     */
    public static void consistir(Musica musica) throws Exception {
        consistirBK(musica);

    }

    /**
     * Método que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(String chave, String valor, boolean reset,Transacao t) throws Exception {
        if (reset) {
            excluir(t);
        }
        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, chave, valor) ");
        sql.add("VALUES ");
        sql.add(" (:id, :chave, :valor)");

        sql.setParam("id", null);
        sql.setParam("chave", chave);
        sql.setParam("valor", valor);


        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que tenta alterar um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int alterar(Musica musica, Transacao t) throws Exception {
        consistir(musica);

        SQL sql = new SQL();
        sql.add("UPDATE " + TBL);
        sql.add("SET nome = :nome, autor = :autor, genero =:genero, album = :album, img = :img, caminho = :caminho          ");
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());
        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));
        sql.setParam("nome", musica.getNome());
        sql.setParam("autor", musica.getAutor());
        sql.setParam("genero", musica.getGenero());
        sql.setParam("img", musica.getImg().replace("'", "<&aspas>"));

        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que tenta excluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int excluir(Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE 1 = 1 ");



        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     */
    public static boolean existe(Musica musica, Transacao t) throws Exception {
        consistirBK(musica);

        SQL sql = new SQL();
        sql.add("SELECT id FROM " + TBL);
        sql.add("WHERE caminho = :caminho ");

        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));

        ResultSet rs = t.executeQuery(sql.getSql());

        try {
            if (!rs.next()) {
                return false;
            }
            musica.setId(rs.getInt("id"));
            return true;
        } finally {
            rs.close();
        }
    }

    /**
     * Método que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     */
    public static boolean carregar(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }

            musica.setId(rs.getInt("id"));
            musica.setCaminho(rs.getString("caminho"));
            musica.setNome(rs.getString("nome"));
            musica.setAutor(rs.getString("autor"));
            musica.setGenero(rs.getString("genero"));
            musica.setAlbum(rs.getString("album"));
            musica.setImg(rs.getString("img"));


            return true;
        } finally {
            rs.close();
        }
    }

    /** Método que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @param t Contendo a transação.
     * @return ArrayList Contendo uma lista de Musicas. */
    public static ArrayList listar(MusicaSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1=1");

        if (filtro.getNome() != null && !filtro.getNome().equals("")) {
            sql.add("AND UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
        }
        if (filtro.getAutor() != null && !filtro.getAutor().equals("")) {
            sql.add("AND UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().equals("")) {
            sql.add("AND UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
        }
        if (filtro.getGenero() != null && !filtro.getGenero().equals("")) {
            sql.add("AND UCASE (genero) like :genero");
            sql.setParam("genero", filtro.getGenero().toUpperCase() + "%");
        }
        sql.add("ORDER BY nome ");



        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList();
            while (rs.next()) {
                Musica musica = new Musica();
                musica.setId(rs.getInt("id"));
                musica.setCaminho(rs.getString("caminho"));
                musica.setNome(rs.getString("nome"));
                musica.setAutor(rs.getString("autor"));
                musica.setGenero(rs.getString("genero"));
                musica.setAlbum(rs.getString("album"));
                musica.setImg(rs.getString("img"));

                lista.add(musica);
            }
            return lista;
        } finally {
            rs.close();
        }
    }

    private static HashMap listar(String que, Transacao t) throws Exception {



        SQL sql = new SQL();
        sql.add("SELECT chave,valor");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1=1");
        if (que != null && !que.equals("")) {
            sql.add(" and chave=" + que);
        }


        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            HashMap lista = new HashMap();
            while (rs.next()) {
                lista.put(rs.getString("chave"), rs.getString("valor"));
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
     * Método que tenta excluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static void incluir(String chave, String valor, boolean reset) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(chave, valor,reset, t);
            t.commit();

        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }

    }

    /**
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param musica Contendo a musica.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     */
    public static boolean existe(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = existe(musica, t);
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
     * @param musica Contendo a musica.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     */
    public static boolean carregar(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = carregar(musica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    public static HashMap listar(String chave) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            HashMap r = listar(chave, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }
}
