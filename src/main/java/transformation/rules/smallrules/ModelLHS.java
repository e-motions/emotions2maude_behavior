package main.java.transformation.rules.smallrules;

import Maude.RecTerm;
import Maude.Variable;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;

/**
 * Creates a pattern for the LHS of a *mte* or *rule*.
 * 
 * @author Antonio Moreno-Delgado <amoreno@lcc.uma.es>
 *
 */
public class ModelLHS extends Rule {
	
	private Pattern lhs;
	private RecTerm model;

	public ModelLHS(MyMaudeFactory maudeFact, Pattern lhs) {
		super(maudeFact);
		this.lhs = lhs;
	}

	@Override
	public void transform() {
		model = maudeFact.createRecTerm(MaudeOperators.MODEL);
		Maude.Variable mm = maudeFact.getVariableMM();

		model.getArgs().add(mm);
		
		if (lhs.getEls().isEmpty()) {
			model.getArgs().add(maudeFact.getVariableObjectSet());
		} else {
			// TODO to be changed
			model.getArgs().add(maudeFact.getVariableObjectSet());
		}
		
		res = model;
	}
	
	/**
	 * Creates a clock object.
	 * @return
	 */
	public ModelLHS withClock() {
		/* model and res are the same */
		if (model == null) {
			transform();
		}
		RecTerm set;
		/* the term of the model is just the objset variable? */
		if (model.getArgs().get(1) instanceof Maude.Variable) {
			set = maudeFact.createRecTerm(MaudeOperators.SET);
			set.getArgs().add(model.getArgs().get(1));
			model.getArgs().add(set);
		} else {
			set = (RecTerm) model.getArgs().get(1);
		}
		
		set.getArgs().add(new ClockObject(maudeFact).get());
		
		objsetVariableToTheEnd();
		
		return this;
	}
	
	/**
	 * Creates a id counter object.
	 * @return
	 */
	public ModelLHS withIdCounter() {
		/* model and res are the same */
		if (model == null) {
			transform();
		}
		RecTerm set;
		/* the term of the model is just the objset variable? */
		if (model.getArgs().get(1) instanceof Maude.Variable) {
			set = maudeFact.createRecTerm(MaudeOperators.SET);
			set.getArgs().add(model.getArgs().get(1));
			model.getArgs().add(set);
		} else {
			set = (RecTerm) model.getArgs().get(1);
		}
		
		set.getArgs().add(new ClockObject(maudeFact).get());
		
		objsetVariableToTheEnd();
		
		return this;
	}
	
	/**
	 * it swaps the positions of the arguments inside the model to set the objset variable
	 * to the end.
	 */
	private void objsetVariableToTheEnd() {
		RecTerm set = (RecTerm) model.getArgs().get(1);
		Maude.Variable objset = (Variable) set.getArgs().stream()
				.filter(e -> e instanceof Maude.Variable && ((Variable) e).getName().equals("OBJSET@")).findFirst().get();
		set.getArgs().remove(set.getArgs().indexOf(objset));
		set.getArgs().add(objset);
	}

}
