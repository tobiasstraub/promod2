import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class View extends Thread {
	/**
	 * Deklariert die grafische Benutzeroberfläche des Servers.
	 */
	Frame controllerFrame;
	/**
	 * Legt den Standard-Port fest, welcher für den Kommunikationsaufbau
	 * zwischen Server und Client dient, sofern der Benutzer in der grafischen
	 * Benutzeroverfläche des Servers keinen anderen Port wählt.
	 */
	final static String PORT = "56789";

	/**
	 * Deklariert die Datenbasis
	 */
	String defaultBaseFile;

	/**
	 * Der Konstruktor der Klasse View nimmt den übergebenen Standard-Dateinamen
	 * für die Datenbasis entgegen. Dieser wird verwendet, sofern der Benutzer
	 * keine andere Datenbasis wählt.
	 * 
	 * @param defaultBaseFile
	 *            Standard-Dateiname der Datenbasis
	 */
	View(String defaultBaseFile) {
		this.defaultBaseFile = defaultBaseFile;
	}

	/**
	 * Initialisiert die grafische Benutzeroberfläche mit einem Hauptfenster.
	 * Sollte das Fenster über das 'X'-Symbol versucht werden zu beenden, wird
	 * dem Nutzer eine Sicherheitsabfrage gestellt, ob das Programm wirklich
	 * beendet werden soll. Auf der grafischen Oberfläche wird der Port
	 * abgefragt, in Form eines Textfeldes. Dieses Textfeld ist dabei mit dem
	 * Standardport vorbelegt. Weiterhin kann zwischen der Datenbasis 'CSV' oder
	 * 'Datenbank' ausgewählt werden. Wird 'CSV' ausgewählt, so öffnet sich der
	 * File-Dialog, bei dem eine wahlfreie Datei für die Speicherung der Daten
	 * ausgewählt werden kann. Wird keine angegeben, so wird die
	 * Standard-CSV-Datenbasis verwendet. Zudem befinden sich zwei Buttons
	 * 'Serverstart' und 'Serverstop' innerhalb der GUI. Dise haben ihrem Namen
	 * entsprechende Funktion. Nach dem Start des Servers wird dem Benutzer
	 * angezeigt, in welche Datenbasis geschrieben wird (Name der Datenbasis)
	 * und wie viele Clients derzeit connected sind.
	 */
	@Override
	public void run() {
		controllerFrame = new Frame("Server");
		controllerFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				final Dialog yesNoDialog = new Dialog(controllerFrame);
				yesNoDialog.setLayout(new GridLayout(3, 1));
				Label label = new Label(
						"Wollen Sie das Programm wirklich beenden?");
				yesNoDialog.add(label);
				Button yes = new Button("Ja");
				yes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				Button no = new Button("Nein");
				no.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						yesNoDialog.dispose();
					}
				});
				yesNoDialog.add(yes);
				yesNoDialog.add(no);
				yesNoDialog.setSize(300, 100);
				yesNoDialog.setVisible(true);
			}
		});
		controllerFrame.setLayout(new GridLayout(2, 1));
		Panel data = new Panel();
		data.setLayout(new GridLayout(2, 1));
		data.add(new Label("Port: "));
		final TextField port = new TextField(5);
		port.setText(PORT);
		data.add(port);
		CheckboxGroup dataBase = new CheckboxGroup();
		Checkbox csv = new Checkbox("CSV", dataBase, false);
		data.add(csv);
		Checkbox database = new Checkbox("Datenbank", dataBase, false);
		data.add(database);

		/**
		 * BaseFileHelper wird verwendet um aus der inneren Klasse und aus der
		 * Klasse in der diese inneren Klasse eingebettet ist, auf Attribute
		 * zuzugreifen und diese zu verändern.
		 * 
		 */
		class BaseFileHelper {
			String baseFile = defaultBaseFile;
			String basePath = defaultBaseFile;
		}
		final BaseFileHelper baseFileHelper = new BaseFileHelper();

		csv.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				FileDialog dataFile = new FileDialog(controllerFrame,
						"Datei auswählen", FileDialog.LOAD);
				dataFile.setFile("*.csv");
				dataFile.setVisible(true);
				if (dataFile.getFile() != null) {
					baseFileHelper.baseFile = dataFile.getFile();
					baseFileHelper.basePath = dataFile.getDirectory()
							+ dataFile.getFile();
				}
			}
		});

		Panel controls = new Panel();
		data.setLayout(new GridLayout(3, 1));
		Button serverStart = new Button("Serverstart");
		final Button serverStop = new Button("Serverstop");
		serverStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				Controller controller = new Controller(Integer.parseInt(port
						.getText()), baseFileHelper.basePath);
				controller.start();

				controllerFrame.dispose();
				Panel info = new Panel();
				info.setLayout(new FlowLayout());
				if (baseFileHelper.baseFile == null) {
					info.add(new Label("Datenbasis: " + defaultBaseFile));
				} else {
					info.add(new Label("Datenbasis: " + baseFileHelper.baseFile));
				}
				controllerFrame.add(info);
				controllerFrame.setVisible(true);
			}
		});
		serverStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				System.exit(0);
			}
		});

		controls.add(serverStart);
		controls.add(serverStop);

		controllerFrame.add(data);
		controllerFrame.add(controls);
		controllerFrame.setSize(500, 100);
		controllerFrame.setVisible(true);
	}

	/**
	 * Ist für das Update der GUI zuständig, sobald sich die Anzahl der
	 * verbundenen Clients verändert.
	 * 
	 * @param c
	 *            Client Anzahl
	 */
	public void updateGUI(int c) {
		controllerFrame.dispose();
		Panel countClients = new Panel();
		countClients.setLayout(new FlowLayout());
		countClients.add(new Label("Connected Clients: " + c));
		controllerFrame.add(countClients);
		controllerFrame.setVisible(true);
	}
}
