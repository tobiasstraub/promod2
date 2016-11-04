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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	 * Deklariert und initialisiert die URL für die Datenbank
	 */
	final String DB_URL = "jdbc:mysql://127.0.0.1/adreli";

	/**
	 * Deklariert und initialisiert den Benutzernamen für die Datenbank
	 */
	final String DB_USER = "root";

	/**
	 * Deklariert und initialisiert das Passwort für die Datenbank
	 */
	final String DB_PASSWORD = "";

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

		/**
		 * Legt den Dateinamen auf "0xselectDatabase" fest, damit ist es für den
		 * Server klar, dass Daten in und aus der Datenbank geschrieben bzw.
		 * gelesen werden. Der Name "0xselectDatabase" wurde gewählt, da es
		 * unwahrscheinlich ist, dass so ein CSV-File genannt wurde. Der Zustand
		 * "null" kann nicht verwendet werden, da null schon belegt wird, wenn
		 * bei der Auswahl der CSV-Datei der File Dialog abgebrochen wird. Somit
		 * muss man unterscheiden können, ob der File Dialog zur Auswahl der
		 * CSV-Datei nur abgebrochen wurde, oder ob als Datenbasis die Datenbank
		 * ausgewählt wurde.
		 */
		database.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				baseFileHelper.baseFile = "0xselectDatabase";
				baseFileHelper.basePath = "0xselectDatabase";
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
					if (baseFileHelper.baseFile != "0xselectDatabase") {
						info.add(new Label("Datenbasis: "
								+ baseFileHelper.baseFile));
					} else {
						info.add(new Label("Datenbasis: Datenbank"));
					}
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

		Button csvImport = new Button("CSV importieren");
		csvImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				final Dialog persistDialog = new Dialog(controllerFrame);
				Label label;
				persistDialog.setLayout(new FlowLayout());
				if (csvread(DB_URL, DB_USER, DB_PASSWORD)) {
					label = new Label(
							"adrelibase.txt erfolgreich in Datenbank importiert.");
				} else {
					label = new Label(
							"adrelibase.txt konnte nicht erfolgreich in Datenbank importiert werden");
				}
				persistDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						persistDialog.dispose();
					}
				});
				persistDialog.add(ok);
				persistDialog.setSize(300, 100);
				persistDialog.setVisible(true);
			}
		});
		Button csvExport = new Button("Datenbank expotieren");
		csvExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				final Dialog persistDialog = new Dialog(controllerFrame);
				Label label;
				persistDialog.setLayout(new FlowLayout());
				if (csvwrite(DB_URL, DB_USER, DB_PASSWORD)) {
					label = new Label(
							"Datenbank erfolgreich in adrelibase.txt expotiert.");
				} else {
					label = new Label(
							"Datenbank konnte nicht erfolgreich expotiert werden.");
				}
				persistDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						persistDialog.dispose();
					}
				});
				persistDialog.add(ok);
				persistDialog.setSize(300, 100);
				persistDialog.setVisible(true);
			}
		});

		controls.add(csvImport);
		controls.add(csvExport);

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

	/**
	 * Loggt sich in die Datenbank mit den entsprechenden Daten ein und
	 * speichert alle Datensätze der Datenbank als CSV in die Datei
	 * "adreilibase.txt".
	 * 
	 * @param url
	 *            Die URL zur Datenbank
	 * @param user
	 *            Der Benutzername für die Datenbank
	 * @param password
	 *            Das Passwort für die Datenbank
	 */
	public boolean csvwrite(String url, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			ResultSet result = con.createStatement().executeQuery(
					"SELECT * FROM adreli5_gr8");

			FileWriter writer = new FileWriter("adrelibase.txt");
			writer.append("Vorname");
			writer.append(',');
			writer.append("Nachname");
			writer.append(',');
			writer.append("Anrede");
			writer.append(',');
			writer.append("Straße");
			writer.append(',');
			writer.append("Ort");
			writer.append(',');
			writer.append("Telefon");
			writer.append(',');
			writer.append("Fax");
			writer.append(',');
			writer.append("Bemerkung");
			writer.append(',');
			writer.append("Postleitzahl");
			writer.append('\n');
			while (result.next()) {
				writer.append(result.getString(2));
				writer.append(',');
				writer.append(result.getString(3));
				writer.append(',');
				writer.append(result.getString(4));
				writer.append(',');
				writer.append(result.getString(5));
				writer.append(',');
				writer.append(result.getString(7));
				writer.append(',');
				writer.append(result.getString(8));
				writer.append(',');
				writer.append(result.getString(9));
				writer.append(',');
				writer.append(result.getString(10));
				writer.append(',');
				writer.append(result.getString(6));
				writer.append('\n');
			}
			writer.close();
			con.close();
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Liest alle Daten aus dem gewählten CSV-File und persistiert die
	 * Datensätze in der Datenbank, die im Voraus mit entsprechenden Daten
	 * konntaktiert wird.
	 * 
	 * @param url
	 *            Die URL zur Datenbank
	 * @param user
	 *            Der Benutzername für die Datenbank
	 * @param password
	 *            Das Passwort für die Datenbank
	 */
	public boolean csvread(String url, String user, String password) {
		String row;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("adrelibase.txt"));
			row = reader.readLine();
			while ((row = reader.readLine()) != null) {
				String[] data = row.split(",");
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection(url, user,
						password);
				con.createStatement()
						.execute(
								"INSERT INTO adreli5_gr8(vorname,nachname,anrede,strasse,postleitzahl,ort,telefon,fax,bemerkung) VALUES ('"
										+ data[0].trim()
										+ "', '"
										+ data[1].trim()
										+ "', '"
										+ data[2].trim()
										+ "', '"
										+ data[3].trim()
										+ "', '"
										+ data[8].trim()
										+ "', '"
										+ data[4].trim()
										+ "', '"
										+ data[5].trim()
										+ "', '"
										+ data[6].trim()
										+ "', '"
										+ data[7].trim() + "')");
				con.close();
			}
			reader.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		} catch (SQLException e) {
			return false;
		}
	}
}
