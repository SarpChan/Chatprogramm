package client_server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
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
    private JPanel southPanel, registrierPanel,  user, pass, buttons, center;
    private JTextArea textArea;
    private JScrollPane areaScrollPane, nutzerAnzeige;
    private JTextField username;
    private JPasswordField password;
    private JButton absenden, registrieren, anmelden, abmelden;
    private Client client;
    private JList<String> nutzerliste;
    private DefaultListModel <String> model;
    private Dimension centerDim;
    

    



    public UI(Client cl){
        super(gc);
        this.client = cl;

        setLayout(new BorderLayout());

        this.centerDim = new Dimension(400,450);

        this.southPanel = new JPanel(new FlowLayout());
        this.textArea = new JTextArea(4, 20);
        this.areaScrollPane = new JScrollPane(this.textArea);
        this.registrierPanel = new JPanel();
        this.model = new DefaultListModel<>();
        this.model.addElement("hallo");
        this.model.addElement("wie bitte");
        this.nutzerliste = new JList<>(model);
        this.nutzerAnzeige = new JScrollPane(this.nutzerliste);
        this.model.addElement("update");
        
        
       


        
        //this.nutzerliste.setCellRenderer(new NutzerlisteRenderer());
        
        
        this.center = new JPanel();
        
       
        
        this.center.setPreferredSize(this.centerDim);
        this.center.setMaximumSize(this.centerDim);
        this.nutzerAnzeige.setPreferredSize(this.centerDim);
        this.nutzerAnzeige.setMaximumSize(this.centerDim);
        
        
        
        
        

        
        this.center.add(this.nutzerAnzeige);

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
        this.username.grabFocus();



        // LoginPanel
        this.nutzerliste.setBackground(Color.GRAY);

        

        this.username.setSize(100, 20);
        this.password.setSize(100, 20);
        this.username.setMaximumSize(new Dimension(100,20));
        this.password.setMaximumSize(new Dimension(100,20));

        this.user.setLayout(new BoxLayout(this.user, BoxLayout.X_AXIS));
        this.user.add(new JLabel("Username: "));
        this.user.add(this.username);
        this.user.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.user.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.pass.setLayout(new BoxLayout(this.pass, BoxLayout.X_AXIS));
        this.pass.add(new JLabel("Password: "));
        this.pass.add(this.password);
        this.pass.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.pass.setAlignmentY(Component.CENTER_ALIGNMENT);

        this.registrierPanel.setLayout(new BoxLayout(this.registrierPanel, BoxLayout.Y_AXIS));
        this.registrierPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.registrierPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        
       
        this.buttons.setLayout(new BoxLayout(this.buttons, BoxLayout.X_AXIS));
        this.buttons.add(this.anmelden);
        this.buttons.add(this.registrieren);
        this.absenden.setEnabled(false);
        this. anmelden.setEnabled(false);
        this.registrieren.setEnabled(false);
        
        this.registrierPanel.add(Box.createVerticalGlue());
        this.registrierPanel.add(this.user);
        this.registrierPanel.add(this.pass);
        this.registrierPanel.add(this.buttons);
        this.registrierPanel.add(Box.createVerticalGlue());

        
        //center.add(this.abmelden);
        

        

        this.username.getDocument().addDocumentListener(new DocumentListener() {

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

        this.password.getDocument().addDocumentListener(new DocumentListener() {

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

        this.textArea.getDocument().addDocumentListener(new DocumentListener() {

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

        this.anmelden.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if(client.login(username.getText(), password.getText(), "1")) {
                	switchView(Views.HOME);
                }

                return;
            }
            
        });

        this.absenden.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                model.addElement("add");
                return;
            }
            
        });

        this.abmelden.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                client.close();
                switchView(Views.LOGIN);

                return;
            }
            
        });

        this.registrieren.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	if(client.login(username.getText(), password.getText(), "0")) {
                	switchView(Views.HOME);
                }

                return;
            }
            
        });
        
        // SouthPanel

        this.textArea.setEditable(false);

        this.areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.areaScrollPane.setPreferredSize(new Dimension(250,80));
        
        this.textArea.setWrapStyleWord(true);
        this.textArea.setLineWrap(true);
        this.textArea.setAutoscrolls(true);
        this.textArea.setMaximumSize(new Dimension(100, 100));
        this.textArea.setMinimumSize(new Dimension(100,100));
        
       

        
        
        this.southPanel.add(this.areaScrollPane);
        this.southPanel.add(this.absenden);
        
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

