package main.java.transformation.rules.coarserules;

import java.util.LinkedList;
import java.util.List;

import Maude.ModElement;
import main.java.transformation.MyMaudeFactory;

/**
 * Explain why ModElements.
 * 
 * Requisites for calling to this class.
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public abstract class CoarseRule {
	
	List<ModElement> res;
	MyMaudeFactory maudeFact;
	
	public CoarseRule(MyMaudeFactory maudeFact) {
		res = new LinkedList<ModElement>();
		this.maudeFact = maudeFact;
	}

	public abstract void transform();
	
	public List<ModElement> get() {
		if (res.size() == 0) 
			transform();
		return res;
	}
	

}
