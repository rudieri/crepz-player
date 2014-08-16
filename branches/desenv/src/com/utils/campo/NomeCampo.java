/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.campo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author rudieri
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NomeCampo {
    String nome();
}
