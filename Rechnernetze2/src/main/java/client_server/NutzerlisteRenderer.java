package client_server;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


 

public class NutzerlisteRenderer extends JLabel implements ListCellRenderer<String> {
    

    
 
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String string, int index,
        boolean isSelected, boolean cellHasFocus) {
        
        
        setText(string);
         
        return this;
    }

   
}