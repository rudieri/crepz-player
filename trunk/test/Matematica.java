

/**
 *
 * @author c90
 */
public class Matematica {
    public static void main(String[] args) {
        int base;
        int expoente;
        int resultado;
        int contador = 1;
        
        base = 8;
        expoente = 6;
        resultado = base;
        
        
        while (contador < expoente) {
            contador = contador + 1;
            resultado = resultado * base;
        }
        System.out.println("O resultado de " + base + " na potência " + expoente + " é " + resultado);
        
    }
}
