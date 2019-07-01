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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Observable;


public class UI extends JFrame {

	static GraphicsConfiguration gc;
	private JPanel chatEingabePanel, registrierPanel, user, pass, buttons, nutzerListPanel, chatFensterPanel, topPanel;
	private JTextArea textArea;
	private JScrollPane areaScrollPane, nutzerAnzeige, chatFenster;
	private JTextField username;
	private JPasswordField password;
	private JButton absenden, registrieren, anmelden, abmelden, back;
	private Client client;
	private CustomJList<String> nutzerliste, nachrichten;
	private DefaultListModel<String> nutzerModel, chatModel;
	private Dimension centerDim;
	private JOptionPane optionPane;
	private ArrayList<JDialog> dialog = new ArrayList();
	private Views lastView, currentView;
	private String anfragender;
	private UI alles;


	public UI(Client cl) {
		super(gc);
		this.client = cl;
		
		setLayout(new BorderLayout());
		this.anfragender = "";
		this.centerDim = new Dimension(400, 450);
		this.currentView = Views.LOGIN;
		this.lastView = null;
		this.chatEingabePanel = new JPanel(new FlowLayout());
		this.textArea = new JTextArea(4, 20);
		this.areaScrollPane = new JScrollPane(this.textArea);
		this.registrierPanel = new JPanel();
		this.nutzerModel = new DefaultListModel<>();
		this.chatModel = new DefaultListModel<>();

		this.nutzerliste = new CustomJList<>(nutzerModel);
		this.nutzerAnzeige = new JScrollPane(this.nutzerliste);
		this.nutzerliste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.nachrichten = new CustomJList<>(chatModel);
		this.chatFenster = new JScrollPane(this.nachrichten);
		this.chatFensterPanel = new JPanel();

		this.nutzerliste.addMouseListener(new MouseAdapter(){
			@Override 
			public void mouseClicked(MouseEvent e){
				JList list = (JList) e.getSource();
				if(list.locationToIndex(e.getPoint()) == -1 ){
					list.clearSelection();
				} else {
					int index = list.locationToIndex(e.getPoint());
					Object name = list.getModel().getElementAt(index);
					client.requestUdpConnection(name.toString());
					switchView(Views.CHAT);
				}
			}
		});

		// this.nutzerliste.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

		//     @Override
		//     public void valueChanged(ListSelectionEvent e) {

		//         if( nutzerliste.getSelectedValue() != null){
		//             //switchView(Views.CHAT); 
		//         }

		//     }

		// });


		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				exitProcedure();
			}
		});


		//this.nutzerliste.setCellRenderer(new NutzerlisteRenderer());

		this.nutzerListPanel = new JPanel();

		this.nutzerListPanel.setPreferredSize(this.centerDim);
		this.nutzerListPanel.setMaximumSize(this.centerDim);
		this.nutzerAnzeige.setPreferredSize(this.centerDim);
		this.nutzerAnzeige.setMaximumSize(this.centerDim);

		this.chatFensterPanel.setPreferredSize(this.centerDim);
		this.chatFensterPanel.setMaximumSize(this.centerDim);
		this.chatFenster.setPreferredSize(this.centerDim);
		this.chatFenster.setMaximumSize(this.centerDim);

		this.chatFensterPanel.add(this.chatFenster);
		this.nutzerListPanel.add(this.nutzerAnzeige);

		this.user = new JPanel();
		this.pass = new JPanel();
		this.buttons = new JPanel();
		this.topPanel = new JPanel();
		this.username = new JTextField();
		this.password = new JPasswordField();
		this.absenden = new JButton("absenden");
		this.registrieren = new JButton("registrieren");
		this.anmelden = new JButton("anmelden");
		this.abmelden = new JButton("abmelden");
		this.back = new JButton("back");

		this.setTitle("Chatsystem");	
		this.setVisible(true);
		this.setSize(400, 600);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.username.grabFocus();

		// LoginPanel
		this.nutzerliste.setBackground(Color.GRAY);
		this.nachrichten.setBackground(Color.GRAY);

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
		this.anmelden.setEnabled(false);
		this.registrieren.setEnabled(false);

		this.topPanel.setLayout(new BoxLayout(this.topPanel, BoxLayout.X_AXIS));
		this.topPanel.add(this.back);
		this.topPanel.add(Box.createHorizontalGlue());
		this.topPanel.add(this.abmelden);
		this.back.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.abmelden.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		
		

		this.registrierPanel.add(Box.createVerticalGlue());
		this.registrierPanel.add(this.user);
		this.registrierPanel.add(this.pass);
		this.registrierPanel.add(this.buttons);
		this.registrierPanel.add(Box.createVerticalGlue());


		
		
		

		this.alles = alles;
		


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

				client.login(username.getText(), password.getText(), "1");

				if(client.isLoggedIn()) {
					switchView(Views.HOME);
					
				}
				return;
			}
		});

		this.absenden.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
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
				client.login(username.getText(), password.getText(), "0");
				
				
				return;
			}
		});
		this.back.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e){
				
				if(lastView != Views.LOGIN){
					switchView(lastView);
				}

			}
		});

		this.client.getLoggedIn().setListener(new BooVariable.ChangeListener(){
		
			@Override
			public void onChange() {
				if(client.isLoggedIn()){
					switchView(Views.HOME);
				}
			}
		});


        this.client.getChatanfrage().setListener(new BooVariable.ChangeListener(){

			
        
            @Override
            public synchronized void onChange() {
                if(client.hasChatanfrage()){
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run(){
							
							
							
							JOptionPane optionPane = new JOptionPane();
							
							dialog.add( new JDialog(alles, 
							"Click a button",
							true));
							int temp = dialog.size()-1;
							

							optionPane.setBounds(getLocationOnScreen().x, getLocationOnScreen().y + 200, 400, 150);
							
							int auswahl = optionPane.showOptionDialog(alles, "Sie haben eine neue Chatanfrage von " + client.getChatanfrage().getVon(), 
							"Chatanfrage",optionPane.YES_NO_OPTION , optionPane.QUESTION_MESSAGE, null, null, 1);
							if (auswahl == 0){
								client.answerUdpConnection(true);
							} else {
								client.answerUdpConnection(false);
							}

							
							
							
                        }
                    });
                }
                
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

				this.chatEingabePanel.add(this.areaScrollPane);
				this.chatEingabePanel.add(this.absenden);

				//frame.add(southPanel, BorderLayout.SOUTH);

				this.add(this.registrierPanel, BorderLayout.CENTER);


				this.client.getActiveUsers().setListener(new ObservableListe.ChangeListener(){
		
					@Override
					public void onChange() {

						nutzerModel.clear();
						for (String ele : client.getActiveUsers().getListe()) {
							nutzerModel.addElement(ele);
						}
						
					}
				});
		
	}




	public void switchView(Views whereTo){

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){
				if (whereTo == Views.LOGIN) {
					lastView = Views.LOGIN;
					currentView = Views.LOGIN;
					getContentPane().removeAll();

					repaint();
					validate();

					add(registrierPanel, BorderLayout.CENTER);
					repaint();
					validate();
				} else if (whereTo == Views.HOME) {
					lastView = currentView;
					currentView = Views.HOME;
					
					getContentPane().removeAll();
					repaint();
					validate();

					add(nutzerListPanel, BorderLayout.CENTER);
					add(topPanel, BorderLayout.NORTH);
					//client.requestActiveUser();

					

					validate();                        

					repaint();
				} else if (whereTo == Views.CHAT) {
					String targetClient = nutzerliste.getSelectedValue();
					//TODO targetClient an Client senden, Socket zwischen target und self erstellen
					//TODO Nachrichten laden, speichern, etc.

					lastView = currentView;
					currentView = Views.CHAT;

					getContentPane().removeAll();
					repaint();
					validate();

					add(chatFensterPanel, BorderLayout.CENTER);

					textArea.setEditable(true);
					absenden.setEnabled(true);
					
					add(topPanel, BorderLayout.NORTH);
					add(chatEingabePanel, BorderLayout.SOUTH);
					

					validate();                        

					repaint();
				}
			}
		});
	}


	


	public void exitProcedure(){
		client.close();
		dispose();

		System.exit(0);
	}

}

