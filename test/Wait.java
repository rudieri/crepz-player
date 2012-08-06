
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rudieri
 */
public class Wait {

    public static void main(String[] args) throws InterruptedException {
        new Wait();
    }

    public Wait() throws InterruptedException {
        init();
    }

    private synchronized void init() throws InterruptedException {
        MyThread myThread = new MyThread(this);
        myThread.start();
        wait();
        System.out.println("GameOver");

    }

    private synchronized static void print() {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.out.println(stackTraceElement);
        }
    }

    private static class MyThread extends Thread implements Runnable {
        private final Wait wait;

        public MyThread(Wait wait) {
            this.wait = wait;
        }

        
        
        @Override
        public void run() {
            try {
                System.out.println("MyThread.run()");
                print();
                Thread.sleep(5000);
               wait.notify();
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Wait.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
}
