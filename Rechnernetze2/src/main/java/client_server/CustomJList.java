package client_server;

import java.awt.Point;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class CustomJList<E> extends JList<E> {

    public CustomJList(DefaultListModel a){
        super(a);
    }

    @Override
    public int locationToIndex(Point location){
        //TODO Message Klasse einbinden
        int index= super.locationToIndex(location);
        if (index != -1 && !getCellBounds(index, index).contains(location)){
            return -1;
        }
        else{
            return index;
        }
        
    }
    
}