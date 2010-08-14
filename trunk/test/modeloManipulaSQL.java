
import com.conexao.SQL;
import com.conexao.Transacao;
import java.io.File;
import java.sql.ResultSet;
import com.musica.Musica;
import com.musica.MusicaBD;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author manchini
 */
public class modeloManipulaSQL {

    public static void main(String[] args){
        Transacao t = new Transacao();
        try{    
            t.begin();


            Musica.mapearDiretorio(new File("D:/Users/manchini/Music/Minhas m�sicas/"), t,null,0);

            SQL sql = new SQL();
            sql.add("SELECT * from musica");
            ResultSet rs = t.executeQuery(sql.getSql());
            while(rs.next()){
                System.out.println(rs.getString("id"));
                System.out.println(rs.getString("nome"));
                System.out.println(rs.getString("caminho"));
                System.out.println(rs.getString("img"));
                System.out.println("******************************");
            }
            t.commit();

        }catch(Exception ex){            
            ex.printStackTrace();
            t.rollback();
        }
    }

}