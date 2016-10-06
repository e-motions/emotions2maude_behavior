package main.java.transformation.rules.smallrules;

import java.util.ArrayList;
import java.util.List;

import Maude.Constant;
import Maude.RecTerm;
import Maude.Term;
import behavior.Variable;
import main.java.transformation.MyMaudeFactory;

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
	public CreateVariable(MyMaudeFactory maudeFact, behavior.Variable behVar, int i) {
		super(maudeFact);
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
		
		RecTerm nameRecTerm = maudeFact.createStructuralFeature(
				maudeFact.getConstant("name@Variable@MGBehavior"),
				maudeFact.getConstant("\"" + behVar.getName() + "\""));
		
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
		RecTerm value = maudeFact.createStructuralFeature(
				maudeFact.getConstant("value@Variable@MGBehavior"),
				maudeFact.getVariableOCLType(behVar.getName()));
		
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
		RecTerm sfs = maudeFact.createStructuralFeatureSet(sfsArgs);
		
		/*
		 * id : Maude!Variable(
			name <- 'VAR'+cont.toString()+'@',
			type <- thisModule.sortOid
			),
		 */
		Maude.Variable id = maudeFact.getVariableOid(name);
		
		/*
		 * varClass : Maude!Constant(
			op <- 'Variable@MGBehavior',
			type <- thisModule.variableSort
			),
		 */
		Constant varClass = maudeFact.getConstant("Variable@MGBehavior");
		
		/*
		 * obRole : Maude!RecTerm(
			op <- thisModule.objectOperator, -- '<_:_|_>'
			type <- thisModule.sortObject,
			args <-	Sequence{id,varClass,argms}
			),
		 */
		maudeVar = maudeFact.createObject(id, varClass, sfs);
	}
	
	@Override
	public RecTerm get() {
		if(maudeVar == null)
			transform();
		return maudeVar;
	}
}
