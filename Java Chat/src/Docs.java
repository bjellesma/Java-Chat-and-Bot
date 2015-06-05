
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bill-Laptop
 */
public class Docs extends JFrame {
    private JTextArea docWindow;
    private JButton signOut;
    public Docs(){
        super("Talk to Dale");
            
            signOut = new JButton();
            signOut.setText("Back to Chat");
            signOut.addActionListener(
                    new ActionListener(){
                        @Override
                    public void actionPerformed(ActionEvent event){                      
                        Docs.this.dispose();
                    }
                }
            );
            add(signOut, BorderLayout.NORTH);
            docWindow = new JTextArea();
            docWindow.setText("Welcome to chat!\n\nHere you can talk to your friends, invite someone to"
                    + "a private chat, or talk to our residents, Steve and Dale");
            add(new JScrollPane(docWindow), BorderLayout.CENTER);
            setSize(600,300);
            setVisible(true);
    }
}
