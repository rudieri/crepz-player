/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teste;

/**
 *
 * @author rudieri
 */
public class Teste {
    public static int VAR_PUBLIC;
    protected static int VAR_PROTECTED;
    private static int VAR_PRIVATE;
    
    public static void main(String[] args) {
        Exception ex = new Exception();
        NullPointerException nex = new NullPointerException();
        System.out.println(ex instanceof NullPointerException);
        System.out.println(nex instanceof Exception);
        nex = (NullPointerException) ex;
    }
}
