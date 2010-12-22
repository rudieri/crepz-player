package com.musica;

import com.conexao.SQL;
import com.conexao.Transacao;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável pela persistência de objetos Musica.
 */
public class MusicaBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "musica";

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
    public static int incluir(Musica musica, Transacao t) throws Exception {
        consistir(musica);

        SQL sql = new SQL();
        int idGenero = inserirGenero(musica.getGenero(), t);
        int idFotos = inserirFotos(musica.getImg(), t);
        int idCompositor = inserirCompositor(musica.getCompositor(), "Não definido", t);
        int idInterprete = inserirInterprete(musica.getAutor(), "Não definido", t);
        int idAlbum = inserirAlbum(musica.getAlbum(), "Não definido",idFotos , idInterprete, t);
        int idMusica= inserirMusica(musica.getCaminho(), musica.getNome(), false, 0, idCompositor, idAlbum, idGenero, t);

        //  inserirMusica(musica.getCaminho(), musica.getNome(), true, 0, codComp, codAlb, codGen, t);
//        MusicaGerencia
//        sql.add("INSERT INTO " + TBL);
//        sql.add(" (id, caminho, nome, autor, genero, album, img) ");
//        sql.add("VALUES ");
//        sql.add(" (:id, :caminho, :nome, :autor, :genero, :album, :img)");
//
//        sql.setParam("id", null);
//        sql.setParam("caminho", musica.getCaminho().replace("'", "<&aspas>"));
//        sql.setParam("nome", musica.getNome());
//        sql.setParam("autor", musica.getAutor());
//        sql.setParam("genero", musica.getGenero());
//        sql.setParam("img", musica.getImg().replace("'", "<&aspas>"));

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
    public static int excluir(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());

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
    public static Musica carregar(Musica musica, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", musica.getId());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return null;
            }

            musica.setId(rs.getInt("id"));
            musica.setCaminho(rs.getString("caminho"));
            musica.setNome(rs.getString("nome"));
            musica.setAutor(rs.getString("autor"));
            musica.setGenero(rs.getString("genero"));
            musica.setAlbum(rs.getString("album"));
            musica.setImg(rs.getString("img"));


            return musica;
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
        sql.add("WHERE");
        int c = 0;

        if (filtro.getNome() != null && !filtro.getNome().equals("")) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().equals("")) {
            sql.add("OR UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().equals("")) {
            sql.add("OR UCASE (album) like :album");
            sql.setParam("album", filtro.getAlbum().toUpperCase() + "%");
            c++;
        }
        if (filtro.getGenero() != null && !filtro.getGenero().equals("")) {
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

    public static ArrayList listarAgrupado(MusicaSC filtro, String agrupar, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT " + agrupar + " as agrup , count(*) as m, max(img) as capa");
        sql.add("FROM " + TBL);
        sql.add("WHERE ");
        int c = 0;
        if (filtro.getNome() != null && !filtro.getNome().equals("")) {
            sql.add("UCASE (nome) like :nome");
            sql.setParam("nome", filtro.getNome().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAutor() != null && !filtro.getAutor().equals("")) {
            sql.add("OR UCASE (autor) like :autor");
            sql.setParam("autor", filtro.getAutor().toUpperCase() + "%");
            c++;
        }
        if (filtro.getAlbum() != null && !filtro.getAlbum().equals("")) {
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
                JCapa capa = new JCapa(rs.getString("capa").replace("<&aspas>", "'"), rs.getString("agrup"), new Integer(rs.getInt("m")));

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

                musica.setId(rs.getInt("id"));
                musica.setCaminho(rs.getString("caminho"));
                musica.setNome(rs.getString("nome"));
                musica.setAutor(rs.getString("autor"));
                musica.setGenero(rs.getString("genero"));
                musica.setAlbum(rs.getString("album"));
                musica.setImg(rs.getString("img"));
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
    public static Musica carregar(Musica musica) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            Musica r = carregar(musica, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /** Método que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @return ArrayList Contendo uma lista de Musicas. */
    public static ArrayList listar(MusicaSC filtro) throws Exception {
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

    public static int existe(String valor, String coluna, String tabela, Transacao t) {
        try {
            SQL sql = new SQL();
            sql.add("SELECT ID FROM :tabela WHERE :coluna = :valor");
            sql.setParam(":tabela", tabela);
            sql.setParam("coluna", coluna);
            sql.setParam(":valor", valor);
            ResultSet rs = t.executeQuery(sql.getSql());
            if (!rs.next()) {
                return -1;
            }
            return rs.getInt(coluna);
        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static int inserirGenero(String genero, Transacao t) {
        try {
            int id = existe(genero, "nome", "genero", t);
            if (id != -1) {
                return id;
            }
            SQL sql = new SQL();
            sql.add("INSERT INTO GENERO VALUES(:id,:genero)");
            sql.setParam("id", null);
            sql.setParam(":genero", genero);
            t.executeUpdate(sql.getSql());
            id = existe(genero, "nome", "genero", t);
            return id;

        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static int inserirFotos(String endereco, Transacao t) {
        try {
            int id = existe(endereco, "endereco", "fotos", t);
            if (id != -1) {
                return id;
            }
            SQL sql = new SQL();
            sql.add("INSERT INTO FOTOS VALUES(:id,:endereco)");
            sql.setParam("id", null);
            sql.setParam(":endereco", endereco);
            t.executeUpdate(sql.getSql());
            id = existe(endereco, "endereco", "fotos", t);
            return id;

        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static int inserirArtista(String nome, String pais, Transacao t) {
        try {
            int id = existe(nome, "nome", "artista", t);
            if (id != -1) {
                return id;
            }
            SQL sql = new SQL();
            sql.add("INSERT INTO ARTISTA VALUES(:id,:nome,:pais)");
            sql.setParam("id", null);
            sql.setParam(":nome", nome);
            sql.setParam(":pais", pais);
            t.executeUpdate(sql.getSql());
            id = existe(nome, "nome", "artista", t);
            return id;

        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static boolean inserirArtista(int id, String tipo, Transacao t) {
        try {
            SQL sql = new SQL();
            sql.add("INSERT INTO" + tipo + " VALUES(:id)");
            sql.setParam("id", id);
            t.executeUpdate(sql.getSql());
            return true;

        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public static int inserirInterprete(String nome, String pais, Transacao t) {
        try {
            int id = inserirArtista(nome, pais, t);
            inserirArtista(id, "interprete", t);
            return id;
        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public static int inserirCompositor(String nome, String pais, Transacao t) {
        try {
            int id = inserirArtista(nome, pais, t);
            inserirArtista(id, "compositor", t);
            return id;
        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
    Inseri um album.
     *@param nome Nome do album.
     * @param dataLanc Data do lancamento do album.
     * @param codFoto Código da foto do album.
     * @param codInterprete Código do interprete ou banda.
    @return true
     */
    public static int inserirAlbum(String nome, String dataLanc, int codFoto, int codInterprete, Transacao t) {
        try {
            int id = existe(nome, "nome", "album", t);
            if (id != -1) {
                return id;
            }
            SQL sql = new SQL();
            sql.add("INSERT INTO ALBUM VALUES(:id,:nome,:dataLanc,:codFoto,:codInterprete)");
            sql.setParam("id", null);
            sql.setParam(":nome", nome);
            sql.setParam(":dataLanc", dataLanc);
            sql.setParam(":codFoto", nome);
            sql.setParam(":codInterprete", codInterprete);
            t.executeUpdate(sql.getSql());
            id = existe(nome, "nome", "album", t);
            return id;
        } catch (Exception ex) {
            Logger.getLogger(MusicaBD.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     *@param arquivo Endereço do arquivo.
     *@param nome Nome da música.
     * @param favorita Se a musica está marcada como favorita.
     * @param Xtocada Quantas vezes a música foi todcada (comneça me 0)
     * @param codComp Código que se relaciona com o compositor.
     * @param codAlb Código que diz em que album está a música.
     * @param codGen Código que diz o gênero da música.
     *
     * @return Se foi inserido.
     */
    public static int inserirMusica(String arquivo, String nome, boolean favorita, int Xtocada, int codComp, int codAlb, int codGen, Transacao t) {
         try{int id = existe(arquivo, "arquivo", "musica", t);
            if (id != -1) {
                return id;
            }
        SQL sql = new SQL();
        sql.add("INSERT INTO ALBUM VALUES(:id,:arquivo,:nome,:favorita,:Xtocada,:codComp,:codAlb,:codGen)");
        sql.setParam("id", null);
        sql.setParam(":arquivo", arquivo);
        sql.setParam(":nome", nome);
        sql.setParam(":favorita", favorita);
        sql.setParam(":Xtocada", Xtocada);
        sql.setParam(":codComp", codComp);
        sql.setParam(":codAlb", codAlb);
        sql.setParam(":codGen", codGen);
        t.executeUpdate(sql.getSql());
        id = existe(arquivo, "arquivo", "musica", t);
        return id;
        }catch(Exception ex){
             System.out.println("Problemas ao inserir a musica!!!");
             return -1;
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
