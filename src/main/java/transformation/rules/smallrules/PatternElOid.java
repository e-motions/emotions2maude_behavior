package main.java.transformation.rules.smallrules;

import behavior.ActionExec;
import behavior.PatternEl;
import main.java.exceptions.NotValidArgumentsE2MException;
import main.java.transformation.MyMaudeFactory;

/**
 * It creates variable for a identifier Oid.
 * 
 * Used by {@link #CreateNac}.
 * 
 *	lazy rule PatternElOid{
 *	from
 *		o : Behavior!PatternEl	
 *	to
 *		d : Maude!Variable(
 *			name <- o.id,
 *			type <- thisModule.oclTypeSort
 *		)
 *	}
 * 
 * @precondition It only supports Objects and ActionExec
 * 
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class PatternElOid extends Rule {
	
	private PatternEl pattern;
	
	public PatternElOid(MyMaudeFactory maudeFact, PatternEl pattern) {
		super(maudeFact);
		this.pattern = pattern;
	}

	@Override
	public void transform() {
		if (pattern instanceof behavior.Object) {
			res = maudeFact.getVariableOCLType(((behavior.Object) pattern).getId());
		} else if (pattern instanceof ActionExec) {
			res = maudeFact.getVariableOCLType(((ActionExec) pattern).getId());
		} else {
			throw new NotValidArgumentsE2MException("Rule PatternElOid only supports Objects and ActionExecs");
		}
	}

}
