package main.java.transformation;

import org.eclipse.emf.ecore.EClass;

import Maude.RecTerm;
import Maude.Term;
import behavior.Pattern;
import gcs.ClassGD;

/**
 * This class transforms a Behavior!Object into a Maude object.
 * The id of the Maude object is the id of the behavior object.
 * The class of the Maude object is the name of the operator given by the method: 
 * @author amoreno
 *
 */
public class Object2RecTerm extends Rule {
	
	private behavior.Object behObj;
	private Pattern behPattern;
	
	private RecTerm maudeObject;
	
	/**
	 * lazy rule Object2RecTerm{
			from
				obj : Behavior!Object,
				p : Behavior!Pattern
			to
				rt : Maude!RecTerm(
					op <- thisModule.objectOperator, -- '<_:_|_>'
					type <- thisModule.sortObject,
					args <-	if p.isRHSPattern() then					
								Sequence{id,objClass,
									if (obj.outLinks -> isEmpty() and 
										obj.OppositeLinks()->isEmpty() and 
										obj.sfs -> isEmpty() and 
										obj.getLHSDeletedLinksFromRHSObject()->isEmpty() and 
										obj.OppositeLHSLinksNiIni()->isEmpty() ) then 
										sfeat
									else 
										thisModule.ObjectArgmsRHS(p,obj,sfeat)
									endif
									}
							else
								Sequence{id,objClass,
									if ((obj.outLinks -> isEmpty() and obj.OppositeLinks()->isEmpty())and(obj.sfs -> isEmpty())) then sfeat
									--if ((obj.outLinks -> isEmpty())and(obj.sfs -> isEmpty())) then sfeat
									else thisModule.ObjectArgmsLHS(p,obj,sfeat) 							
									endif
									}
							endif						
					),
				id : Maude!Variable(
					name <- obj.id,
					type <- thisModule.oclTypeSort
					),
				objClass : Maude!Variable(
					name <- obj.classGD.class.maudeName().toUpper()+'@'+obj.id+'@CLASS',
					type <- thisModule.Class2Sort(obj.classGD.class)
					),
				sfeat : Maude!Variable(
					name <- obj.id + '@SFS',
					type <- thisModule.sortSetSfi
					)
		}
	 */
	public Object2RecTerm(behavior.Object obj, Pattern pattern) {
		super();
		behObj = obj;
		behPattern = pattern;
	}

	@Override
	public void transform() {
		/* the variable to match the Oid of the object */
		Maude.Variable id = _maudeFact.getVariableOCLType(behObj.getId());
		/* TODO */
		Maude.Variable objClass = _maudeFact.getVariableObjectClass(behObj);
		
		
	}

	@Override
	public Term get() {
		if (maudeObject == null)
			transform();
		return maudeObject;
	}

}
