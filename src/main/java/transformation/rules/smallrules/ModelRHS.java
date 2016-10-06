package main.java.transformation.rules.smallrules;

import java.util.List;
import java.util.stream.Collectors;

import Maude.RecTerm;
import behavior.AtomicRule;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;



public class ModelRHS extends Rule {

	private AtomicRule behRule;
	
	private List<behavior.Object> lhsObjects;
	private List<behavior.Object> rhsObjects;
	private List<String> lhsIds;
	private List<String> rhsIds;
	
	public ModelRHS(MyMaudeFactory maudeFact, AtomicRule behRule) {
		super(maudeFact);
		this.behRule = behRule;
		
		/* init */
		lhsObjects = behRule.getLhs().getEls().stream().filter(e -> e instanceof behavior.Object)
				.map(ob -> (behavior.Object) ob).collect(Collectors.toList());
		rhsObjects = behRule.getRhs().getEls().stream().filter(e -> e instanceof behavior.Object)
				.map(ob -> (behavior.Object) ob).collect(Collectors.toList());
		lhsIds = lhsObjects.parallelStream().map(o -> o.getId()).collect(Collectors.toList());
		rhsIds = rhsObjects.parallelStream().map(o -> o.getId()).collect(Collectors.toList());
	}

	@Override
	public void transform() {
		if (notDeletedObjects()) {
			// && notDeletedLinks() TODO
			/* we do not need readjust operator */
			RecTerm model = maudeFact.createRecTerm(MaudeOperators.MODEL);
			Maude.Variable mm = maudeFact.getVariableMM();

			model.getArgs().add(mm);
			
			/* set of objects, is a set because at least there are the
			 * clock and the OBJSET variable
			 */
			
			RecTerm objset = maudeFact.createRecTerm(MaudeOperators.SET);
			model.getArgs().add(objset);
			
			/*
			 *  New objects. New objects are those which are present in the RHS
			 *  and not in the LHS.
			 */
			List<behavior.Object> newObjects = rhsObjects.parallelStream()
					.filter(nO -> !lhsIds.contains(nO.getId())).collect(Collectors.toList());
			List<RecTerm> maudeNewObjects = newObjects.parallelStream()
					.map(obj -> (RecTerm) new NewObjectRHS(maudeFact, obj).get())
					.collect(Collectors.toList());
			objset.getArgs().addAll(maudeNewObjects);

			
			objset.getArgs().add(new ClockObject(maudeFact).get());
			
			objset.getArgs().add(maudeFact.getVariableObjectSet());
			
			res = model;
		} else {
			/* readjust operator */
		}
	}

	private boolean notDeletedObjects() {
		List<String> lhsIds = lhsObjects.parallelStream().map(o -> o.getId()).collect(Collectors.toList());
		List<String> rhsIds = rhsObjects.parallelStream().map(o -> o.getId()).collect(Collectors.toList());
		return lhsIds.isEmpty() || lhsIds.stream().anyMatch(id -> !rhsIds.contains(id));
	}

}
