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
    private JLabel poster, dateTime;
    private JTextArea text;

    public messageCellRenderer() {
        cell = new JPanel();
        cell.setLayout(new BorderLayout());

        // icon
        iconPanel = new JPanel(new BorderLayout());
        poster = new JLabel(); 
        dateTime = new JLabel();
        iconPanel.add(poster, BorderLayout.NORTH);
        iconPanel.add(dateTime, BorderLayout.NORTH);
        cell.add(iconPanel, BorderLayout.WEST);

        // text;
        text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        cell.add(text, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(final JList list,
            final Message message, final int index, final boolean isSelected,
            final boolean hasFocus) {

        poster.setText(message.getSender());
        text.setText(message.getMessage());
        dateTime.setText(message.getDateTime().toString());
        int width = list.getWidth();
        // this is just to lure the ta's internal sizing mechanism into action
        if (width > 0)
            text.setSize(width, Short.MAX_VALUE);
        return cell;

    }
}