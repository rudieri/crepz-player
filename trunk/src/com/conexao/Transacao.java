package com.conexao;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

/** Classe que representa uma transacao no bando de dados. */
public class Transacao {

    /** Atributo que mant�m uma conex�o com o bando de dados. */
    private Connection conn;
    private Statement stmt;

    /** M�todo construtor default. */
    public Transacao() {
    }

    /** M�todo que retorna a conex�o. */
    public Connection getConexao() {
        return conn;
    }

    /** M�todo que inicia a transa��o. */
    public void begin() throws Exception {
        conn = new BD().getConexao();
        conn.setAutoCommit(false);
        stmt = conn.createStatement();

    }

    /** M�todo que realiza o commit na transa��o. */
    public void commit() throws Exception {
        conn.commit();
//        conn.close();
        conn.createStatement().execute("SHUTDOWN");
        conn = null;
    }

    /** M�todo que realiza o rollback na transa��o. */
    public void rollback() {
        try {
            conn.rollback();
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /** M�todo que realiza a execu��o de um comando de atualiza��o sql, a saber, insert, update, delete. */
    public int executeUpdate(String sql) throws Exception {

        int r = 0;
        try {
            r = stmt.executeUpdate(sql);
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("foreign key") == -1) {
                throw ex;
            } else {
                throw new Exception(" - Existem registros associados.Mensagem do BD: \n - " + ex.getMessage());
            }
        }
        return r;
    }

    /** M�todo que realiza a execu��o de um comando sql de consulta, a saber, selects. */
    public java.sql.ResultSet executeQuery(String sql) throws Exception {

        return conn.createStatement().executeQuery(sql);
    }
}

