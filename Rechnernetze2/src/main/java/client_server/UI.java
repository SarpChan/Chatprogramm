package client_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;





public class UI extends JFrame{

    static GraphicsConfiguration gc;
    private JPanel southPanel, registrierPanel,  user, pass, buttons;
    private JTextArea textArea;
    private JScrollPane areaScrollPane;
    private JTextField username;
    private JPasswordField password;
    private JButton absenden, registrieren, anmelden, abmelden;
    private Client client;
    private JList center;

    



    public UI(Client cl){
        super(gc);
        this.client = cl;
        this.southPanel = new JPanel(new FlowLayout());
        this.textArea = new JTextArea(4, 20);
        this.areaScrollPane = new JScrollPane(this.textArea);
        this.registrierPanel = new JPanel();
        String [] hallo = {"hallo", "wie bitte"};
        this.center = new JList<>(hallo);
        this.user = new JPanel();
        this.pass = new JPanel();
        this.buttons = new JPanel();
        this.username = new JTextField();
        this.password = new JPasswordField();
        this.absenden = new JButton("absenden");
        this.registrieren = new JButton("registrieren");
        this.anmelden = new JButton("anmelden");
        this.abmelden = new JButton("abmelden");
        

        this.setTitle("Chatsystem");	
        this.setVisible(true);
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);



        // LoginPanel
        center.setBackground(Color.GRAY);

        

        username.setSize(100, 20);
        password.setSize(100, 20);
        username.setMaximumSize(new Dimension(100,20));
        password.setMaximumSize(new Dimension(100,20));

        user.setLayout(new BoxLayout(this.user, BoxLayout.X_AXIS));
        user.add(new JLabel("Username: "));
        user.add(this.username);
        user.setAlignmentX(Component.CENTER_ALIGNMENT);
        user.setAlignmentY(Component.CENTER_ALIGNMENT);
        pass.setLayout(new BoxLayout(this.pass, BoxLayout.X_AXIS));
        pass.add(new JLabel("Password: "));
        pass.add(this.password);
        pass.setAlignmentX(Component.CENTER_ALIGNMENT);
        pass.setAlignmentY(Component.CENTER_ALIGNMENT);

        registrierPanel.setLayout(new BoxLayout(this.registrierPanel, BoxLayout.Y_AXIS));
        registrierPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrierPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        
       
        buttons.setLayout(new BoxLayout(this.buttons, BoxLayout.X_AXIS));
        buttons.add(this.anmelden);
        buttons.add(this.registrieren);
        absenden.setEnabled(false);
        anmelden.setEnabled(false);
        registrieren.setEnabled(false);
        
        registrierPanel.add(Box.createVerticalGlue());
        registrierPanel.add(this.user);
        registrierPanel.add(this.pass);
        registrierPanel.add(this.buttons);
        registrierPanel.add(Box.createVerticalGlue());

        


        

        username.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (username.getText().isEmpty() || password.getText().isEmpty()) {
                    if (anmelden.isEnabled()) {
                        anmelden.setEnabled(false);
                        registrieren.setEnabled(false);

                    }
                } else {
                    anmelden.setEnabled(true);
                    registrieren.setEnabled(true);
                }
            }

        });

        password.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (username.getText().isEmpty() || password.getText().isEmpty()) {
                    if (anmelden.isEnabled()) {
                        anmelden.setEnabled(false);
                        registrieren.setEnabled(false);

                    }
                } else {
                    anmelden.setEnabled(true);
                    registrieren.setEnabled(true);
                }
            }

        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                if (textArea.getText().isEmpty()) {
                    if (absenden.isEnabled()) {
                        absenden.setEnabled(false);
                        

                    }
                } else {
                    absenden.setEnabled(true);
                    
                }
            }

        });

        anmelden.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                client.login(username.getText(), password.getText());

                switchView(Views.HOME);
                
                return;
            }
            
        });

        registrieren.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
            
        });
        
        // SouthPanel

        textArea.setEditable(false);

        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250,80));
        
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setAutoscrolls(true);
        textArea.setMaximumSize(new Dimension(100, 100));
        textArea.setMinimumSize(new Dimension(100,100));
        
       

        
        
        southPanel.add(this.areaScrollPane);
        southPanel.add(this.absenden);
        
        //frame.add(southPanel, BorderLayout.SOUTH);


        this.add(this.registrierPanel, BorderLayout.CENTER);
        
    }
    
    
    public void switchView(Views whereTo){
        
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run(){
                    if(whereTo == Views.LOGIN){
                        getContentPane().removeAll();
                        
                        repaint();
                        validate();
            
                        add(registrierPanel, BorderLayout.CENTER);
                        repaint();
                        validate();
            
                    } else if ( whereTo == Views.HOME){
            
                        

                        getContentPane().removeAll();
                        repaint();
                        validate();

                        add(abmelden, BorderLayout.CENTER);
                        add(center, BorderLayout.CENTER);
                        textArea.setEditable(true);
                        
                        add(southPanel, BorderLayout.SOUTH);
                        
                        validate();                        
            
                        repaint();
            
                    } else if( whereTo == Views.CHAT){
            
                    }
                }
            });
    
    }
    
}

