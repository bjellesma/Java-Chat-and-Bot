import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    static int loggedTime;
    static long curTime;
    
    public static void setUserOnline(int id){
         try{
             userId = id;
            String query = "UPDATE `users` SET `online`=1 WHERE `id`="+id+";";
            //this doesn't require a result set
            Main.st.executeUpdate(query);
            curTime = System.currentTimeMillis()/1000;
            loggedTime = (int) curTime;
            String query2 = "UPDATE `users` SET `time`=" + loggedTime + " WHERE `id`="+id+";";
            
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

    /*
    *Function to get time of login
    */
    public static int getLogTime(){
        return loggedTime;
    }
    public static String getUser(){
        
        return user;
    }
    public static int getUserId(){
        return userId;
    }
    /*
    *Set signoff
    */
    public static void setSignOff(int signOff){
        String query = "UPDATE `users` SET `signoff`="+signOff+" WHERE `id`="+userId+";";
        try {
            Main.st.executeUpdate(query);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        
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

