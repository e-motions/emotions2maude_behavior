package main.java.transformation.rules.smallrules;

import java.util.List;

import Maude.RecTerm;
import behavior.ActionExec;
import main.java.transformation.MyMaudeFactory;

/**
 * 
 * Creates a set of Oid variables.
 * 
 * Used by the first argument in the NACs {@link #CreateNac}.
 * 
 * lazy rule ManyPatternElOid {
 * 	from
 *		r : Behavior!Rule
 *	to
 *		participantsRT : Maude!RecTerm(
 *			op <- thisModule.mSetOperator, --thisModule.setOperator, -- '_`,_'
 *			type <- thisModule.mSetSort, --thisModule.listSort, 
 *			args <- r.objActExecLHSRule()->collect(e|thisModule.PatternElOid(e))
 *		)
 *  }
 *  
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class ManyPatternElOid extends Rule {
	
	private List<behavior.Object> objects;
	private List<ActionExec> actions;

	public ManyPatternElOid(MyMaudeFactory maudeFact, List<behavior.Object> objects, List<ActionExec> actions) {
		super(maudeFact);
		this.objects = objects;
		this.actions = actions;
	}

	@Override
	public void transform() {
		res = maudeFact.createRecTerm("_`,_");
		for(behavior.Object o : objects) {
			((RecTerm) res).getArgs().add(new PatternElOid(maudeFact, o).get());
		}
		for(ActionExec a : actions) {
			((RecTerm) res).getArgs().add(new PatternElOid(maudeFact, a).get());
		}
	}

}
