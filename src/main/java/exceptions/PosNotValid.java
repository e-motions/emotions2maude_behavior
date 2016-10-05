package main.java.exceptions;

/**
 * It is thrown if there exists a behavior.Link with the <code>pos</code> with any value and 
 * the reference associated to the link is an unordered reference.
 * 
 * @author Antonio Moreno-Delgado <amoreno@lcc.uma.es>
 *
 */
@SuppressWarnings("serial")
public class PosNotValid extends RuntimeException {
	
	public PosNotValid() { super(); }
	public PosNotValid(String msg) { super(msg); }
}
