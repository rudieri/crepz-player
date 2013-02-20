/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao;

import com.conexao.SQL;
import com.conexao.Transacao;
import com.musica.Musica;
import com.playlist.Playlist;
import com.playlist.listainteligente.condicao.operadores.OperadorComparativo;
import com.playlist.listainteligente.condicao.operadores.OperadorLogico;
import com.utils.campo.Campo;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rudieri
 */
public class CondicaoBD {

    /**
     * Constante que define o nome da tabela.
     */
    public static final String TBL = "condicao";

    /**
     * Método que consiste a BK de Condicao.
     *
     * @param condicao
     * @throws Exception
     */
    public static void consistirBK(Condicao condicao) throws Exception {
        if (condicao == null) {
            throw new Exception(" - Condicao não informado.");
        }
    }

    /**
     * Método que consiste os dados de Condicao.
     *
     * @param condicao
     * @throws Exception
     */
    public static void consistir(Condicao condicao) throws Exception {
        consistirBK(condicao);
    }

    /**
     * Método que tenta incluir um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int incluir(Condicao condicao, Transacao t) throws Exception {
        consistir(condicao);

        // depois as condições filhas
        if (condicao.getTipoValorCondicao1() == TipoValorCondicao.CONDICAO) {
            incluir((Condicao) condicao.getValor1(), t);
        }
        if (condicao.getTipoValorCondicao2() == TipoValorCondicao.CONDICAO) {
            incluir((Condicao) condicao.getValor2(), t);
        }
        Date dateInc = new Date();
        // primeiro inclui a condição principal
        SQL sql = new SQL();
        sql.add("INSERT INTO " + TBL);
        sql.add(" (id, playlist, tipo_operador, operador, tipo_valor1, valor1, tipo_valor2, valor2, dt_inc) ");
        sql.add("VALUES ");
        sql.add(" (:id, :playlist, :tipo_operador, :operador, :tipo_valor1, :valor1, :tipo_valor2, :valor2, :dt_inc)");

        sql.setParam("id", null);
        sql.setParam("playlist", condicao.getPlaylist() == null ? null : condicao.getPlaylist().getId());
        sql.setParam("tipo_operador", condicao.getOperador().getClass().getName());
        sql.setParam("operador", ((Enum) condicao.getOperador()).ordinal());
        sql.setParam("tipo_valor1", condicao.getTipoValorCondicao1().ordinal());
        sql.setParam("valor1", condicao.getValor1ToBD());
        sql.setParam("tipo_valor2", condicao.getTipoValorCondicao2().ordinal());
        sql.setParam("valor2", condicao.getValor2ToBD());
        sql.setParam("dt_inc", dateInc.getTime());
        int numLinhas = t.executeUpdate(sql.getSql());

        carregarPelaDtInc(condicao, dateInc.getTime(), t);


        return numLinhas;
    }

    /**
     * Método que tenta alterar um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int alterar(Condicao condicao, Transacao t) throws Exception {
//        consistir(condicao);
//
//        SQL sql = new SQL();
//        sql.add("UPDATE " + TBL);
//        sql.add("SET  tipo_operador = :tipo_operador, operador = :operador, ");
//        sql.add("tipo_valor1 = :tipo_valor1, valor1 = :valor1, tipo_valor2 = :tipo_valor2, valor2 = :valor2");
//        sql.add("WHERE id = :id ");
//
//        sql.setParam("tipo_operador", condicao.getOperador().getClass().getName());
//        sql.setParam("operador", ((Enum) condicao.getOperador()).ordinal());
//        sql.setParam("tipoValor1", condicao.getTipoValorCondicao1().ordinal());
//        sql.setParam("valor1", condicao.getValor1().toString());
//        sql.setParam("tipoValor2", condicao.getTipoValorCondicao2());
//        sql.setParam("valor2", condicao.getValor2().toString());
//        int numLinhas = t.executeUpdate(sql.getSql());
//        
//        return numLinhas;
        throw new UnsupportedOperationException("Não implementado.");
    }

    /**
     * Método que tenta excluir um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @param t Contendo a transação.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int excluir(Condicao condicao, Transacao t) throws Exception {

        // Depois procura recursivamente por condições filhas
        if (condicao.getTipoValorCondicao1() == TipoValorCondicao.CONDICAO) {
            excluir((Condicao) condicao.getValor1(), t);
        }
        if (condicao.getTipoValorCondicao2() == TipoValorCondicao.CONDICAO) {
            excluir((Condicao) condicao.getValor2(), t);
        }
        // primeiro exclui a condição principal
        SQL sql = new SQL();
        sql.add("DELETE FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", condicao.getId());
        int numLinhas = t.executeUpdate(sql.getSql());

        return numLinhas;
    }

//    public static int excluir(Playlist playlist, Transacao t) throws Exception {
//        SQL sql = new SQL();
//        sql.add("DELETE FROM " + TBL);
//        sql.add("WHERE playlist = :playlist ");
//
//        sql.setParam("playlist", playlist.getId());
//        int numLinhas = t.executeUpdate(sql.getSql());
//        return numLinhas;
//    }
//   
    /**
     * Método que carrega o objeto Condicao pelo ID.
     *
     *
     * @param condicao Contendo a condicao.
     * @param t Contendo a transação.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception
     */
    public static boolean carregar(Condicao condicao, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE id = :id ");

        sql.setParam("id", condicao.getId());

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }
            carregarObjeto(condicao, rs, t);
            return true;
        } finally {
            rs.close();
        }
    }

    private static boolean carregarPelaDtInc(Condicao condicao, long time, Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT * FROM " + TBL);
        sql.add("WHERE dt_inc = :dt_inc ");

        sql.setParam("dt_inc", time);

        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            if (!rs.next()) {
                return false;
            }
            carregarObjeto(condicao, rs, t);
            return true;
        } finally {
            rs.close();
        }
    }

    public static void carregarObjeto(Condicao condicao, ResultSet rs, Transacao t) throws Exception {
        condicao.setId(rs.getInt("id"));
        Playlist playlist = new Playlist();
        playlist.setId(rs.getInt("playlist"));
        if (rs.wasNull()) {
            playlist = null;
        }
        condicao.setPlaylist(playlist);
        String nomeTipoOperador = rs.getString("tipo_operador");
        if (nomeTipoOperador.equals(OperadorComparativo.class.getName())) {
            OperadorComparativo oc = OperadorComparativo.values()[rs.getInt("operador")];
            TipoValorCondicao tvc1 = TipoValorCondicao.values()[rs.getInt("tipo_valor1")];
            TipoValorCondicao tvc2 = TipoValorCondicao.values()[rs.getInt("tipo_valor2")];
            ValorCondicao valorCondicao1;
            if (tvc1 == TipoValorCondicao.CAMPO) {
                valorCondicao1 = new ValorCondicao(new Campo(Musica.class, rs.getString("valor1")));
            } else if (tvc1 == TipoValorCondicao.INTEGER) {
                valorCondicao1 = new ValorCondicao(rs.getInt("valor1"));
            } else if (tvc1 == TipoValorCondicao.SHORT) {
                valorCondicao1 = new ValorCondicao(Short.valueOf(rs.getString("valor1")));
            } else if (tvc1 == TipoValorCondicao.BYTE) {
                valorCondicao1 = new ValorCondicao(Byte.valueOf(rs.getString("valor1")));
            } else if (tvc1 == TipoValorCondicao.LONG) {
                valorCondicao1 = new ValorCondicao(Long.valueOf(rs.getString("valor1")));
            } else {
                valorCondicao1 = new ValorCondicao(rs.getString("valor1"));
            }
            ValorCondicao valorCondicao2;
            if (tvc2 == TipoValorCondicao.CAMPO) {
                valorCondicao2 = new ValorCondicao(new Campo(Musica.class, rs.getString("valor2")));
            } else if (tvc2 == TipoValorCondicao.INTEGER) {
                valorCondicao2 = new ValorCondicao(Integer.valueOf(rs.getString("valor2")));
            } else if (tvc2 == TipoValorCondicao.SHORT) {
                valorCondicao2 = new ValorCondicao(Short.valueOf(rs.getString("valor2")));
            } else if (tvc2 == TipoValorCondicao.BYTE) {
                valorCondicao2 = new ValorCondicao(Byte.valueOf(rs.getString("valor2")));
            } else if (tvc2 == TipoValorCondicao.LONG) {
                valorCondicao2 = new ValorCondicao(Long.valueOf(rs.getString("valor2")));
            } else {
                valorCondicao2 = new ValorCondicao(rs.getString("valor2"));
            }
            condicao.setValoresCondicao(oc, valorCondicao1, valorCondicao2);
        } else if (nomeTipoOperador.equals(OperadorLogico.class.getName())) {
            OperadorLogico oc = OperadorLogico.values()[rs.getInt("operador")];
            Condicao c1 = new Condicao(Integer.valueOf(rs.getString("valor1")));
            Condicao c2 = new Condicao(Integer.valueOf(rs.getString("valor2")));
            carregar(c1, t);
            carregar(c2, t);
            condicao.setValoresCondicao(oc, c1, c2);
        } else {
            throw new IllegalStateException("Tipo operador não encontrado.");
        }
    }

    /**
     * Método que retorna uma lista de Condicaos de acordo com o filtro.
     *
     * @param filtro Contendo o filtro.
     * @param t Contendo a transação.
     * @return ArrayList Contendo uma lista de Condicaos.
     * @throws Exception
     */
    public static ArrayList<Condicao> listar(CondicaoSC filtro, Transacao t) throws Exception {
        if (filtro == null) {
            throw new Exception(" - Filtro não informado.");
        }


        SQL sql = new SQL();
        sql.add("SELECT * ");
        sql.add("FROM " + TBL);
        sql.add("WHERE 1 = 1 AND ");

        if (filtro.getPlaylist() != null) {
            sql.add("playlist  = :playlist");
            sql.setParam("playlist", filtro.getPlaylist().getId());
        }



        sql.add("ORDER BY id ");



        ResultSet rs = t.executeQuery(sql.getSql());
        try {
            ArrayList lista = new ArrayList(10);
            while (rs.next()) {
                Condicao condicao = new Condicao(rs.getInt("id"));
                carregarObjeto(condicao, rs, t);
                lista.add(condicao);
            }
            System.gc();
            return lista;
        } finally {
            rs.close();
        }
    }

    public static int contarCondicaos(Transacao t) throws Exception {
        SQL sql = new SQL();
        sql.add("SELECT count(id) as qtd FROM " + TBL);
        ResultSet rs = t.executeQuery(sql.getSql());
        if (rs.next()) {
            return rs.getInt("qtd");
        } else {
            return 0;
        }
    }

    /*
     * ######################################### METODOS SEM TRANSACAO
     *#########################################
     */
    /**
     * Método que tenta incluir um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int incluir(Condicao condicao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = incluir(condicao, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que tenta alterar um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int alterar(Condicao condicao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = alterar(condicao, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que tenta excluir um objeto Condicao.
     *
     *
     * @param condicao Contendo a condicao.
     * @return int Contendo o numero de linhas afetadas.
     * @throws Exception
     */
    public static int excluir(Condicao condicao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            int r = excluir(condicao, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que carrega o objeto Condicao pelo ID.
     *
     *
     * @param condicao Contendo a condicao.
     * @return boolean Contendo TRUE se está cadastrado e FALSE se não estiver.
     * @throws Exception
     */
    public static boolean carregar(Condicao condicao) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            boolean r = carregar(condicao, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    /**
     * Método que retorna uma lista de Condicaos de acordo com o filtro.
     *
     * @param filtro Contendo o filtro.
     * @return ArrayList Contendo uma lista de Condicaos.
     * @throws Exception
     */
    public static ArrayList<Condicao> listar(CondicaoSC filtro) throws Exception {
        Transacao t = new Transacao();
        try {
            t.begin();
            ArrayList<Condicao> r = listar(filtro, t);
            t.commit();
            return r;
        } catch (Exception ex) {
            t.rollback();
            throw ex;
        }
    }

    private CondicaoBD() {
    }
}
