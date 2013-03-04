/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.campo;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Campo {

    private Field field;

    public static boolean contemAnotacaoNecessaria(Field field1) {
        NomeCampo annotation = field1.getAnnotation(NomeCampo.class);
        return annotation != null;
    }

    public Campo(Field field) {
        this.field = field;
    }

    public Campo(Class classe, String nomeCampo) {
        try {
            this.field = classe.getDeclaredField(nomeCampo);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Campo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Campo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isByte() {
        return field.getType() == byte.class || field.getType() == Byte.class;

    }

    public boolean isShort() {
        return field.getType() == short.class || field.getType() == Short.class;

    }

    public boolean isLong() {
        return field.getType() == long.class || field.getType() == Long.class;

    }
    public boolean isBoolean() {
        return field.getType() == boolean.class || field.getType() == Boolean.class;

    }

    public boolean isInteger() {
        return field.getType() == int.class || field.getType() == Integer.class;
    }

    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Campo.class) {
            return false;
        }
        ;
        return field.equals(((Campo) obj).getField());
//        return field.getDeclaringClass() == ((Campo)obj).getField().getDeclaringClass()
//                && field.getName().equals(((Campo)obj).getField().getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.field != null ? this.field.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        NomeCampo annotation = field.getAnnotation(NomeCampo.class);
        return annotation == null ? field.getName() : annotation.nome();
    }
}
