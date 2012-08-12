
import java.awt.Color;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rudieri
 */
public class Operadores {

    
    public static void main(String[] args) {
        System.out.println(2<<(2^3));
        System.out.println((true^true));
        int preto = Color.BLACK.getRGB();
        int branco = Color.white.getRGB();
        System.out.println(preto);
        System.out.println(branco);
        System.out.println(branco^preto);
//        System.out.println(4>>3);
//        System.out.println(4>>>3);
    }
}
