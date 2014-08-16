package com.utils.model.tablemodel;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author c90
 */
public class ColunaTabela {

    private final Field[] fields;

    public ColunaTabela(Field... fields) {
        this.fields = fields;
    }

    public Field getField() {
        return fields[fields.length - 1];
    }

    public Object getValor(Object instancia) {
        try {
            return getValor(instancia, 0);
        } catch (Exception ex) {
            Logger.getLogger(ColunaTabela.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private Object getValor(Object instancia, int idxCampo) throws IllegalArgumentException, IllegalAccessException {
        if (idxCampo == fields.length - 1) {
            return fields[idxCampo].get(instancia);
        } else {
            return getValor(fields[idxCampo].get(instancia), idxCampo + 1);
        }
    }
}
