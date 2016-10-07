package main.java.transformation.rules.smallrules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EReference;

import Maude.Constant;
import Maude.RecTerm;
import behavior.Link;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.common.ObjectOperations;
import main.java.transformation.utils.MaudeOperators;

/**
 * 
 * This rule creates new objects that appear in the RHS of a rule.
 * 
 * The equivalent to the ATL rule:
 * 
 * <pre>
 * lazy rule NewObjectRHS2RecTerm{
 *	from
 *		obj : Behavior!Object,
 *		p : Behavior!Pattern
 *	to
 *		cmplt : Maude!RecTerm(
 *			op <- thisModule.completeOp,
 *			type <- thisModule.sortObject,
 *			args <- rt
 *			),
 *		rt : Maude!RecTerm(
 *			op <- thisModule.objectOperator, -- '<_:_|_>'
 *			type <- thisModule.sortObject,
 *			args <-	Sequence{id,objClass,
 *					if ((obj.outLinks -> isEmpty() and obj.OppositeLinks()->isEmpty())and(obj.sfs -> isEmpty())) then thisModule.CreateConstant('empty',thisModule.attributeSetSort)
 *					else
 *						if (obj.outLinks -> isEmpty() and obj.OppositeLinks()->isEmpty())and(obj.sfs->size()=1) then thisModule.Slots2RecTermRHS(obj.sfs->first())
						else
							if (obj.sfs -> isEmpty())and thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())->size()=1 then
								thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())-> collect(r|thisModule.LinksUpdate(r,obj,true))
							else thisModule.NewObjectArgmsRHS(obj)
							endif
						endif						
					endif
					}			
					),
		id : Maude!Variable(
			name <- obj.id,
			type <- thisModule.oclTypeSort
			),
		objClass : Maude!Constant(
			op <- obj.classGD.class.maudeName(),
			type <- thisModule.Class2Sort(obj.classGD.class)
			)
		}
 * </pre>
 * 
 * 
 * @author Antonio Moreno-Delgado <amoreno@lcc.uma.es>
 *
 */
public class NewObjectRHS extends Rule {

	private behavior.Object behObject;
	
	public NewObjectRHS(MyMaudeFactory maudeFact, behavior.Object behObject) {
		super(maudeFact);
		this.behObject = behObject;
	}

	@Override
	public void transform() {
		res = maudeFact.createRecTerm("complete");
		
		Maude.Variable id = maudeFact.getVariableOCLType(behObject.getId());
		Maude.Constant cid = maudeFact.createConstantCid(behObject);
		
		/* 
		 * Structural Features
		 */
		RecTerm sfi = null;
		if (behObject.getOutLinks().isEmpty() && behObject.getSfs().isEmpty()) {
			// constant empty
			/* TODO need more cases */
		} else {
			/* OutLinks and Sfs */
			/** 
			 * This code is like:
			 * <pre>
			 * lazy rule NewObjectArgmsRHS{
			 *	from
			 *		obj : Behavior!Object
			 *	to
			 *		sfi : Maude!RecTerm(
			 *			op <- thisModule.featOperator,
			 *			type <- thisModule.sortSetSfi,
			 *			args <-	thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())
			 *						->collect(r|thisModule.LinksUpdate(r,obj,true))
			 *						->union(obj.sfs -> collect(s|thisModule.Slots2RecTermRHS(s)))						
			 *			)
			 *	}
			 * </pre>
			 */
			sfi = maudeFact.createRecTerm(MaudeOperators.SFS_SET);
			Map<EReference, List<Link>> references = ObjectOperations.mapRef2Links(behObject.getOutLinks());
			for (EReference ref : references.keySet()) {
				/**
				 * Code for creating update of links
				 * 
				 * LinksUpdate rule
				 * <pre>
				 * lazy rule LinksUpdate{
					from
						r : Behavior!EReference,
						obj : Behavior!Object,
						isNewObject : Boolean
					to
						ref : Maude!RecTerm(
							op <- thisModule.sfsOperator,
							type <- thisModule.sortRefInst,
							args <- Sequence{left,right}
							),
						left : Maude!Constant(
							op <- r.maudeName().processSpecOpChars(),
							type <- thisModule.oclTypeSort
						),
						right : Maude!RecTerm(
							op <- thisModule.updateOp,					
							type <- thisModule.oclTypeSort,
							args <- if isNewObject then 
										Sequence{refArg,
												thisModule.CreateConstant(thisModule.nullOperator,thisModule.oclTypeSort),
												thisModule.CreateConstant(thisModule.nilOperator,thisModule.emptyListSort),
												thisModule.LinksToAdd(r.LinksWithConcreteRef(obj.outLinks),obj.GetOppositeLinks(r))
												}
									else Sequence{refArg,
												thisModule.CreateVariable(r.name.toUpper().processSpecOpChars()+'@'+obj.id+'@ATT',thisModule.oclTypeSort),
												obj.LinksToDelete(r,r.LinksWithConcreteRef(obj.outLinks),obj.GetOppositeLinks(r)), 
												thisModule.LinksToAdd(r.LinksWithConcreteRef(obj.outLinks),obj.GetOppositeLinks(r))
												}
									endif														 
						),	 
						refArg : Maude!Constant(
							op <- r.maudeName().processSpecOpChars(), --l.ref.maudeName().processSpecOpChars(),
							type <- thisModule.sortRefSimple
						)
					}
				 * </pre>
				 */
				Constant refName = maudeFact.getConstant(MaudeIdentifiers.get(ref));
				/* the `update` operation has four arguments */ 
				RecTerm updateTerm = maudeFact.createRecTerm("update");
				/* The reference name */
				Constant refName2 = maudeFact.getConstant(MaudeIdentifiers.get(ref));
				/* The old value for this reference */
				Constant oldValue = maudeFact.getConstant("null");
				/* List of references to be deleted */
				Constant deleted = maudeFact.getConstant("nil");
				/* Lis of references to be added */
				
				updateTerm.getArgs().addAll(Arrays.asList(refName2, oldValue, deleted));
				
				sfi.getArgs().add(maudeFact.createStructuralFeature(refName, updateTerm));
			}
		}
		
		((RecTerm) res).getArgs().add(maudeFact.createObject(id, cid, sfi));
	}

}
