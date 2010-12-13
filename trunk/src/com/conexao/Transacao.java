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
        conn.setAutoCommit(true);
        stmt = conn.createStatement();

    }

    /** Método que realiza o commit na transação. */
    public void commit() throws Exception {
        conn.commit();
//        conn.close();
//        conn.createStatement().execute("SHUTDOWN");
    }

    /** Método que realiza o rollback na transação. */
    public void rollback() {
        try {
            conn.rollback();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    /** Método que realiza a execução de um comando de atualização sql, a saber, insert, update, delete. */
    public int executeUpdate(String sql) throws SQLException {
        int r = 0;
        r = stmt.executeUpdate(sql);
        return r;
    }

    /** Método que realiza a execução de um comando sql de consulta, a saber, selects. */
    public java.sql.ResultSet executeQuery(String sql) throws Exception {

        return conn.createStatement().executeQuery(sql);
    }
}
