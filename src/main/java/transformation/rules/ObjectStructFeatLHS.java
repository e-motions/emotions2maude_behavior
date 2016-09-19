package main.java.transformation.rules;

import behavior.Pattern;
import main.java.exceptions.NotValidArgumentsE2MException;

/**
 * This class is intended to generate the structural features of a behavior object given by the argument.
 * 
 * The ATL rule is the following:
 * <pre>
 * 
 * lazy rule ObjectArgmsLHS{
 *	from
 *		p : Behavior!Pattern,
 *		obj : Behavior!Object,
 *		sfeat : Maude!Variable
 *	to
 *		sfi : Maude!RecTerm(
 *			op <- thisModule.featOperator, --thisModule.setOperator, -- '_`,_'
 *			type <- thisModule.sortSetSfi,
 *			args <-	
 *				thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())
 *					->collect(r|thisModule.Links2RecTerm(r,obj.id))
 *					->union(obj.sfs -> collect(s|thisModule.Slots2RecTerm(s)))
 *	 				->append(sfeat)
 *			)
 *  }
 * 
 * </pre>
 * 
 * @precondition The object has at least one *needed* structural feature. By needed we mean:
 * 	- out link
 *  - slot
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class ObjectStructFeatLHS extends Rule {

	private behavior.Object obj;
	private Pattern pattern;
	
	public ObjectStructFeatLHS(behavior.Object obj, Pattern pattern) {
		this.obj = obj;
		if (obj.getOutLinks().isEmpty() && obj.getSfs().isEmpty())
			throw new NotValidArgumentsE2MException("Structural features not needed for object with id " + obj.getId());
		this.pattern = pattern;
	}

	@Override
	public void transform() {
		/* 
		 * Links to references 
		 * 
		 */
		
	}

}
