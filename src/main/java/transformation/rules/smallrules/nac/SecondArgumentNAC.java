package main.java.transformation.rules.smallrules.nac;

import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.rules.smallrules.CreateSetVariables;
import main.java.transformation.rules.smallrules.CreateVariable;
import main.java.transformation.rules.smallrules.Rule;

public class SecondArgumentNAC extends Rule {

	private Pattern nac;
	
	public SecondArgumentNAC(MyMaudeFactory maudeFact, Pattern nac) {
		super(maudeFact);
		this.nac = nac;
	}

	@Override
	public void transform() {
		if(nac.getRule().getVbles().isEmpty()) {
			/* thisModule.VariableEmpty('') */
			res = maudeFact.getConstantNone();
		} else if(nac.getRule().getVbles().size() == 1) {
			/* thisModule.CreateVar(n."rule".vbles -> first(),1) */
			res = new CreateVariable(maudeFact, nac.getRule().getVbles().get(0), 1).get();
		} else {
			/* thisModule.CreateSetVar(n."rule") */
			res = new CreateSetVariables(maudeFact, nac.getRule().getVbles()).get();
		}
	}

}
