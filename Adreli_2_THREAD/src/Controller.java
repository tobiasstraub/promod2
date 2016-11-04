import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Die Klasse Controller dient als Schnittstelle zwischen der View und dem
 * Model. Sie ist für den Aufbau der Pipes verantwortlich.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */
public class Controller {

	public static void main(String[] args) throws IOException,
			InterruptedException, AdrException { /* TODO: IOE */
		/**
		 * pisModel erzeugt einen InputStream, welcher von der Klasse Model
		 * gelesen werden können soll
		 */
		PipedInputStream pisModel = new PipedInputStream();

		/**
		 * posView erzeugt einen OutputStream, welcher aus der Klasse View
		 * beschrieben werden kann. Der Stream wird mit dem InputStream posModel
		 * verbunden
		 */
		PipedOutputStream posView = new PipedOutputStream(pisModel);

		/**
		 * streamOutModel schreibt die kompletten Personen Objekte auf den
		 * Objektstream streamOutModel, welcher wiederrum die Objekte byteweise
		 * in den posView Stream legt. Damit können Personen Objekte aus dem
		 * Model in die View gestreamt werden.
		 */
		ObjectOutputStream streamOutModel = new ObjectOutputStream(posView);

		/**
		 * streamInView liest die kompletten Personen Objekte auf den
		 * Objektstream streamInView, welcher wiederrum sich die Daten byteweise
		 * vom pisModel Stream holt. Damit können Personen Objekte aus der View
		 * gelesen werden die vom Model gestreamt wurde.
		 */
		ObjectInputStream streamInView = new ObjectInputStream(pisModel);

		/**
		 * pisView erzeugt einen InputStream, welcher von der Klasse View
		 * gelesen werden können soll
		 */
		PipedInputStream pisView = new PipedInputStream();

		/**
		 * posModel erzeugt einen OutputStream, welcher aus der Klasse Model
		 * beschrieben werden kann. Der Stream wird mit dem InputStream posView
		 * verbunden
		 */
		PipedOutputStream posModel = new PipedOutputStream(pisView);

		/**
		 * streamOutView schreibt die kompletten Personen Objekte auf den
		 * Objektstream streamOutView, welcher wiederrum die Objekte byteweise
		 * in den posModel Stream legt. Damit können Personen Objekte aus der
		 * View in das Model gestreamt werden.
		 */
		ObjectOutputStream streamOutView = new ObjectOutputStream(posModel);

		/**
		 * streamInModel liest die kompletten Personen Objekte auf den
		 * Objektstream streamInModel, welcher wiederrum sich die Daten
		 * byteweise vom pisView Stream holt. Damit können Personen Objekte aus
		 * dem Model gelesen werden die von der View gestreamt wurde.
		 */
		ObjectInputStream streamInModel = new ObjectInputStream(pisView);

		/**
		 * STORE_FILE legt den Dateipfad inklusive Dateiname und Dateiendung der
		 * Datenbasis fest
		 */
		final String STORE_FILE = "adreli.csv";

		/**
		 * Erzeugt ein Objekt der Klasse Model mit entsprechenden Parametern.
		 * 
		 * @param streamOutModel
		 *            Der zuständige ObjektOutputStream um Objekte an die View
		 *            zu senden
		 * @param streamInModel
		 *            Der zuständige ObjektInputStream um Objekte von der View
		 *            zu empfangen
		 * @param STORE_FILE
		 *            Dateipfad der Datenbasis
		 */
		Model model = new Model(streamOutModel, streamInModel, STORE_FILE);

		/**
		 * Setzt einen möglichen startenden Model Thread als Daemon. Somit
		 * arbeitet die Perisitierung und Datenabfrage der Datenbasis als
		 * Hintergrundprozess und wird erst beenden, sobald alle anderen
		 * User-Threads beenden sind (hier konkret: sobald View beendet ist)
		 */
		model.setDaemon(true);

		/**
		 * Startet einen eigenen Thread für die Klasse Model
		 */
		model.start();

		/**
		 * Erzeugt ein Objekt der Klasse View mit entsprechenden Parametern.
		 * 
		 * @param streamOutView
		 *            Der zuständige ObjektOutputStream um Objekte an das Model
		 *            zu schicken
		 * @param streamInView
		 *            Der zuständige ObjektInputStream um Objekte vom Model zu
		 *            empfangen
		 */
		View view = new View(streamOutView, streamInView);

		/**
		 * Startet einen eigenen Thread für die Klasse View
		 */
		view.start();

		/**
		 * Es wird gewartet bis der View Thread "stirbt", erst dann kann auch
		 * der main-Thread fortführen
		 */
		view.join();

		/**
		 * Beendet den Model-OutputStream
		 */
		streamOutModel.close();

		/**
		 * Beendet den View-OutputStream
		 */
		streamOutView.close();

		/**
		 * Beendet den Model-InputStream
		 */
		streamInModel.close();

		/**
		 * Beendet den View-InputStream
		 */
		streamInView.close();
	}
}
