package main.java.transformation.rules.coarserules;

import Maude.BooleanCond;
import Maude.Equation;
import Maude.MaudeFactory;
import Maude.RecTerm;
import behavior.AtomicRule;
import behavior.Pattern;
import main.java.transformation.MyMaudeFactory;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.rules.smallrules.ModelLHS;
import main.java.transformation.rules.smallrules.nac.FirstArgumentNAC;
import main.java.transformation.rules.smallrules.nac.SecondArgumentNAC;

/**
 * 
 * This rule creates the MTE for a rule.
 * 
 * The equivalent MTE rule is:
 * 
 * <pre>
 *    lazy rule mteInmediate{
 *		from
 *			rl :Behavior!Rule
 *		to
 *			eq : Maude!Equation(
 *				"module" <- thisModule.mainModule,
 *				label <- rl.name,
 *				lhs <- lhsTerm,
 *				rhs <- rhsTerm,
 *				conds <- 
 *						Sequence{thisModule.LhsCondInstantaneous(rl)}->
 *							union(rl.lhs.els -> select(e|e.oclIsTypeOf(Behavior!Link))->collect(o|o.LinkComputation()))	
 *				),
 *			lhsTerm : Maude!RecTerm(
 *				op <- 'mte',
 *				type <- thisModule.oclExpSort,
 *				args <- lhsT
 *				),
 *			lhsT : Maude!RecTerm(
 *				op <- thisModule.modelOperator, -- '_{_}'
 *				type <- thisModule.intSort,
				args <- Sequence{mm,lhsTermArgs}
				),
			mm : Maude!Variable( --se utiliza una variable para el nombre del metamodelo para poder utilizar cualquiera
				name <- thisModule.oidMetamodel,
				type <- thisModule.sortMetamodel
				),
				
			lhsTermArgs : Maude!RecTerm(
				op <- thisModule.objSetOperator, -- '__'
				type <- thisModule.sortSetObject,
				args <- 				
						Sequence{
						rl.objActExecLHSRule()->collect(i|if i.oclIsTypeOf(Behavior!Object) then thisModule.Object2RecTermApplyOngoingLHS(i,rl.lhs)		
														else thisModule.ActEx2RecTerm(i,i.ActExecExistsInRHS())
														endif),
						thisModule.CreateClock(rl.lhs),
						thisModule.CreateCounter(0),thisModule.CreateRandomCounter(false),thisModule.CreateOBJSET('')
						}					
				),			
			rhsTerm : Maude!Constant(
				op <- '0',
				type <- thisModule.intSort
				)
		do{
			thisModule.counter <- rl.objActExecLHSRule()->size()+1;
			for (p in rl.vbles){
				thisModule.counterV <- thisModule.counterV +1;
				eq.conds <- eq.conds -> prepend(thisModule.CondVAR(thisModule.counterV,thisModule.counter));
				thisModule.counter <- thisModule.counter +1;
			}
	
			thisModule.counter <- 0;
			thisModule.counterV <- 0;
			
			for (p in rl.lhs.ActionExecEls()){
				for (q in p.participants){
					thisModule.countORAE <- thisModule.countORAE +1;
					lhsTermArgs.args <- lhsTermArgs.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});			
				}
				thisModule.countORAE <- 0;
			}	
			thisModule.countORAE <- 0;			
			
			
			if (rl.oclIsTypeOf(Behavior!AtomicRule)){
				eq.conds <- eq.conds -> union(rl.vbles -> collect(s|thisModule.VarComputation(s)));
	--			eq.conds <- eq.conds -> append(thisModule.MinDurationComputation(rl));
	--			if ((not rl.maxDuration.oclIsUndefined()) and rl.maxDuration<>''){
	--				eq.conds <- eq.conds -> append(thisModule.MaxDurationComputation(rl));
	--			}
			}
			
			--if (not rl.lowerBound.oclIsUndefined() and ( rl.lowerBound.toString() <> '')){
			if (rl.lowerBound < 0.0 or rl.lowerBound > 0.0){
				eq.conds <- eq.conds -> append(thisModule.LowerBoundCondition(rl));
			}
			
			--if (not rl.upperBound.oclIsUndefined() and ( rl.upperBound.toString() <> '')) {
			if (rl.upperBound < -1.0 or rl.upperBound > -1.0) {
				eq.conds <- eq.conds -> append(thisModule.UpperBoundCondition(rl));
			}
			
	
	
			if (rl.oclIsTypeOf(Behavior!AtomicRule)){
				if (rl.periodicity > 0.0){
					--eq.conds <- eq.conds -> append(thisModule.inPeriodCondition(rl));			
					eq.conds <- eq.conds -> append(thisModule.AlreadyCondition(rl));
					---thisModule.AlreadyOpEq(rl);				
					if (not rl.soft){
						eq.conds <- eq.conds -> append(thisModule.multipleCondition(rl));
					}
				}
			}			
			
			eq.conds <- eq.conds ->union(rl.objectsLHSRule() -> collect(t|t.sfs) -> flatten() -> collect(t|thisModule.SlotsComputation(t)));
			eq.conds <- eq.conds ->union(rl.lhs.els -> select(j|j.oclIsTypeOf(Behavior!Condition)) -> collect(r|thisModule.OCLConditionsComputation(r)));
			
			eq.conds <- eq.conds -> union(rl.nacs -> collect(o|thisModule.NacsCond(o)));
			
			if ( not rl.behavior.oclIsUndefined() ){
				if ( rl.behavior.formalization = #dpo ){
				eq.conds <- eq.conds -> append(thisModule.DpoCondition(rl));
				}
			}
		}
	}
 * </pre>
 * 
 * @author Antonio Moreno-Delgado <amoreno@lcc.uma.es>
 *
 */
public class CreateMTE extends CoarseRule {
	
	private AtomicRule rule;
	private Equation eq;
	
	public CreateMTE(MyMaudeFactory maudeFact, AtomicRule rule) {
		super(maudeFact);
		this.rule = rule;
	}

	@Override
	public void transform() {
		eq = MaudeFactory.eINSTANCE.createEquation();

		eq.setLabel(rule.getName() + "MTE");
		
		/*
		 * LHS
		 */
		RecTerm lhsTerm = maudeFact.createRecTerm("mte");
		
		/* creating model */
		lhsTerm.getArgs().add(new ModelLHS(maudeFact, rule.getLhs()).get());
		
		eq.setLhs(lhsTerm);
		
		/*
		 * RHS
		 */
		eq.setRhs(maudeFact.getConstant("0"));
		
		/* 
		 * Conditions
		 */
		for (Pattern nac : rule.getNacs()) {
			BooleanCond cond = MaudeFactory.eINSTANCE.createBooleanCond();
			RecTerm booleanTerm = maudeFact.createRecTerm("not");
			
			RecTerm nacTerm = maudeFact.createRecTerm(
					MaudeIdentifiers.processSpecialChars(nac.getName().toLowerCase()) + "@" + nac.getRule().getName());
			
			nacTerm.getArgs().add(new FirstArgumentNAC(maudeFact, nac).get());
			nacTerm.getArgs().add(new SecondArgumentNAC(maudeFact, nac).get());
			nacTerm.getArgs().add(new ModelLHS(maudeFact, rule.getLhs()).get());
			
			booleanTerm.getArgs().add(nacTerm);
			cond.setLhs(booleanTerm);
			eq.getConds().add(cond);
		}
		
		/* print attribute */
		eq.getAtts().add("print \" - mte " + rule.getName() + "\"");
		
		res.add(eq);
	}

}
