/**
 * 
 * AdrException wird beim Werfen der Exception ausfegührt.
 * 
 * Die Fehlerbehandlung wird jeweils direkt beim Werfen der Exception behandelt,
 * so dass die hier ankommende Meldung nur als "Protokoll" dient und mit super
 * an die übergeordnete Instanz übergeben wird.
 * 
 * @author Kponvi Komlan, Thomas Berblinger, Tobias Straub
 * @version 1.0
 * 
 */

public class AdrException extends Exception {
	AdrException(String s) {
		super(s);
	}
}
