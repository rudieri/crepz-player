
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class conectaAccess {

    public static void main(String[] args) {
        try {
            try {
                Class.forName("org.hsqldb.jdbcDriver");
            } catch (Exception e) {
                System.out.println("Erro ao carregar o driver JDBC. ");
            }
           
            Connection con = DriverManager.getConnection("jdbc:hsqldb:file:C:/JPlayer/BD", "sa", "");
//            con.setAutoCommit(false);
//            Statement st =con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
//
//            System.out.println(st.executeUpdate("insert into PUBLIC.TESTE values(9);"));
//            st.close();
//            con.commit();
//            ResultSet rs = con.createStatement().executeQuery("select * from teste;");
//            while (rs.next()) {
//                System.out.println(rs.getString("ID"));
//            }
//            con.commit();
//            con.close();

            // table definition: CREATE TABLE T (NAME VARCHAR(12), ...)


            Statement st;
            con.setAutoCommit(false);
            st = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);


            st = con.createStatement();
// type of the parameter is VARCHAR(12), which limits length to 12 characters
           ResultSet rs = st.executeQuery("SELECT * FROM TESTE"); // executes with no exception and does not find any rows
            
            while (rs.next()) {
                System.out.println(rs.getString("ID"));
            }
// but if an UPDATE is attempted, an exception is raised
            st.executeUpdate("insert into PUBLIC.TESTE values(9);");
            con.commit();
//            con.close();
            con.createStatement().execute("SHUTDOWN");

//st.setString(1, "Eyjafjallajokull"); // string is longer than type, but no exception is raised here
//            st.executeBatch(); // exception is thrown when HyperSQL checks the value for update
            


        } catch (Exception ex) {
            Logger.getLogger(conectaAccess.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
