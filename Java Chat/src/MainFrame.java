
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 *
 * @author Bill
 */
public class MainFrame extends javax.swing.JFrame {
    
    /**
     * Creates new form MainFrame
     */
    
    final int timeDifferential = 1;
    String host, time, usersOnlineString, chatAreaString, eventLogString;
    ArrayList<String> usersOnlineArrayList;
    StringBuilder usersOnlineStringBuilder = new StringBuilder();
    StringBuilder chatAreaStringBuilder = new StringBuilder();
    StringBuilder eventLogStringBuilder = new StringBuilder();
    int request = 0;
    long chatUpdateTime;  
    long updateTime;
    String userName = Core.getUser();
    public MainFrame() {
        this.usersOnlineArrayList = new ArrayList<String>();
        
        initComponents();
        time = Core.formatTime(Core.getLogTime()); 
        userNameLabel.setText(userName);
        eventLogStringBuilder.append("<html>");
        
        try {
            String queryOnline = "SELECT `username`, `color` from `users` WHERE `online`=1;";
            Main.rs = Main.st.executeQuery(queryOnline);
            usersOnlineStringBuilder.append("<html>");
            //while the result set is still not empty
            while (Main.rs.next()) {
                String userOnline = Main.rs.getString("username");
                String userColor = Main.rs.getString("color");
                usersOnlineArrayList.add(userOnline); 
                usersOnlineStringBuilder.append("<font color='").append(userColor).append("'>").append(userOnline).append("<br />");
                usersOnlineString = usersOnlineStringBuilder.toString();
                usersOnlineTextPane.setText(usersOnlineString);
            }
            String queryChat = "SELECT chat.user, chat.chat, chat.time, users.color from `chat`"
                    + "INNER JOIN users ON users.username=chat.user;";
            Main.rs = Main.st.executeQuery(queryChat);
            chatAreaStringBuilder.append("<html>");
            //while the result set is still not empty
            while (Main.rs.next()) {
                String user = Main.rs.getString("user");
                String chat = Main.rs.getString("chat");
                int time = Main.rs.getInt("time");
                String userColor = Main.rs.getString("color");
                chatAreaStringBuilder.append("<font color='").append(userColor).append("'>").append(user).append(":").append(chat).append("(").append(Core.formatTime(time)).append(")<br />");
                chatAreaString = chatAreaStringBuilder.toString();
                chatAreaTextPane.setText(chatAreaString);
            } 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error in Connectivity2: " + e);
        }
        
    }
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        final Runnable refreshChat = new Runnable() {

            @Override
            public void run() {
                
                try{
                    String query = "SELECT chat.chatID, chat.user, chat.chat, chat.time, chat.transit, users.color FROM `chat`"
                            + " INNER JOIN users ON users.username=chat.user WHERE chat.transit=1;";
                    Main.rs = Main.st.executeQuery(query);
                    System.out.println("here");
                    //while the result set is still not empty
                    while (Main.rs.next()) {
                        int chatID = Main.rs.getInt("chatID");
                        String user = Main.rs.getString("user");
                        String chat = Main.rs.getString("chat");
                        int time = Main.rs.getInt("time");
                        
                        String userColor = Main.rs.getString("color");
                        chatAreaStringBuilder.append("<font color='").append(userColor).append("'>").append(user).append(":").append(chat).append("(").append(Core.formatTime(time)).append(")<br />");
                        chatAreaString = chatAreaStringBuilder.toString();
                        chatAreaTextPane.setText(chatAreaString);
                        //might not be good for scalability
                        String queryChat3 = "UPDATE `chat` SET `transit`=0 WHERE `chatID`="+chatID+";"; 
                        Main.st.executeUpdate(queryChat3);
                        }
                    //scroll to bottom of text area
                    chatAreaTextPane.setCaretPosition(chatAreaTextPane.getDocument().getLength());

                    String query3 = "SELECT `username`, `time`, `color` from `users` WHERE `online`=1;";
                    Main.rs = Main.st.executeQuery(query3);
                    while (Main.rs.next()) {
                        String userOnline = Main.rs.getString("username");
                        //Slight fix for inconsistencies
                        if(!usersOnlineArrayList.contains(userOnline)){
                            String userColor = Main.rs.getString("color");
                            String time = Core.formatTime(Main.rs.getInt("time"));
                            usersOnlineArrayList.add(userOnline);
                            usersOnlineStringBuilder.append("<font color='").append(userColor).append("'>").append(userOnline).append("<br />");
                            usersOnlineString = usersOnlineStringBuilder.toString();
                            usersOnlineTextPane.setText(usersOnlineTextPane.getText() + usersOnlineString);
                            eventLogStringBuilder.append("<font color='").append(userColor).append("'>").append(userOnline).append("<br />").append(" has joined the server(").append(time).append(")");
                            eventLogString = eventLogStringBuilder.toString();
                            eventLogTextPane.setText(eventLogTextPane.getText() + usersOnlineString);
                        }
                        
                     }
                    //update the event log
                    String query4 = "SELECT `username`, `time`, `color` from `users` WHERE `signoff`=1;";
                    Main.rs = Main.st.executeQuery(query4);
                    while (Main.rs.next()) {
                        String userOnline = Main.rs.getString("username");
                        String time = Core.formatTime(Main.rs.getInt("time"));
                        String userColor = Main.rs.getString("color");
                        eventLogStringBuilder.append("<font color='").append(userColor).append("'>").append(userOnline).append("<br />").append(" has joined the server(").append(time).append(")");
                            eventLogString = eventLogStringBuilder.toString();
                            eventLogTextPane.setText(eventLogTextPane.getText() + usersOnlineString);
                     }
                    //set the signoff value back to zero
                    String query5 = "UPDATE `users` SET `signoff`=0 WHERE `signoff`=1;"; 
                    Main.st.executeUpdate(query5);
                    /*String query6 = "SELECT `host` FROM `privatechat` WHERE `client`='" + userName + "';";
                    Main.rs = Main.st.executeQuery(query6);
                    if(Main.rs.next() && request == 0 ){
                        host = Main.rs.getString("host");
                        request = 1;
                        eventLogTextArea.append(host + "has invited" + userName + " to a private chat...accept?");
                    }
                    if(request==1){
                        //reset the chat request so that they can't be requested again
                        request = -1;
                        int option = JOptionPane.showConfirmDialog(null, host + " has invited you to private chat...accept?", "Chat Request", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            new PrivateChat().setVisible(true);
                            MainFrame.this.dispose();
                        }else{
                            eventLogTextArea.append(userName + " has decline " + host + "\'s invitation to a private chat");
                            String query7 = "DELETE FROM `privatechat` WHERE `client`='" + userName + "');";
                            Main.st.executeUpdate(query7);
                            //reset request so  others can request
                            request = 0;
                        }
                    }*/
                } catch (Exception e) {
                }
            }
        };
        final Runnable refreshOnline = new Runnable() {

            @Override
            public void run() {
                try{
                    long onlineTime = System.currentTimeMillis()/1000 - 30;
                    String query2 = "UPDATE `users` SET `online`=0,`signoff`=1 WHERE `time`<" + onlineTime + " AND `online`=1;"; 
                    Main.st.executeUpdate(query2);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //the scheduled task runs every 10 seconds
        final ScheduledFuture autoRefreshChat = scheduler.scheduleAtFixedRate(refreshChat, timeDifferential, 10, SECONDS);
        //final ScheduledFuture autoRefreshOnline = scheduler.scheduleAtFixedRate(refreshOnline, 10, 10, SECONDS);
        //test
        
/*
         * Set the username in the corner
         */
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        disconnectButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        privateChatButton = new javax.swing.JButton();
        steveButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        inputTextArea = new javax.swing.JTextArea();
        daleButton = new javax.swing.JButton();
        accountButton = new javax.swing.JButton();
        docsButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersOnlineTextPane = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        chatAreaTextPane = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        eventLogTextPane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Chat- Global Chat");

        disconnectButton.setText("Sign Out");
        disconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Users Online");

        sendButton.setText("Chat");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Username:");

        jLabel3.setText("Event Log");

        privateChatButton.setText("Private Chat");
        privateChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateChatButtonActionPerformed(evt);
            }
        });

        steveButton.setText("Talk to Steve!");
        steveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                steveButtonActionPerformed(evt);
            }
        });

        inputTextArea.setColumns(20);
        inputTextArea.setRows(5);
        jScrollPane3.setViewportView(inputTextArea);

        daleButton.setText("Talk to Dale!");
        daleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daleButtonActionPerformed(evt);
            }
        });

        accountButton.setText("My Account");
        accountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountButtonActionPerformed(evt);
            }
        });

        docsButton.setText("Documentation");
        docsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docsButtonActionPerformed(evt);
            }
        });

        usersOnlineTextPane.setContentType("text/html");
        jScrollPane1.setViewportView(usersOnlineTextPane);

        chatAreaTextPane.setContentType("text/html");
        jScrollPane5.setViewportView(chatAreaTextPane);

        eventLogTextPane.setContentType("text/html");
        jScrollPane2.setViewportView(eventLogTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(212, 212, 212)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(docsButton)
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(disconnectButton)
                                .addGap(33, 33, 33)
                                .addComponent(steveButton)
                                .addGap(37, 37, 37)
                                .addComponent(daleButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(privateChatButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sendButton)
                                .addGap(0, 8, Short.MAX_VALUE))
                            .addComponent(jScrollPane5))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(accountButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(67, 67, 67))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(docsButton))
                                    .addComponent(userNameLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(disconnectButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(steveButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(daleButton, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(accountButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(privateChatButton, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(20, 20, 20)))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(46, 46, 46))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane2))))
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    
        private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
            String chat = inputTextArea.getText();
            try {
                String query = "INSERT INTO `chat` (`user`, `chat`, `time`, `transit`) VALUES ('" + userName + "', '" + chat + "', "+ System.currentTimeMillis()/1000+", 1);";
                
                //this doesn't require a result set
                Main.st.executeUpdate(query);
                String query2 = "UPDATE `users` SET `time`=" + System.currentTimeMillis()/1000 + " WHERE `username`='"+userName+"';";
                Main.st.executeUpdate(query2);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: Could not send chat: " + e);
            }
            inputTextArea.setText("");
    }//GEN-LAST:event_sendButtonActionPerformed

    private void disconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectButtonActionPerformed
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out");
        if (option == JOptionPane.YES_OPTION) {
            try {
                String query = "UPDATE `users` SET `online`=0, `signoff`=1 WHERE `username`='" + userName + "';";
                Main.st.executeUpdate(query); 
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error in Connectivity: " + e);
            }
            MainFrame.this.dispose();
            new Login().setVisible(true);
        }
    }//GEN-LAST:event_disconnectButtonActionPerformed

    private void privateChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateChatButtonActionPerformed
        //new PrivateChatsetup().setVisible(true);
        /*
        String client = JOptionPane.showInputDialog("Who would you like to invite:");
        if(Core.checkUser(client) && Core.userOnline(client)){
            try {
                String query = "INSERT INTO `privatechat` (`host`, `client`) VALUES ('" + userName + "','" + client + "');";
                Main.st.executeUpdate(query);  
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error in Connectivity: " + e);
            }
        new PrivateChat().setVisible(true);
        MainFrame.this.dispose(); 
        }else{
           JOptionPane.showMessageDialog(this, "That user doesn\'t exist and/or isn\'t online"); 
        }
        */
        JOptionPane.showMessageDialog(this, "Functionality coming soon!"); 
    }//GEN-LAST:event_privateChatButtonActionPerformed

    private void steveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_steveButtonActionPerformed
        JOptionPane.showMessageDialog(this, "Functionality coming soon!"); 
        /*SteveBot charlie = new SteveBot("127.0.0.1");
        charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        charlie.start(); */
    }//GEN-LAST:event_steveButtonActionPerformed

    private void daleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daleButtonActionPerformed
        DaleBot dale = new DaleBot("127.0.0.1");
        dale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dale.start();
    }//GEN-LAST:event_daleButtonActionPerformed

    private void accountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountButtonActionPerformed
        Account account = new Account(userName);
    }//GEN-LAST:event_accountButtonActionPerformed

    private void docsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docsButtonActionPerformed
        Docs docs = new Docs();
    }//GEN-LAST:event_docsButtonActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accountButton;
    private javax.swing.JTextPane chatAreaTextPane;
    private javax.swing.JButton daleButton;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JButton docsButton;
    private javax.swing.JEditorPane eventLogTextPane;
    private javax.swing.JTextArea inputTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton privateChatButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JButton steveButton;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextPane usersOnlineTextPane;
    // End of variables declaration//GEN-END:variables
}
