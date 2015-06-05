import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bill
 */
public class Account extends JFrame {
    
    private String name;
    private String motto;
    private String picLoc;
    private JPanel cred;
    private JLabel label;
    private JLabel userLabel;
    private JLabel nameLabel;
    private JLabel mottoLabel;
    private JButton changePic;
    private ImageIcon profilePic;
    
    
    
    public Account(String userName){
        super(userName + "\'s Account");
        cred = new JPanel();
        try{
       
            String query = "SELECT `pic`, `name`, `motto` FROM `users` WHERE `username`='" + userName + "';";
            Main.rs = Main.st.executeQuery(query);
            Main.rs.next();
            picLoc = Main.rs.getString("pic");
            name = Main.rs.getString("name");
            motto = Main.rs.getString("motto");  
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e);
        }
        profilePic = new ImageIcon(".\\pic\\" + picLoc);
        label = new JLabel(profilePic);
        add(label, BorderLayout.NORTH);
        changePic = new JButton();
        changePic.setText("Change Profile Picture");
            changePic.addActionListener(
                    new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent event){                      
                            final JFileChooser fc = new JFileChooser();
                            fc.setDialogTitle("Profile Picture");
                            fc.setFileFilter(new FileExtension());
                            int returnVal = fc.showOpenDialog(null);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();

                                    try {
                                        Core.copyfile(file);
                                    } catch (FileNotFoundException ex) {
                                        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (IOException ex) {
                                        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    Core.updatePic(file.getName());
                            }
                        }
                    }
            );
        add(changePic, BorderLayout.WEST);
        userLabel = new JLabel(userName);
        nameLabel = new JLabel(name);
        mottoLabel = new JLabel(motto);
        cred.add(nameLabel);
        cred.add(mottoLabel);
        cred.add(userLabel, BorderLayout.NORTH); 
        cred.add(nameLabel);
        cred.add(mottoLabel);
        add(cred, BorderLayout.CENTER);
        setSize(1200,600);
        setVisible(true);
    }
    }
    
