package com.configuracao;

import com.conexao.SQL;
import com.conexao.Transacao;
import java.io.File;
import java.sql.*;
import java.util.*;

/**
 *
 * @author -moNGe_
 */
public class ConfiguracaoBD {

    /** Constante que define o nome da tabela. */
    public static final String TBL = "configuracao";

    /**
     * Método que consiste a BK de Musica.
     */
    public static void consistirBK(Configuracao configuracao) throws Exception {
        if (configuracao == null) {
            throw new Exception(" - Configuracao não informado.");
        }

        if (configuracao.getChave() == null || configuracao.getChave().equals("")) {
            throw new Exception(" - Chave da configuracao não informado.");
        }


    }

    /**
     * Método que consiste os dados de Musica.
     */
    public static void consistir(Configuracao configuracao) throws Exception {
        consistirBK(configuracao);

    }

    /**
     * Método que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(Configuracao configuracao, Transacao t) throws Exception {
        consistir(configuracao);

        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (chave,valor) ");
        sql.add("VALUES ");
        sql.add(" (:chave, :valor)");

        sql.setParam("chave", configuracao.getChave());
        sql.setParam("valor", configuracao.getValor());

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
    public static int alterar(Configuracao configuracao, Transacao t) throws Exception {
        consistir(configuracao);

        SQL sql = new SQL();
        sql.add("UPDATE " + TBL);
        sql.add("SET valor = :valor");
        sql.add("WHERE chave = :chave");

        sql.setParam("chave", configuracao.getChave());
        sql.setParam("valor", configuracao.getValor());

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
    public static int excluir(Configuracao configuracao, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE chave = :chave");

        sql.setParam("chave", configuracao.getChave());

        return t.executeUpdate(sql.getSql());
    }

    public static int excluirChavesQueComecam(String chave, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE chave like :chave");

        sql.setParam("chave", chave + "%");

        return t.executeUpdate(sql.getSql());
    }

    /**
     * Método que carrega o objeto Musica pelo ID.
     *
     *
     * @param musica Contendo a musica.
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     */
    public static boolean carregar(Configuracao configuracao, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE chave = :chave ");

        sql.setParam("chave", configuracao.getChave());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }

            configuracao.setChave(rs.getString("chave"));
            configuracao.setValor(rs.getString("valor"));

            return true;
        } finally {
            rs.close();
        }
    }

    /** Método que retorna uma lista de Musicas de acordo com o filtro.
     * @param filtro Contendo o filtro.
     * @param t Contendo a transação.
     * @return ArrayList Contendo uma lista de Musicas. */
    public static HashMap<String, String> listar(ConfiguracaoSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1=1");

        if (filtro.parteChave != null) {
            sql.add("AND chave like :chave");
            sql.setParam("chave", filtro.parteChave + "%");
        }

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            HashMap<String, String> lista = new HashMap<String, String>();
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
     * Método que tenta incluir um objeto Musica.
     *
     *
     * @param musica Contendo a musica.
     * @return int Contendo o numero de linhas afetadas.
     */
    public static int incluir(Configuracao configuracao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(configuracao, t);
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
    public static int alterar(Configuracao configuracao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = alterar(configuracao, t);
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
    public static int excluir(Configuracao configuracao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluir(configuracao, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    public static int excluirChavesQueComecam(String chave) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluirChavesQueComecam(chave, t);
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
    public static boolean carregar(Configuracao configuracao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = carregar(configuracao, t);
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
    public static HashMap<String, String> listar(ConfiguracaoSC filtro) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            HashMap<String, String> r = listar(filtro, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    public static ArrayList<File> listarPastas() {
        Transacao t = new Transacao();
        ArrayList<File> pastas = new ArrayList<File>();
        try {
            t.begin();
            ConfiguracaoSC filtro = new ConfiguracaoSC();
            filtro.parteChave = "pastaMonitorada";
            HashMap<String, String> lista = listar(filtro, t);
            Set chaves = lista.keySet();

            for (int i = 0; i < chaves.toArray().length; i++) {
                pastas.add(new File(lista.get(chaves.toArray()[i])));
            }
            t.commit();

        } catch (Exception ex) {
            t.rollback();
            ex.printStackTrace();
        }
        return pastas;
    }
}
