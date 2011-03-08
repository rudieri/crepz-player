package com.conexao;

import java.util.*;
import java.text.*;

/** Classe que representa uma instrução SQL. */
public class SQL {

    /** Atributo que mantém a instrução SQL. */
    private StringBuffer sql = new StringBuffer();
    /** Atributo que mantém uma lista com todos os parâmetros e seus valores do SQL em questão. */
    private HashMap params = new HashMap();
    /** Atributo que mantém o ultimo sqlOK. */
    private StringBuffer sqlOk;
    /** Atributo que mantém a flag modificado. */
    private boolean modificado;

    /** Método que retorna um SQL sem transação. */
    public static SQL getInstanciaSemTransacao() {
        return new SQL();
    }

    /** Método construtor principal. */
    public SQL() {
    }

    /** Método construtor que inicializa a instrução SQL.*/
    public SQL(String sql) {
        add(sql);
    }

    /** Método que limpa o sql atual.*/
    public void clear() {
        modificado = true;
        sql = new StringBuffer();
    }

    /** Método que adiciona uma parte ou todo a instrução SQL.
     * @param sql Contendo parte ou a instrução SQL.*/
    public void add(String sql) {
        modificado = true;
        this.sql.append(sql.trim()).append("\n");
    }

    /** Método que seta valor no parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo Object. */
    public void setParam(String param, Object value) throws Exception {
        modificado = true;

        if (param == null || param.equals("")) {
            return;
        }

        String p = ":" + param;
        String v = "";
        if (value == null) {
            v = "null";
        } else if (value instanceof String) {
            v = "'" + value.toString().trim().replaceAll("'", "") + "'";
        } else if (value instanceof Integer) {
            v = value.toString();
        } else if (value instanceof Double) {
            v = value.toString();
        } else if (value instanceof Float) {
            v = value.toString();
        } else if (value instanceof java.sql.Date) {
            v = "'" + dateToString(value, "MM/dd/yyyy") + "'";
        } else if (value instanceof java.sql.Timestamp) {
            v = "'" + dateToString(value, "MM/dd/yyyy HH:mm:ss") + "'";
        } else if (value instanceof java.util.Date) {
            v = "'" + dateToString(value, "MM/dd/yyyy") + "'";
        }  else {
            throw new Exception("classe " + value.getClass().getName() + " não tratada em SQL.");
        }

        params.put(p, v);
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo int. */
    public void setParam(String param, int value) throws Exception {
        setParam(param, new Integer(value));
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo float. */
    public void setParam(String param, float value) throws Exception {
        setParam(param, new Float(value));
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo double. */
    public void setParam(String param, double value) throws Exception {
        setParam(param, new Double(value));
    }

    /** Método que retorna o SQL original, sem os valores dos parâmetros.
     * @return String Contendo o sql original. */
    public String getSqlOriginal() {
        return sql.toString();
    }



    public String getSql() {
        if (modificado) {
            modificado = false;
            sqlOk = new StringBuffer();

            String param = "";
            String letra = "";
            boolean estaNoParametro = false;
            for (int i = 0; i < sql.length(); i++) {
                letra = String.valueOf(sql.charAt(i));
                if (estaNoParametro) {
                    if (letra.equals(",") || letra.equals(" ") || letra.equals("\n") || letra.equals("\t") || letra.equals(")")) {
                        estaNoParametro = false;
                        sqlOk.append(params.get(":" + param)).append(letra);
                        param = "";
                    } else {
                        param += letra;
                    }
                } else {
                    if (letra.equals(":")) {
                        estaNoParametro = true;
                    } else {
                        sqlOk.append(letra);
                    }
                }
            }
        }

        String sql2 = sqlOk.substring(0, sqlOk.length() - 1) + ";";
        System.out.println(sql2);
        return sql2;
    }

   
    /** Método que imprime o sql na saida padrao. */
    public void print() {
        System.out.println(getSql());
    }

    /** Método que retorna uma data como uma String. */
    private String dateToString(Object data, String formato) {
        return new SimpleDateFormat(formato).format(data);
    }

 
}
