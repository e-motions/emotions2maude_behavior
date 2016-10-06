package main.java.transformation.common;

import Maude.MaudeFactory;
import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;

public class CopyRecTerm {
	
	private MyMaudeFactory maudeFact;
	
	private RecTerm source;
	private RecTerm target;
	
	public CopyRecTerm(MyMaudeFactory maudeFact, RecTerm source) {
		this.maudeFact = maudeFact;
		this.source = source;
	}
	
	private void transform() {
		//target = maudeFact.createRecTerm(op)
	}
	
	public RecTerm get() {
		if (target == null)
			transform();
		return target;
	}
}
