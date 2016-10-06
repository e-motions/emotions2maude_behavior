package main.java.transformation.rules.coarserules;

import behavior.AtomicRule;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.rules.smallrules.ModelLHS;
import main.java.transformation.rules.smallrules.ModelRHS;

public class CreateInstantaneousRl extends CoarseRule {

	private AtomicRule behRule;
	
	public CreateInstantaneousRl(MyMaudeFactory maudeFact, AtomicRule rule) {
		super(maudeFact);
		behRule = rule;
	}

	@Override
	public void transform() {
		String ruleLabel = MaudeIdentifiers.processSpecialChars(behRule.getName()) + "@Instantaneous";
		Maude.Rule maudeRule = maudeFact.createRule(ruleLabel);
		maudeRule.setLhs(new ModelLHS(maudeFact, behRule.getLhs())
				.withClock()
				.withIdCounter()
				.withRandomCounter().get());
		maudeRule.setRhs(new ModelRHS(maudeFact, behRule).get());
		
		res.add(maudeRule);
	}

}
