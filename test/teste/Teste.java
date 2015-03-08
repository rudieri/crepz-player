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
    
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(200);
        long time = System.currentTimeMillis();
        for (long i = 0; i < 10000000000l; i++) {
            //"abra".equals("");//4677
//            "abra".equals("");
//            "cacaa".equals("");
//            "caca".equals("");
//            "cacaaa".equals("");
//            "cacaar".equals("");
        }
        System.out.println("Tempo: " + (System.currentTimeMillis() - time));
        
    }
}
