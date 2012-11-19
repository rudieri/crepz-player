package com.musica;

import com.biblioteca.Capa;
import com.conexao.SQL;
import com.conexao.Transacao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe responsável pela persistência de objetos Musica.
 */
public class MusicaBD {

    public static final String ASPAS = "<&aspas>";
    /**
     * Constante que define o nome da tabela.
     */
    public static final String TBL = "musica";

    /**
     * Método que consiste a BK de Musica.
     */
    public static void consistirBK(Musica musica) throws Exception {
        if (musica == null) {
            throw new Exception(" - Musica não informado.");
        }

        if (musica.getCaminho() == null || musica.getCaminho().isEmpty()) {
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
    public static int incluir(Musica musica, Transacao t) throws Exception {
        consistir(musica);

        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, caminho, nome, autor, genero, album, img, tempo, nro_reproducoes, dt_mod_arquivo, perdida) ");
        sql.add("VALUES ");
        sql.add(" (:id, :caminho, :nome, :autor, :genero, :album, :img, :tempo, :nro_reproducoes, :dt_mod_arquivo, :perdida)");

        sql.setParam("id", null);
        sql.setParam("caminho", musica.getCaminho().replace("'", ASPAS));
        sql.setParam("nome", musica.getNome());
        sql.setParam("autor", musica.getAutor());
        sql.setParam("genero", musica.getGenero());
        sql.setParam("album", musica.getAlbum());
        sql.setParam("img", musica.getImg() == null ? null : musica.getImg().replace("'", ASPAS));
        sql.setParam("nro_reproducoes", musica.getNumeroReproducoes());
        sql.setParam("dt_mod_arquivo", musica.getDtModArquivo());
        sql.setParam("perdida", musica.isPerdida() ? 1 : 0);

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
        sql.add("SET nome = :nome, autor = :autor, genero =:genero, album = :album, img = :img,");
        sql.add("caminho = :caminho, tempo=:tempo, nro_reproducoes=:nro_reproducoes, dt_mod_arquivo=:dt_mod_arquivo,");
        sql.add("perdida = :perdida");
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());
        sql.setParam("caminho", musica.getCaminho().replace("'", ASPAS));
        sql.setParam("nome", musica.getNome());
        sql.setParam("autor", musica.getAutor());
        sql.setParam("genero", musica.getGenero());
        sql.setParam("album", musica.getAlbum());
        sql.setParam("tempo", musica.getTempo() == null ? 0 : musica.getTempo().getMilissegundos());
        sql.setParam("img", musica.getImg() == null ? null : musica.getImg().replace("'", ASPAS));
        sql.setParam("nro_reproducoes", musica.getNumeroReproducoes());
        sql.setParam("dt_mod_arquivo", musica.getDtModArquivo());
        sql.setParam("perdida", musica.isPerdida() ? 1 : 0);
        int numLinhas = t.executeUpdate(sql.getSql());
        CacheDeMusica.adicionar(musica);
        return numLinhas;
    }

    /**
     * Método que tenta excluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int excluir(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());
        int numLinhas = t.executeUpdate(sql.getSql());
        CacheDeMusica.remover(musica);
        return numLinhas;
    }

    /**
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se
     * estiver, carrega o ID do objeto.
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

        sql.setParam("caminho", musica.getCaminho().replace("'", ASPAS));

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
            carregarObjeto(musica, rs);
            CacheDeMusica.adicionar(musica);
            return true;
        } finally {
            rs.close();
        }
    }

    public static boolean carregarPeloEndereco(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE caminho = :caminho ");

        sql.setParam("caminho", musica.getCaminho().replace("'", ASPAS));

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }
            carregarObjeto(musica, rs);
            CacheDeMusica.adicionar(musica);
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
            sql.setParam("path", path.replace("'", ASPAS) + "%");
        } else {
            sql.add("where caminho = :path");
            sql.setParam("path", path.replace("'", ASPAS));
        }
        ResultSet rs = t.executeQuery(sql);
        if (rs.next()) {
            return rs.getLong("max_dt");
        } else {
            return 0l;
        }
    }

    public static void carregarObjeto(Musica musica, ResultSet rs) throws SQLException {
        musica.setId(rs.getInt("id"));
        musica.setCaminho(rs.getString("caminho").replace(ASPAS, "'"));
        musica.setNome(rs.getString("nome"));
        musica.setAutor(rs.getString("autor"));
        musica.setGenero(rs.getString("genero"));
        musica.setAlbum(rs.getString("album"));
        musica.setImg(rs.getString("img") == null ? null : rs.getString("img").replace(ASPAS, "'"));
        musica.setTempo(new Tempo(rs.getInt("tempo")));
        musica.setNumeroReproducoes(rs.getShort("nro_reproducoes"));
        musica.setDtModArquivo(rs.getLong("dt_mod_arquivo"));
        musica.setPerdida(rs.getByte("perdida") == 1);
    }

    /**
     * Método que retorna uma lista de Musicas de acordo com o filtro.
     *
     * @param filtro Contendo o filtro.
     * @param t Contendo a transação.
     * @return ArrayList Contendo uma lista de Musicas.
     */
    public static ArrayList<Musica> listar(MusicaSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE perdida <> 1 AND (");
        int c = 0;

        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().isEmpty()) {
            if (c > 0) {
                sql.add("OR");
            }
            sql.add("UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().isEmpty()) {
            if (c > 0) {
                sql.add("OR");
            }
            sql.add("UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
            c++;
        }
        if (filtro.getGenero() != null && !filtro.getGenero().isEmpty()) {
            if (c > 0) {
                sql.add("OR");
            }
            sql.add("UCASE (genero) like :genero");
            sql.setParam("genero", filtro.getGenero().toUpperCase() + "%");
            c++;
        }


        if (c == 0) {
            sql.add("1 = 1 )");
        }else{
            sql.add(" )"); // fecha o OR
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
            System.gc();
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
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT " + agrupar + " as agrup , count(*) as m, max(img) as capa");
        sql.add("FROM " + TBL);
        sql.add("WHERE perdida <> 1 AND (");
        int c = 0;
        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().isEmpty()) {
            if (c > 0) {
                sql.add("OR");
            }
            sql.add("UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().isEmpty()) {
            if (c > 0) {
                sql.add("OR");
            }
            sql.add("UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
            c++;
        }
        if (c == 0) {
            sql.add("1=1 )");
        }else{
            sql.add(")");
        }

        sql.add("GROUP BY " + agrupar);



        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList();
            while (rs.next()) {
                final String enderecoCapa = rs.getString("capa");
                if (enderecoCapa == null) {
                    continue;
                }
                Capa capa = new Capa(enderecoCapa.replace(ASPAS, "'"), rs.getString("agrup"), rs.getInt("m"));

                lista.add(capa);
            }
            return lista;
        } finally {
            rs.close();
        }
    }

    /*
     * ######################################### METODOS SEM TRANSACAO
     *#########################################
     */
    /**
     * Método que tenta incluir um objeto Musica.
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
     * Método que tenta alterar um objeto Musica.
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
     * Método que tenta excluir um objeto Musica.
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
     * Método que verifica pelo BK se o objeto Musica está cadastrado. Se
     * estiver, carrega o ID do objeto.
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

    /**
     * Método que retorna uma lista de Musicas de acordo com o filtro.
     *
     * @param filtro Contendo o filtro.
     * @return ArrayList Contendo uma lista de Musicas.
     */
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
