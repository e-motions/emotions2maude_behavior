package main.java.transformation.rules.smallrules;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EReference;

import Maude.RecTerm;
import Maude.Term;
import behavior.Link;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;

/**
 * This rule is intended to create a structural feature for a given EReference.
 * The original rule in ATL is:
 * <pre>
 *	 lazy rule Links2RecTerm{
 *		from		
 *			linkRef : Behavior!EReference,
 *			objId : String
 *		to
 *			ref : Maude!RecTerm(
 *				op <- thisModule.sfsOperator, -- '_:_'
 *				type <- thisModule.sortRefInst,
 *				args <- Sequence{constSF,varSF}
 *				),
 *			constSF : Maude!Constant(
 *				op <-  linkRef.maudeName().processSpecOpChars(),
 *				type <- thisModule.sortRefSimple		
 *				),
 *			varSF : Maude!Variable(
 *				name <- linkRef.name.toUpper().processSpecOpChars()+'@'+objId+'@ATT', --link.target.id, --.toUpper() + '@' + link.src.id + '@ATT' ,
 *				type <- thisModule.oclTypeSort --thisModule.collectionSort  
 *				)
 *	}
 * </pre>
 * 
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class Link2Term extends Rule {
	
	private EReference ref;
	private List<Link> links;
	private String objOid;

	public Link2Term(MyMaudeFactory maudeFact, EReference ref, List<Link> links, String objOid) {
		super(maudeFact);
		this.ref = ref;
		this.links = links;
		this.objOid = objOid;
	}

	@Override
	public void transform() {
		Term consequent;
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
				if (ref.isOrdered()) {
					// List
					consequent = maudeFact.createRecTerm(MaudeOperators.COLL_SEQUENCE);
				} else {
					// OrderedSet
					consequent = maudeFact.createRecTerm(MaudeOperators.COLL_ORDERED_SET);
				}
				 
				Maude.Variable initList = maudeFact.getVariableOrderedLists(objOid + "List1");
				Maude.Variable endList = maudeFact.getVariableOrderedLists(objOid + "List2");
				Maude.Variable targetId = maudeFact.getVariableOCLType(links.get(0).getTarget().getId());
				
				RecTerm content = maudeFact.createRecTerm(MaudeOperators.ORDERED_LIST_SEPARATOR);
				content.getArgs().addAll(Arrays.asList(initList, targetId, endList));
				
				((RecTerm) consequent).getArgs().add(content);
				
			} else if (!ref.isOrdered()) {
				if (!ref.isUnique()) {
					// Bag
					consequent = maudeFact.createRecTerm(MaudeOperators.COLL_BAG);
				} else {
					// Set
					consequent = maudeFact.createRecTerm(MaudeOperators.COLL_SET);
				}
				
				RecTerm content = maudeFact.createRecTerm(MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
				for (Link l : links) {
					content.getArgs().add(maudeFact.getVariableOCLType(l.getTarget().getId()));
				}
				content.getArgs().add(maudeFact.getVariableMSet(objOid + "MList"));
				((RecTerm) consequent).getArgs().add(content);
			} else {
				/* it should be done as in the legacy transformation */
			}
		}

	}
}
