import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

public class View extends Thread {
	/**
	 * streamOut ist für den Stream von Objekten von der View in die Model
	 * zuständig
	 */
	ObjectOutputStream streamOut;

	/**
	 * streamIn ist für das Auslesen des Streams der von der Model in die View
	 * läuft, zuständig
	 */
	ObjectInputStream streamIn;

	/**
	 * Scanner liest den Eingabestream der Konsole
	 */
	Scanner scanner = new Scanner(System.in);

	/**
	 * bufferedObjects ist eine Arrayliste welche die angelegten Personen
	 * Objekte vor der Persistierung zwischenspeichern soll
	 */
	private ArrayList<Person> bufferedObjects = new ArrayList<>();

	/**
	 * interrupt stellt ein Flag da um das Programm sauber beenden zu können
	 */
	boolean interrupt = true;

	/**
	 * Initialisiert die lokalen Variablen mit den übergebenen Parametern
	 * 
	 * @param streamOut
	 *            Zuständig für das Versenden von Objekten von View in Model
	 * @param streamIn
	 *            Zuständig für das Auslesen von Objekten in der Pipe die von
	 *            Model nach View verläuft
	 */
	View(ObjectOutputStream streamOut, ObjectInputStream streamIn) {
		this.streamOut = streamOut;
		this.streamIn = streamIn;
	}

	public void run() {
		do {
			printMenu();
			switch (scanner.nextInt()) {
			case 1:
				newRecord();
				break;
			case 2:
				printAllRecords();
				break;
			case 3:
				if (persistBufferedObjects()) {
					System.out.println("Persistierung erfolgreich!\n");
				} else {
					System.out
							.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 4:
				if (addPersistedObjects()) {
					System.out
							.println("Alle persistierten Objekte wurden erfolgreich geladen!\n");
				} else {
					System.out
							.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 5:
				if (sortBufferedObjects()) {
					System.out
							.println("Alle gepufferten Objekte wurden erfolgreich sortiert!\n");
				} else {
					System.out
							.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 6:
				if (deletePersistedObjects()) {
					System.out.println("Persistierte Datei wurde geloescht!\n");
				} else {
					System.out
							.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 7:
				scanner.close();
				interrupt = false;
				break;
			}
		} while (interrupt);
	}

	/**
	 * Gibt das Benutzermenü auf der Konsole aus
	 */
	private void printMenu() {
		System.out.println("	ADRELI - Adressverwaltung\n");
		System.out.println("Wollen Sie...\n");
		System.out.println("		eine neue Person aufnehmen: > 1");
		System.out.println("			 Records auflisten: > 2");
		System.out.println("	     Records in eine Datei sichern: > 3");
		System.out.println("	     Records aus einer Datei laden: > 4");
		System.out.println("               in-memory Records sortieren: > 5");
		System.out.println("	               	    Datei loeschen: > 6");
		System.out.println("                    das Programm verlassen: > 7");
	}

	/**
	 * Erstellt ein neues Objekt der Klasse Person und speichert die vom
	 * Benutzer eingegebenen Werte entsprechend ab. Der Benutzer wird
	 * anschließend gefragt, ob die Angaben stimmen. Beantwortet der Benutzer
	 * diese Frage mit Nein, werden die Daten verworfen und der Benutzer kann
	 * die Daten erneut eingeben. Im Anschluss daran hat der Benutzer die
	 * Möglichkeit direkt ein weiteres neues Objekt anzulegen. Möchte der
	 * Benutzer keine weiteren Personen Objekte anlegen, wird das Hauptmenü
	 * ausgegeben.
	 */
	private void newRecord() {
		Person person = new Person();
		scanner.nextLine(); // Flush Buffer
		System.out.println("Geben Sie bitte die Daten ein:\n");
		boolean breakIt;
		do {
			try {
				System.out.print("Name: ");
				person.setLastname(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDer Nachname muss zwischen drei und 20 Zeichen haben "
								+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Vorname: ");
				person.setFirstname(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDer Vorname muss zwischen drei und 20 Zeichen haben "
								+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Anrede: ");
				person.setSalutation(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDie Anrede muss Herr oder Frau sein.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Strasse: ");
				person.setStreet(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDie Straße muss zwischen drei und 30 Zeichen haben "
								+ "und darf nur Buchstaben, Bindestriche, Leerzeichen und Zahlen enthalten.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("PLZ: ");
				person.setZip(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDie Postleitzahl muss aus genau 5 Ziffern bestehen und darf nicht mit Null beginnen.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Ort: ");
				person.setPlace(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDer Ort muss zwischen drei und 20 Zeichen haben "
								+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Telefon: ");
				person.setPhone(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDie Telefonnummer muss mit einer Laendervorwahl starten"
								+ "oder alternativ mit der Ziffer 0.\n"
								+ "Sie muss mit mindestens einer weiteren Ziffer oder Punkt oder Leerzeichen oder Slash bestehen.\n");
				breakIt = true;
			}
		} while (breakIt);
		do {
			try {
				System.out.print("Fax: ");
				person.setFax(scanner.nextLine());
				breakIt = false;
			} catch (AdrException e) {
				System.out
						.println("Ups, da ging was schief.\nDie Telefonnummer muss mit einer Laendervorwahl starten"
								+ "oder alternativ mit der Ziffer 0.\n"
								+ "Sie muss mit mindestens einer weiteren Ziffer oder Punkt oder Leerzeichen oder Slash bestehen.\n");
				breakIt = true;
			}
		} while (breakIt);
		System.out.print("Bemerkung: ");
		person.setRemark(scanner.nextLine());

		System.out.print("\n\nStimmts (J/N)? ");
		if ((scanner.next().toLowerCase()).equals("j")) {
			if (bufferObject(person)) {
				System.out
						.print("\n\nObjekt erfolgreich aufgenommen.\nMoechten Sie eine weitere Person aufnehmen (J/N)? ");
				if ((scanner.next().toLowerCase()).equals("j")) {
					System.out.println("");
					newRecord();
				}
			} else {
				System.out
						.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.");
			}
		} else {
			newRecord();
		}
	}

	/**
	 * Speichert das übergebene Objekt vom Typ Person in der Puffer Liste ab
	 * 
	 * @param obj
	 *            Objekt der Klasse Person
	 * @return true bei erfolgreicher Speicherung in der Puffer Liste
	 */
	private boolean bufferObject(Person obj) {
		try {
			bufferedObjects.add(obj);
			return true;
		} catch (ConcurrentModificationException e) {
			return false;
		}
	}

	/**
	 * Gibt alle Objekte vom Typ Person zurück, die sich in der Pufferliste
	 * befinden, dabei wird jeweils immer nur ein Personenobjekt zurückgegeben
	 * und anschließend auf ein Benutzer-Return gewartet.
	 * 
	 */
	private void printAllRecords() {
		scanner.nextLine(); // Flush
		if (bufferedObjects.size() == 0)
			System.out
					.println("Leider keine Datensätze vorhanden. Das tut uns Leid. :-(\n");
		for (int i = 0; i < bufferedObjects.size(); i++) {
			System.out.println("Satzinhalt (" + (i + 1) + ". Satz)\n");
			System.out.println("Name: " + bufferedObjects.get(i).getLastname());
			System.out.println("Vorname: "
					+ bufferedObjects.get(i).getFirstname());
			System.out.println("Anrede: "
					+ bufferedObjects.get(i).getSalutation());
			System.out
					.println("Strasse: " + bufferedObjects.get(i).getStreet());
			System.out.println("PLZ: " + bufferedObjects.get(i).getZip());
			System.out.println("Ort: " + bufferedObjects.get(i).getPlace());
			System.out.println("Telefon: " + bufferedObjects.get(i).getPhone());
			System.out.println("Fax: " + bufferedObjects.get(i).getFax());
			System.out.println("Bemerkug: "
					+ bufferedObjects.get(i).getRemark());
			System.out.println("Weiter? <Return>\n");
			while (!(scanner.nextLine().equals(""))) {
				// Wait of a Return
			}
			if (i == bufferedObjects.size() - 1)
				System.out.println("Keine weiteren Datensätze vorhanden.\n");
		}
	}

	/**
	 * Zeigt der Model Klasse per Pipe an, dass zu persistierende Objekte
	 * folgen. Im Anschluss werden alle im Buffer befindlichen Objekte per Pipe
	 * an das Model zur persistierung versandt. Durch Java zwischengepufferte
	 * Ströme werden mit flush direkt zum Versenden angewiesen. Im Anschluss
	 * wird der Buffer geleert.
	 * 
	 * @return Bei erfolgreicher Persistierung true
	 */
	public boolean persistBufferedObjects() {
		try {
			streamOut.writeInt(1);
			for (int i = 0; i < bufferedObjects.size(); i++) {
				streamOut.writeObject(bufferedObjects.get(i));
			}
			streamOut.writeObject(null);
			streamOut.flush();
			bufferedObjects.clear();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sortiert die Liste der gepufferten Objekte in aufsteigender Reihenfolge
	 * nach Lastname
	 * 
	 * @return true bei erfolgreicher Sortierung
	 */
	public boolean sortBufferedObjects() {
		try {
			Collections.sort(bufferedObjects);
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 * Teilt dem Model per Pipe mit, dass die Datenbasis gelöscht werden soll.
	 * 
	 * @return true im Efolgsfall
	 */
	public boolean deletePersistedObjects() {
		try {
			streamOut.writeInt(3);
			streamOut.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * addPersistedObjects teilt dem Model mit, dass es die Datenbasis auslesen
	 * möchte. Im Anschluss daran werden auf diese Objekte gewartet und bei
	 * Übersendung in den Buffer persistiert. Sobald das Model ein "null"-Objekt
	 * sendet, wird der Vorgang beendet.
	 * 
	 * @return
	 */
	public boolean addPersistedObjects() {
		Person p;

		try {
			streamOut.writeInt(2);
			streamOut.flush();

			do {
				p = (Person) streamIn.readObject();
				if (p != null)
					bufferedObjects.add((Person) p);
			} while (p != null);

			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
}
