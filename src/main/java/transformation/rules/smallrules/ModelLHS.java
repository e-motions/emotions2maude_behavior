package main.java.transformation.rules.smallrules;

import Maude.RecTerm;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;

public class ModelLHS extends Rule {
	
	private Pattern lhs;

	public ModelLHS(MyMaudeFactory maudeFact, Pattern lhs) {
		super(maudeFact);
		this.lhs = lhs;
	}

	@Override
	public void transform() {
		RecTerm model = maudeFact.createRecTerm(MaudeOperators.MODEL);
		Maude.Variable mm = maudeFact.getVariableMM();

		model.getArgs().add(mm);
		
		if (lhs.getEls().isEmpty()) {
			model.getArgs().add(maudeFact.getVariableObjectSet());
		}
		
		res = model;
	}

}
