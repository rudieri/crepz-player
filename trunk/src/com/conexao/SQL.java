package com.conexao;

import java.util.*;
import java.text.*;

/** Classe que representa uma instrução SQL. */
public final class SQL {

    /** Atributo que mantém a instrução SQL. */
    private StringBuilder sql = new StringBuilder(50);
    /** Atributo que mantém uma lista com todos os parâmetros e seus valores do SQL em questão. */
    private HashMap params = new HashMap(8);
    /** Atributo que mantém o ultimo sqlOK. */
    private StringBuilder sqlOk;
    /** Atributo que mantém a flag modificado. */
    private boolean modificado;
    private static final SimpleDateFormat MMddyyyy = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat MMddyyyyHHmmss = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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
        sql = new StringBuilder(50);
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

        if (param == null || param.isEmpty()) {
            return;
        }

        String p = ":" + param;
        String v;
        if (value == null) {
            v = "null";
        } else if (value instanceof String) {
            v = "'" + value.toString().replaceAll("'", "") + "'";
        } else if (value instanceof Integer || value instanceof Double || value instanceof Float || value instanceof Long) {
            v = value.toString();
        } else if (value instanceof java.sql.Date || value instanceof java.util.Date) {
            v = "'" + dateToString(value, MMddyyyy) + "'";
        } else if (value instanceof java.sql.Timestamp) {
            v = "'" + dateToString(value, MMddyyyyHHmmss) + "'";
        } else {
            throw new Exception("classe " + value.getClass().getName() + " não tratada em SQL.");
        }

        params.put(p, v);
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo int. */
    public void setParam(String param, int value) throws Exception {
        String p = ":" + param;
        params.put(p, Integer.toString(value));
//        setParam(param, new Integer(value));
    }
    public void setParam(String param, long value) throws Exception {
        String p = ":" + param;
        params.put(p, Long.toString(value));
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo float. */
    public void setParam(String param, float value) throws Exception {
        String p = ":" + param;
        params.put(p, Float.toString(value));
    }

    /** Método que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo double. */
    public void setParam(String param, double value) throws Exception {
        String p = ":" + param;
        params.put(p, Double.toString(value));
    }

    /** Método que retorna o SQL original, sem os valores dos parâmetros.
     * @return String Contendo o sql original. */
    public String getSqlOriginal() {
        return sql.toString();
    }

    public String getSql() {
        if (modificado) {
            modificado = false;
            sqlOk = new StringBuilder(50);

            StringBuilder param = new StringBuilder(10);
            char letra;
            boolean estaNoParametro = false;
            for (int i = 0; i < sql.length(); i++) {
                letra = sql.charAt(i);
                if (estaNoParametro) {
                    if (letra == ',' || letra == ' ' || letra == '\n' || letra == '\t' || letra == ')') {
                        estaNoParametro = false;
                        sqlOk.append(params.get(":" + param.toString())).append(letra);
                        param = new StringBuilder(10);
                    } else {
                        param.append(letra);
                    }
                } else {
                    if (letra == ':') {
                        estaNoParametro = true;
                    } else {
                        sqlOk.append(letra);
                    }
                }
            }
        }

        String sql2 = sqlOk.substring(0, sqlOk.length() - 1) + ";";
//        System.out.println(sql2);
        return sql2;
    }

    /** Método que imprime o sql na saida padrao. */
    public void print() {
        System.out.println(getSql());
    }

    /** Método que retorna uma data como uma String. */
    private String dateToString(Object data, SimpleDateFormat formato) {
        return formato.format(data);
    }
}
