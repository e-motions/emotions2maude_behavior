package main.java.transformation.rules.coarserules;

import Maude.Constant;
import Maude.Equation;
import Maude.MaudeFactory;
import Maude.ModElement;
import Maude.Operation;
import Maude.RecTerm;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.rules.smallrules.PatternNAC;
import main.java.transformation.rules.smallrules.nac.FirstArgumentNAC;
import main.java.transformation.rules.smallrules.nac.SecondArgumentNAC;

/**
 * 
 * This class creates the <code>ModElement</code>s needed for a NAC pattern.
 * 
 *  The rule in ATL is:
 * <pre>  
 *  lazy rule OperationNac{
	from
		n : Behavior!NAC
	to
		o : Maude!Operation(
			name <- n.name.toLower().processSpecOpChars() + '@' + n."rule".name.processSpecOpChars(),
			arity <- Sequence{thisModule.setSort,thisModule.sortSetObject,thisModule.sortModel},
			coarity <- thisModule.boolSort,
			"module" <- thisModule.mainModule
		),
		--- Conditional equation
		condEq : Maude!Equation(
			lhs <- lhsTerm,
			rhs <- rhsTerm,
			"module" <- thisModule.mainModule,
			conds <- Sequence{wholeModel} ->
						union(n.patternObjects() -> collect(t|t.sfs) -> flatten() -> collect(t|thisModule.SlotsComputation(t))) ->
						union(n.els -> select(j|j.oclIsTypeOf(Behavior!Condition)) -> collect(r|thisModule.OCLConditionsComputation(r))) ->
						union(n.els -> select(e|e.oclIsTypeOf(Behavior!Link))->collect(o|o.LinkComputation()))
		),
		wholeModel : Maude!MatchingCond(
			lhs <- lhsWM,
			rhs <- thisModule.NacElements(n)
		),
		lhsWM : Maude!Variable(
			name <- 'MODEL@',
			type <- thisModule.sortModel
		),

		lhsTerm : Maude!RecTerm(
			op <- n.name.toLower().processSpecOpChars() + '@' + n."rule".name.processSpecOpChars(),
			type <- thisModule.boolSort, 			
			args <- if n."rule".vbles -> isEmpty() then Sequence{set,thisModule.VariableEmpty(''),thisModule.NacElements(n)}
					else
						if n."rule".vbles -> size()=1 then Sequence{set,thisModule.CreateVar(n."rule".vbles -> first(),1),thisModule.NacElements(n)}
						else Sequence{set,thisModule.CreateSetVar(n."rule"),thisModule.NacElements(n)}
						endif
					endif			
		),
		set : Maude!RecTerm(
			op <- thisModule.setCollection,
			type <- thisModule.setSort,
			--args <- itemsSet
			args <- if n."rule".objActExecLHSRule()->isEmpty() then thisModule.ConstantEmpty('')
					else
						if n."rule".objActExecLHSRule()->size()=1 then thisModule.PatternElOid(n."rule".objActExecLHSRule()->first())
						else thisModule.ManyPatternElOid(n."rule")
						endif
					endif
			),
		rhsTerm : Maude!Constant(
			op <- 'true',
			type <- thisModule.boolSort
			),
			
		---Equation with the condition to FALSE---
		eqFalse : Maude!Equation(
			lhs <- lhsEqFalse,
			rhs <- rhsEqFalse,
			"module" <- thisModule.mainModule,
			atts <- 'owise'
		),
		lhsEqFalse : Maude!RecTerm(
			op <- n.name.toLower().processSpecOpChars() + '@' + n."rule".name.processSpecOpChars(),
			type <- thisModule.boolSort,
			args <- Sequence{oidsetVar,currentTimerFalseVar,modelVar}
		),
		oidsetVar : Maude!Variable(
			name <- 'OIDSET@',
			type <- thisModule.setSort
		),		
		currentTimerFalseVar : Maude!Variable(
			name <- 'OBJSET@',
			type <- thisModule.sortSetObject
		),
		modelVar : Maude!Variable(
			name <- 'MODEL@',
			type <- thisModule.sortModel
		),
		rhsEqFalse : Maude!Constant(
			op <- 'false',
			type <- thisModule.boolSort
		)
	}
 * </pre>
 * 
 * @author Antonio Moreno-Delgado <code>amoreno@lcc.uma.es</code>
 *
 */
public class CreateNac extends CoarseRule {
	
	private Pattern nac;
	private String opName;
	
	public CreateNac(MyMaudeFactory maudeFact, Pattern nac) {
		super(maudeFact);
		this.nac = nac;
		opName = MaudeIdentifiers.processSpecialChars(nac.getName().toLowerCase()) + "@" + nac.getRule().getName();
	}

	@Override
	public void transform() {
		/* operator */
		res.add(createNACoperator());
		/* equaiton with the condition */
		res.add(createConditionEquation());
		/* equation with the `owise` attribute */
		//eq <nac>@<rule>(OIDSET@:Set, OBJSET@:Set{@Object}, MODEL@:@Model) = false [owise] .
		res.add(createOwiseEquation());
	}
	
	/**
	 * Creates the main equation for the NAC.
	 * 
	 * This equation is something similar to:
	 * 
	 * ceq noarrow@Initial(
	 * 	Set`{_`}(mt), 
	 *  none, 
	 * 	_`{_`}(MM@:@Metamodel, 
     *	  __(
     * 		<_:_|_>(b:OCL-Type, ARROW@DEFAULTNAME@b@CLASS:Arrow@defaultname, b@SFS:Set{@StructuralFeatureInstance}), OBJSET@:Set{@Object})))
     * = true
     *  if MODEL@:@Model := _`{_`}(MM@:@Metamodel, 
     *   __(
     *    <_:_|_>(b:OCL-Type, ARROW@DEFAULTNAME@b@CLASS:Arrow@defaultname, b@SFS:Set{@StructuralFeatureInstance}), OBJSET@:Set{@Object})) .
	 * 
	 * @return the equation
	 */
	private ModElement createConditionEquation() {
		MaudeFactory mf = MaudeFactory.eINSTANCE;
		
		Equation equation1 = mf.createEquation();
		
		/* lhs */
		RecTerm lhsTerm = maudeFact.createRecTerm(MaudeIdentifiers.processSpecialChars(nac.getName().toLowerCase()) + "@" + nac.getRule().getName());
		
		/*
		 * First argument: the set of Objects or Action executions in the LHS
		 */
		lhsTerm.getArgs().add(new FirstArgumentNAC(maudeFact, nac).get());
		
		/*
		 * Second argument: the set of Variables in the NAC's LHS
		 */
		lhsTerm.getArgs().add(new SecondArgumentNAC(maudeFact, nac).get());
		
		/* 
		 * Third argument: the objects and action executions involved in the NAC 
		 */
		lhsTerm.getArgs().add(new PatternNAC(maudeFact, nac).get());
		
		/*
		 * RHS of the equation
		 */
		
		Constant rhsTerm = maudeFact.getConstant("true");
		
		equation1.setLhs(lhsTerm);
		equation1.setRhs(rhsTerm);
		
		return equation1;
	}

	/** A NAC operation will have as arguments:
	 *  1- The set of Oids of the elements in the LHS
	 *  2- The set of objects representing the variables
	 *  3- A variable that will have the whole model
	 
	 *  Creates an operator as the following for the NAC pattern:
	 *  
	 *  op <nac>@<rule> : Set Set{@Object} @Model -> Bool .
	 *  
	 * @return the Maude operation
	 */
	private Operation createNACoperator() {
		Operation res = maudeFact.createOperation(opName);
		res.getArity().add(maudeFact.getSort("Set"));
		res.getArity().add(maudeFact.getSort("Set{@Object}"));
		res.getArity().add(maudeFact.getSort("@Model"));
		res.setCoarity(maudeFact.getSort("Bool"));
		return res;
	}
	
	private Equation createOwiseEquation() {
		MaudeFactory mf = MaudeFactory.eINSTANCE;
		Equation equation2 = mf.createEquation();
		equation2.setLhs(mf.createRecTerm());
		
		((RecTerm) equation2.getLhs()).setOp(opName);
		((RecTerm) equation2.getLhs()).getArgs().add(maudeFact.getVariableOidSet());
		((RecTerm) equation2.getLhs()).getArgs().add(maudeFact.getVariableObjectSet());
		((RecTerm) equation2.getLhs()).getArgs().add(maudeFact.getVariableModel());
		equation2.setRhs(maudeFact.getConstantFalse());
		equation2.getAtts().add("owise");
		
		return equation2;
	}

}
