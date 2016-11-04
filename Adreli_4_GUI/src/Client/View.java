import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	 * Eingabe Stream der Konsole
	 */
	Scanner scanner;

	/**
	 * Deklariert entsprechende Benutzeroberflächen für das Hauptfenster, das
	 * Fenster für den neuen Datensatz, sowie das Fenster für das Anzeigen
	 * bestehender Datensätze im Puffer.
	 */
	Frame mainFrame, newRecordFrame, listRecordFrame;

	/**
	 * Die Pufferrung der Personen-Objekte wird in Form einer ArrayList
	 * realisiert. Die folgende Anweisung deklariert und initialisiert diese.
	 */
	private final ArrayList<Person> bufferedObjects = new ArrayList<>();

	/**
	 * Der Konstruktor sorgt für die korrekte Initialisierung der lokalen
	 * Attribute mit den übergebenen Parametern
	 * 
	 * @param streamOut
	 *            Damit können Objekte zum Server gesendet werden.
	 * @param readIn
	 *            Damit können Objekte, welche vom Server gesendet wurden,
	 *            gelesen werden.
	 */
	View(ObjectOutputStream streamOut, ObjectInputStream readIn) {
		this.streamOut = streamOut;
		this.readIn = readIn;
	}

	/**
	 * Initialisiert das Hauptfenster, und ruft beim Klick auf das 'X'-Symbol
	 * die Methode exitDialog() auf, welche für das korrekte Beenden des
	 * Programms sorgt. Ruft weiterhin die Methode getMenubar auf und added das
	 * Menü der Frame.
	 */
	@Override
	public void run() {
		mainFrame = new Frame("Adressverwaltungssystem");
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exitDialog();
			}
		});
		mainFrame.setMenuBar(this.getMenubar());
		mainFrame.setSize(400, 100);
		mainFrame.setVisible(true);
	}

	/**
	 * Erzeugt ein neues Menü mit entsprechenden ActionListener die als innere
	 * Klassen realisiert sind. Dabei werden ab un zu auf Hilfsklassen (Helper)
	 * zurückgegriffen, welche für das Schreiben und Verändern von Attributen
	 * aus der inneren Klasse sowie auch aus der Klasse in der die innere Klasse
	 * eingebetten ist zuständig ist.
	 * 
	 * @return
	 */
	private MenuBar getMenubar() {
		MenuBar menueLeiste = new MenuBar();
		Menu administration = new Menu("Verwaltung");
		MenuItem newRecord = new MenuItem("Person anlegen");
		newRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newRecord();
			}
		});
		MenuItem listRecords = new MenuItem("Personen anzeigen");
		listRecords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				printAllRecords();
			}
		});
		MenuItem saveRecords = new MenuItem("Personen sichern");
		saveRecords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Dialog persistDialog = new Dialog(mainFrame);
				Label label;
				persistDialog.setLayout(new FlowLayout());
				if (persistBufferedObjects()) {
					label = new Label("Alle Personen wurden persistiert.");
				} else {
					label = new Label(
							"Bei der Persistierung ist ein Problem aufgetreten.");
				}
				persistDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						persistDialog.dispose();
					}
				});
				persistDialog.add(ok);
				persistDialog.setSize(300, 100);
				persistDialog.setVisible(true);
			}
		});
		MenuItem loadRecords = new MenuItem("Personen laden");
		loadRecords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Dialog loadDialog = new Dialog(mainFrame);
				Label label;
				loadDialog.setLayout(new FlowLayout());
				if (addPersistedObjects()) {
					label = new Label(
							"Alle Personen wurden in den Puffer geladen.");
				} else {
					label = new Label(
							"Beim Laden der Personen ist ein Problem aufgetreten.");
				}
				loadDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadDialog.dispose();
					}
				});
				loadDialog.add(ok);
				loadDialog.setSize(300, 100);
				loadDialog.setVisible(true);
			}
		});
		MenuItem sortRecords = new MenuItem("Personen sortieren");
		sortRecords.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Dialog sortDialog = new Dialog(mainFrame);
				Label label;
				sortDialog.setLayout(new FlowLayout());
				if (sortBufferedObjects()) {
					label = new Label("Der Puffer wurde sortiert.");
				} else {
					label = new Label(
							"Beim sortieren des Puffers ist ein Problem aufgetreten.");
				}
				sortDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sortDialog.dispose();
					}
				});
				sortDialog.add(ok);
				sortDialog.setSize(300, 100);
				sortDialog.setVisible(true);
			}
		});
		MenuItem deleteFile = new MenuItem("Personen löschen");
		deleteFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Dialog sortDialog = new Dialog(mainFrame);
				Label label;
				sortDialog.setLayout(new FlowLayout());
				if (deletePersistedObjects()) {
					label = new Label("Das Persistierungsfile wurde gelöscht.");
				} else {
					label = new Label(
							"Beim Löschen des Persistierungsfile ist ein Problem aufgetreten.");
				}
				sortDialog.add(label);
				Button ok = new Button("OK");
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						sortDialog.dispose();
					}
				});
				sortDialog.add(ok);
				sortDialog.setSize(300, 100);
				sortDialog.setVisible(true);
			}
		});
		MenuItem exitSoftware = new MenuItem("Programm beenden");
		exitSoftware.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitDialog();
			}
		});
		administration.add(newRecord);
		administration.add(listRecords);
		administration.add(saveRecords);
		administration.add(loadRecords);
		administration.add(sortRecords);
		administration.add(deleteFile);
		administration.add(exitSoftware);
		menueLeiste.add(administration);
		return menueLeiste;
	}

	/**
	 * Fordert den Benutzer auf, zu bestätigen ob er das Programm wirklich
	 * beenden will. Beendet bei entsprechender Zustimmung das Programm korrekt.
	 */
	private void exitDialog() {
		final Dialog yesNoDialog = new Dialog(mainFrame);
		yesNoDialog.setLayout(new GridLayout(3, 1));
		Label label = new Label("Wollen Sie das Programm wirklich beenden?");
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

	/**
	 * Es wird ein neues Personen-Objekt angelegt und die eingegebenen Werte in
	 * den Textfelder des Frames werden validiert und entsprechend an die
	 * Setter-Methoden des Personen Objektes weitergeleitet. Dabei findet die
	 * Validierung der Eingaben immer nach dem Klick auf Speichern und dem
	 * Bestätigen des "Stimmt's"-Dialog statt. Die Validierungsmeldungen weren
	 * entsprechend von den Setter-Methoden geworfen (AdrException) und in der
	 * Form angezeigt. Nach der erfolgreichen Validierung wird der Benutzer mit
	 * Hilfe eines Dialogs gefragt, ob eine neue Person angelegt werden soll.
	 */
	private void newRecord() {
		final Person person = new Person();

		newRecordFrame = new Frame("Neue Person anlegen");
		newRecordFrame.setLayout(new GridLayout(2, 1));
		newRecordFrame.setSize(1024, 500);

		final Panel p1 = new Panel();
		p1.setLayout(new GridLayout(11, 1));
		p1.add(new Label("Name"));
		final TextField fieldLastname = new TextField(20);
		p1.add(fieldLastname);
		p1.add(new Label("Vorname"));
		final TextField fieldFirstname = new TextField(20);
		p1.add(fieldFirstname);
		p1.add(new Label("Anrede"));
		p1.add(new Label(""));
		final CheckboxGroup salutation = new CheckboxGroup();
		final Checkbox checkboxMale = new Checkbox("Herr", salutation, false);
		p1.add(checkboxMale);
		final Checkbox checkboxFemale = new Checkbox("Frau", salutation, false);
		p1.add(checkboxFemale);
		p1.add(new Label("Strasse"));
		final TextField fieldStreet = new TextField(20);
		p1.add(fieldStreet);
		p1.add(new Label("PLZ"));
		final TextField fieldZip = new TextField(20);
		p1.add(fieldZip);
		p1.add(new Label("Ort"));
		final TextField fieldPlace = new TextField(20);
		p1.add(fieldPlace);
		p1.add(new Label("Telefon"));
		final TextField fieldPhone = new TextField(20);
		p1.add(fieldPhone);
		p1.add(new Label("Fax"));
		final TextField fieldFax = new TextField(20);
		p1.add(fieldFax);
		p1.add(new Label("Bemerkung"));
		final TextField fieldRemark = new TextField(20);
		p1.add(fieldRemark);
		Button saveButton = new Button("Speichern");
		p1.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			/**
			 * Sorgt für die Validierung mit Hilfe der "errorSemaphore", d.h.
			 * erst wenn die errorSemaphore 0 ist sind keine Fehler mehr
			 * vorhanden und der Datensatz wird gespeichert. Validierungen
			 * werden mit Hilfe von Labeln und Background-Colors visualisiert.
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				final Dialog correctDialog = new Dialog(newRecordFrame);
				correctDialog.setLayout(new GridLayout(3, 1));
				Label label = new Label("Stimmts?");
				correctDialog.add(label);
				Button yes = new Button("Ja");
				yes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						correctDialog.dispose();
						newRecordFrame.removeAll();
						newRecordFrame.add(p1);

						Panel errorMessages = new Panel();
						errorMessages.setLayout(new GridLayout(9, 1));
						int errorSempahore = 8;

						try {
							person.setLastname(fieldLastname.getText());
							fieldLastname.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldLastname.setBackground(Color.red);
						}

						try {
							person.setFirstname(fieldFirstname.getText());
							fieldFirstname.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldFirstname.setBackground(Color.red);
						}

						try {
							if (salutation.getSelectedCheckbox() == checkboxMale) {
								person.setSalutation("herr");
							} else if (salutation.getSelectedCheckbox() == checkboxFemale) {
								person.setSalutation("frau");
							} else {
								person.setSalutation("");
							}
							checkboxMale.setBackground(Color.green);
							checkboxFemale.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							checkboxMale.setBackground(Color.red);
							checkboxFemale.setBackground(Color.red);
						}

						try {
							person.setStreet(fieldStreet.getText());
							fieldStreet.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldStreet.setBackground(Color.red);
						}

						try {
							person.setZip(fieldZip.getText());
							fieldZip.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldZip.setBackground(Color.red);
						}

						try {
							person.setPlace(fieldPlace.getText());
							fieldPlace.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldPlace.setBackground(Color.red);
						}

						try {
							person.setPhone(fieldPhone.getText());
							fieldPhone.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldPhone.setBackground(Color.red);
						}

						try {
							person.setFax(fieldFax.getText());
							fieldFax.setBackground(Color.green);
							errorSempahore--;
						} catch (AdrException ex) {
							errorSempahore++;
							errorMessages.add(new Label(ex.getMessage()));
							fieldFax.setBackground(Color.red);
						}

						person.setRemark(fieldRemark.getText());
						fieldRemark.setBackground(Color.green);

						newRecordFrame.add(errorMessages);
						newRecordFrame.setVisible(true);

						if (errorSempahore == 0) {
							bufferObject(person);
							final Dialog newRecordDialog = new Dialog(
									newRecordFrame);
							newRecordDialog.setLayout(new GridLayout(3, 1));
							Label label = new Label(
									"Möchten Sie eine weitere Person aufnehmen?");
							newRecordDialog.add(label);
							Button yes = new Button("Ja");
							yes.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									newRecordFrame.dispose();
									newRecord();
								}
							});
							Button no = new Button("Nein");
							no.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									newRecordFrame.dispose();
								}
							});
							newRecordDialog.add(yes);
							newRecordDialog.add(no);
							newRecordDialog.setSize(300, 100);
							newRecordDialog.setVisible(true);
						}
					}
				});
				Button no = new Button("Nein");
				no.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						correctDialog.dispose();
					}
				});
				correctDialog.add(yes);
				correctDialog.add(no);
				correctDialog.setSize(300, 100);
				correctDialog.setVisible(true);
			}
		});

		Button cancelButton = new Button("Abbrechen");
		p1.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newRecordFrame.dispose();
			}
		});

		newRecordFrame.add(p1);

		newRecordFrame.setVisible(true);
		newRecordFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				newRecordFrame.dispose();
			}
		});
	}

	/**
	 * Die Methode sorgt für die Ausgabe aller Personen-Datensätze. Pro "Sicht"
	 * wird immer nur ein Datensatz dargestellt. Zur nächsten "Sicht" kann mit
	 * den entsprechenden "Weiter" und "Zurück" Buttons navigiert werden. Die je
	 * nach Situation eingeblendet oder ausgeblendet sind.
	 */
	private void printAllRecords() {
		if (bufferedObjects.size() == 0) {
			final Dialog noRecords = new Dialog(mainFrame);
			noRecords.setLayout(new FlowLayout());
			noRecords.add(new Label("Leider keine Datensätze vorhanden."));
			Button ok = new Button("OK");
			ok.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					noRecords.dispose();
				}
			});
			noRecords.add(ok);
			noRecords.setSize(300, 100);
			noRecords.setVisible(true);
		} else {
			class HelperContainer {
				int recordCounter = -1;

				public Panel getNextRecord(Panel record, boolean inc) {
					System.out.println("d: " + recordCounter);
					if (inc) {
						++recordCounter;
					} else {
						--recordCounter;
					}
					record.add(new Label("Name"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getLastname()));
					record.add(new Label("Vorname"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getFirstname()));
					record.add(new Label("Anrede"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getSalutation()));
					record.add(new Label("Strasse"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getStreet()));
					record.add(new Label("PLZ"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getZip()));
					record.add(new Label("Ort"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getPlace()));
					record.add(new Label("Telefon"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getPhone()));
					record.add(new Label("Fax"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getFax()));
					record.add(new Label("Bemerkung"));
					record.add(new Label(bufferedObjects.get(recordCounter)
							.getRemark()));
					return record;
				}
			}
			final HelperContainer recordPointer = new HelperContainer();

			listRecordFrame = new Frame("Alle Personen anzeigen");
			listRecordFrame.setSize(250, 250);
			listRecordFrame.setLayout(new GridLayout(2, 1));

			Panel record = new Panel();
			record.setLayout(new GridLayout(9, 1));
			recordPointer.getNextRecord(record, true);
			listRecordFrame.add(record);

			final Panel controls = new Panel();
			controls.setLayout(new FlowLayout());
			final Button forward = new Button("Weiter");
			if (bufferedObjects.size() > 1) {
				controls.add(forward);
			}
			final Button backward = new Button("Zurück");
			listRecordFrame.add(controls);

			forward.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Panel record = new Panel();
					record.setLayout(new GridLayout(9, 1));
					listRecordFrame.removeAll();
					listRecordFrame.add(recordPointer.getNextRecord(record,
							true));
					if (recordPointer.recordCounter + 1 < bufferedObjects
							.size()) {
						controls.add(forward);
						listRecordFrame.add(controls);
					} else {
						controls.remove(forward);
					}
					if (recordPointer.recordCounter > 0) {
						controls.add(backward);
						listRecordFrame.add(controls);
					} else {
						controls.remove(backward);
					}
					listRecordFrame.setVisible(true);
				}
			});

			backward.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Panel record = new Panel();
					record.setLayout(new GridLayout(9, 1));
					listRecordFrame.removeAll();
					listRecordFrame.add(recordPointer.getNextRecord(record,
							false));
					if (recordPointer.recordCounter + 1 < bufferedObjects
							.size()) {
						controls.add(forward);
						listRecordFrame.add(controls);
					} else {
						controls.remove(forward);
					}
					if (recordPointer.recordCounter > 0) {
						controls.add(backward);
						listRecordFrame.add(controls);
					} else {
						controls.remove(backward);
					}
					listRecordFrame.setVisible(true);
				}
			});

			listRecordFrame.setVisible(true);
			listRecordFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					listRecordFrame.dispose();
				}
			});
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
					bufferedObjects.add(p);
			} while (p != null);

			return true;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}

	public boolean isConnected() {
		try {
			if (readIn.readInt() == -1) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
