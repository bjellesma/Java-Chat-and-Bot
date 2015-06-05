import java.io.*;
import javax.swing.JOptionPane;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bill
 */
public class Core{
    final static String db = "jdbc:mysql://localhost/java_chat";
    final static String db_user = "root";
    final static String db_pass = "";
    
    
    static String user;
    static int userId;
    
    public static void setUserOnline(int id){
         try{
        
            String query = "UPDATE `users` SET `online`=1 WHERE `id`="+id+";";
            //this doesn't require a result set
            Main.st.executeUpdate(query);
            String query2 = "UPDATE `users` SET `time`=" + System.currentTimeMillis()/1000 + "WHERE `id`="+id+";";
            Main.st.executeUpdate(query2);
            
            
        }catch(Exception e){
            
        }
    }
    
    public static boolean checkUser(String username){
     try{
        
            String query = "SELECT `username` FROM `users` WHERE `username`='"+username+"';";
            //this doesn't require a result set
            Main.rs = Main.st.executeQuery(query);
            if(Main.rs.next() == true){
                return true;
            }
            
        }catch(Exception e){
            
        }
            return false;
    }
    public static boolean userOnline(String username){
        try{
        
            String query = "SELECT `online` FROM `users` WHERE `username`='"+username+"';";
            //this doesn't require a result set
            Main.rs = Main.st.executeQuery(query);
            Main.rs.next();
            if(Main.rs.getInt("online") == 1){
                return true;
            }
            
        }catch(Exception e){
            
        }
            return false;
    }
    
    public static void insertUser(String regFields, String regData){
        try{
        
            String query = "INSERT INTO `users` ("+regFields+") VALUES ("+regData+");";
            JOptionPane.showMessageDialog(null, query);
            //this doesn't require a result set
            Main.st.executeUpdate(query);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    public static String formatTime(int time){
        time = (time - 14400) % 86400;
                        String timeStamp = " a.m.";
                        int hours = time / 3600;
                        time = time % 3600;
                        int minutes = time / 60;
                        time = time % 60;
                        int seconds = time;
                        if(hours>=12){
                            timeStamp = " p.m.";
                            if(hours>12){
                                hours-=12;
                            }
                        }
                        else if(hours == 0){
                            hours = 12;
                        }
                        String preMin = "";
                        if(minutes < 10){
                             preMin = "0";
                        }
                        String preSec = "";
                        if(seconds < 10){
                            preSec = "0";
                        }
                        String formattedTime = hours + ":" + preMin + minutes + ":" + preSec + seconds + timeStamp;
                        return formattedTime;
    }
    
    public static String loadUserPic(String userName){
        String pic = null;
        try{
        
            String query = "SELECT `pic` FROM `users` WHERE `username`='" + userName + "';";
            Main.rs = Main.st.executeQuery(query);
            Main.rs.next();
            pic = Main.rs.getString("pic");
            
            
        }catch(Exception e){
            
        }
        return pic;
    }
    public static void setUser(String username){
        user = username;
         try{
        
            String query = "SELECT `id` FROM `users` WHERE `username`='"+username+"';";
            
            Main.rs = Main.st.executeQuery(query);
            Main.rs.next();
            int userId = Main.rs.getInt("id");
            
        }catch(Exception e){
            
        }
    }
    public static String getUser(){
        
        return user;
    }
    public static int getUserId(){
        return userId;
    }
    
    public static void copyfile(File file) throws FileNotFoundException, IOException{
        File f2 = new File(file.getName());
        
        
        InputStream in = new FileInputStream(file);
        OutputStream out = new FileOutputStream(".\\pic\\"+f2);
        
        byte[] buf = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        
        in.close();
        out.close();
    }
    public static void updatePic(String pic){
        try{
        
            String query = "UPDATE `users` SET `pic`='"+pic+"';";
            Main.st.executeUpdate(query);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
}

