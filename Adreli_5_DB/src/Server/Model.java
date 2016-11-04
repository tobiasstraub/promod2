import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Model ist für die I/O-Vorgänge der CSV Datei zuständig
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Model extends Thread {
	/**
	 * Deklariert den Socket, über welchen eine eingehende Verbindung läuft
	 */
	Socket socket = null;

	/**
	 * Eingabestrom von Client
	 */
	ObjectInputStream readIn;

	/**
	 * Ausgabestrom vom Server zum Client
	 */
	ObjectOutputStream streamOut;

	/**
	 * Legt den Namen der CSV Datei fest
	 */
	final String STORE_FILE;

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
	 * Deklariert den FileWriter, welcher für die Beschreibung in die CSV-Datei
	 * zuständig ist
	 */
	FileWriter writer;

	/**
	 * Deklariert ein Personen-Objekt
	 */
	Person p;

	/**
	 * Deklariert den FileWriter, welcher für die Beschreibung in die Log-Datei
	 * zuständig ist
	 */
	FileWriter logWriter;

	/**
	 * Der Konstruktor sorgt für die korrekte Initialisierung der lokalen
	 * Attribute mit den übergebenen Parametern
	 * 
	 * @param readIn
	 *            Damit können Objekte, welche vom Client gesendet wurden,
	 *            gelesen werden.
	 * @param streamOut
	 *            Damit können Objekte zum Client gesendet werden.
	 * @param STORE_FILE
	 *            Enthält den Namen der CSV-Datei
	 * @param logWriter
	 *            Enthält den FileWriter der Log-Datei
	 */
	Model(ObjectInputStream readIn, ObjectOutputStream streamOut,
			String STORE_FILE, FileWriter logWriter) {
		this.readIn = readIn;
		this.streamOut = streamOut;
		this.STORE_FILE = STORE_FILE;
		this.logWriter = logWriter;
	}

	/**
	 * Die run()-Methode wird, beim Starten des im Controller erzeugten Model
	 * Objekt als Thread, ausgeführt. In einer Endlosschleife wird auf ein int
	 * Objekt im Socket InputStream gewartet. Sobald der Client eine Zahl
	 * zwischen 1 und 3 sendet werden die entsprechenden Codezeilen ausgeführt.
	 * In Case 1 wird eine entsprechend Log geloggt und anschließend eine neue
	 * CSV Datei angelegt bzw. wenn Datenbank gewählt wurde wird keine angelegt.
	 * Danach werden auf Objekte gewartet und ankommende entsprechend in der CSV
	 * persistiert bzw. bei Auswahl der Datenbank in dieser persistiert. Dieser
	 * Vorgang wird solange ausgeführt bis der Client ein null-Objekt schickt,
	 * dadurch wird der Vorgang beendet. Mit flush werden die von Java
	 * zurückgehaltenen Objekte sofort perisistiert. In Case 2 wird eine
	 * entsprechende Log geloggt und anschließend readCSV() bzw. bei Auswahl der
	 * Datenbank readDatabase() ausgeführt. In Case 3 wird die CSV Datei
	 * versucht zu löschen bzw. bei Auswahl der Datenbank versucht diese zu
	 * leeren. Im Erfolgsfall wird ein Log geloggt. Im Fehlerfall wird ebenfalls
	 * geloggt.
	 */
	@Override
	public void run() {
		while (true)
			try {
				switch (readIn.readInt()) {
				case 1:
					if (STORE_FILE != "0xselectDatabase") {
						Controller.writeToLog("[" + this.getName()
								+ "] Neue CSV Datei wird angelegt", logWriter);
						makeNewCSV();
						do {
							p = (Person) readIn.readObject();
							if (p != null)
								persistObjectInCSV(p);
						} while (p != null);
						writer.flush();
						writer.close();
					} else {
						do {
							p = (Person) readIn.readObject();
							if (p != null)
								persistObjectInDatabase(DB_URL, DB_USER,
										DB_PASSWORD, p);
						} while (p != null);
					}
					break;
				case 2:
					if (STORE_FILE != "0xselectDatabase") {
						Controller.writeToLog("[" + this.getName()
								+ "] Lese CSV Datei " + STORE_FILE, logWriter);
						readCSV();
					} else {
						Controller.writeToLog("[" + this.getName()
								+ "] Lese Datenbank " + DB_URL, logWriter);
						readDatabase(DB_URL, DB_USER, DB_PASSWORD);
					}
					break;
				case 3:
					if (STORE_FILE != "0xselectDatabase") {
						try {
							new File(STORE_FILE).delete();
							Controller.writeToLog("[" + this.getName()
									+ "] CSV Datei " + STORE_FILE
									+ " erfolgreich gelöscht", logWriter);
						} catch (NullPointerException e) {
							Controller.writeToLog("[" + this.getName()
									+ "] CSV Datei " + STORE_FILE
									+ " kann nicht gelöscht werden", logWriter);
						}
					} else {
						Connection con = DriverManager.getConnection(DB_URL,
								DB_USER, DB_PASSWORD);
						con.createStatement().executeUpdate(
								"TRUNCATE TABLE adreli5_gr8");
					}
					break;
				}
			} catch (IOException | ClassNotFoundException | SQLException e) {
			}
	}

	/**
	 * Die Methode erzeugt eine neue CSV Datei und legt die Kopfdaten fest. Im
	 * Erfolgsfall wird eine entsprechende Log geloggt. Im Fehlerfall ebenfalls.
	 * 
	 * @throws IOException
	 * 
	 */
	private void makeNewCSV() throws IOException {
		try {
			writer = new FileWriter(STORE_FILE);
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
			Controller.writeToLog("[" + this.getName() + "] Neue CSV Datei "
					+ STORE_FILE
					+ " erfolgreich angelegt und mit Kopfdaten beschrieben",
					logWriter);
		} catch (IOException e) {
			Controller
					.writeToLog(
							"["
									+ this.getName()
									+ "] Neue CSV Datei "
									+ STORE_FILE
									+ " kann nicht angelegt und mit Kopfdaten beschrieben werden",
							logWriter);
		}
	}

	/**
	 * Die Methode persistiert ein übergebenes Personen-Objekt in der CSV-Datei.
	 * Im Erfolgsfall wird eine entsprechend Meldung geloggt. Im Fehlerfall
	 * ebenfalls.
	 * 
	 * @param p
	 *            Personen-Objekt
	 * @throws IOException
	 */
	private void persistObjectInCSV(Person p) throws IOException {
		try {
			writer.append(p.getFirstname());
			writer.append(',');
			writer.append(p.getLastname());
			writer.append(',');
			writer.append(p.getSalutation());
			writer.append(',');
			writer.append(p.getStreet());
			writer.append(',');
			writer.append(p.getPlace());
			writer.append(',');
			writer.append(p.getPhone());
			writer.append(',');
			writer.append(p.getFax());
			writer.append(',');
			writer.append(p.getRemark());
			writer.append(',');
			writer.append(p.getZip());
			writer.append('\n');
			Controller.writeToLog(
					"[" + this.getName() + "] Personen-Objekt '"
							+ p.getFirstname() + " " + p.getLastname() + " "
							+ p.getSalutation() + " " + p.getStreet() + " "
							+ p.getZip() + " " + p.getPlace() + " "
							+ p.getPhone() + " " + p.getFax() + " "
							+ p.getRemark() + "' erfolgreich in der Datei "
							+ STORE_FILE + " persistiert", logWriter);
		} catch (IOException e) {
			Controller.writeToLog("[" + this.getName() + "] Personen-Objekt '"
					+ p.getFirstname() + " " + p.getLastname()
					+ "' kann nicht in der Datei " + STORE_FILE
					+ " persistiert werden", logWriter);
		}
	}

	/**
	 * Liest die CSV-Datei aus und erzeugt pro Satz jeweils in Personen-Objekt
	 * mit diesen ausgelesenen Daten. Das Objekt wird anschließend an den
	 * Cliener jetzt auf keine weiteren Objekte mehr warten muss.t geschickt. Am
	 * Ende wird ein null-Objekt gesendet, damit ist für den Client klar, dass
	 * der Vorgang nun beendet ist. Mit flush werden durch Java entsprechend
	 * zurückgehaltene Objekte sofort gesendet.
	 * 
	 * @throws IOException
	 */
	private void readCSV() throws IOException {
		try {
			String row;
			BufferedReader reader = new BufferedReader(new FileReader(
					"adreli.csv"));
			row = reader.readLine();
			while ((row = reader.readLine()) != null) {
				String[] data = row.split(",");

				Person p = new Person();
				p.setFirstname(data[0].trim());
				p.setLastname(data[1].trim());
				p.setSalutation(data[2].trim());
				p.setStreet(data[3].trim());
				p.setPlace(data[4].trim());
				p.setPhone(data[5].trim());
				p.setFax(data[6].trim());
				p.setRemark(data[7].trim());
				p.setZip(data[8].trim());
				streamOut.writeObject(p);
				streamOut.flush();
				Controller.writeToLog("[" + this.getName()
						+ "] Sende Personen-Objekt '" + data[0].trim() + " "
						+ data[1].trim() + "' an Client " + this.getName(),
						logWriter);
			}
			reader.close();
			streamOut.writeObject(null);
			streamOut.flush();
			Controller.writeToLog(
					"[" + this.getName()
							+ "] Alle Personen-Objekte erfolgreich an Client "
							+ this.getName() + " gesendet", logWriter);

		} catch (IOException | AdrException e) {
			Controller.writeToLog(
					"[" + this.getName()
							+ "] Konnte nicht alle Personen-Objekte an Client "
							+ this.getName() + " senden", logWriter);
		}
	}

	/**
	 * Kontaktiert die Datenbank, loggt sich mit den entsprechenden Daten ein,
	 * liest anschließend immer ein Datensatz aus der Datenbank, bis keine
	 * weiteren mehr verfügbar sind. Die einzelnen Datensätze werden zu einem
	 * Personen-Objekt zusammengesetzt und anschließend jeweils an den
	 * anfragenden Client versendet.
	 * 
	 * @param url
	 *            Die URL zur Datenbank
	 * @param user
	 *            Der Benutzername für die Datenbank
	 * @param password
	 *            Das Passwort für die Datenbank
	 */

	public void readDatabase(String url, String user, String password) {
		try {
			Connection con = DriverManager.getConnection(url, user, password);
			ResultSet result = con.createStatement().executeQuery(
					"SELECT * FROM adreli5_gr8");

			while (result.next()) {
				Person p = new Person();
				p.setFirstname(result.getString(2));
				p.setLastname(result.getString(3));
				p.setSalutation(result.getString(4));
				p.setStreet(result.getString(5));
				p.setZip(result.getString(6));
				p.setPlace(result.getString(7));
				p.setPhone(result.getString(8));
				p.setFax(result.getString(9));
				p.setRemark(result.getString(10));
				streamOut.writeObject(p);
				streamOut.flush();
				Controller.writeToLog(
						"[" + this.getName() + "] Sende Personen-Objekt '"
								+ result.getString(2) + " "
								+ result.getString(3) + "' an Client "
								+ this.getName(), logWriter);
			}
			con.close();
			streamOut.writeObject(null);
			streamOut.flush();
			Controller.writeToLog(
					"[" + this.getName()
							+ "] Alle Personen-Objekte erfolgreich an Client "
							+ this.getName() + " gesendet", logWriter);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (AdrException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Kontaktiert die Datenbank, loggt sich mit den übergebenen Daten ein und
	 * persistiert das übergebene Objekt in der Datenbank.
	 * 
	 * @param url
	 *            Die URL zur Datenbank
	 * @param user
	 *            Der Benutzername für die Datenbank
	 * @param password
	 *            Das Passwort für die Datenbank
	 * @param p
	 *            Das in der Datenbank zu persistierende Personen-Objekt
	 */
	public void persistObjectInDatabase(String url, String user,
			String password, Person p) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, user, password);
			con.createStatement()
					.execute(
							"INSERT INTO adreli5_gr8(vorname,nachname,anrede,strasse,postleitzahl,ort,telefon,fax,bemerkung) VALUES ('"
									+ p.getFirstname().trim()
									+ "', '"
									+ p.getLastname().trim()
									+ "', '"
									+ p.getSalutation().trim()
									+ "', '"
									+ p.getStreet().trim()
									+ "', '"
									+ p.getZip().trim()
									+ "', '"
									+ p.getPlace().trim()
									+ "', '"
									+ p.getPhone().trim()
									+ "', '"
									+ p.getFax().trim()
									+ "', '"
									+ p.getRemark().trim() + "')");
			con.close();
			Controller.writeToLog(
					"[" + this.getName() + "] Personen-Objekt '"
							+ p.getFirstname() + " " + p.getLastname() + " "
							+ p.getSalutation() + " " + p.getStreet() + " "
							+ p.getZip() + " " + p.getPlace() + " "
							+ p.getPhone() + " " + p.getFax() + " "
							+ p.getRemark() + "' erfolgreich in der Datenbank "
							+ DB_URL + " persistiert", logWriter);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
