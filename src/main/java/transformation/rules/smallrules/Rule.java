package main.java.transformation.rules.smallrules;

import Maude.Term;
import main.java.transformation.MyMaudeFactory;

public abstract class Rule {
	
	protected MyMaudeFactory maudeFact;
	protected Term res;
	
	public Rule(MyMaudeFactory maudeFact) {
		this.maudeFact = maudeFact;
	}
	
	public abstract void transform();
	
	public Term get() {
		if (res == null)
			transform();
		return res;
	}
}
