package com.utils.campo;

import com.utils.model.tablemodel.ObjetoTabela;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rudieri
 */
public class Campo implements Serializable{
    private static final long serialVersionUID = 2L;

    private transient Field field;
    private Class classe;
    private String caminhoCampo;

    public static boolean contemAnotacaoNecessaria(Field field1) {
        NomeCampo annotation = field1.getAnnotation(NomeCampo.class);
        return annotation != null;
    }
    public static boolean contemFilhos(Field field){
        return field.getAnnotation(ObjetoTabela.class) != null;
    }


    public Campo(Class classe, String caminhoCampo) {
        this.classe = classe;
        this.caminhoCampo = caminhoCampo;
        createField(classe, caminhoCampo);
    }

    public boolean isByte() {
        return getField().getType() == byte.class || getField().getType() == Byte.class;

    }

    public boolean isShort() {
        return getField().getType() == short.class || getField().getType() == Short.class;

    }

    public boolean isLong() {
        return getField().getType() == long.class || getField().getType() == Long.class;

    }
    public boolean isBoolean() {
        return getField().getType() == boolean.class || getField().getType() == Boolean.class;

    }

    public boolean isInteger() {
        return getField().getType() == int.class || getField().getType() == Integer.class;
    }

    public Field getField() {
        if (field == null) {
            createField(classe, caminhoCampo);
        }
        return field;
    }

    private void createField(Class classe, String nomeCampo) {
        try {
            String[] split = nomeCampo.split("[.]", 2);
            if (split.length == 2 && !split[1].isEmpty()) {
                createField(classe.getDeclaredField(split[0]).getType(), split[1]);
            } else {
                this.field = classe.getDeclaredField(nomeCampo);
                field.setAccessible(true);
            }
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Campo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Campo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getCaminhoCampo() {
        return caminhoCampo;
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Campo.class) {
            return false;
        }
        return getField().equals(((Campo) obj).getField());
    }

    @Override
    public int hashCode() {
        Field f = this.getField();
        int hash = 7;
        hash = 29 * hash + (f != null ? f.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        Field f = getField();
        NomeCampo annotation = f.getAnnotation(NomeCampo.class);
        return annotation == null ? f.getName() : annotation.nome();
    }
}
