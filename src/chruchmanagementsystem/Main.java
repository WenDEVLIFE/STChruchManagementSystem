/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chruchmanagementsystem;

import UI.Login;
import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.UIManager;

/**
 *
 * @author Frouen Junior
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    try{
        UIManager.setLookAndFeel(new FlatIntelliJLaf());
                Login login = new Login();
        login.setVisible(true);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
    } catch(Exception e){
        e.printStackTrace();
    }
         
    }
    
}
