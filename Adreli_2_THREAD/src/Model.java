import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Die Klasse Model ist für die I/O-Prozesse auf der Datenbasis zuständig.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Model extends Thread {
	/**
	 * streamOut ist für den Stream von Objekten von der Model in die View
	 * zuständig
	 */
	ObjectOutputStream streamOut;

	/**
	 * streamIn ist für das Auslesen des Streams der von der View in die Model
	 * läuft, zuständig
	 */
	ObjectInputStream streamIn;

	/**
	 * STORE_FILE beinhaltet den Dateipfad der Datenbasis
	 */
	final String STORE_FILE;

	/**
	 * p ist ein neues Objekt der Klasse Person
	 */
	Person p;

	/**
	 * writer ist zuständig für den Datenstrom in die Datenbasis
	 */
	FileWriter writer;

	/**
	 * reader ist zuständig für den Datenstrom aus der Datenbasis
	 */
	FileReader reader;

	/**
	 * Initialisiert die lokalen Variablen mit den übergebenen Parametern
	 * 
	 * @param streamOut
	 *            Zuständig für das Versenden von Objekten von Model in View
	 * @param streamIn
	 *            Zuständig für das Auslesen von Objekten in der Pipe die von
	 *            View nach Model verläuft
	 * @param STORE_FILE
	 *            Enthält den Dateipfad zur Datenbasis
	 */
	Model(ObjectOutputStream streamOut, ObjectInputStream streamIn,
			String STORE_FILE) {
		this.streamOut = streamOut;
		this.streamIn = streamIn;
		this.STORE_FILE = STORE_FILE;
	}

	/**
	 * run wird direkt beim Starten des jeweiligen Threads ausgeführt. Hierbei
	 * wird auf einen Integer 1, 2 oder 3 gewartet. Dabei wird kein Polling
	 * (busy waiting) verursacht, da readInt den Thread blockiert, bis eine
	 * Eingabe in der Pipe verfügbar ist. Wird 1 aufgerufen, so wird eine neue
	 * CSV Datei (Datenbasis) angelegt und auf Personen Objekte, welche von der
	 * View per Pipe gesendet werden, gewartet. Sobald ein Objekt gesendet wird,
	 * wird die Methode persistObject mit dem Objekt als Parameter aufgerufen.
	 * Der Anwendungsfall (case) 1 wird beendet sobald ein null-Objekt gesendet
	 * wird. Es wird dann weder auf eine weitere Anforderung in Form eines
	 * Integers gewartet. Wird 2 aufgerufen, so wird die Methode readCSV
	 * aufgerufen. Wird 3 aufgerufen, so wird die Datenbasis gelöscht. Beim
	 * Beschreiben der Datenbasis wird intern durch Java gepuffert. Dieser
	 * Puffer wird mit flush entsprechend geleert und der Inhalt sofort in die
	 * Datei geschrieben.
	 */
	public void run() {

		while (true)
			try {
				switch (streamIn.readInt()) {
				case 1:
					makeNewCSV();
					do {
						p = (Person) streamIn.readObject();
						if (p != null)
							persistObject(p);
					} while (p != null);
					writer.flush();
					writer.close();
					break;
				case 2:
					readCSV();
					break;
				case 3:
					try {
						new File(STORE_FILE).delete();
					} catch (NullPointerException e) {
						System.out.println("Ein Fehler ist aufgetreten!");
					}
					break;
				default:

				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Ein Fehler ist aufgetreten!");
			}

	}

	/**
	 * makeNewCSV legt eine neue Datei (Datenbasis) mit dem Namen welcher in
	 * STORE_FILE hinterlegt ist an und schreibt in die Datei jeweils
	 * "Spalten"-Überschrifen. Somit ist die Datenbasis auch bei der manuellen
	 * Einsicht gut verständlich.
	 * 
	 * @return Bei erfolgreicher Anlegung und "Spalten"-Beschriftung der Datei
	 *         true.
	 */
	private boolean makeNewCSV() {
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
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * persistObject holt sich über die entsprechenden Getter-Methoden die Werte
	 * des übergebenen Objekts und schreibt die Werte entsprechend im CSV-Format
	 * in die Datenbasis
	 * 
	 * @param p
	 *            Das zu persistierende Personen-Objekt
	 * @return Bei erfolgreicher Beschreibung true.
	 */
	private boolean persistObject(Person p) {
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
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * readCSV öffnet die Datenbasis und liest Zeilenweise die Datensätze ein,
	 * dabei werden die einzelnen Werte, welche durch Kommata gesplittet sind,
	 * in einem Array zwischengespeichert. Weiterhin wird ein neues
	 * Personen-Objekt angelegt und die Inhalte des Arrays werden per
	 * Setter-Methoden im Objekt gespeichert. Das neue Objekt wird dabei per
	 * OutputStream an die View gesendet. Hierbei werden die von Java
	 * gepufferten Inhalte wieder mittels flush sofort geschrieben.
	 * 
	 * @return
	 * @throws IOException
	 * @throws AdrException
	 */
	private boolean readCSV() {
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
			}
			reader.close();
			streamOut.writeObject(null);
			streamOut.flush();
			return true;

		} catch (IOException | AdrException e) {
			return false;
		}
	}
}
