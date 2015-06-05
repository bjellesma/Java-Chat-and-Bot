
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bill
 */
public class ChatServer {
    public static void main(String args[]){
    try{
            ServerSocket mySS = new ServerSocket(9999);
            System.out.println("waiting for client");
            
            while(true){
            //accepting the server socket
            Socket SS_accept = mySS.accept();
            System.out.println("Connect");
        
            //read the information
            BufferedReader SS_BR = new BufferedReader(new InputStreamReader (SS_accept.getInputStream()));
            PrintStream SS_PS = new PrintStream(SS_accept.getOutputStream());
            
            //create a string to read the line
            String temp = SS_BR.readLine();
        
            //display the line to the user
            SS_PS.println(temp);
        
            
            }
        }
        catch(Exception e){
            
        }
    }
}
