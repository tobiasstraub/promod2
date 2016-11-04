import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Controller ist für das Erzeugen und Bereitstellen von Ressourcen sowie für
 * die Erzeugung des View Objektes und entsprechend Threads zuständig.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Controller {

	/**
	 * Der im späteren Verlauf zu öffnende Socket wird deklariert und mit null
	 * initialisiert, so dass in einem finally Block geprüft werden kann ob ein
	 * geöffneter Socket (ungleich null) geschlossen werden muss.
	 */
	static Socket socket = null;

	/**
	 * Erzeugt einen Objekt-Ausgabe-Stream
	 */
	static ObjectOutputStream streamToServer;

	/**
	 * Erzeugt einen Objekt-Eingabe-Stream
	 */
	static ObjectInputStream readFromServer;

	/**
	 * Legt den Port fest, welcher für den Kommunikationsaufbau zwischen Client
	 * und Server dient.
	 */
	final static String PORT = "56789";

	/**
	 * Deklariert die grafische Benutzeroberfläche um sich mit einem Server zu
	 * verbinden.
	 */
	static Frame controllerFrame;

	/**
	 * Initialisiert die grafische Benutzeroverfläche um sich mit dem Server zu
	 * verbinden. Wählt der Benutzer das 'X'-Symbol so wird er aufgefordert zu
	 * Bestätigen, dass er das Programm wirklich verlassen möchte. Die grafische
	 * Benutzeroberfläche enthält ein Textfeld um die IP-Adresse einzugebgen und
	 * ein Textfeld um den Port einzugeben, dabei ist das Textfeld des Ports mit
	 * dem Standard-Port vorbelegt. Weiterhin kann mit dem Button
	 * "Verbindung aufbauen..." die Verbindung zum Server aufgebaut werden.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		controllerFrame = new Frame("Verbindung zum Server...");
		controllerFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				final Dialog yesNoDialog = new Dialog(controllerFrame);
				yesNoDialog.setLayout(new GridLayout(3, 1));
				Label label = new Label(
						"Wollen Sie das Programm wirklich beenden?");
				yesNoDialog.add(label);
				Button yes = new Button("Ja");
				yes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				Button no = new Button("Nein");
				no.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						yesNoDialog.dispose();
					}
				});
				yesNoDialog.add(yes);
				yesNoDialog.add(no);
				yesNoDialog.setSize(300, 100);
				yesNoDialog.setVisible(true);
			}
		});
		controllerFrame.setLayout(new GridLayout(3, 1));
		controllerFrame.add(new Label("IP-Adresse: "));
		final TextField fieldIP = new TextField(15);
		controllerFrame.add(fieldIP);
		controllerFrame.add(new Label("Port: "));
		final TextField fieldPort = new TextField(5);
		fieldPort.setText(PORT);
		controllerFrame.add(fieldPort);
		Button connect = new Button("Verbindung aufbauen...");
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				connect(fieldIP, fieldPort);
			}
		});
		controllerFrame.add(connect);
		controllerFrame.setSize(350, 75);
		controllerFrame.setVisible(true);
	}

	public static void connect(TextField fieldIP, TextField fieldPort) {
		try {
			/**
			 * Kontaktiert die vom Nutzer eingegebene IP-Adresse mit dem vom
			 * Benutzer eingegebenen Port und speichert bei erfolgreichem Aufbau
			 * den Verbindungspunkt im Attribut socket ab
			 */
			socket = new Socket(fieldIP.getText(), Integer.parseInt(fieldPort
					.getText()));

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
			 * "streamToServer", "readFromServer" als Parameter dem Konstruktor
			 * der Klasse View
			 */
			View view = new View(streamToServer, readFromServer);

			/**
			 * Startet das vorher erzeugte View-Objekt als eigenständiger Thread
			 */
			view.start();

			/**
			 * Nach erfolgreichem Aufbau wird das Verbindungsaufbau-Window
			 * geschlossen.
			 */
			controllerFrame.dispose();

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
			 * Sollte der Server nicht erreichbar sein, wird ein entsprechender
			 * Dialog angezeigt
			 */
			final Dialog errorDialog = new Dialog(controllerFrame);
			errorDialog.setLayout(new FlowLayout());
			errorDialog.add(new Label("Der Server ist nicht erreichbar."));
			Button ok = new Button("OK");
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorDialog.dispose();
				}
			});
			errorDialog.add(ok);
			errorDialog.setSize(300, 100);
			errorDialog.setVisible(true);
		} catch (IOException e) {
			/**
			 * Sollte der Server die Verbindung verweigern, wird ein
			 * entsprechender Dialog angezeigt
			 */
			final Dialog errorDialog = new Dialog(controllerFrame);
			errorDialog.setLayout(new FlowLayout());
			errorDialog.add(new Label("Der Server verweigert die Verbindung."));
			Button ok = new Button("OK");
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorDialog.dispose();
				}
			});
			errorDialog.add(ok);
			errorDialog.setSize(300, 100);
			errorDialog.setVisible(true);
		} catch (InterruptedException e) {
			/**
			 * Sollte der Thread interrupted werden, wird ein entsprechender
			 * Dialog angezeigt.
			 */
			final Dialog errorDialog = new Dialog(controllerFrame);
			errorDialog.setLayout(new FlowLayout());
			errorDialog.add(new Label("Der Prozess wurde beendet."));
			Button ok = new Button("OK");
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorDialog.dispose();
				}
			});
			errorDialog.add(ok);
			errorDialog.setSize(300, 100);
			errorDialog.setVisible(true);
		} /*
		 * finally { /** Wenn Verbindungspunkt besteht, werden Ressourcen
		 * geschlossen. Können die Ressourcen nicht geschlossen werden, wird
		 * eine entsprechende Fehlermeldung ausgegeben.
		 */
		/*
		 * if (socket != null) { try { socket.close(); } catch (IOException e) {
		 * final Dialog errorDialog = new Dialog(controllerFrame);
		 * errorDialog.setLayout(new FlowLayout()); errorDialog .add(new Label(
		 * "Die Verbindung zum Server konnte nicht korrekt beendet werden."));
		 * Button ok = new Button("OK"); ok.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * errorDialog.dispose(); } }); errorDialog.add(ok);
		 * errorDialog.setSize(300, 100); errorDialog.setVisible(true); } } }
		 */
	}
}
