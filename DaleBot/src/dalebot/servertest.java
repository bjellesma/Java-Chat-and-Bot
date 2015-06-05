package dalebot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bill
 */
import javax.swing.JFrame;

public class servertest {
    public static void main(String[] args){
        DaleBot dale = new DaleBot();
        
        dale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dale.start();
    }
}
