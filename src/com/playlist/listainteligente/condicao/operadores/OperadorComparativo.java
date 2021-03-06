/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao.operadores;

import com.musica.MusicaS;
import com.playlist.listainteligente.condicao.ValorCondicao;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author rudieri
 */
public enum OperadorComparativo implements Operador<ValorCondicao> {

    IGUAL("Igual", "==", TipoOperadorComparativo.AMBOS),
    DIFERENTE("Diferente", "!=", TipoOperadorComparativo.AMBOS),
    CONTEM("Contém", "C", TipoOperadorComparativo.TEXTUAL),
    NAO_CONTEM("Não Contém", "!C", TipoOperadorComparativo.TEXTUAL),
    COMECA_COM("Começa Com", "x*", TipoOperadorComparativo.TEXTUAL),
    TERMINA_COM("Termina Com", "*x", TipoOperadorComparativo.TEXTUAL),
    MAIOR("Maior", ">", TipoOperadorComparativo.NUMERICO),
    MAIOR_IGUAL("Maior ou Igual", ">=", TipoOperadorComparativo.NUMERICO),
    MENOR("Menor", "<", TipoOperadorComparativo.NUMERICO),
    MENOR_IGUAL("Diferente", "<=", TipoOperadorComparativo.NUMERICO),
    REGEX("Expressão Regular", "Regex", TipoOperadorComparativo.AMBOS);
    private final String nome;
    private final String representacao;
    private final TipoOperadorComparativo tipoOperadorComparativo;

    private OperadorComparativo(String nome, String representacao, TipoOperadorComparativo tipoOperadorComparativo) {
        this.nome = nome;
        this.representacao = representacao;
        this.tipoOperadorComparativo = tipoOperadorComparativo;
    }

    @Override
    public boolean resolverOperacao(ValorCondicao valor1, ValorCondicao valor2, MusicaS musica) {
        Object v1 = valor1.getValor(musica);
        Object v2 = valor2.getValor(musica);


        if (v1 == null || v2 == null) {
            return false;
        }
        switch (this) {
            case IGUAL:
                return v1 == v2 || v1.equals(v2)
                        || v1.toString().toLowerCase().equals(v2.toString().toLowerCase());
            case DIFERENTE:
                return v1.equals(v2);
            case CONTEM:
                return v1.toString().toLowerCase().contains(v2.toString().toLowerCase());
            case NAO_CONTEM:
                return !v1.toString().toLowerCase().contains(v2.toString().toLowerCase());
            case COMECA_COM:
                return v1.toString().toLowerCase().startsWith(v2.toString().toLowerCase());
            case TERMINA_COM:
                return v1.toString().toLowerCase().endsWith(v2.toString().toLowerCase());
            case MAIOR:
                return v1 instanceof Comparable && v2 instanceof Comparable ? ((Comparable) v1).compareTo(v2) > 0 : v1.hashCode() > v2.hashCode();
            case MAIOR_IGUAL:
                return v1 instanceof Comparable && v2 instanceof Comparable ? ((Comparable) v1).compareTo(v2) >= 0 : v1.hashCode() >= v2.hashCode();
            case MENOR:
                return v1 instanceof Comparable && v2 instanceof Comparable ? ((Comparable) v1).compareTo(v2) < 0 : v1.hashCode() < v2.hashCode();
            case MENOR_IGUAL:
                return v1 instanceof Comparable && v2 instanceof Comparable ? ((Comparable) v1).compareTo(v2) <= 0 : v1.hashCode() <= v2.hashCode();
            case REGEX:
                return v1.toString().matches(v2.toString());
            default:
                return false;

        }
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() {
        return nome;
    }

    public TipoOperadorComparativo getTipoOperadorComparativo() {
        return tipoOperadorComparativo;
    }

    @Override
    public String getRepresentacao() {
        return representacao;
    }

    public static ArrayList<OperadorComparativo> listar(TipoOperadorComparativo tipoOperadorComparativo) {
        ArrayList<OperadorComparativo> lista = new ArrayList<OperadorComparativo>(5);
        if (tipoOperadorComparativo == TipoOperadorComparativo.NUMERICO) {
            lista.addAll(Arrays.asList(OperadorComparativo.values()));
        } else {
            for (OperadorComparativo operadorComparativo : values()) {
                if (operadorComparativo.getTipoOperadorComparativo() == tipoOperadorComparativo) {
                    lista.add(operadorComparativo);
                }
            }
        }
        return lista;
    }
}
