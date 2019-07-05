package client_server;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

public class messageCellRenderer  implements ListCellRenderer<Message> {

    private JPanel cell;
    private JPanel iconPanel;
    private JLabel poster;
    private JTextArea text;

    public messageCellRenderer() {
        cell = new JPanel();
        cell.setLayout(new BorderLayout());

        // icon
        iconPanel = new JPanel(new BorderLayout());
        poster = new JLabel(); 
        iconPanel.add(poster, BorderLayout.NORTH);
        cell.add(iconPanel, BorderLayout.WEST);

        // text;
        text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        cell.add(text, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Message message, final int index, final boolean isSelected,
            final boolean hasFocus) {

        poster.setText(message.getSender());
        text.setText(message.getMessage());
        
        
        return cell;

    }
}