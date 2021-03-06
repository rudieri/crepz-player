/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.playlist.listainteligente.condicao;

import com.utils.campo.Campo;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class ValorCondicao implements Serializable {

    private static final long serialVersionUID = 2L;

    private Object valor;
    private Campo campo;

    public ValorCondicao(Boolean valor) {
        this.valor = valor;
    }

    public ValorCondicao(String valor) {
        this.valor = valor;
    }

    public ValorCondicao(Byte valor) {
        this.valor = valor;
    }

    public ValorCondicao(Short valor) {
        this.valor = valor;
    }

    public ValorCondicao(Integer valor) {
        this.valor = valor;
    }

    public ValorCondicao(Long valor) {
        this.valor = valor;
    }

    public ValorCondicao(Campo campo) {
        this.campo = campo;
    }

    public Object getValor(Object objeto) {
        return getValor(objeto, null);
    }

    public Object getValor(Object objeto, String novoCaminnho) {
        if (valor != null) {
            return valor;
        } else {
            try {
                if (novoCaminnho == null) {
                    novoCaminnho = campo.getCaminhoCampo();
                }
                if (novoCaminnho.indexOf('.') == -1) {
                    return campo.getField().get(objeto);
                } else {
                    String[] split = novoCaminnho.split("[.]", 2);
                    if (split[1].isEmpty()) {
                        Field f = objeto.getClass().getDeclaredField(split[0]);
                        if (!f.isAccessible()) {
                            f.setAccessible(true);
                        }
                        return f.get(objeto);
                    } else {
                        Field f = objeto.getClass().getDeclaredField(split[0]);
                        if (!f.isAccessible()) {
                            f.setAccessible(true);
                        }
                        return getValor(f.get(objeto), split[1]);
                    }
//                    musica.getClass().g
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(ValorCondicao.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

    }

    public TipoValorCondicao getTipoValorCondicao() {
        if (valor == null) {
            return TipoValorCondicao.CAMPO;
        } else if (valor instanceof String) {
            return TipoValorCondicao.STRING;
        } else if (valor instanceof Byte) {
            return TipoValorCondicao.BYTE;
        } else if (valor instanceof Short) {
            return TipoValorCondicao.SHORT;
        } else if (valor instanceof Long) {
            return TipoValorCondicao.LONG;
        } else if (valor instanceof Boolean) {
            return TipoValorCondicao.BOOLEAN;
        } else {
            return TipoValorCondicao.INTEGER;
        }
    }

    @Override
    public String toString() {
        if (valor == null) {
            return campo.toString();
        } else {
            return valor.toString();
        }
    }

    public String toBD() {
        if (valor == null) {
            return campo.getField().getName();
        } else {
            return valor.toString();
        }
    }

    public Campo getCampo() {
        return campo;
    }
}
