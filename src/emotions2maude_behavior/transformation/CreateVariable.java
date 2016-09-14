package emotions2maude_behavior.transformation;

import java.util.ArrayList;
import java.util.List;

import Maude.Constant;
import Maude.RecTerm;
import Maude.Term;
import behavior.Variable;

public class CreateVariable extends Rule {
	
	private Variable behVar;
	private String name;
	
	private RecTerm maudeVar;
	
	/**
	 * 
	 * lazy rule CreateVar{
	from
		var : Behavior!Variable,
		cont : Integer		
	to
		obRole : Maude!RecTerm(
			op <- thisModule.objectOperator, -- '<_:_|_>'
			type <- thisModule.sortObject,
			args <-	Sequence{id,varClass,argms}
			),
		id : Maude!Variable(
			name <- 'VAR'+cont.toString()+'@',
			type <- thisModule.sortOid
			),
		varClass : Maude!Constant(
			op <- 'Variable@MGBehavior',
			type <- thisModule.variableSort
			),
		argms : Maude!RecTerm(
			op <- thisModule.featOperator, -- '_`,_'
			type <- thisModule.sortSetSfi,
			args <- Sequence{name,value}
			),
		name : Maude!RecTerm(
			op <- thisModule.sfsOperator, -- '_:_'
			type <- thisModule.sortRefInst,
			args <- Sequence{nameIzq,nameDer}
			),
		nameIzq : Maude!Constant(
			op <-  'name@Variable@MGBehavior',
			type <- thisModule.sortRefSimple		
			),
		nameDer : Maude!Constant(
			op <- '"'+var.name.toString()+'"',
			type <- thisModule.stringSort
			),
		value : Maude!RecTerm(
			op <- thisModule.sfsOperator, -- '_:_'
			type <- thisModule.sortRefInst,
			args <- Sequence{valueIzq,valueDer}
			),
		valueIzq : Maude!Constant(
			op <-  'value@Variable@MGBehavior',
			type <- thisModule.sortRefSimple		
			),
		valueDer : Maude!Variable(
			name <- var.name.toString(),
			type <- thisModule.oclTypeSort
			)
		}
	 * 
	 *  It creates a Maude object representing an e-motions variable
	 */
	public CreateVariable(behavior.Variable behVar, int i) {
		super();
		this.behVar = behVar;
		name = "VAR" + i + "@";
	}
	

	@Override
	public void transform() {
		/*
		 * name : Maude!RecTerm(
			op <- thisModule.sfsOperator, -- '_:_'
			type <- thisModule.sortRefInst,
			args <- Sequence{nameIzq,nameDer}
			),
		 * nameIzq : Maude!Constant(
			op <-  'name@Variable@MGBehavior',
			type <- thisModule.sortRefSimple		
			),
		 * nameDer : Maude!Constant(
			op <- '"'+var.name.toString()+'"',
			type <- thisModule.stringSort
			),
		 */
		
		List<Term> nameArgs = new ArrayList<>();
		nameArgs.add(_maudeFact.getConstant("name@Variable@MGBehavior"));
		nameArgs.add(_maudeFact.getConstant("\"" + behVar.getName() + "\""));
		RecTerm nameRecTerm = _maudeFact.createStructuralFeature(nameArgs);
		
		/*
		 * value : Maude!RecTerm(
			op <- thisModule.sfsOperator, -- '_:_'
			type <- thisModule.sortRefInst,
			args <- Sequence{valueIzq,valueDer}
			),
		 * valueIzq : Maude!Constant(
			op <-  'value@Variable@MGBehavior',
			type <- thisModule.sortRefSimple		
			),
		 * valueDer : Maude!Variable(
			name <- var.name.toString(),
			type <- thisModule.oclTypeSort
			)
		 */
		List<Term> valueArgs = new ArrayList<>();
		valueArgs.add(_maudeFact.getConstant("value@Variable@MGBehavior"));
		valueArgs.add(_maudeFact.createVariableOCLType(behVar.getName()));
		RecTerm value = _maudeFact.createStructuralFeature(valueArgs);
		
		/*
		 * argms : Maude!RecTerm(
			op <- thisModule.featOperator, -- '_`,_'
			type <- thisModule.sortSetSfi,
			args <- Sequence{name,value}
			),
		 */
		List<Term> sfsArgs = new ArrayList<>();
		sfsArgs.add(nameRecTerm);
		sfsArgs.add(value);
		RecTerm sfs = _maudeFact.createStructuralFeatureSet(sfsArgs);
		
		/*
		 * id : Maude!Variable(
			name <- 'VAR'+cont.toString()+'@',
			type <- thisModule.sortOid
			),
		 */
		Maude.Variable id = _maudeFact.createVariableOid(name);
		
		/*
		 * varClass : Maude!Constant(
			op <- 'Variable@MGBehavior',
			type <- thisModule.variableSort
			),
		 */
		Constant varClass = _maudeFact.getConstant("Variable@MGBehavior");
		
		/*
		 * obRole : Maude!RecTerm(
			op <- thisModule.objectOperator, -- '<_:_|_>'
			type <- thisModule.sortObject,
			args <-	Sequence{id,varClass,argms}
			),
		 */
		maudeVar = _maudeFact.createObject(id, varClass, sfs);
	}
	
	@Override
	public RecTerm get() {
		if(maudeVar == null)
			transform();
		return maudeVar;
	}
}
