package main.java.transformation.rules.coarserules;

import behavior.AtomicRule;
import behavior.Pattern;
import main.java.Debugger;
import main.java.transformation.MyMaudeFactory;

public class AtomicRuleInstantaneousNotPeriodic extends CoarseRule {
	
	private AtomicRule rule;
	
	public AtomicRuleInstantaneousNotPeriodic(MyMaudeFactory maudeFact, AtomicRule rule) {
		super(maudeFact);
		this.rule = rule;
	}

	@Override
	public void transform() {
		/* NACs ? */
		for(Pattern nac : rule.getNacs()) {
			Debugger.debug("     NAC: " + nac.getName());
			res.addAll(new CreateNac(maudeFact, nac).get());
		}
	}

}
