/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dalebot;
import java.util.Random;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; 
import javax.swing.*;
/**
 *
 * @author Bill
 */
public class DaleBot extends JFrame {
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    String[] greetingMessages = {"hi", "How are you", "whats up", "yo", "hola"};
    String[] statementMessages = {"I know what you mean", "dude, that\'s sick", 
        "You know, I was once in a production of \"Fiddler on the Room\"", "You should probably see a doctor about that", 
        "You should watch \"How I Met your Mother\""};
    String[] questionMessages = {"That all depends...", "Obviously the square root of pie", 
        "Wait, say that again", "I don\'t know but I\'ll ask my therapist", 
        "I don\'t know...I\'m sorry"};
    String[] exclamationMessages = {"Seriously!", "dude, that\'s hilarious", 
        "That same thing happened to me at band camp", "That\'s way too much information", 
        "It\'s a good idea in theory..."};
    Random randomGenerator;
    ArrayList<String> greetings = new ArrayList<>();
    
    
    /**
     * @param args the command line arguments
     */
    public DaleBot(){
        super("DaleBot");
        greetings.add("hello");
        greetings.add("whats up");
        greetings.add("hows it hangin");
        
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }
       
    public void start(){
        try{
            //a server on port 9999 with 100 person que
            server = new ServerSocket(9999, 100);
            while(true){
                try{
                    //connect and have conversation
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(Exception e){
                    showMessage("\nServer ended the connection");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        }
        
        private void waitForConnection() throws IOException{
            showMessage( "Waiting for someone to Connect...");
            //once someone accepts...
            connection = server.accept();
            showMessage( "Now connected to"+ connection.getInetAddress().getHostName());
            
        }
        
        private void setupStreams() throws IOException{
            output = new ObjectOutputStream(connection.getOutputStream());
            //flush out the excess
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            //cant flush an input stream
            showMessage("\n Streams are now set up \n");
        }
        
        private void whileChatting() throws IOException, ClassNotFoundException{
            
            String message = "You are now connected";
            sendMessage(message);
            String userName = (String) input.readObject();
            do{
                    randomGenerator = new Random();
                    int randNum = randomGenerator.nextInt(4);
                try{
                    message = (String) input.readObject();
                    message = message.toLowerCase();
                    showMessage("\n"+connection.getInetAddress().getHostName()+" - " + message);
                    if(greetings.contains(message)){
                        sendMessage(greetingMessages[randNum] + " " + userName);
                    }
                    else if(message.substring(message.length()-1, message.length()).equals(".")){
                        sendMessage(statementMessages[randNum]);
                    }
                    else if(message.substring(message.length()-1, message.length()).equals("?")){
                        sendMessage(questionMessages[randNum]);
                    }
                    else if(message.substring(message.length()-1, message.length()).equals("!")){
                        sendMessage(exclamationMessages[randNum]);
                    }
                    
                    else{
                        sendMessage("Say again Ghost Rider... how about you use some punctuation");
                    }
                }catch(ClassNotFoundException e){
                    showMessage("\n idk");
                }
                //as long as the user doesn't type end
            }while(!message.equals("CLIENT - END"));
        }
        
        /*
         * Basic housekeeping method
         */
        private void closeCrap(){
            showMessage("\n Closing connections...\n");
            
            try{
                output.close();
                input.close();
                connection.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        
        /*
         * Send messages
         */
        private void sendMessage(String message){
            try{
                output.writeObject("Dale - " + message);
                output.flush();
                showMessage("\nDale - " + message);
            }catch(IOException e){
                chatWindow.append("ERROR");
            }
        }
        
        private void showMessage(final String text){
            //update the chatWindow GUI
            SwingUtilities.invokeLater(
                   new Runnable(){
                       public void run(){
                           chatWindow.append(text);
                       }
                   }
            );
        }
        
         private void ableToType(final boolean ToF){
            //update the chatWindow GUI
            SwingUtilities.invokeLater(
                   new Runnable(){
                       public void run(){
                           userText.setEditable(ToF);
                       }
                   }
            );
        }
}
