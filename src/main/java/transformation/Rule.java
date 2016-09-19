package main.java.transformation;

import Maude.Term;

public abstract class Rule {
	
	MyMaudeFactory _maudeFact;
	
	public Rule() {
		_maudeFact = MyMaudeFactory.getDefault();
	}
	
	public abstract void transform();
	public abstract Term get();
}
