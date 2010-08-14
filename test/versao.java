/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author manchini
 */
public class versao {
    public static void main(String[] args){
        System.out.println(System.getProperty("java.runtime.name"));
        System.out.println(System.getProperty("java.vendor"));

        System.out.println(System.getProperties().toString().replace(",", "\n"));
    }

}
