import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller ist für das Erzeugen und Bereitstellen von Ressourcen sowie für
 * die Erzeugung des Model Objektes und entsprechend Threads zuständig.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Controller {

	public static void main(String[] args) throws IOException {
		/**
		 * Legt den Port fest, welcher für den Kommunikationsaufbau zwischen
		 * Server und Client dient.
		 */
		final int PORT = 56789;

		/**
		 * serverSocket überwacht den oben festgelegten Port und leitet
		 * eingehende Verbindungen (Client) an einen freien Port, welcher für
		 * die weitere Kommunikation zwischen dem verbundenen Client und Server
		 * dient. Somit ist der zu überwachende Port wieder frei für weitere
		 * Verbindungen. Damit wird gewährleistet, dass der Server mehrere
		 * Verbindungen annehmen kann, die alle über den gleichen Port ihre
		 * Verbindung herstellen.
		 */
		ServerSocket serverSocket = new ServerSocket(PORT);

		/**
		 * Erzeugt einen Objekt-Eingabe-Stream
		 */
		ObjectInputStream readFromClient;

		/**
		 * Erzeugt einen Objekt-Ausgabe-Stream
		 */
		ObjectOutputStream streamToClient;

		/**
		 * Legt den Namen der CSV Datei fest
		 */
		final String STORE_FILE = "adreli.csv";

		/**
		 * Deklariert den Socket, über welchen eine eingehende Verbindung läuft
		 */
		Socket socket = null;

		/**
		 * Deklariert und initialisiert den FileWriter, der für das Schreiben
		 * des Server Logfiles zuständig ist.
		 */
		FileWriter logWriter = new FileWriter("adrelilog.txt");

		writeToLog("Server wurde erfolgreich gestartet", logWriter);

		/**
		 * In einer Endlosschleife wartet der Server ständig auf neue
		 * Verbindungen über den oben genannten Port. Sobald eine Verbindung
		 * über diesen Port eingeht, wird dieser als Verbindungspunkt
		 * gespeichert und ein neuer Model-Thread wird für diese Verbindung
		 * gestaret. Dabei erhält das Model-Objekt die zwei Streams
		 * "readFromClient" und "streamToClient" als Parameter für den
		 * Konstruktor. Außerdem sind die Parameter "STORE_FILE" (Name der CSV
		 * Datei) und "logWriter" vorhanden. Der für eine entsprechende
		 * Verbindung zuständige Thread erhält einen Zufallsnamen, mit dem er in
		 * der später in der Logfile identifiziertbar ist.
		 */
		while (true) {
			writeToLog("Warte auf eine Client Connection", logWriter);
			socket = serverSocket.accept();
			writeToLog(
					"Der Client mit der IP Adresse "
							+ socket.getRemoteSocketAddress()
							+ " hat sich mit dem Port " + PORT + " connected",
					logWriter);
			writeToLog("Dem Client wird Port " + socket.getPort()
					+ " für die Kommunikation zugewiesen", logWriter);
			readFromClient = new ObjectInputStream(socket.getInputStream());
			streamToClient = new ObjectOutputStream(socket.getOutputStream());
			Model model = new Model(readFromClient, streamToClient, STORE_FILE,
					logWriter);
			model.start();
			Integer threadName = new Integer((int) (Math.random() * 999));
			model.setName("CLIENT#" + threadName.toString());
			writeToLog("Für den Client wird ein neuer Thread 'CLIENT#"
					+ threadName.toString() + "' gestartet", logWriter);
		}
	}

	/**
	 * Schreibt eine Nachricht inklusive Datum und Uhrzeit in eine entsprechende
	 * Datei.
	 * 
	 * @param message
	 *            Nachricht, welche in der Logile stehen soll
	 * @param logWriter
	 *            FileWriter der den Schreibvorgang durchführt
	 * @throws IOException
	 */
	public static void writeToLog(String message, FileWriter logWriter)
			throws IOException {
		logWriter.append("["
				+ new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
						.format(new Date()) + "] ::: " + message + "\n");
		logWriter.flush();
	}
}
