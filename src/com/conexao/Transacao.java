package com.conexao;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

/** Classe que representa uma transacao no bando de dados. */
public class Transacao {

    /** Atributo que mantém uma conexão com o bando de dados. */
    private Connection conn;
    private Statement stmt;

    /** Método construtor default. */
    public Transacao() {
    }

    /** Método que retorna a conexão. */
    public Connection getConexao() {
        return conn;
    }

    /** Método que inicia a transação. */
    public void begin() throws Exception {
        conn = new BD().getConexao();
        conn.setAutoCommit(false);
        stmt = conn.createStatement();

    }

    /** Método que realiza o commit na transação. */
    public void commit() throws Exception {
        conn.commit();
//        conn.close();
        conn.createStatement().execute("SHUTDOWN");
        conn = null;
    }

    /** Método que realiza o rollback na transação. */
    public void rollback() {
        try {
            conn.rollback();
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /** Método que realiza a execução de um comando de atualização sql, a saber, insert, update, delete. */
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

    /** Método que realiza a execução de um comando sql de consulta, a saber, selects. */
    public java.sql.ResultSet executeQuery(String sql) throws Exception {

        return conn.createStatement().executeQuery(sql);
    }
}

