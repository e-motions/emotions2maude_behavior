package main.java.transformation.rules.coarserules;

import behavior.AtomicRule;
import behavior.Pattern;
import main.java.Debugger;
import main.java.transformation.MyMaudeFactory;

/**
 * The equivalent ATL rule is:
 * 
 * <pre>
 * rule InmediateAtomicRules{
	from
		rl :Behavior!AtomicRule (not(rl.minDuration.oclIsUndefined() or rl.minDuration='') and rl.minDuration = '0')
	to		
		crlpre : Maude!Rule( 		
			"module" <- thisModule.mainModule,
			label <- rl.name.processSpecOpChars() + '@Instantaneous',
			lhs <- lhsTerm,
			rhs <- rhsTermReadjust,
			--Aqu� se llaman a las lazy rules que a�adir�n las condiciones de la Regla.
			--Se computan los atributos, las variables y las referencias y se a�aden las diferentes condiciones
			conds <- Sequence{thisModule.TimerCond(''),thisModule.LhsCondInstantaneous(rl)}	->	--,thisModule.CurrentExecCondition(rl)} ->
						union(rl.vbles -> collect(s|thisModule.VarComputation(s))) ->
						union(rl.objectsLHSRule() -> collect(t|t.sfs) -> flatten() -> collect(t|thisModule.SlotsComputation(t))) ->
						union(rl.lhs.els -> select(j|j.oclIsTypeOf(Behavior!Condition)) -> collect(r|thisModule.OCLConditionsComputation(r))) ->
						--append(thisModule.MinDurationComputation(rl)) ->
						append(thisModule.ExecutionTimeComputation('')) ->
						union(rl.lhs.els -> select(e|e.oclIsTypeOf(Behavior!Link))->collect(o|o.LinkComputation()))							
			),
			
		--------------- lhs(Maude) de la INSTANTANEOUS RULE (representa el modelo) -----------------------	
		lhsTerm : Maude!RecTerm(
			op <- thisModule.modelOperator, -- '_{_}'
			type <- thisModule.sortModel,
			args <- Sequence{mm,lhsTermArgs}
			),
		mm : Maude!Variable( 
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
					thisModule.CreateClock(rl.lhs),thisModule.CreateCounter(0),
					thisModule.CreateRandomCounter(false),thisModule.CreateOBJSET('')
					}			
				),

		
		--------------------FIN lhs(Maude) de la INSTANTANEOUS rule-------------

		
		--------------------rhs(Maude) de la INSTANTANEOUS RULE-----------------------------------------------
		rhsTermReadjust : Maude!RecTerm(
			op <- thisModule.readjustOp,
			type <- thisModule.sortModel, 
			args <- --Sequence{deletedEl,rhsTermPost}
--					if rl.deletedLinks() -> isEmpty() then Sequence{deletedEl,thisModule.CreateConstant(thisModule.nilOperator,thisModule.emptyListSort),rhsTermPost}
--					else
--						if rl.deletedLinks() -> size()=1 then Sequence{deletedEl,thisModule.LinkSimple(rl.deletedLinks()->first()),rhsTermPost}
--						else Sequence{deletedEl,thisModule.LinkList(rl.deletedLinks()),rhsTermPost}
--						endif
--					endif
					if rl.SlotsOfReferences()->isEmpty() then Sequence{deletedEl,thisModule.CreateConstant(thisModule.nilOperator,thisModule.emptyListSort),rhsTermPost}
					else
						if rl.SlotsOfReferences()->size()=1 then Sequence{deletedEl,thisModule.SlotRefSimple(rl.SlotsOfReferences()->first()),rhsTermPost}
						else Sequence{deletedEl,thisModule.SlotRefList(rl.SlotsOfReferences()),rhsTermPost}
						endif
					endif
			),
		deletedEl : Maude!RecTerm( --se crea el contenedor Set_ para el conjunto de elementos borrados
			op <- thisModule.setCollection,
			type <- thisModule.setSort,
			args <- if rl.deletedObjects() -> isEmpty() then thisModule.ConstantEmpty('')
					else
						if rl.deletedObjects()->size()=1 then thisModule.PatternElDeleted(rl.deletedObjects()->first())
						else thisModule.PatternElDeletedMany(rl.deletedObjects())							
						endif
					endif
			),
--		itemsDeleted : Maude!RecTerm(
--			op <- thisModule.bracketsOp, --{_}
--			type <- thisModule.itemListSort,
--			args <- if rl.deletedObjects() -> isEmpty() then thisModule.ConstantEmpty('')
--					else
--						if rl.deletedObjects()->size()=1 then thisModule.PatternElDeleted(rl.deletedObjects()->first())
--						else thisModule.PatternElDeletedMany(rl.deletedObjects())							
--						endif
--					endif
--			),					
			
		rhsTermPost : Maude!RecTerm(
			op <- thisModule.modelOperator,
			type <- thisModule.sortModel, 
			args <- Sequence{mmrhsPost,rhsTermArgsPost}
			),
		mmrhsPost : Maude!Variable(
			name <- thisModule.oidMetamodel,
			type <- thisModule.sortMetamodel
			),
		rhsTermArgsPost : Maude!RecTerm(   --Cuidado: Aqu� estamos haciendo rhs de Maude, PERO RHS de Behavior
			op <- thisModule.objSetOperator, -- '__'
			type <- thisModule.sortSetObject,
			args <- 		
					Sequence{
					rl.objActExecRHSRule()->collect(i|if i.oclIsTypeOf(Behavior!Object) then rl.ObjectRHS2RecTermApplyOngoing(i)		
													else thisModule.ActEx2RecTerm(i,true)
													endif),
					--thisModule.CreateTimerRealization(rl,true),
					thisModule.CreateClock(rl.rhs),
					--thisModule.CreateCounter(rl.numObjCreatedInstantaneous()+1),
					--thisModule.CreateCounter(rl.numObjCreatedInstantaneousAux()+1),
					thisModule.CreateRandomCounter(true),thisModule.CreateOBJSET('')
					}
					->union(if rl.periodicity > 0.0 then Sequence{thisModule.CreateTimerRealization(rl,true),thisModule.CreateCounter(rl.numObjCreatedInstantaneous()+rl.vbles->size()+1)}
							else Sequence{thisModule.CreateCounter(rl.numObjCreatedInstantaneousAux()+rl.vbles->size()+1)}
							endif)
					--se a�aden tambi�n los elementos borrados (elementos de lhs que no aparecen en RHS) que ser�n manejados de una forma especial
					->union(rl.deletedElements()->collect(i|if i.oclIsTypeOf(Behavior!Object) then
																thisModule.Object2RecTerm(i,rl.lhs)
															else
																--thisModule.ActEx2RecTerm(i)
																thisModule.ActExInterrupted2RecTerm(i)
															endif)
															)
					
				)

		-------------------------FIN rhs(Maude) de la INSTANTANEOUS RULE------------------------------
		
	do{					
		for (p in rl.objActExecLHSRule()){
			thisModule.counter <- thisModule.counter +1;
			if (rl.periodicity > 0.0){
				rhsTermArgsPost.args <- rhsTermArgsPost.args -> union(Sequence{thisModule.CreateObjRole(p,thisModule.counter)});
			}
			crlpre.conds <- crlpre.conds -> prepend(thisModule.CondOR(thisModule.counter,thisModule.counter));
		}
		if (rl.periodicity > 0.0){
			for (p in rl.NewObjActExecRHS()){
				thisModule.counter <- thisModule.counter +1;
				crlpre.conds <- crlpre.conds -> prepend(thisModule.CondOR(thisModule.counter,thisModule.counter));
				rhsTermArgsPost.args <- rhsTermArgsPost.args -> union(Sequence{thisModule.CreateObjRole(p,thisModule.counter)});
			}		
		}
		thisModule.counter <- thisModule.counter +1;
		for (p in rl.vbles){
			thisModule.counterV <- thisModule.counterV +1;
			crlpre.conds <- crlpre.conds -> prepend(thisModule.CondVAR(thisModule.counterV,thisModule.counter));
			thisModule.counter <- thisModule.counter +1;
		}
		thisModule.counterV <- 0;
		--Para los ORi de la parte derecha
		--for (p in rl.NewObjectsRHS()){
		for (p in rl.NewObjActExecRHS()){
			crlpre.conds <- crlpre.conds -> prepend(thisModule.CondInitializeVar(p.id,thisModule.counter));
			thisModule.counter <- thisModule.counter + 1;
		}
		thisModule.counter <- 0;
		
		for (p in rl.lhs.ActionExecEls()){
			for (q in p.participants){
				thisModule.countORAE <- thisModule.countORAE +1;
				lhsTermArgs.args <- lhsTermArgs.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});
				--rhsTermArgs.args <- rhsTermArgs.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});				
				--crlpre.conds <- crlpre.conds -> prepend(thisModule.CondInitializeVar('OR'+thisModule.counter.toString()+'@'+p.id,thisModule.counter));
			}
			thisModule.countORAE <- 0;
		}	
		thisModule.countORAE <- 0;		
		
	
		thisModule.counterRhs <- 0;
		--Para crear las VARi@
		if (rl.periodicity > 0.0){
			for (p in rl.vbles){
				thisModule.counterVar <- thisModule.counterVar +1;
				--rhsTermArgs.args <- rhsTermArgs.args -> union(Sequence{thisModule.CreateVar(p,thisModule.counterVar)});
				rhsTermArgsPost.args <- rhsTermArgsPost.args -> union(Sequence{thisModule.CreateVar(p,thisModule.counterVar)});
			}
		}
		thisModule.counterVar <- 0;
		
		
		for (p in rl.rhs.ActionExecEls()){
			for (q in p.participants){
				thisModule.countORAE <- thisModule.countORAE +1;
				rhsTermArgsPost.args <- rhsTermArgsPost.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});
				--crlpre.conds <- crlpre.conds -> prepend(thisModule.CondInitializeVar('OR'+thisModule.counter.toString()+'@'+p.id,thisModule.newCounter));
			}
			thisModule.countORAE <- 0;
		}
		
		for(r in rl.deletedElements()->select(i|i.oclIsTypeOf(Behavior!ActionExec))){
			for (q in r.participants){
				thisModule.countORAE <- thisModule.countORAE +1;
				rhsTermArgsPost.args <- rhsTermArgsPost.args -> union(Sequence{thisModule.CreateObjRoleActionExec(q,thisModule.countORAE)});				
			}
			thisModule.countORAE <- 0;
		}			
		
		
		
		
-----------CONDICIONES QUE ESTARAN PRESENTES SEGUN LAS CARACTERISTICAS CONCRETAS DE LA REGLA---------------------------------

--Problema: las condiciones lowerbound y upperbound funcionan pero si al editar el modelo se seleccionan las propiedades
--ya no se detectan vac�as aunque las dejemos en blanco
--Soluci�n: En una versi�n posterior las propiedades upperBound y lowerBound pasaron a ser de tipo EFloat con valores por defecto
--de -1.0 y 0.0 respectivamente. 
		
		if (rl.lowerBound < 0.0 or rl.lowerBound > 0.0){
			crlpre.conds <- crlpre.conds -> append(thisModule.LowerBoundCondition(rl));
		}
		if (rl.upperBound < -1.0 or rl.upperBound > -1.0) {
			crlpre.conds <- crlpre.conds -> append(thisModule.UpperBoundCondition(rl));
		}		

		---PARA PERIODIC RULES: se introduce la condici�n 'inPeriod'
		
--		if ((rl.oclIsTypeOf(Behavior!AtomicRule)) and (rl.periodicity > 0.0)){
--			crlpre.conds <- crlpre.conds -> append(thisModule.inPeriodCondition(rl));
--		}
		
		--Para reglas que NO sean "SOFT PERIODIC" se incluye la operaci�n 'multiple'
		--Si la regla fuera SOFT PERIODIC s incluir�a (en lugar de la operaci�n 'multiple') la condici�n alreadyExecInPeriod
--		if (rl.periodicity > 0.0){
--			if (rl.soft){
--				crlpre.conds <- crlpre.conds -> append(thisModule.AlreadyCondition(rl));
--				thisModule.AlreadyOpEq(rl);
--			}
--			else{
--				crlpre.conds <- crlpre.conds -> append(thisModule.multipleCondition(rl));
--			}
--		}
		
		if (rl.periodicity > 0.0){
			crlpre.conds <- crlpre.conds -> append(thisModule.AlreadyCondition(rl));
			thisModule.AlreadyOpEq(rl);					
			if (not rl.soft){
				crlpre.conds <- crlpre.conds -> append(thisModule.multipleCondition(rl));
			}
		}
		
		
--		if ((not rl.maxDuration.oclIsUndefined()) and rl.maxDuration<>''){
--			crlpre.conds <- crlpre.conds -> append(thisModule.MaxDurationComputation(rl));
--		}
		
----------------------------------------------------------------------------------------------------------------------------		
		
		--La operaci�n CURRENT EXEC sirve para evitar un numero indefinido de ejecuciones de la regla
		--thisModule.CurrentExecOp(rl);
		

		if ( not rl.behavior.oclIsUndefined() ){
			if ( rl.behavior.formalization = #dpo ){
			crlpre.conds <- crlpre.conds -> append(thisModule.DpoCondition(rl));
			}
		}


		--Si la regla tiene asociados patrones NAC se llama a una regla espec�fica para crear la operaci�n de dicho NAC
		for (p in rl.nacs){
			thisModule.OperationNac(p);
		}

		----La ecuaci�n mte se define para EAGER RULES. Dichas reglas se ejecutan tan pronto como es posible.
		if (not((rl.soft) and not(rl.periodicity > 0.0 ))){			
			thisModule.mteInmediate(rl);
		}
		
		crlpre.conds <- crlpre.conds -> union(rl.nacs -> collect(o|thisModule.NacsCond(o)));
	}
}
 * </pre>
 * 
 * 
 * 
 * @author Antonio Moreno-Delgado <amoreno@lcc.uma.es>
 *
 */
public class AtomicRuleInstantaneousNotPeriodic extends CoarseRule {
	
	private AtomicRule rule;
	
	public AtomicRuleInstantaneousNotPeriodic(MyMaudeFactory maudeFact, AtomicRule rule) {
		super(maudeFact);
		this.rule = rule;
	}

	@Override
	public void transform() {
		/* NACs ? */
		for(Pattern nac : rule.getNacs()) {
			Debugger.debug("     NAC: " + nac.getName());
			res.addAll(new CreateNac(maudeFact, nac).get());
		}
		/* MTE */
		if (!rule.isSoft() && !(rule.getPeriodicity() > 0.0)){			
			res.addAll(new CreateMTE(maudeFact, rule).get());
		}
		/* Rule */
		res.addAll(new CreateInstantaneousRl(maudeFact, rule).get());
	}

}
