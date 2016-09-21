package main.java.transformation.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EReference;

import Maude.Term;
import behavior.Link;
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
 * </pre>
 * 
 * ## RefWithoutDuplicates helper
 * <pre>
 * helper def : RefWithoutDuplicates( inCollection : Sequence(Behavior!Link) ) : Sequence(Behavior!EReference) =
 *	inCollection -> iterate(e; outCollection : Sequence(Behavior!EReference) = Sequence{} |
 *		if outCollection ->one(i|i=e.ref) then outCollection
 *		else outCollection ->append(e.ref)
 *		endif);
 * </pre>
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
	
	public ObjectStructFeatLHS(behavior.Object obj, Pattern pattern) {
		this.obj = obj;
		if (obj.getOutLinks().isEmpty() && obj.getSfs().isEmpty())
			throw new NotValidArgumentsE2MException("Structural features not needed for object with id " + obj.getId());
		this.pattern = pattern;
	}

	@Override
	public void transform() {
		List<Term> sfsArgs = new ArrayList<>();
		
		/* 
		 * Links to references 
		 * 
		 */
		Map<EReference, List<Link>> references = mapRef2Links(obj.getOutLinks());
		for (EReference ref : references.keySet()) {
			// Links2RecTerm
			/**
			 * This is where we will perform  the optimizations
			 * 
			 * lazy rule Links2RecTerm{
			 *	from		
			 *		linkRef : Behavior!EReference,
			 *		objId : String
			 *	to
			 *		ref : Maude!RecTerm(
			 *			op <- thisModule.sfsOperator, -- '_:_'
			 *			type <- thisModule.sortRefInst,
			 *			args <- Sequence{constSF,varSF}
			 *			),
			 *		constSF : Maude!Constant(
			 *			op <-  linkRef.maudeName().processSpecOpChars(),
			 *			type <- thisModule.sortRefSimple		
			 *			),
			 *		varSF : Maude!Variable(
			 * 			name <- linkRef.name.toUpper().processSpecOpChars()+'@'+objId+'@ATT', --link.target.id, --.toUpper() + '@' + link.src.id + '@ATT' ,
			 *			type <- thisModule.oclTypeSort --thisModule.collectionSort  
			 *			)
			 *	}
			 */
			/*
			 * Kinds of relations:
			 *  - unitary
			 *  - collections
			 *  	- ordered
			 *  	- unique
			 */
			if (ref.getUpperBound() == 1) {
				// it is unique and its the simplest thing
				
			} else {
				// it is a collection
				/*
				 * Steps to perform this:
				 * 	- check the kind of collection
				 *  - check the number of links for each reference
				 */
				if (ref.isOrdered() && !ref.isUnique() && references.get(ref).size() == 1) {
					// List
					/* if the size is greater than 1, it cannot be set in the matching */
				} else if (ref.isOrdered() && ref.isUnique() && references.get(ref).size() == 1) {
					// OrderedSet
					/* if the size is greater than 1, it cannot be set in the matching */
				} else if (!ref.isOrdered() && !ref.isUnique()) {
					// Bag
					
				} else if (!ref.isOrdered() && !ref.isUnique()) {
					// Set
				} else {
					/* it should be done as in the legacy transformation */
				}
			}
		}
		/*
		 * Slots
		 */
		
		sfsArgs.add(_maudeFact.getVariableSFS(obj));
		res = _maudeFact.createStructuralFeatureSet(sfsArgs);
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
