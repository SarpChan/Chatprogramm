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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class UI extends JFrame {

	static GraphicsConfiguration gc;
	private JPanel chatEingabePanel, registrierPanel, user, pass, buttons, nutzerListPanel, chatFensterPanel;
	private JTextArea textArea;
	private JScrollPane areaScrollPane, nutzerAnzeige, chatFenster;
	private JTextField username;
	private JPasswordField password;
	private JButton absenden, registrieren, anmelden, abmelden;
	private Client client;
	private CustomJList<String> nutzerliste, nachrichten;
	private DefaultListModel<String> nutzerModel, chatModel;
	private Dimension centerDim;
	private final JOptionPane optionPane;
	private final JDialog dialog;


	public UI(Client cl) {
		super(gc);
		this.client = cl;

		setLayout(new BorderLayout());

		this.centerDim = new Dimension(400, 450);

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

		this.registrierPanel.add(Box.createVerticalGlue());
		this.registrierPanel.add(this.user);
		this.registrierPanel.add(this.pass);
		this.registrierPanel.add(this.buttons);
		this.registrierPanel.add(Box.createVerticalGlue());


		this.optionPane = new JOptionPane(
				"Sie haben eine neue Chatanfrage",
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION);

		this.dialog = new JDialog(this, 
				"Click a button",
				true);
		this.dialog.setContentPane(optionPane);


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

				client.login(username.getText(), password.getText(), "1");

				if(client.isLoggedIn()) {
					switchView(Views.HOME);
					//updateNutzerListe();
				}
				return;
			}
		});

		this.absenden.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateNutzerListe();
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
				try {
					// Eingefügt, da die Kommuniation zwischen Server und Client zu lange dauert und client.loggedIn in der nächsten Abfrage sonst immer false ergibt
					Thread.sleep(50); 
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
//				TODO listener auf loggedIn
				if(client.isLoggedIn()) {
					switchView(Views.HOME);
				}
				return;
			}
		});


		this.client.chatanfrage.addListener((observable, oldValue, newValue) -> {
			System.out.println("Aber hier");
			if (newValue.booleanValue() == true) {
				System.out.println("Ich war hier");
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run(){
						dialog.setVisible(true);
					}
				});
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
	}




	public void switchView(Views whereTo){

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run(){
				if (whereTo == Views.LOGIN) {
					getContentPane().removeAll();

					repaint();
					validate();

					add(registrierPanel, BorderLayout.CENTER);
					repaint();
					validate();
				} else if (whereTo == Views.HOME) {
					getContentPane().removeAll();
					repaint();
					validate();

					add(nutzerListPanel, BorderLayout.CENTER);

					absenden.setEnabled(true);
					add(absenden, BorderLayout.SOUTH);

					validate();                        

					repaint();
				} else if (whereTo == Views.CHAT) {
					String targetClient = nutzerliste.getSelectedValue();
					//TODO targetClient an Client senden, Socket zwischen target und self erstellen
					//TODO Nachrichten laden, speichern, etc.

					getContentPane().removeAll();
					repaint();
					validate();

					add(chatFensterPanel, BorderLayout.CENTER);

					textArea.setEditable(true);

					add(chatEingabePanel, BorderLayout.SOUTH);

					validate();                        

					repaint();
				}
			}
		});
	}


	public void updateNutzerListe(){
		client.requestActiveUser();
		try {
			// Eingefügt, da die Kommuniation zwischen Server und Client zu lange dauert und clients sonst nicht aktualisiert werden
			Thread.sleep(60); 
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
//		TODO listener fuer ActiveUsers
		String text = client.getActiveUsers();
		if (text != null)
		{
			String [] liste = text.split(" ");

			nutzerModel.clear();

			for (String ele : liste) {
				nutzerModel.addElement(ele);
			}
		}
	}


	public void exitProcedure(){
		client.close();
		dispose();

		System.exit(0);
	}

}

