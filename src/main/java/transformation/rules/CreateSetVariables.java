package main.java.transformation.rules;

import java.util.ArrayList;
import java.util.List;

import Maude.RecTerm;
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
	public CreateSetVariables(List<behavior.Variable> vbles) {
		super();
		counter = 1;
		this.vbles = new ArrayList<>();
		this.vbles.addAll(vbles);
	}

	@Override
	public void transform() {
		setVariables = _maudeFact.createRecTerm(MaudeOperators.SET);
		for(behavior.Variable var : vbles) {
			setVariables.getArgs().add(new CreateVariable(var, counter++).get());
		}
	}

	@Override
	public RecTerm get() {
		if(setVariables == null)
			transform();
		return setVariables;
	}

}
