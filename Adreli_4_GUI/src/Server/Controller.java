import java.awt.Frame;
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
public class Controller extends Thread {
	/**
	 * serverSocket überwacht den oben festgelegten Port und leitet eingehende
	 * Verbindungen (Client) an einen freien Port, welcher für die weitere
	 * Kommunikation zwischen dem verbundenen Client und Server dient. Somit ist
	 * der zu überwachende Port wieder frei für weitere Verbindungen. Damit wird
	 * gewährleistet, dass der Server mehrere Verbindungen annehmen kann, die
	 * alle über den gleichen Port ihre Verbindung herstellen.
	 */
	ServerSocket serverSocket;

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
	static String storeFile = "adreli.csv";

	/**
	 * Deklariert den Socket, über welchen eine eingehende Verbindung läuft
	 */
	Socket socket = null;

	/**
	 * Deklariert den FileWriter, der für das Schreiben des Server Logfiles
	 * zuständig ist.
	 */
	static FileWriter logWriter;

	/**
	 * Deklariert das grafische Frame für die GUI
	 */
	static Frame controllerFrame;

	/**
	 * Deklariert den Port der vom Benutzer eingegeben werden kann
	 */
	int port;

	/**
	 * Deklariert das View Objekt, welches das grafische Interface für den
	 * Server bereitstellt.
	 */
	static View view;

	/**
	 * Der Konstruktor der Klasse Controller nimmt den Port, sowie eine
	 * angegebene Datenbasis an und stellt sie dem Objekt zur Verfügung.
	 * 
	 * @param port
	 *            Port
	 * @param storeFile
	 *            Datenbasis mit konkretem Pfad, wenn die Datenbasis sich nicht
	 *            sowieso im gleichen Ordner wie das Projekt befindet
	 */

	Controller(int port, String storeFile) {
		this.port = port;
		this.storeFile = storeFile;
	}

	/**
	 * Initialisiert den logWriter um die Logdatei zu beschreiben und startet
	 * ein Thread der Klasse View, welches das grafische Interface des Servers
	 * bereitstellt.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		logWriter = new FileWriter("adrelilog.txt");

		view = new View(storeFile);
		view.start();

	}

	@Override
	public void run() {
		int clientCounter = 0;
		try {
			serverSocket = new ServerSocket(port);
			writeToLog("Server wurde erfolgreich gestartet", logWriter);
			/**
			 * In einer Endlosschleife wartet der Server ständig auf neue
			 * Verbindungen über den oben genannten Port. Sobald eine Verbindung
			 * über diesen Port eingeht, wird dieser als Verbindungspunkt
			 * gespeichert und ein neuer Model-Thread wird für diese Verbindung
			 * gestaret. Dabei erhält das Model-Objekt die zwei Streams
			 * "readFromClient" und "streamToClient" als Parameter für den
			 * Konstruktor. Außerdem sind die Parameter "store_file" (Name der
			 * CSV Datei) und "logWriter" vorhanden. Der für eine entsprechende
			 * Verbindung zuständige Thread erhält einen Zufallsnamen, mit dem
			 * er in der später in der Logfile identifiziertbar ist. Desweiteren
			 * wird der Anzahl der Clients um eins erhöht und in dem grafischen
			 * Interface des Servers entsprechend angezeigt.
			 */
			while (true) {
				writeToLog("Warte auf eine Client Connection", logWriter);
				socket = serverSocket.accept();
				writeToLog(
						"Der Client mit der IP Adresse "
								+ socket.getRemoteSocketAddress()
								+ " hat sich mit dem Port " + port
								+ " connected", logWriter);
				writeToLog("Dem Client wird Port " + socket.getPort()
						+ " für die Kommunikation zugewiesen", logWriter);
				readFromClient = new ObjectInputStream(socket.getInputStream());
				streamToClient = new ObjectOutputStream(
						socket.getOutputStream());
				Model model;
				model = new Model(readFromClient, streamToClient, storeFile,
						logWriter);
				model.start();
				Integer threadName = new Integer((int) (Math.random() * 999));
				model.setName("CLIENT#" + threadName.toString());
				writeToLog("Für den Client wird ein neuer Thread 'CLIENT#"
						+ threadName.toString() + "' gestartet", logWriter);
				view.updateGUI(++clientCounter);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
