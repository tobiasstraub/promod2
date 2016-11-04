

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Die Klasse Person ist die Modell Klasse (Fachklasse) und enthält die
 * eigentlichen Daten einer Person. Sie kann serialisiert werden.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 */
public class Person implements Serializable, Comparable<Person> {
	/**
	 * Versionsnummer für das Serialisieren bzw. Deserialisieren des Objektes
	 */
	private static final long serialVersionUID = -1786465931612057244L;
	/**
	 * Regulärer Ausdruck für a-z sowie Umlaute, Bindestriche und Whitespaces,
	 * mit einer Mindestlänge von 3 bis maximal 40 Zeichen
	 */
	public static final Pattern REGEX_CHARS_ONLY = Pattern
			.compile("[-a-z\\säöüß]{3,40}");
	/**
	 * Regulärer Ausdruck mit den Optionen herr oder frau
	 */
	public static final Pattern REGEX_SALUTATION = Pattern
			.compile("(herr|frau)");
	/**
	 * Regulärer Ausdruck für a-z sowie Umlaute, Bindestriche, Whitespaces und
	 * Zahlen von 0 bis 9, mit einer Mindestlänge von 3 bis maximal 30 Zeichen
	 */
	public static final Pattern REGEX_CHARS_DIGITS = Pattern
			.compile("[-a-z\\s\\däöüß]{3,30}");
	/**
	 * Regulärer Ausdruck für Zahlen von 1 bis 9 mit genau 5 Ziffern
	 */
	public static final Pattern REGEX_ZIP = Pattern
			.compile("[a-z]{1}-[1-9]{5}");
	/**
	 * Regulärer Ausdruck mit der Option +, 00 mit anschließendem zweiziffrigem
	 * Wert oder alternativ die Ziffer 0. Es folgen beliebig viele Ziffern,
	 * Whitespaces, Punkte und Schrägstriche.
	 */
	public static final Pattern REGEX_PHONE_FAX = Pattern
			.compile("(([+|00]\\d{2})|0)([.\\s\\d/]{4,})");
	/**
	 * firstname beinhaltet den erfolgreich Syntax überprüften (siehe:
	 * {@link #setFirstname(String)}) Vornamen einer Person
	 */
	private String firstname;
	/**
	 * lastname beinhaltet den erfolgreich Syntax überprüften (siehe:
	 * {@link #setLastname(String)}) Nachnamen einer Person
	 */
	private String lastname;
	/**
	 * salutation beinhaltet die erfolgreich Syntax überprüfte (siehe:
	 * {@link #setSalutation(String)}) Anrede einer Person
	 */
	private String salutation;
	/**
	 * street beinhaltet den erfolgreich Syntax überprüften (siehe:
	 * {@link #setSalutation(String)}) Straßennamen einer Person
	 */
	private String street;
	/**
	 * street beinhaltet die erfolgreich Syntax überprüfte (siehe:
	 * {@link #setStreet(String)}) Straße einer Person
	 */
	private String place;
	/**
	 * phone beinhaltet die erfolgreich Syntax überprüfte (siehe:
	 * {@link #setPhone(String)}) Telefonnummer einer Person
	 */
	private String phone;
	/**
	 * fax beinhaltet die erfolgreich Syntax überprüfte (siehe:
	 * {@link #setFax(String)}) Faxnummer einer Person
	 */
	private String fax;
	/**
	 * remark beinhaltet die Bemerkung zu einer Person
	 */
	private String remark;
	/**
	 * zip beinhaltet die erfolgreich Syntax überprüfte (siehe:
	 * {@link #setZip(String)}) Postleitzahl einer Person
	 */
	private String zip;

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #lastname} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Nachname der Person
	 */
	public String getLastname() {
		return splitAndFormat(lastname, "\\s|-");
	}

	/**
	 * Wandelt den zu setzenden Nachnamen zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft ihn dann entsprechend nach dem
	 * vorhandenem Pattern.
	 * 
	 * @param lastname
	 *            ist der zu setzende Nachname
	 * @throws AdrException
	 */
	public void setLastname(String lastname) throws AdrException {
		lastname = lastname.toLowerCase();
		if (Person.REGEX_CHARS_ONLY.matcher(lastname).matches()) {
			this.lastname = lastname;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Der Nachname muss zwischen drei und 40 Zeichen haben "
							+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #firstname} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Vorname der Person
	 */
	public String getFirstname() {
		return splitAndFormat(firstname, "\\s|-");
	}

	/**
	 * Wandelt den zu setzenden Vornamen zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft ihn dann entsprechend nach dem
	 * vorhandenem Pattern.
	 * 
	 * @param firstname
	 *            ist der zu setzende Vorname
	 * @throws AdrException
	 */
	public void setFirstname(String firstname) throws AdrException {
		firstname = firstname.toLowerCase();
		if (Person.REGEX_CHARS_ONLY.matcher(firstname).matches()) {
			this.firstname = firstname;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Der Vorname muss zwischen drei und 40 Zeichen haben "
							+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #salutation} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Anrede der Person
	 */
	public String getSalutation() {
		return splitAndFormat(salutation, "^");
	}

	/**
	 * Wandelt die zu setzende Anrede zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param salutation
	 *            ist die zu setzende Anrede
	 * @throws AdrException
	 */
	public void setSalutation(String salutation) throws AdrException {
		salutation = salutation.toLowerCase();
		if (Person.REGEX_SALUTATION.matcher(salutation).matches()) {
			this.salutation = salutation;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Die Anrede kann nur Herr oder Frau sein.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #lastname} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Nachname der Person
	 */
	public String getStreet() {
		return splitAndFormat(street, "\\s|-");
	}

	/**
	 * Wandelt die zu setzenden Straße zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param street
	 *            ist die zu setzende Straße
	 * @throws AdrException
	 */
	public void setStreet(String street) throws AdrException {
		street = street.toLowerCase();
		if (Person.REGEX_CHARS_DIGITS.matcher(street).matches()) {
			this.street = street;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Die Straße muss zwischen drei und 30 Zeichen haben "
							+ "und darf nur Buchstaben, Bindestriche, Leerzeichen und Zahlen enthalten.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #place} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Wohnort der Person
	 */
	public String getPlace() {
		return splitAndFormat(place, "\\s|-");
	}

	/**
	 * Wandelt den zu setzenden Wohnort zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param place
	 *            ist der zu setzende Wohnort
	 * @throws AdrException
	 */
	public void setPlace(String place) throws AdrException {
		place = place.toLowerCase();
		if (Person.REGEX_CHARS_ONLY.matcher(place).matches()) {
			this.place = place;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Der Ort muss zwischen drei und 40 Zeichen haben "
							+ "und darf nur Buchstaben, Bindestriche oder Leerzeichen enthalten.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #phone} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Telefonnummer der Person
	 */
	public String getPhone() {
		return splitAndFormat(phone, "^");
	}

	/**
	 * Wandelt die zu setzenden Telefonnummer zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param phone
	 *            ist die zu setzende Telefonnummer
	 * @throws AdrException
	 */
	public void setPhone(String phone) throws AdrException {
		phone = phone.toLowerCase();
		if (Person.REGEX_PHONE_FAX.matcher(phone).matches()) {
			this.phone = phone;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Die Telefonnummer muss mit einer Laendervorwahl starten"
							+ "oder alternativ mit der Ziffer 0. "
							+ "Sie muss mit mindestens einer weiteren Ziffer oder Punkt oder Leerzeichen oder Slash bestehen.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #fax} einer Person nach einem Pattern und gibt diesen entsprechend
	 * zurück
	 * 
	 * @return Faxnummer der Person
	 */
	public String getFax() {
		return splitAndFormat(fax, "^");
	}

	/**
	 * Wandelt die zu setzende Faxnummer zur besseren Syntaxüberprüfung in
	 * Kleinbuchstaben um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param fax
	 *            ist die zu setzende Faxnummer
	 * @throws AdrException
	 */
	public void setFax(String fax) throws AdrException {
		fax = fax.toLowerCase();
		if (Person.REGEX_PHONE_FAX.matcher(fax).matches()) {
			this.fax = fax;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Die Faxnummer muss mit einer Laendervorwahl starten"
							+ "oder alternativ mit der Ziffer 0. "
							+ "Sie muss mit mindestens einer weiteren Ziffer oder Punkt oder Leerzeichen oder Slash bestehen.");
		}
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #remark} einer Person nach einem Pattern und gibt diesen
	 * entsprechend zurück
	 * 
	 * @return Bemerkung zur Person
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * Gibt die Bemerkung zu einer Person zurück
	 * 
	 * @param remark
	 *            ist die zu setzende Bemerkung
	 * @throws AdrException
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * Formatiert (siehe: {@link #splitAndFormat(String, String)}) den
	 * {@link #zip} einer Person nach einem Pattern und gibt diesen entsprechend
	 * zurück
	 * 
	 * @return Postleitzahl der Person
	 */
	public String getZip() {
		return splitAndFormat(zip, "^");
	}

	/**
	 * Wandelt die zu setzenden Postleitzahl zur besseren Syntaxüberprüfung in
	 * einen String um und überprüft dann entsprechend nach dem vorhandenem
	 * Pattern.
	 * 
	 * @param zip
	 *            ist die zu setzende Postleitzahl
	 * @throws AdrException
	 */
	public void setZip(String zip) throws AdrException {
		zip = zip.toLowerCase();
		if (Person.REGEX_ZIP.matcher(zip).matches()) {
			this.zip = zip;
		} else {
			throw new AdrException(
					"Ups, da ging was schief. Die Postleitzahl muss aus genau 5 Ziffern bestehen und darf nicht mit Null beginnen.");
		}
	}

	/**
	 * Formatiert einen gegebenen String nach eingem gegbenen Pattern. Dabei
	 * wird der String nach dem Pattern entsprechend gesplittet und die ersten
	 * Buchstaben der gesplitteten Wörter werden wie im Deutschen üblich in
	 * Großbuchstaben umgewandelt.
	 * 
	 * @param s
	 *            Der zu formatierende String
	 * @param p
	 *            Regulärer Ausdruck, welcher die Regelung für die Splittung
	 *            festlegt
	 * @return Der korrekt formatierte String
	 */
	public String splitAndFormat(String s, String p) {
		String[] split = s.split(p);
		String temporary = "";
		for (int i = 0; i < split.length; i++) {
			temporary += Character.toUpperCase(split[i].charAt(0))
					+ split[i].substring(1) + " ";
		}
		return temporary;
	}

	/**
	 * Vergleicht den Nachnamen des aktuellen Obejekts mit dem Nachnamen des
	 * gegebenen Obejekts und ist so für die Sortierung zuständig.
	 */
	@Override
	public int compareTo(Person p) {
		return p.getLastname().compareTo(this.lastname);
	}
}
