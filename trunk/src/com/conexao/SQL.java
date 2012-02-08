package com.conexao;

import java.util.*;
import java.text.*;

/** Classe que representa uma instru��o SQL. */
public final class SQL {

    /** Atributo que mant�m a instru��o SQL. */
    private StringBuilder sql = new StringBuilder(50);
    /** Atributo que mant�m uma lista com todos os par�metros e seus valores do SQL em quest�o. */
    private HashMap params = new HashMap(8);
    /** Atributo que mant�m o ultimo sqlOK. */
    private StringBuilder sqlOk;
    /** Atributo que mant�m a flag modificado. */
    private boolean modificado;
    private static final SimpleDateFormat MMddyyyy = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat MMddyyyyHHmmss = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    /** M�todo que retorna um SQL sem transa��o. */
    public static SQL getInstanciaSemTransacao() {
        return new SQL();
    }

    /** M�todo construtor principal. */
    public SQL() {
    }

    /** M�todo construtor que inicializa a instru��o SQL.*/
    public SQL(String sql) {
        add(sql);
    }

    /** M�todo que limpa o sql atual.*/
    public void clear() {
        modificado = true;
        sql = new StringBuilder(50);
    }

    /** M�todo que adiciona uma parte ou todo a instru��o SQL.
     * @param sql Contendo parte ou a instru��o SQL.*/
    public void add(String sql) {
        modificado = true;
        this.sql.append(sql.trim()).append("\n");
    }

    /** M�todo que seta valor no parametro.
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
            throw new Exception("classe " + value.getClass().getName() + " n�o tratada em SQL.");
        }

        params.put(p, v);
    }

    /** M�todo que seta valor ao parametro.
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

    /** M�todo que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo float. */
    public void setParam(String param, float value) throws Exception {
        String p = ":" + param;
        params.put(p, Float.toString(value));
    }

    /** M�todo que seta valor ao parametro.
     * @param param Contem o parametro a ser setado o valor.
     * @param value Contem o valor a ser atribuido ao parametro, do tipo double. */
    public void setParam(String param, double value) throws Exception {
        String p = ":" + param;
        params.put(p, Double.toString(value));
    }

    /** M�todo que retorna o SQL original, sem os valores dos par�metros.
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

    /** M�todo que imprime o sql na saida padrao. */
    public void print() {
        System.out.println(getSql());
    }

    /** M�todo que retorna uma data como uma String. */
    private String dateToString(Object data, SimpleDateFormat formato) {
        return formato.format(data);
    }
}
