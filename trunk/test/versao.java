

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author manchini
 */
public class versao {
    // int: 400.048 (~4 Bytes)
    // Integer: 800.024 (~8 Bytes)/ 2.797.464
    // String: 800.024 (~8 Bytes) / 7.377.804 
//    public Integer[] ai;

    public versao() {
//        this.ai = new Integer[100000];
//        for (int i = 0; i < ai.length; i++) {
//            ai[i] = i;
//            
//        }
        // + = 1578611, 1873104, 2122471
        long nanoTime = System.nanoTime();
        double a;
        for (int i = 0; i < 100000000; i++) {
            a = Math.sqrt(i);
        }
        System.out.println(System.nanoTime() - nanoTime + "ns");
    }

    public static void main(String[] args) throws InterruptedException {
        versao versao = new versao();
//        while (true) {
//
//        }

    }

}
