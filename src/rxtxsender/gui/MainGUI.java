package rxtxsender.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import rxtxsender.pack.OperationProcessor;

public class MainGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	public static MainGUI mainGUIfrm;

	public File loadedFile;
	public JComboBox availablePorts;

	public JPanel mainPane;

	public JTextArea txtarLogs;

	public JLabel lblConnectionStatus;
	public JButton btnConnect;
	public JButton btnDisconnect;

	JSpinner xDir;
	JSpinner yDir;
	JSpinner zDir;

	Properties confProperties;

	OperationProcessor operationProcessor = null;
	private JTextField textField;

	private void createObjects() {
		operationProcessor = new OperationProcessor(this);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainGUIfrm = new MainGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public MainGUI() {
		initialize();
		setVisible(true);
		createObjects();
		operationProcessor.searchForPorts();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// jakieœ logi?
			e.printStackTrace();
		}
		Image icon = Toolkit.getDefaultToolkit().getImage("./img/imim_logo.gif");
		setIconImage(icon);
		setBounds(100, 100, 751, 765);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		mainPane = new JPanel();
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPane);
		mainPane.setLayout(null);
		mainPane.setEnabled(false);

		/* MENU */

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 745, 21);
		mainPane.add(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmDisconnect = new JMenuItem("Disconnect");
		mnFile.add(mntmDisconnect);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(646, 693, 89, 23);
		btnClear.setToolTipText("Clear the messages and logs text area.");
		mainPane.add(btnClear);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 421, 727, 2);
		mainPane.add(separator);

		/* MAIN PANE END */

		/* BOTTOM TABBED PANE */

		JTabbedPane bottomTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		bottomTabbedPane.setBounds(8, 437, 729, 245);
		mainPane.add(bottomTabbedPane);

		JScrollPane scrollPane2 = new JScrollPane();
		bottomTabbedPane.addTab("Logs", null, scrollPane2, null);
		scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		txtarLogs = new JTextArea();
		scrollPane2.setViewportView(txtarLogs);
		txtarLogs.setEditable(false);
		DefaultCaret caretLogs = (DefaultCaret)txtarLogs.getCaret();
		caretLogs.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		/* BOTTOM TABBED PANE END */

		/* MAIN TABBED PANE */

		JTabbedPane mainTabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
		mainTabbedPane.setBounds(10, 32, 331, 380);
		mainPane.add(mainTabbedPane);

		/* MAIN TABBED PANE END */

		/* CONNECT PANE */

		JPanel connectPane = new JPanel();
		connectPane.setLayout(null);

		JLabel lblConnectTo = new JLabel("Connect to:");
		lblConnectTo.setBounds(10, 11, 71, 14);
		connectPane.add(lblConnectTo);

		availablePorts = new JComboBox();
		availablePorts.setBounds(91, 8, 149, 20);
		connectPane.add(availablePorts);

		btnConnect = new JButton("Connect");
		btnConnect.setBounds(151, 39, 89, 23);
		connectPane.add(btnConnect);

		lblConnectionStatus = new JLabel("Not connected!", JLabel.RIGHT);
		lblConnectionStatus.setForeground(Color.RED);
		lblConnectionStatus.setBounds(109, 141, 131, 20);
		connectPane.add(lblConnectionStatus);

		JTextPane txtpnConnectionInfo = new JTextPane();
		txtpnConnectionInfo.setEditable(false);
		txtpnConnectionInfo.setEnabled(false);
		txtpnConnectionInfo.setText("In order to connect, please choose a port and click \"Connect\" button.");
		txtpnConnectionInfo.setBounds(10, 36, 131, 48);
		connectPane.add(txtpnConnectionInfo);

		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.setBounds(151, 107, 89, 23);
		btnDisconnect.setEnabled(false);
		connectPane.add(btnDisconnect);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(151, 73, 89, 23);
		connectPane.add(btnRefresh);
		getContentPane().setLayout(null);

		/* CONTROL MANUALLY PANE END */

		mainTabbedPane.addTab("Connect", connectPane);
		
		textField = new JTextField();
		textField.setBounds(10, 694, 527, 20);
		mainPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(547, 693, 89, 23);
		mainPane.add(btnSend);
		
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnSendActionPerformed(event);
			}
		});

		mntmDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnDisconnectActionPerformed(event);
			}
		});
		
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnConnectActionPerformed(event);
			}
		});

		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnDisconnectActionPerformed(event);
			}
		});
		
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnRefreshActionPerformed(event);
			}
		});

		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int close = JOptionPane.showConfirmDialog(mainGUIfrm, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (close == 0) {
					dispose();
				}
			}
		});

		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(mainGUIfrm, "Biorobot v" + confProperties.getProperty("version") + "\nWritten by: " + confProperties.getProperty("author") + "\n\nCooperator: dr. Roman Major\nInstitute of Metallurgy and Materials Science\nPolish Academy of Sciences", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				btnClearActionPerformed(event);
			}
		});

		/* ACTIONS END */
	}

	private void btnSendActionPerformed(ActionEvent event) {
		operationProcessor.writeData(textField.getText());
	}
	
	private void btnDisconnectActionPerformed(ActionEvent event) {
		operationProcessor.disconnect();
	}
	
	private void btnRefreshActionPerformed(ActionEvent event) {
		operationProcessor.searchForPorts();
	}

	private void btnConnectActionPerformed(ActionEvent event) {
		operationProcessor.connect();
		if (operationProcessor.isConnectedToPort() == true)
		{
			if (operationProcessor.initIOStream() == true)
			{
				operationProcessor.initListener();
			}
		}
	}

	private void btnClearActionPerformed(ActionEvent event) {
		txtarLogs.setText("");
	}
}
