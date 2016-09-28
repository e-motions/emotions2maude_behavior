package main.java.transformation.rules.smallrules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EReference;

import Maude.RecTerm;
import Maude.Term;
import behavior.Link;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.utils.MaudeOperators;

/**
 * This class is intended to generate the structural features of a behavior object given by the argument.
 * 
 * We are not following precisely this rule, instead, we return even just a variable in the case we do 
 * not need structural features.
 * 
 * The legacy ATL rule is the following:
 * <pre>
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
 * </pre>
 * 
 * 
 * ## AllObjectReferences helper
 * <pre>
 * helper def : AllObjectReferences(r:Sequence(Behavior!EReference),op:Sequence(Behavior!EReference)) : Sequence(Behavior!EReference) =
 *	 r->union(op)->asSet()->asSequence();
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
	
	public ObjectStructFeatLHS(MyMaudeFactory maudeFact, behavior.Object obj, Pattern pattern) {
		super(maudeFact);
		this.obj = obj;
		this.pattern = pattern;
	}

	@Override
	public void transform() {
		if (obj.getOutLinks().size() + obj.getSfs().size() == 0) {
			res = maudeFact.getVariableSFS(obj);
		} else {
			List<Term> sfsArgs = new ArrayList<>();
			/* 
			 * Links to references 
			 * 
			 */
			Map<EReference, List<Link>> references = mapRef2Links(obj.getOutLinks());
			System.out.println("Object: " + obj);
			System.out.println("References: " + references);
			for (EReference ref : references.keySet()) {
				// Links2RecTerm
				if (references.get(ref).size() == 1 && ref.getUpperBound() == -1 
						&& ref.isUnique() && ref.isOrdered()) {
					
					/* Creating the structural feature */
					RecTerm sf = maudeFact.createRecTerm(MaudeOperators.SF);
					sf.getArgs().add(maudeFact.getConstant(MaudeIdentifiers.get(ref)));
					
					/* OrderedSet 
					 * an example of the resulting Maude term is:
					 *   OrderedSet{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
					 */
					RecTerm orderedSet = maudeFact.createRecTerm(MaudeOperators.COLL_ORDERED_SET);
					
					Maude.Variable targetId = maudeFact.getVariableOCLType(references.get(ref).get(0).getTarget().getId());
					/* head of the list */
					Maude.Variable headList = maudeFact.getVariableOrderedLists(ref.getName() + "@Head");
					/* tail of the list */
					Maude.Variable tailList = maudeFact.getVariableOrderedLists(ref.getName() + "@Tail");
					
					orderedSet.getArgs().addAll(Arrays.asList(headList, targetId, tailList));
					
					sf.getArgs().add(orderedSet);
					
					sfsArgs.add(sf);
				}
			}
			/*
			 * Slots
			 */
			
			
			sfsArgs.add(maudeFact.getVariableSFS(obj));
			res = maudeFact.createStructuralFeatureSet(sfsArgs);
		}
	}
	
	/**
	 * Given a list of behavior links, it returns a Map association EReferences to Links.
	 * 
	 * The ATL code is:
	 * <pre>
	 * helper def : RefWithoutDuplicates( inCollection : Sequence(Behavior!Link) ) : Sequence(Behavior!EReference) =
	 *	inCollection -> iterate(e; outCollection : Sequence(Behavior!EReference) = Sequence{} |
	 *		if outCollection ->one(i|i=e.ref) then outCollection
	 *		else outCollection ->append(e.ref)
	 *		endif);
	 * </pre>
	 * 
	 * @param links
	 * @return map of EReferences
	 */
	private Map<EReference, List<Link>> mapRef2Links(List<Link> links) {
		Set<EReference> refs = links.stream().map(r -> (EReference) r.getRef()).collect(Collectors.toSet());
		Map<EReference, List<Link>> res = new HashMap<EReference, List<Link>>();
		for (EReference r : refs) {
			res.put(r, links.stream().filter(ref -> (ref.getRef() == r)).collect(Collectors.toList()));
		}
		return res;
	}

}
