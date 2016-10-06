package main.java.exceptions;

@SuppressWarnings("serial")
public class EmotionsNotCompletedModel extends RuntimeException {
	public EmotionsNotCompletedModel(String msg) {
		super(msg);
	}
}
