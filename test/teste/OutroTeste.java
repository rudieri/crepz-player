/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package teste;

import java.util.Arrays;

/**
 *
 * @author rudieri
 */
public class OutroTeste extends Teste {

    public static void main(String[] args) {
        int[][] xxx = new int[][]{{1,2}, {3,4,7}};
        
        System.out.println(Arrays.toString(xxx));
        while (true) {
            
        }
    }
    private static int[] xxx(int[] xxx){
        xxx[0] = 666;
        return xxx;
    }
}
