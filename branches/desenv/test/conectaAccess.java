
import com.conexao.BancoServer;
import java.util.logging.Level;
import java.util.logging.Logger;



public class conectaAccess {

    public static void main(String[] args) {
        try {
            BancoServer s = new BancoServer("BD");
//            Thread.sleep(5000);
//            s.stop();

        } catch (Exception ex) {
            Logger.getLogger(conectaAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
