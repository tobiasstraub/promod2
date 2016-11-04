import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Controller ist für das Erzeugen und Bereitstellen von Ressourcen sowie für
 * die Erzeugung des View Objektes und entsprechend Threads zuständig.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Controller {

	public static void main(String[] args) {
		/**
		 * Erzeugt einen Eingabe Stream
		 */
		Scanner scanner = new Scanner(System.in);

		/**
		 * Der im späteren Verlauf zu öffnende Socket wird deklariert und mit
		 * null initialisiert, so dass in einem finally Block geprüft werden
		 * kann ob ein geöffneter Socket (ungleich null) geschlossen werden
		 * muss.
		 */
		Socket socket = null;

		/**
		 * Erzeugt einen Objekt-Ausgabe-Stream
		 */
		ObjectOutputStream streamToServer;

		/**
		 * Erzeugt einen Objekt-Eingabe-Stream
		 */
		ObjectInputStream readFromServer;

		/**
		 * Legt den Port fest, welcher für den Kommunikationsaufbau zwischen
		 * Client und Server dient.
		 */
		final int PORT = 56789;

		System.out.print("Bitte IP Adresse des Servers eingeben: ");
		try {
			/**
			 * Kontaktiert die vom Nutzer eingegebene IP-Adresse mit dem oben
			 * festgelegten Port und speichert bei erfolgreichem Aufbau den
			 * Verbindungspunkt im Attribut socket ab
			 */
			socket = new Socket(scanner.next(), PORT);

			/**
			 * Codiert Objekete mit Hilfe von ObjectOutputStream für den
			 * Socket-OutputStream
			 */
			streamToServer = new ObjectOutputStream(socket.getOutputStream());

			/**
			 * Dekodiert den Socket-InputStream mit Hilfe von ObjectInputStream
			 * zu Objekten
			 */
			readFromServer = new ObjectInputStream(socket.getInputStream());

			/**
			 * Erzeugt ein neues View-Objekt und übergibt die drei Streams
			 * "streamToServer", "readFromServer" und "scanner" als Parameter
			 * dem Konstruktor der Klasse View
			 */
			View view = new View(streamToServer, readFromServer, scanner);

			/**
			 * Startet das vorher erzeugte View-Objekt als eigenstÃ¤ndiger Thread
			 */
			view.start();

			/**
			 * Sorgt dafür, dass der Main-Thread nicht im normalen Ablauf
			 * beendet wird, sondern solange "am Leben" bleibt, solange der
			 * vorher erzeugte View-Thread existiert. Das sorgt dafür, dass die
			 * im Main-Thread deklarierten und initialisierten Streams nicht
			 * frühzeitig mit dem beenden des Main-Threads gekillt werden.
			 */
			view.join();
		} catch (UnknownHostException e) {
			/**
			 * Sollte die IP-Adresse nicht erreichbar sein
			 */
			System.out
					.println("Konnte den Server nicht erreichen. Bitte kontaktieren Sie den Administrator.");
		} catch (IOException e) {
			/**
			 * Sollte die IP-Adresse erreichbar sein, der Server aber die
			 * Verbindung verweigert.
			 */
			System.out
					.println("Der Server lehnt die Verbindung ab. Bitte kontaktieren Sie den Administrator.");
		} catch (InterruptedException e) {
			/**
			 * Sollte der Thread interrupted werden
			 */
			System.out
					.println("Es ist ein Fehler aufgetreten. Bitte kontaktieren Sie den Administrator.");
		} finally {
			/**
			 * Wenn Verbindungspunkt besteht, werden Ressourcen geschlossen.
			 * Können die Ressourcen nicht geschlossen werden, wird eine
			 * entsprechende Fehlermeldung ausgegeben.
			 */
			if (socket != null) {
				try {
					scanner.close();
					socket.close();
				} catch (IOException e) {
					System.out
							.println("Konnte den Socket nicht schliessen. Bitte kontaktieren Sie den Administrator.");
				}
			}
		}

	}
}
