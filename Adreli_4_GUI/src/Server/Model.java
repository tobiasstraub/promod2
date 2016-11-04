import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
	 * CSV Datei angelegt. Danach werden auf Objekte gewartet und ankommende
	 * entsprechend persistiert. Dieser Vorgang wird solange ausgeführt bis der
	 * Client ein null-Objekt schickt, dadurch wird der Vorgang beendet. Mit
	 * flush werden die von Java zurückgehaltenen Objekte sofort perisistiert.
	 * In Case 2 wird eine entsprechende Log geloggt und anschließend readCSV()
	 * ausgeführt. In Case 3 wird die CSV Datei versucht zu löschen und im
	 * Erfolgsfall ein Log geloggt. Im Fehlerfall wird ebenfalls geloggt.
	 */
	@Override
	public void run() {

		while (true)
			try {
				switch (readIn.readInt()) {
				case 1:
					Controller.writeToLog("[" + this.getName()
							+ "] Neue CSV Datei wird angelegt", logWriter);
					makeNewCSV();
					do {
						p = (Person) readIn.readObject();
						if (p != null)
							persistObject(p);
					} while (p != null);
					writer.flush();
					writer.close();
					break;
				case 2:
					Controller.writeToLog("[" + this.getName()
							+ "] Lese CSV Datei " + STORE_FILE, logWriter);
					readCSV();
					break;
				case 3:
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
					break;
				}
			} catch (IOException | ClassNotFoundException e) {
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
	private void persistObject(Person p) throws IOException {
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
}
