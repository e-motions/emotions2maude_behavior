package main.java.transformation.rules.smallrules;

import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;

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
							if (obj.sfs -> isEmpty())and thisModule.AllObjectReferences(thisModule.RefWithoutDuplicates(obj.outLinks),obj.OppositeLinks())->size()=1 then --(obj.outLinks->size()=1) then 
								--thisModule.LinksAddRefRHS(obj.outLinks->first())
								--thisModule.RefWithoutDuplicates(obj.outLinks)->collect(r|thisModule.LinksUpdate(r,obj,true))
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
		
		
		
		//((RecTerm) res).getArgs().add(maudeFact.createObject(null, null, null));
	}

}
