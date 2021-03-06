package main.java.transformation.rules.smallrules;

import java.util.stream.Collectors;

import com.sun.xml.internal.bind.v2.TODO;

import Maude.RecTerm;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.utils.MaudeOperators;

/**
 * This rule creates a NAC pattern composed by `Object`s and `ActionExec`s.
 * It is quite similar to the one generated by {@link TODO}, but without seeds and clocks.
 * 
 * An example of the generated term could be:
 * 
 * <pre>
 *  MM@:@Metamodel {
 *  	
 *  }
 * </pre>
 * 
 * The ATL rule is:
 * 
 * <pre>
 * lazy rule NacElements{
		from
			n : Behavior!Pattern
		to
			model : Maude!RecTerm(
				op <- thisModule.modelOperator, -- '_{_}'
				type <- thisModule.sortModel,
				args <- Sequence{mm,lhsTermArgs}
				),
			mm : Maude!Variable( 
				name <- thisModule.oidMetamodel,
				type <- thisModule.sortMetamodel
				),
			lhsTermArgs : Maude!RecTerm(
				op <- thisModule.objSetOperator,
				type <- thisModule.sortSetObject,
				args <-  			
						Sequence{
						n.patternObjActExec()->collect(i|if i.oclIsTypeOf(Behavior!Object) then thisModule.Object2RecTerm(i,n)
														else thisModule.ActEx2RecTerm(i,true)
														endif),
						thisModule.CreateOBJSET('')
						}				
				)
		do{
			for (p in n.ActionExecEls()){
				for (q in p.participants){
					thisModule.countORAE <- thisModule.countORAE +1;
					lhsTermArgs.args <- lhsTermArgs.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});								
					--crlpre.conds <- crlpre.conds -> prepend(thisModule.CondInitializeVar('OR'+thisModule.counter.toString()+'@'+p.id,thisModule.counter));
				}
				thisModule.countORAE <- 0;
			}	
			thisModule.countORAE <- 0;
		}
	}
 * </pre>
 * 
 * 
 * 
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class PatternNAC extends Rule {
	
	private Pattern nac;

	public PatternNAC(MyMaudeFactory maudeFact, Pattern nac) {
		super(maudeFact);
		this.nac = nac;
	}

	@Override
	public void transform() {
		RecTerm model = maudeFact.createRecTerm(MaudeOperators.MODEL);
		Maude.Variable mm = maudeFact.getVariableMM();
		
		Maude.RecTerm lhsTermArgs = maudeFact.createRecTerm(MaudeOperators.SET);
		
		/* create the model elements */
		
		// adding objects
		lhsTermArgs.getArgs().addAll(nac.getEls().stream()
				.filter(e -> e instanceof behavior.Object)
				.map(o -> (behavior.Object) o)
				.map(obj -> new Object2RecTermLHS(maudeFact, obj, nac).get())
				.collect(Collectors.toList()));
		// adding actions
		// TODO
		
		lhsTermArgs.getArgs().add(maudeFact.getVariableObjectSet());
		/* end of model elements */
		
		model.getArgs().add(mm);
		model.getArgs().add(lhsTermArgs);
		
		res = model;
	}

}
