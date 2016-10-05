package main.java.transformation.rules.smallrules.nac;

import java.util.List;
import java.util.stream.Collectors;

import Maude.RecTerm;
import behavior.ActionExec;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.rules.smallrules.ManyPatternElOid;
import main.java.transformation.rules.smallrules.PatternElOid;
import main.java.transformation.rules.smallrules.Rule;

public class FirstArgumentNAC extends Rule {
	
	private Pattern nac;

	public FirstArgumentNAC(MyMaudeFactory maudeFact, Pattern nac) {
		super(maudeFact);
		this.nac = nac;
	}

	@Override
	public void transform() {
		RecTerm set = maudeFact.createRecTerm("Set`{_`}"); 
		List<behavior.Object> objects = nac.getRule().getLhs()
				.getEls().stream()
				.filter(x -> x instanceof behavior.Object)
				.map(o -> (behavior.Object) o)
				.collect(Collectors.toList());
		List<ActionExec> actions = nac.getRule().getLhs()
				.getEls().stream()
				.filter(x -> x instanceof ActionExec)
				.map(o -> (ActionExec) o)
				.collect(Collectors.toList());
		if(objects.isEmpty() && actions.isEmpty()) {
			set.getArgs().add(maudeFact.getConstantEmpty());
		} else if(objects.isEmpty() && actions.size() == 1) {
			set.getArgs().add(new PatternElOid(maudeFact, actions.get(0)).get());
		} else if(objects.size() == 1 && actions.isEmpty()) {
			set.getArgs().add(new PatternElOid(maudeFact, objects.get(0)).get());
		} else {
			set.getArgs().add(new ManyPatternElOid(maudeFact, objects, actions).get());
		}
		
		res = set;
	}
	
	

}
