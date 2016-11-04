package de.hsfurtwangen.general;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;

/**
 * Die Klasse Controller ist für die eigentliche Logik der Software zuständig.
 * Sie ist "Vermittler" zwischen der Klasse View und der Modell Klasse Person.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Controller {
	/**
	 * FileOutputStream ist der Outputstram für die Speicherung der Personen
	 * Objekte
	 */
	private FileOutputStream fos = null;
	/**
	 * FileInputStream ist der Inputstream für die Speicherung der Personen
	 * Objekte
	 */
	private FileInputStream fis = null;
	/**
	 * bufferedObjects ist eine Arrayliste welche die angelegten Personen
	 * Objekte vor der Persistierung zwischenspeichern soll
	 */
	private ArrayList<Person> bufferedObjects;
	/**
	 * STORE_FILE enthält den Dateinamen für die Datei mit den persistierten
	 * Objekte
	 */
	public static final String STORE_FILE = "storeFile.objects";

	/**
	 * Parameterloser Konstruktor, welcher für die Deklarierung bestimmter Typen
	 * zuständig ist
	 */
	Controller() {
		bufferedObjects = new ArrayList<>();
	}

	/**
	 * Gibt die Liste der gepufferten Objekte zurück
	 * 
	 * @return Arrayliste von Typ Person
	 */
	public ArrayList<Person> getBufferedObjects() {
		return bufferedObjects;
	}

	/**
	 * Speichert das übergebene Objekt vom Typ Person in der Puffer Liste ab
	 * 
	 * @param obj
	 *            Objekt der Klasse Person
	 * @return true bei erfolgreicher Speicherung in der Puffer Liste
	 */
	public boolean bufferObject(Person obj) {
		try {
			bufferedObjects.add(obj);
			return true;
		} catch (ConcurrentModificationException e) {
			return false;
		}
	}

	/**
	 * Persistiert alle gepufferten Objekte in der Datei {@value #STORE_FILE}
	 * und leert im Anschluss an die erfolgreiche Persistierung die Puffer Liste
	 * {@link #bufferedObjects}
	 * 
	 * @return true bei erfolgreicher Persistierung und Leerung der Puffer Liste
	 *         {@link #bufferedObjects}
	 */
	public boolean persistBufferedObjects() {
		try {
			fos = new FileOutputStream(Controller.STORE_FILE);
			ObjectOutputStream outStream = new ObjectOutputStream(fos);
			for (int i = 0; i < bufferedObjects.size(); i++) {
				outStream.writeObject(bufferedObjects.get(i));
			}
			fos.close();
			bufferedObjects.clear();
		} catch (IOException e) {
			return false;
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Liest die Datei {@value #STORE_FILE}, deserialisiert die darin
	 * persistierten Objekte und fügt sie der Puffer Liste
	 * {@link #bufferedObjects} an
	 * 
	 * @return true bei erfolgreicher Deserialisierung und Zufügung der Objekte
	 *         der Puffer Liste {@link #bufferedObjects}
	 */
	public boolean addPersistedObjects() {
		try {
			fis = new FileInputStream(Controller.STORE_FILE);
			ObjectInputStream inStream = new ObjectInputStream(fis);

			while (true) {
				bufferedObjects.add((Person) inStream.readObject());
			}
		} catch (EOFException e) {
			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * Sortiert die Liste der gepufferten Objekte {@link #bufferedObjects} in
	 * aufsteigender Reihenfolge nach Lastname
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
	 * Entfernt die Datei mit den persistierten Objekten {@value #STORE_FILE}
	 * 
	 * @return true bei erfolgreicher Durchführung
	 */
	public boolean deletePersistedObjects() {
		try {
			new File(Controller.STORE_FILE).delete();
			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
}
