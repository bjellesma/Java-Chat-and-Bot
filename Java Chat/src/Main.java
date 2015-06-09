import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.JOptionPane;


public class Main {
    public static Connection conn;
    public static Statement st;
    public static ResultSet rs;
    public static ResultSet rs2;
    public static int userID;
    /*public Main(){
        //not sure what to do with the catch block
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
             Connection conn = (Connection)DriverManager.getConnection("jdbc:mysql://gator890.hostgator.com:3306/billj_chat", "billj_bill2", "jells0640");
        } catch (Exception e) {
            
        }
           

    }*/

    public static void main(String[] args) {
        //Make Connection
        
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = (java.sql.Connection) DriverManager.getConnection(Core.db, Core.db_user, Core.db_pass);
            st = conn.createStatement();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        new Welcome().setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    String query = "UPDATE `users` SET `online`=0, `signoff`=1 WHERE `id`='" + userID + "';";
                    System.out.println(query);
                    Main.st.executeUpdate(query); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
        
    }
    
    

}
