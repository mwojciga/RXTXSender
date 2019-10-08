package rxtxsender.pack;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import rxtxsender.gui.MainGUI;

public class OperationProcessor implements SerialPortEventListener {

	/* OTHER */
	MainGUI mainGUI;
	private Enumeration availablePorts = null;
	private HashMap portMap = new HashMap();
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort openedSerialPort = null;
	private boolean connectedToPort = false;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	final static int TIMEOUT = 2000;
	final static int DATA_RATE = 19200;
	final static int NEWLINE_ASCII = 10;
	final static int DASH_ASCII = 45;
	final static int SPACE_ASCII = 32;
	public static Logger logger = Logger.getLogger(OperationProcessor.class);
	private String sendedMessage = "initialMessage";

	byte[] buffer = new byte[1024];
	int bytes;
	String end = "\n";
	StringBuilder curMsg = new StringBuilder();
	public String inputMessage = "";

	public OperationProcessor(MainGUI mainGUI) {
		this.mainGUI = mainGUI;
	}

	public void searchForPorts() {
		mainGUI.availablePorts.removeAllItems();
		availablePorts = CommPortIdentifier.getPortIdentifiers();
		while (availablePorts.hasMoreElements()) {
			CommPortIdentifier currentPort = (CommPortIdentifier) availablePorts.nextElement();
			mainGUI.txtarLogs.append("Found port: " + currentPort.getName() + "\n");
			logger.info("Found port: " + currentPort.getName());
			if (currentPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				mainGUI.availablePorts.addItem(currentPort.getName());
				portMap.put(currentPort.getName(), currentPort);
				mainGUI.txtarLogs.append(currentPort.getName() + " is a serial port. Added.\n");
				logger.info(currentPort.getName() + " is a serial port. Added.");
			}

		}
	}

	public void connect() {
		mainGUI.txtarLogs.append("Connecting to " + mainGUI.availablePorts.getSelectedItem() + "...\n");
		logger.info("Connecting to " + mainGUI.availablePorts.getSelectedItem());
		String selectedPort = (String) mainGUI.availablePorts.getSelectedItem();
		selectedPortIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
		CommPort commPort = null;
		try {
			commPort = selectedPortIdentifier.open("Biorobot", TIMEOUT);
			openedSerialPort = (SerialPort) commPort;
			openedSerialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			connectedToPort = true;
			mainGUI.btnConnect.setEnabled(false);
			mainGUI.btnDisconnect.setEnabled(true);
			mainGUI.lblConnectionStatus.setText("Connected to " + commPort.getName());
			mainGUI.lblConnectionStatus.setForeground(Color.BLUE);
			mainGUI.txtarLogs.append("Successfully connected to " + commPort.getName() + "\n");
			logger.info("Successfully connected to " + commPort.getName());
		} catch (PortInUseException e) {
			mainGUI.txtarLogs.append("Could not connect: port is already in use.\n");
			logger.info("Could not connect: port is already in use.");
		} catch (Exception e) {
			logger.info("Could not connect: " + e.toString());
		}
	}

	public boolean initIOStream() {
		mainGUI.txtarLogs.append("Opening IOStream.\n");
		logger.info("Opening IOStream.");
		boolean ioStreamOpened = false;
		try {
			inputStream = openedSerialPort.getInputStream();
			outputStream = openedSerialPort.getOutputStream();
			ioStreamOpened = true;
			mainGUI.txtarLogs.append("IOStream successfully opened.\n");
			logger.info("IOStream successfully opened.");
		} catch (IOException e) {
			mainGUI.txtarLogs.append("Could not open IOStream. " + e.toString() + "\n");
			logger.info("Could not open IOStream." + e.toString());
		}
		return ioStreamOpened;
	}

	public void initListener() {
		try {
			mainGUI.txtarLogs.append("Initializing listener.\n");
			logger.info("Initializing listener.");
			openedSerialPort.addEventListener(this);
			openedSerialPort.notifyOnDataAvailable(true);
		} catch (TooManyListenersException e) {
			mainGUI.txtarLogs.append("Could not add event listener. " + e.toString() + "\n");
			logger.info("Could not add event listener. " + e.toString());
		}
	}

	public void disconnect() {
		if (connectedToPort == true) {
			openedSerialPort.removeEventListener();
			openedSerialPort.close();
			mainGUI.txtarLogs.append("Disconnected from " + openedSerialPort.getName() + "\n");
			logger.info("Disconnected from " + openedSerialPort.getName());
			try {
				inputStream.close();
				outputStream.close();
				connectedToPort = false;
				mainGUI.lblConnectionStatus.setText("Disconnected!");
				mainGUI.lblConnectionStatus.setForeground(Color.RED);
				mainGUI.btnConnect.setEnabled(true);
				mainGUI.btnDisconnect.setEnabled(false);
				mainGUI.txtarLogs.append("IOStream closed.\n");
				logger.info("IOStream closed.");
			} catch (IOException e) {
				mainGUI.txtarLogs.append("Could not close IOStream." + e.toString() + "\n");
				logger.info("Could not close IOStream." + e.toString());
			}
		} else {
			mainGUI.txtarLogs.append("Tried to disconnect, but no port is opened.\n");
			logger.info("Tried to disconnect, but no port is opened.");
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				inputMessage = "";
				bytes = inputStream.read(buffer);
				curMsg.append(new String(buffer, 0, bytes, Charset.forName("UTF-8")));
				int endIdx = curMsg.indexOf(end);
				if (endIdx != -1) {
					inputMessage = curMsg.substring(0, endIdx + end.length()).trim();
					curMsg.delete(0, endIdx + end.length());
					mainGUI.txtarLogs.append("Received: " + inputMessage + "\n");
					System.out.println("[R]: " + inputMessage);
				}
			} catch (IOException e) {
				mainGUI.txtarLogs.append("Error while receiveing data: " + e.toString() + "\n");
				logger.info("Error while receiving data: " + e.toString());
			}
		}

	}

	public void writeData(String toSend) {
		try {
			outputStream.flush();
			sendedMessage = toSend;
			System.out.println("[S]: " + toSend);
			outputStream.write(toSend.getBytes());
			outputStream.flush();
		} catch (Exception e) {
			logger.info("Could not write data: " + e.toString());
		}
	}

	/* GETTERS & SETTERS */

	public boolean isConnectedToPort() {
		return connectedToPort;
	}

	public void setConnectedToPort(boolean connectedToPort) {
		this.connectedToPort = connectedToPort;
	}

	public String getSendedMessage() {
		return sendedMessage;
	}

	public void setSendedMessage(String sendedMessage) {
		this.sendedMessage = sendedMessage;
	}

	public String getInputMessage() {
		return inputMessage;
	}

	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}


}