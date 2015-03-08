
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author c90
 */
public class Diff {
    
    
    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader br1 = new BufferedReader(new FileReader("/home/c90/.local/share/wesnoth/1.10/saves/FFa-Diving_Intervention-Salvo-automaticamente4"));
        BufferedReader br2 = new BufferedReader(new FileReader("/home/c90/.local/share/wesnoth/1.10/saves/FFa-Diving_Intervention-Salvo-automaticamente5"));
        diff(br1, br2);
    }
    
    public static void diff(BufferedReader fr1, BufferedReader fr2){
        try {
            int contaLinha = 0;
            String linha;
            while((linha = fr1.readLine()) != null){
                contaLinha++;
                String linha2 = fr2.readLine();
                if (linha2 == null) {
                    System.out.println("F1: " + contaLinha);
                    System.out.println("F2: EOF");
                    return ;
                }
                if (!linha.equals(linha2)) {
                    System.out.println("DIF: " + contaLinha);
                    System.out.println("F1: " + linha);
                    System.out.println("F2: " + linha2);
                    
                }
            }
            if (fr2.readLine() != null) {
                contaLinha++;
                System.out.println("F1: EOF");
                System.out.println("F2: " + contaLinha);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
}
