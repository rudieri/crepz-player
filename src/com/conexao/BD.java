/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.conexao;

import com.utils.FileUtils;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author manchini
 */
public class BD {

    private Connection conn;
    private Statement st;
    private String local = new File("").getAbsolutePath();
    private String banco = "BD";
    private String URL = "jdbc:hsqldb:file:";
    private String user = "sa";
    private String senha = "";
    private static boolean primeiraVez = true;

    public BD() {
    }

    public BD(String local, String banco, String user, String senha) {
        this.user = user;
        this.senha = senha;
    }

    private String getArquivosBanco() {
        String ret = null;
        if (new File(new File("").getAbsoluteFile() + "/" + banco + ".properties").exists()) {
            ret = new File("").getAbsoluteFile() + "/" + banco;
        } else if (new File(new File("").getAbsoluteFile() + "/dist/" + banco + ".properties").exists()) {
            ret = new File("").getAbsoluteFile() + "/dist/" + banco;

        } else if (new File(new File("").getAbsolutePath().replace("dist", "") + "/" + banco + ".properties").exists()) {
            ret = new File("").getAbsolutePath().replace("dist", "") + "/" + banco;
        }

        if(ret == null){
            ret = new File("").getAbsolutePath()+"/"+banco;
        }

//        System.out.println("BANCO: ---- "+ret);
        return ret;
    }

    public Statement getStatement() throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            conn = DriverManager.getConnection(URL + getArquivosBanco(), user, senha);
            System.out.println("Conectado com: " + URL + getArquivosBanco());
            return conn.createStatement();
        } catch (ClassNotFoundException ex) {
            testarTabelas(conn);
            throw new SQLException(ex);
        }
    }

    public Connection getConexao() throws SQLException {
        try {

            Class.forName("org.hsqldb.jdbcDriver");
            conn = DriverManager.getConnection(URL + getArquivosBanco(), user, senha);
            System.out.println("Conectado com: " + URL + getArquivosBanco());
            testarTabelas(conn);
            return conn;
        } catch (ClassNotFoundException ex) {
            testarTabelas(conn);
            throw new SQLException(ex);
        }
    }

    public void testarTabelas(Connection con) {
        if (primeiraVez) {
            primeiraVez = false;
             if(System.getProperty("java.runtime.name").toString().indexOf("OpenJDK") > -1)
                JOptionPane.showMessageDialog(new JDialog(), "Sua Versão JAVA pode não ser compativel.", "DANGER", JOptionPane.ERROR_MESSAGE);
            
            System.out.println("Primeira Vez Testa Tabelas");
            try {
                Statement st = con.createStatement();
                st.execute("select * from musica limit 1");
            } catch (Exception e) {
                //Se Deu erro pq não tem as tabelas
                if(e.getMessage().equals("user lacks privilege or object not found: MUSICA")){
                    try {                       
                        //dai cria
                        Statement st = con.createStatement();
                        st.executeUpdate(FileUtils.leArquivo(new File(getClass().getResource("/com/config/config.sql").toURI())).toString());
                        st.executeUpdate(FileUtils.leArquivo(new File(getClass().getResource("/com/musica/musica.sql").toURI())).toString());
                        st.executeUpdate(FileUtils.leArquivo(new File(getClass().getResource("/com/playlist/playlist.sql").toURI())).toString());
                        st.executeUpdate(FileUtils.leArquivo(new File(getClass().getResource("/com/playmusica/playmusica.sql").toURI())).toString());

                    } catch (Exception ex) {
                        System.out.println("Crepz Fatal.");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public static void hadukem(){
        String banco = new BD().getArquivosBanco();
        File properties = new File(banco+".properties");
        if(!properties.delete()){
            System.out.println("Não deu pra excluir o BD.properties");
        }

        File script = new File(banco+".script");
        if(!script.delete()){
            System.out.println("Não deu pra excluir o BD.script");
        }
    }
}
