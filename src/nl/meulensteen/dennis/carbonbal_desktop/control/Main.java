/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_desktop.control;

import nl.meulensteen.dennis.carbonbal_desktop.view.MainScreen;
import java.io.IOException;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {

        SwingUtilities.invokeLater(() -> {
            MainScreen mainScreen = new MainScreen();
            mainScreen.setVisible(true);
        });
        
        
    }

}
