/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.swing.*;


/**
 *
 * @author Bill
 */
public class SteveBot extends JFrame {
    
    private JTextField userText;
    private JTextArea chatWindow;
    private JButton signOut;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    final String userName = Core.getUser();
    public SteveBot(String host){
        
        super("Client Instant Messanger");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
            @Override
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.SOUTH);
        signOut = new JButton();
        signOut.setText("Back to Global Chat");
        signOut.addActionListener(
                new ActionListener(){
                    @Override
                public void actionPerformed(ActionEvent event){
                    closeConn();
                    SteveBot.this.dispose();
                }
            }
        );
        add(signOut, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }
    
    //connect to server
    public void start(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(Exception e){
            e.printStackTrace();
        }                

    }
    
    //connect to server
    private void connectToServer(){
        try{
        showMessage("Attempting Connection");
        connection = new Socket(InetAddress.getByName(serverIP), 9999);
        showMessage("\nConnect to" + connection.getInetAddress().getHostName());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void setupStreams(){
        try{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nGood to go");
        output.writeObject(userName);
         output.flush();
        
        }catch(Exception e){
            e.printStackTrace();
        }
        ableToType(true);
        
    }
    
    private void whileChatting(){
        ableToType(true);
        
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException e){
                showMessage("IDK");
            }catch(IOException e){
                e.printStackTrace();
            }               
        
    }
    
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        final Runnable steveChat = new Runnable() {

            @Override
            public void run() {
                
                
                  ableToType(true);
        
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException e){
                showMessage("IDK");
            }catch(IOException e){
                e.printStackTrace();
            }
            ableToType(true);


            }
        };
        //the scheduled task runs every 10 seconds
        final ScheduledFuture steveRefreshChat = scheduler.scheduleAtFixedRate(steveChat, 10, 10, SECONDS);
        
    
    private void closeConn(){
        showMessage("\nClosing Connection");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void sendMessage(String message){
        try{
            output.writeObject(message);
            ableToType(false);
            output.flush();
              showMessage("\n"+ userName + " - " + message);      
        }catch(IOException e){
            chatWindow.append("\n Something went wrong");
        }
    }
    
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(m);
                    }
                });
    }
            
    private void ableToType(final boolean ToF){
               SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(ToF);
                    }
                });
    }
    
    

    
}
