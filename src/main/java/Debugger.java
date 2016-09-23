package main.java;

public final class Debugger {

	public Debugger() {
	}
	
	/**
	 * Prints to the PrintWriter given as parameter the string 'string'
	 * @param string
	 */
	public static void debug(String message) {
		System.out.println(" - " + message);
	}

}
