package main.java.transformation.rules.smallrules;

import java.util.ArrayList;
import java.util.List;

import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;

public class CreateSetVariables extends Rule {
	
	List<behavior.Variable> vbles;
	
	private RecTerm setVariables;
	private int counter;
	
	/**
	 * lazy rule CreateSetVar{
		from
			r : Behavior!AtomicRule
		to
			rt : Maude!RecTerm(
				op <- thisModule.objSetOperator,
				type <- thisModule.sortSetObject
				)
		do{
			for (p in r.vbles){
				thisModule.counterVbles <- thisModule.counterVbles +1;
				rt.args <- rt.args -> append(thisModule.CreateVar(p,thisModule.counterVbles));
			}
			thisModule.counterVbles <- 0;
		}
	}
	 */
	public CreateSetVariables(MyMaudeFactory maudeFact, List<behavior.Variable> vbles) {
		super(maudeFact);
		counter = 1;
		this.vbles = new ArrayList<>();
		this.vbles.addAll(vbles);
	}

	@Override
	public void transform() {
		setVariables = maudeFact.createRecTerm(MaudeOperators.SET);
		for(behavior.Variable var : vbles) {
			setVariables.getArgs().add(new CreateVariable(maudeFact, var, counter++).get());
		}
	}

	@Override
	public RecTerm get() {
		if(setVariables == null)
			transform();
		return setVariables;
	}

}
