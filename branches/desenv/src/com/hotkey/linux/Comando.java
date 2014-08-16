/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotkey.linux;

/**
 *
 * @author c90
 */
public class Comando {

    private TipoComando tipoComando;
    private StringBuilder argumentos;

    public Comando(String comandoCompleto) {
        String[] tokens = comandoCompleto.split(" ");
        tipoComando = TipoComando.valueOf(tokens[0]);
        if (tokens.length > 1) {
            argumentos = new StringBuilder();
            for (int i = 1; i < tokens.length; i++) {
                String arg = tokens[i];
                argumentos.append(arg).append(' ');
            }
            argumentos.deleteCharAt(argumentos.length() - 1);
        }
    }

    public Comando(TipoComando tipoComando) {
        this.tipoComando = tipoComando;
    }

    public Comando(TipoComando tipoComando, String argumentos) {
        this.tipoComando = tipoComando;
        this.argumentos = new StringBuilder(argumentos);
    }

    public String getArgumentos() {
        return argumentos.toString();
    }

    public TipoComando getTipoComando() {
        return tipoComando;
    }

    @Override
    public String toString() {
        return tipoComando.getComando() + (argumentos == null ? "" : " " + argumentos);
    }
}
