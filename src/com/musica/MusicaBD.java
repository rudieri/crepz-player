package com.musica;

import com.conexao.SQL;
import com.conexao.Transacao;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe respons�vel pela persist�ncia de objetos Musica.
 */
public class MusicaBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "musica";

    /**
     * M�todo que consiste a BK de Musica.
     */
    public static void consistirBK(Musica musica) throws Exception {
        if (musica == null) {
            throw new Exception(" - Musica n�o informado.");
        }

        if (musica.getCaminho() == null || musica.getCaminho().isEmpty()) {
            throw new Exception(" - Caminho da Musica n�o informado.");
        }


    }

    /**
     * M�todo que consiste os dados de Musica.
     */
    public static void consistir(Musica musica) throws Exception {
        consistirBK(musica);

//        if (musica.getNome() == null || musica.getNome().equals("")) {
//            throw new Exception(" - Nome da Musica n�o informado.");
//        }

    }

    /**
     * M�todo que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(Musica musica, Transacao t) throws Exception {
        consistir(musica);

        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, caminho, nome, autor, genero, album, img, tempo, nro_reproducoes, dt_mod_arquivo) ");
        sql.add("VALUES ");
        sql.add(" (:id, :caminho, :nome, :autor, :genero, :album, :img, :tempo, :nro_reproducoes, :dt_mod_arquivo)");

        sql.setParam("id", null);
        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));
        sql.setParam("nome", musica.getNome());
        sql.setParam("autor", musica.getAutor());
        sql.setParam("genero", musica.getGenero());
        sql.setParam("img", musica.getImg().replace("'", "<&aspas>"));
        sql.setParam("nro_reproducoes", musica.getNumeroReproducoes());
        sql.setParam("dt_mod_arquivo", musica.getDtModArquivo());

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
    public static int alterar(Musica musica, Transacao t) throws Exception {
        consistir(musica);

        SQL sql = new SQL();
        sql.add("UPDATE " + TBL);
        sql.add("SET nome = :nome, autor = :autor, genero =:genero, album = :album, img = :img,");
        sql.add("caminho = :caminho, tempo=:tempo, nro_reproducoes=:nro_reproducoes, dt_mod_arquivo=:dt_mod_arquivo");
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());
        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));
        sql.setParam("nome", musica.getNome());
        sql.setParam("autor", musica.getAutor());
        sql.setParam("genero", musica.getGenero());
        sql.setParam("tempo", musica.getTempo() == null ? 0 : musica.getTempo().getMilissegundos());
        sql.setParam("img", musica.getImg().replace("'", "<&aspas>"));
        sql.setParam("nro_reproducoes", musica.getNumeroReproducoes());
        sql.setParam("dt_mod_arquivo", musica.getDtModArquivo());

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
    public static int excluir(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());

        return t.executeUpdate(sql.getSql());
    }

    /**
     * M�todo que verifica pelo BK se o objeto Musica est� cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
     */
    public static boolean existe(Musica musica, Transacao t) throws Exception {
        consistirBK(musica);

        SQL sql = new SQL();
        sql.add("SELECT id FROM " + TBL);
        sql.add("WHERE caminho = :caminho ");

        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));

        ResultSet rs = t.executeQuery(sql.getSql());

        try {
            if (rs.next()) {
                musica.setId(rs.getInt("id"));
                return true;
            } else {
                return false;
            }
        } finally {
            rs.close();
        }
    }

    /**
     * M�todo que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transa��o.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
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
            carregarObjeto(musica, rs);



            return true;
        } finally {
            rs.close();
        }
    }

    public static boolean carregarPeloEndereco(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE caminho = :caminho ");

        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }
            carregarObjeto(musica, rs);



            return true;
        } finally {
            rs.close();
        }
    }

    public static long getMaxDtModArquivo(String path, boolean ehDiretorio, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT max(dt_mod_arquivo) as max_dt FROM " + TBL);
        if (ehDiretorio) {
            sql.add("where caminho LIKE :path");
            sql.setParam("path", path.replace("'", "<&aspas>") + "%");
        }else{
            sql.add("where caminho = :path");
            sql.setParam("path", path.replace("'", "<&aspas>"));
        }
        ResultSet rs = t.executeQuery(sql);
        if (rs.next()) {
            return rs.getLong("max_dt");
        } else {
            return 0l;
        }
    }

    private static void carregarObjeto(Musica musica, ResultSet rs) throws SQLException {
        musica.setId(rs.getInt("id"));
        musica.setCaminho(rs.getString("caminho"));
        musica.setNome(rs.getString("nome"));
        musica.setAutor(rs.getString("autor"));
        musica.setGenero(rs.getString("genero"));
        musica.setAlbum(rs.getString("album"));
        musica.setImg(rs.getString("img"));
        musica.setTempo(new Tempo(rs.getInt("tempo")));
        musica.setNumeroReproducoes(rs.getShort("nro_reproducoes"));
        musica.setDtModArquivo(rs.getLong("dt_mod_arquivo"));
    }

    /** M�todo que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @param t Contendo a transa��o.
     * @return ArrayList Contendo uma lista de Musicas. */
    public static ArrayList<Musica> listar(MusicaSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro n�o informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE");
        int c = 0;

        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().isEmpty()) {
            sql.add("OR UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().isEmpty()) {
            sql.add("OR UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
            c++;
        }
        if (filtro.getGenero() != null && !filtro.getGenero().isEmpty()) {
            sql.add("OR UCASE (genero) like :genero");
            sql.setParam("genero", filtro.getGenero().toUpperCase() + "%");
            c++;
        }


        if (c == 0) {
            sql.add("1=1");
        }
        sql.add("ORDER BY nome ");



        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList();
            while (rs.next()) {
                Musica musica = new Musica();
                carregarObjeto(musica, rs);

                lista.add(musica);
            }
            return lista;
        } finally {
            rs.close();
        }
    }

    public static int contarMusicas(Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT count(id) as qtd FROM " + TBL);
        ResultSet rs = t.executeQuery(sql.getSql());
        if (rs.next()) {
            return rs.getInt("qtd");
        } else {
            return 0;
        }
    }

    public static ArrayList listarAgrupado(MusicaSC filtro, String agrupar, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro n�o informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT " + agrupar + " as agrup , count(*) as m, max(img) as capa");
        sql.add("FROM " + TBL);
        sql.add("WHERE ");
        int c = 0;
        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().isEmpty()) {
            sql.add("OR UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().isEmpty()) {
            sql.add("OR UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
            c++;
        }
        if (c == 0) {
            sql.add("1=1");
        }

        sql.add("GROUP BY " + agrupar);



        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList();
            while (rs.next()) {
                JCapa capa = new JCapa(rs.getString("capa").replace("<&aspas>", "'"), rs.getString("agrup"), rs.getInt("m"));

                lista.add(capa);
            }
            return lista;
        } finally {
            rs.close();
        }
    }

    public Musica pesquisar(String parametro, String valor) {
        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE");
        sql.add(parametro + "=" + valor);
        Transacao t = new Transacao();
        Musica musica = new Musica();
        try {
            t.begin();
            ResultSet rs = t.executeQuery(sql.getSql());
            t.commit();

            while (rs.next()) {
                carregarObjeto(musica, rs);
                rs.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return musica;
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
    public static int incluir(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(musica, t);
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
    public static int alterar(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = alterar(musica, t);
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
    public static int excluir(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluir(musica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * M�todo que verifica pelo BK se o objeto Musica est� cadastrado. Se estiver, carrega o ID do objeto.
     *
     *
     * @param musica Contendo a musica.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
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
     * M�todo que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @return boolean Contendo TRUE se est� cadastrado e FALSE se n�o estiver.
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

    /** M�todo que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @return ArrayList Contendo uma lista de Musicas. */
    public static ArrayList<Musica> listar(MusicaSC filtro) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            ArrayList<Musica> r = listar(filtro, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    public static long getMaxDtModArquivo(String path, boolean ehDiretorio) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            long r = getMaxDtModArquivo(path, ehDiretorio, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    public static int contarMusicas() throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = contarMusicas(t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }

    }

    public static ArrayList listarAgrupado(MusicaSC filtro, String agrupador) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            ArrayList r = listarAgrupado(filtro, agrupador, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }
}
