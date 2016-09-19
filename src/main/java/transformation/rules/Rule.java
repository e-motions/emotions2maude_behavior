package main.java.transformation.rules;

import Maude.Term;
import main.java.transformation.MyMaudeFactory;

public abstract class Rule {
	
	MyMaudeFactory _maudeFact;
	private Term res;
	
	public Rule() {
		_maudeFact = MyMaudeFactory.getDefault();
	}
	
	public abstract void transform();
	
	public Term get() {
		if (res == null)
			transform();
		return res;
	}
}
