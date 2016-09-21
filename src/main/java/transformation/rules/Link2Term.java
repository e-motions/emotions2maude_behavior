package main.java.transformation.rules;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EReference;

import Maude.Constant;
import Maude.RecTerm;
import behavior.Link;
import main.java.transformation.utils.MaudeOperators;

/**
 * This rule is intended to create a structural feature for a given EReference.
 * The original rule in ATL is:
 * <pre>
 * lazy rule ObjectArgmsLHS{
 *	from
 *		p : Behavior!Pattern,
 *		obj : Behavior!Object,
 *		sfeat : Maude!Variable
 *	to
 *		sfi : Maude!RecTerm(
 *			op <- thisModule.featOperator,
 *			type <- thisModule.sortSetSfi,
 *			args <-	
 *				thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())
 *					->collect(r|thisModule.Links2RecTerm(r,obj.id))
 *					->union(obj.sfs -> collect(s|thisModule.Slots2RecTerm(s)))
 *	 				->append(sfeat)
 *			)
 *  }
 * </pre>
 * 
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class Link2Term extends Rule {
	
	private EReference ref;
	private List<Link> links;
	private String objOid;

	public Link2Term(EReference ref, List<Link> links, String objOid) {
		this.ref = ref;
		this.links = links;
		this.objOid = objOid;
	}

	@Override
	public void transform() {
		/*
		 * Kinds of relations: - unitary - collections - ordered - unique
		 */
		if (ref.getUpperBound() == 1) {
			// it is unique and its the simplest thing

		} else {
			// it is a collection
			/*
			 * Steps to perform this: - check the kind of collection - check the
			 * number of links for each reference
			 *
			 * if the size is greater than 1, it cannot be set in the matching
			 */
			if (ref.isOrdered() && !ref.isUnique() && links.size() == 1 && links.get(0).getPos().equals("")) {
				// List
				res = _maudeFact.createRecTerm(MaudeOperators.COLL_SEQUENCE);
				Maude.Variable initList = _maudeFact.getVariableSequence(objOid + "1");
				Maude.Variable endList = _maudeFact.getVariableSequence(objOid + "2");
				Constant targetId = _maudeFact.getConstant(links.get(0).getTarget().getId());
				
				RecTerm seqContent = _maudeFact.createRecTerm(MaudeOperators.ORDERED_LIST);
				seqContent.getArgs().addAll(Arrays.asList(initList, targetId, endList));
				
				((RecTerm) res).getArgs().add(seqContent);
				
			} else if (ref.isOrdered() && ref.isUnique() && links.size() == 1) {
				// OrderedSet
				/*
				 * if the size is greater than 1, it cannot be set in the
				 * matching
				 */
				res = _maudeFact.createRecTerm(MaudeOperators.COLL_ORDERED_SET);
				
			} else if (!ref.isOrdered() && !ref.isUnique()) {
				// Bag
				res = _maudeFact.createRecTerm(MaudeOperators.COLL_BAG);
				
			} else if (!ref.isOrdered() && !ref.isUnique()) {
				// Set
				res = _maudeFact.createRecTerm(MaudeOperators.COLL_SET);
				
			} else {
				/* it should be done as in the legacy transformation */
			}
		}

	}

}
