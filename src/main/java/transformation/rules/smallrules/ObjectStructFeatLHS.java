package main.java.transformation.rules.smallrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;

import Maude.RecTerm;
import Maude.Term;
import behavior.Link;
import behavior.Slot;
import main.java.exceptions.EmotionsNotCompletedModel;
import main.java.exceptions.PosNotValid;
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
 *  Link2Term rule
 *<pre>
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
 * The rule `Slots2RecTerm` here coded is:
 * <pre>
 * lazy rule Slots2RecTerm{
 *	from
 *		slot : Behavior!Slot
 *	to
 *		stft : Maude!RecTerm(
 *			op <- thisModule.sfsOperator, -- '_:_'
 *			type <- slot.sf.TypeOfInstance(),
 *			args <- Sequence{constIzq,constDer}
 *			),
 *		constIzq : Maude!Constant(
 *			op <-  slot.sf.maudeName().processSpecOpChars(),--slot.sf.name,
 *			type <- slot.sf.slotType()			
 *			),
 *		constDer : Maude!Variable(
 *			name <- slot.sf.name.toUpper().processSpecOpChars() + '@' + slot.object.id + '@ATT',
 *			type <- thisModule.oclTypeSort--slot.sf.structFeatType() 
 *		)
 * }
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
	
	public ObjectStructFeatLHS(MyMaudeFactory maudeFact, behavior.Object obj) {
		super(maudeFact);
		this.obj = obj;
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
			for (EReference ref : references.keySet()) {
				// Links2RecTerm
				
				/* Creating the structural feature */
				RecTerm sf = maudeFact.createRecTerm(MaudeOperators.SF);
				sf.getArgs().add(maudeFact.getConstant(MaudeIdentifiers.get(ref)));
				
				if (references.get(ref).size() == 1 && posNotSet(references.get(ref).get(0))
						&& ref.getUpperBound() == -1 
						&& ref.isUnique() && ref.isOrdered()) {
					/*
					 * OrderedSet
					 *
					 * an example of the resulting Maude term is:
					 *   OrderedSet{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
					 */
					RecTerm orderedSet = maudeFact.createRecTerm(MaudeOperators.COLL_ORDERED_SET);
					
					Maude.Variable targetId = maudeFact.getVariableOCLType(references.get(ref).get(0).getTarget().getId());
					/* head of the list */
					Maude.Variable headList = maudeFact.getVariableOrderedLists(ref.getName() + "@Head");
					/* tail of the list */
					Maude.Variable tailList = maudeFact.getVariableOrderedLists(ref.getName() + "@Tail");
					
					orderedSet.getArgs().add(maudeFact.createOrderedList(headList, targetId, tailList));
					
					sf.getArgs().add(orderedSet);
					
					
				} else if (references.get(ref).size() == 1  && posNotSet(references.get(ref).get(0))
						&& ref.getUpperBound() == -1
						&& !ref.isUnique() && ref.isOrdered()) {
					/*
					 * Sequence
					 *
					 * an example of the resulting Maude term is:
					 *   Sequence{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
					 */
					RecTerm sequence = maudeFact.createRecTerm(MaudeOperators.COLL_SEQUENCE);
					
					Maude.Variable targetId = maudeFact.getVariableOCLType(references.get(ref).get(0).getTarget().getId());
					/* head of the list */
					Maude.Variable headList = maudeFact.getVariableOrderedLists(ref.getName() + "@Head");
					/* tail of the list */
					Maude.Variable tailList = maudeFact.getVariableOrderedLists(ref.getName() + "@Tail");
					
					sequence.getArgs().add(maudeFact.createOrderedList(headList, targetId, tailList));
					
					sf.getArgs().add(sequence);
				} else if (!ref.isOrdered() && ref.isUnique()) {
					/*
					 * Set
					 * 
					 * an example of the resulting Maude term is:
					 * 	Set{ lin1:OCL-Type ; link2:OCL-Type ; remainings:MSet{OCL-Exp}}
					 * 
					 */
					if(references.get(ref).stream().anyMatch(l -> !posNotSet(l))) {
						throw new PosNotValid("Link whose reference is " + ref.getName() 
							+ " cannot have pos. In rule " + obj.getPattern().getRule().getName() + ".");
					}
					RecTerm set = maudeFact.createRecTerm(MaudeOperators.COLL_SET);
					RecTerm mset = maudeFact.createRecTerm(MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
					for (Link l : references.get(ref)) {
						mset.getArgs().add(maudeFact.getVariableOCLType(l.getTarget().getId()));
					}
					mset.getArgs().add(maudeFact.getVariableNotOrderedLists(ref.getName() + "@Remainings"));
					set.getArgs().add(mset);
					sf.getArgs().add(set);
				} else if (!ref.isOrdered() && !ref.isUnique()) {
					/*
					 * 
					 * Bag
					 * 
					 * an example of the resulting Maude term is:
					 *  Bag{ lin1:OCL-Type ; link2:OCL-Type ; remainings:MSet{OCL-Exp}}
					 */
					if(references.get(ref).stream().anyMatch(l -> !posNotSet(l))) {
						throw new PosNotValid("Link whose reference is " + ref.getName() 
							+ " cannot have pos. In rule " + obj.getPattern().getRule().getName() + ".");
					}
					RecTerm bag = maudeFact.createRecTerm(MaudeOperators.COLL_BAG);
					RecTerm mset = maudeFact.createRecTerm(MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
					for (Link l : references.get(ref)) {
						mset.getArgs().add(maudeFact.getVariableOCLType(l.getTarget().getId()));
					}
					mset.getArgs().add(maudeFact.getVariableNotOrderedLists(ref.getName() + "@Remainings"));
					bag.getArgs().add(mset);
					sf.getArgs().add(bag);
				} else {
					/* The identifier is: linkRef.name.toUpper().processSpecOpChars()+'@'+objId+'@ATT' */
					Maude.Variable reference = maudeFact.getVariableOCLType(MaudeIdentifiers.getRefIdentifier(obj, ref));
					sf.getArgs().add(reference);
				}
				sfsArgs.add(sf);
			}			
			
			/*
			 * Slots
			 * 
			 * This rule is equal to the one in ATL `Slots2RecTerm`.
			 */
			for (Slot slot : obj.getSfs()) {
				RecTerm sf = maudeFact.createRecTerm(MaudeOperators.SF);
				EAttribute attribute = (EAttribute) slot.getSf();
				if (attribute == null) 
					throw new EmotionsNotCompletedModel("Rule: " + obj.getPattern().getRule().getName() 
							+ ". Pattern: " + obj.getPattern().getName() + ". Object: " + obj.getId() 
							+ ". Structural Feature not set.");
				sf.getArgs().add(maudeFact.getConstant(MaudeIdentifiers.get((EAttribute) slot.getSf())));
				sf.getArgs().add(maudeFact.getVariableOCLType(MaudeIdentifiers.getVariable(slot)));
				sfsArgs.add(sf);
			}
			
			
			sfsArgs.add(maudeFact.getVariableSFS(obj));
			res = maudeFact.createStructuralFeatureSet(sfsArgs);
		}
	}
	
	private boolean posNotSet(Link link) {
		return link.getPos() == null || link.getPos().equals("");
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
