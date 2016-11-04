package de.hsfurtwangen.general;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Die Klasse View ist für die korrekte Anzeige aller Daten zuständig. Weiterhin
 * ist sie Verantwortlich für die Ausgabe von möglichen Fehlern.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class View {
	public static void main(String[] args) {
		Controller cont = new Controller();
		Scanner scanner = new Scanner(System.in);
		try {
			do {
				printMenu();
				switch (scanner.nextInt()) {
				case 1:
					View.newRecord(cont, scanner);
					break;
				case 2:
					View.printAllRecords(cont, scanner);
					break;
				case 3:
					if (cont.persistBufferedObjects()) {
						System.out.println("Persistierung erfolgreich!\n");
					} else {
						System.out
								.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
					}
					break;
				case 4:
					if (cont.addPersistedObjects()) {
						System.out
								.println("Alle persistierten Objekte wurden erfolgreich geladen!\n");
					} else {
						System.out
								.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
					}
					break;
				case 5:
					if (cont.sortBufferedObjects()) {
						System.out
								.println("Alle gepufferten Objekte wurden erfolgreich sortiert!\n");
					} else {
						System.out
								.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
					}
					break;
				case 6:
					if (cont.deletePersistedObjects()) {
						System.out
								.println("Persistierte Datei wurde geloescht!\n");
					} else {
						System.out
								.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.\n");
					}
					break;
				case 7:
					scanner.close();
					System.exit(0);
					break;
				default:
					System.out
							.println("Wir konnten Ihre Eingabe nicht erkennen. Haben Sie sich vertippt?\n");
				}
			} while (true);
		} catch (InputMismatchException e) {
			System.out
					.println("Es ist ein Fehler aufgetreten. Wir konnten Ihre Eingabe nicht erkennen.\n");
		}
	}

	/**
	 * Gibt das Benutzermenü auf der Konsole aus
	 */
	public static void printMenu() {
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
	 * 
	 * @param cont
	 *            Das zuständige Controller Objekt
	 * @param scanner
	 *            Der für die Eingabeverarbeitung zuständige Scanner
	 */
	public static void newRecord(Controller cont, Scanner scanner) {
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
			if (cont.bufferObject(person)) {
				System.out
						.print("\n\nObjekt erfolgreich aufgenommen.\nMoechten Sie eine weitere Person aufnehmen (J/N)? ");
				if ((scanner.next().toLowerCase()).equals("j")) {
					System.out.println("");
					View.newRecord(cont, scanner);
				}
			} else {
				System.out
						.println("Entschuldigung, da ging was schief. Bitte kontaktieren Sie den Administrator.");
			}
		} else {
			View.newRecord(cont, scanner);
		}
	}

	/**
	 * Gibt alle Objekte vom Typ Person zurück, die sich in der Pufferliste
	 * {@link Controller#bufferObject(Person)} befinden, dabei wird jeweils
	 * immer nur ein Personenobjekt zurückgegeben und anschließend auf ein
	 * Benutzer-Return gewartet.
	 * 
	 * @param cont
	 *            Das zuständige Controller Objekt
	 * @param scanner
	 *            Der für die Eingabeverarbeitung zuständige Scanner
	 */
	public static void printAllRecords(Controller cont, Scanner scanner) {
		scanner.nextLine(); // Flush
		if (cont.getBufferedObjects().size() == 0)
			System.out
					.println("Leider keine Datensätze vorhanden. Das tut uns Leid. :-(\n");
		for (int i = 0; i < cont.getBufferedObjects().size(); i++) {
			System.out.println("Satzinhalt (" + (i + 1) + ". Satz)\n");
			System.out.println("Name: "
					+ cont.getBufferedObjects().get(i).getLastname());
			System.out.println("Vorname: "
					+ cont.getBufferedObjects().get(i).getFirstname());
			System.out.println("Anrede: "
					+ cont.getBufferedObjects().get(i).getSalutation());
			System.out.println("Strasse: "
					+ cont.getBufferedObjects().get(i).getStreet());
			System.out.println("PLZ: "
					+ cont.getBufferedObjects().get(i).getZip());
			System.out.println("Ort: "
					+ cont.getBufferedObjects().get(i).getPlace());
			System.out.println("Telefon: "
					+ cont.getBufferedObjects().get(i).getPhone());
			System.out.println("Fax: "
					+ cont.getBufferedObjects().get(i).getFax());
			System.out.println("Bemerkug: "
					+ cont.getBufferedObjects().get(i).getRemark());
			System.out.println("Weiter? <Return>\n");
			while (!(scanner.nextLine().equals(""))) {
				// Wait of an Return
			}
			if (i == cont.getBufferedObjects().size() - 1)
				System.out.println("Keine weiteren Datensätze vorhanden.\n");
		}
	}
}
