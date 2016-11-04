import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

/**
 * View ist für die Kommunikation mit dem Benutzer zuständig. View ist
 * multithreading-fähig.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class View extends Thread {
	/**
	 * Ausgabestrom von Client zu Server
	 */
	ObjectOutputStream streamOut;

	/**
	 * Eingabestrom von Server
	 */
	ObjectInputStream readIn;

	/**
	 * Sorgt für das "sanfte" Beenden des Threads durch Interrupten der
	 * Menü-Schleife
	 */
	boolean interrupt = true;

	/**
	 * Eingabe Stream der Konsole
	 */
	Scanner scanner;

	/**
	 * Die Pufferrung der Personen-Objekte wird in Form einer ArrayList
	 * realisiert. Die folgende Anweisung deklariert und initialisiert diese.
	 */
	private ArrayList<Person> bufferedObjects = new ArrayList<>();

	/**
	 * Der Konstruktor sorgt für die korrekte Initialisierung der lokalen
	 * Attribute mit den übergebenen Parametern
	 * 
	 * @param streamOut
	 *            Damit können Objekte zum Server gesendet werden.
	 * @param readIn
	 *            Damit können Objekte, welche vom Server gesendet wurden,
	 *            gelesen werden.
	 * @param scanner
	 *            Damit kann der Eingabe Stream der Konsole gelesen werden
	 */
	View(ObjectOutputStream streamOut, ObjectInputStream readIn, Scanner scanner) {
		this.streamOut = streamOut;
		this.readIn = readIn;
		this.scanner = scanner;
	}

	/**
	 * Die run()-Methode wird, beim Starten des im Controller erzeugten View
	 * Objekt als Thread, ausgeführt. In einer Endlosschleife, welche durch das
	 * interrupt boolean Flag beendet werden kann, wird das Menü ausgegeben.
	 * Anschließend wird auf eine Zahleneingabe des Benutzers zwischen 1 und 7
	 * auf der Konsole gewartet. In einer switch-case werden die im
	 * nachfolgenden näher erläuterteten Methoden aufgerufen. Bei fast allen
	 * Methoden-Aufrufen wird im Erfolgsfall true und im Fehlerfall false
	 * zurückgeliefert und entsprechend mit einer Nachricht quittiert. Bei
	 * einigen Methoden wird keine Rückgabe in Form von true und false
	 * geliefert. Diese Methoden übernehmen aus verschiedensten Gründen eine
	 * mögliche Fehlerausgabe oder Erfolgsquittung direkt selbst. Der Case 7
	 * bildet eine Ausnahme und ruft keine Methode auf sondern setzt das boolean
	 * Interrupt Flag und beendet somit die Schleife und damit den gesamten
	 * Thread. Der Server wird dabei nicht beendet. Lediglich der Client.
	 */
	public void run() {
		do {
			printMenu();
			switch (scanner.nextInt()) {
			case 1:
				newRecord();
				scanner.nextLine(); // Flush
				break;
			case 2:
				printAllRecords();
				break;
			case 3:
				if (persistBufferedObjects()) {
					System.out.println("Persistierung erfolgreich!\n");
				} else {
					System.out
							.println("Konnte die gepufferten Objekte nicht auf der Festplatte persistieren. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 4:
				if (addPersistedObjects()) {
					System.out
							.println("Alle persistierten Objekte wurden erfolgreich geladen!\n");
				} else {
					System.out
							.println("Konnte die Objekte aus der CSV Datei nicht in den Puffer laden. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 5:
				if (sortBufferedObjects()) {
					System.out
							.println("Alle gepufferten Objekte wurden erfolgreich sortiert!\n");
				} else {
					System.out
							.println("Konnte die gepufferten Objekte nicht sortieren. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 6:
				if (deletePersistedObjects()) {
					System.out.println("Persistierte Datei wurde geloescht!\n");
				} else {
					System.out
							.println("Konnte die Datei nicht loeschen. Bitte kontaktieren Sie den Administrator.\n");
				}
				break;
			case 7:
				interrupt = false;
				break;
			}
		} while (interrupt);
	}

	/**
	 * Die Methode gibt das Menü auf der Konsole aus.
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
	 * Die Methode erzeugt ein neues Personen-Objekt und fällt dessen Attribute
	 * mit den vom Benutzer, auf Abfrage des Programms, eingegebenen Werten.
	 * Nach der Abfrage des Programms nach einem Wert, welchen der Benutzer dann
	 * entsprechend eingibt, wird versucht diesen Wert mit der Set-Methode des
	 * Personen-Objektes zu setzen. Im Erfolgsfall wird der nächste Wert
	 * abgefragt. Im Fehlerfall hat die Set-Methode des Personen-Objektes die
	 * AdrException geworfen. In diesem Fall wird die lokale Variable "breakIt"
	 * true gesetzt, welche dafür sorgt, dass eine while-Schleife ausführt,
	 * dessen Zweck es ist solange den Wert abzufragen, bis dieser korrekt ist.
	 * Sollte der Wert korrekt sein, wird die Schleife beendet, in dem die
	 * lokale Variable "breakIt" auf false gesetzt wird. Nachdem alle Werte
	 * korrekt eingegeben wurden, wird der Benutzer aufgefordert die Korrektheit
	 * der Daten zu bestätigen. Tut er dies mit Ja (J/j) wird das Objekt in den
	 * Puffer aufgenommen und der Benutzer wird gefragt, ob er ein weiteres
	 * Objekt aufnehmen möchte. Beantwortet er diese Frage mit Ja (J/j), so wird
	 * der komplette Vorgang wiederholt. Beantwortet er diese Frage mit Nein, so
	 * kehrt das Programm zurück zum Menü. Wird die Korrektheit der Daten nicht
	 * bestätigt, so wird das Objekt nicht in den Puffer aufgenommen und der
	 * komplette Vorgang wird wiederholt.
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
						.println("Ups, da ging was schief.\nDie Strasse muss zwischen drei und 30 Zeichen haben "
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
						.println("Das Objekt konnte nicht im Puffer persistiert werden. Bitte kontaktieren Sie den Administrator.");
			}
		} else {
			newRecord();
		}
	}

	/**
	 * Die Methode prüft, ob sich im Puffer Objekte befinden. Ist dies nicht der
	 * Fall, wird eine entsprechend Meldung ausgegeben. Befinden sich Objekte im
	 * Puffer, so werden diese der Reihe nach auf der Konsole, aufgeschlüsselt
	 * nach Attributen ausgegeben. Dabei erält jeder "Attributsatz" die
	 * Überschrift "Satzinhalt" mit der entsprechenden Positionsnummerierung.
	 * Nach jedem Satzinhalt muss die weitere Ausgabe erst mit dem Druck auf
	 * Return bestätigt werden. Nach dem letzten Satzinhalt wird entsprechend
	 * die Meldung ausgegeben, dass keine weiteren Objekte mehr im Puffer sind.
	 */
	private void printAllRecords() {
		scanner.nextLine(); // Flush
		if (bufferedObjects.size() == 0)
			System.out
					.println("Leider keine Datensaetze vorhanden. Das tut uns Leid. :-(\n");
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
				System.out.println("Keine weiteren Datensaetze vorhanden.\n");
		}
	}

	/**
	 * Die Methode sorgt für die "echte" Persistierung im Puffer. Im Erfolgsfall
	 * wird true an den Aufrufer zurückgegeben. Im Fehlerfall false.
	 * 
	 * @param obj
	 *            ist das Personen-Objekt
	 * @return true im Erfolgsfall
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
	 * Die Methode sortiert die Puffer Liste und liefert im Erfolgsfall true. Im
	 * Fehlerfall false.
	 * 
	 * @return true im Erfolgsfall
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
	 * Schickt an den Server die Aufforderung zu persistieren (1). Anschließend
	 * werden alle im Puffer befindlichen Personen-Objekte zur Persistierung an
	 * den Server geschickt. Anschließend wird ein null-Objekt an den Server
	 * gesendet. Durch dieses null-Objekt beendet der Server das Warten auf
	 * weitere zu persistierende Objekte. Mit flush werden die von Java
	 * zurückgehaltenen Objekte sofort gesendet. Nach diesem Prozess wird der
	 * Puffer geleert. Hat alles funktioniert, liefert die Methode true. Im
	 * fehlerfall false.
	 * 
	 * @return true im Erfolgsfall
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
			return false;
		}
	}

	/**
	 * Schickt dem Server die Aufforderung die auf der Festplatte befindliche
	 * CSV-Datei zu löschen (3). Mit flush wird das von Java zurückgehaltene
	 * int-Objekt sofort gesendet. Liefert im Erfolgsfall true. Im Fehlerfall
	 * false.
	 * 
	 * @return true im Erfolgsfall
	 */
	public boolean deletePersistedObjects() {
		try {
			streamOut.writeInt(3);
			streamOut.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Schickt dem Server die Aufforderung die in der CSV-Datei persistierten
	 * Objekte an den View-Thread zu schicken (2). Wartet anschließend auf
	 * Personen-Objekte und persistiert ankommende im Puffer. Der Vorgang wird
	 * abgeschlossen, sobald der Server ein null-Objekt schickt. Liefert im
	 * Erfolgsfall true. Im Fehlerfall false. Mit flush wird das von Java
	 * zurückgehaltene int-Objekt sofort gesendet.
	 * 
	 * @return true im Erfolgsfall
	 */
	public boolean addPersistedObjects() {
		Person p;

		try {
			streamOut.writeInt(2);
			streamOut.flush();

			do {
				p = (Person) readIn.readObject();
				if (p != null)
					bufferedObjects.add((Person) p);
			} while (p != null);

			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}
}
