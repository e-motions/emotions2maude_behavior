package main.java.transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import Maude.Equation;
import Maude.MaudeFactory;
import Maude.MaudeSpec;
import Maude.ModElement;
import Maude.ModImportation;
import Maude.ModuleIdModExp;
import Maude.Operation;
import Maude.RecTerm;
import Maude.SModule;
import behavior.ActionExec;
import behavior.AtomicRule;
import behavior.Behavior;
import behavior.Pattern;
import behavior.Rule;
import gcs.MetamodelGD;
import main.java.transformation.rules.CreateSetVariables;
import main.java.transformation.rules.CreateVariable;
import main.java.transformation.utils.MaudeOperators;

public class Emotions2Maude {
	
	private PrintWriter pw;
	
	private ModelManager modelManager;
	private MaudeFactory factory;
	
	/* InputModel elements */
	private Behavior beh;
	private MaudeSpec mSpec;
	
	
	/**
	 * Creates a Emotions2Maude transformation. It takes the inputModel path and the
	 * outputModel path as parameters.
	 * 
	 * @param inputModel
	 * @param outputModel
	 * @param pw
	 */
	public Emotions2Maude(File behModel, File gcsModel, File maudeModel, PrintWriter pw) {
		this(behModel, gcsModel, maudeModel);
		
		this.pw = pw;
	}
	
	
	public Emotions2Maude(File behModel, File gcsModel, File maudeModel) {
		modelManager = new ModelManager(behModel, gcsModel, maudeModel);
		factory = MaudeFactory.eINSTANCE;
	}


	public Emotions2Maude runTransformation() {
		/* Model elements */
		SModule smod;
		
		init();
		
		mSpec = createMaudeSpec();
		smod = createSModule();
		mSpec.getEls().add(smod);
		
		EList<ModImportation> importations = getImportations();
		smod.getEls().addAll(importations);
		debug("Added importations");
		
		for(Rule r : beh.getRules()) {
			/* is it Atomic or Ongoing? */
			if(r instanceof AtomicRule) {
				AtomicRule atomic = (AtomicRule) r;
				debug("Rule: " + r.getName() + "  AtomicRule");
				debug("   Min duration: " + atomic.getMinDuration());
				debug("   Periodicity: " + atomic.getPeriodicity());
				/* is it periodic? */
				/* TODO: maybe this if statement should be modified */
 				if(atomic.getPeriodicity() == 0.0) {
 					/* periodic */
 					if(Double.valueOf(atomic.getMaxDuration()) == 0.0) {
 						/* instantaneous */
 						smod.getEls().addAll(atomicRuleInstantaneousNotPeriodic(atomic));
 					}
 				} else {
 					/* not periodic */
 					if(Double.valueOf(atomic.getMaxDuration()) == 0.0) {
 						/* instantaneous */
 					}
 				}
			} else {
				debug("Rule: " + r.getName() + "  OngoingRule");
			}
			debug("   Lower bound: " + r.getLowerBound());
			debug("   Upper bound: " + r.getUpperBound());
			debug("   Max duration: " + r.getMaxDuration());
		}
		
		return this;
	}

	private EList<ModElement> atomicRuleInstantaneousNotPeriodic(AtomicRule atomic) {
		EList<ModElement> result = new BasicEList<ModElement>();
		/* NACs ? */
		for(Pattern nac : atomic.getNacs()) {
			debug("     NAC: " + nac.getName());
			result.addAll(createNac(nac));
		}
		
		return result;
	}

	/**
	 * It creates the operation needed for handling a NAC pattern.
	 * A NAC operation will have as arguments:
	 * 	1- The set of Oids of the elements in the LHS
	 *  2- The set of objects representing the variables
	 *  3- A variable that will have the whole model
	 *  
	 *  The rule in ATL is:
	 *  
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
	 *  
	 *  
	 * @param nac pattern
	 * @return the list of ModElements to be added to the Maude System Module
	 */
	private EList<ModElement> createNac(Pattern nac) {
		EList<ModElement> results = new BasicEList<ModElement>();
		/* name of the operator */
		String name = processSpecialChars(nac.getName().toLowerCase()) + "@" + nac.getRule().getName();
		EmotionsModule _emMod = EmotionsModule.getDefault();
		MyMaudeFactory _maudeFact = MyMaudeFactory.getDefault();
		
		/* operator */
		Operation op = createNACoperator(name);
		
		/* Equation with the condition */
		Equation equation1 = factory.createEquation();
		/* lhs */
		RecTerm lhsTerm = factory.createRecTerm();
		lhsTerm.setOp(name);
		
		/*
		 * First argument: the set of Objects or Action executions in the NAC's LHS
		 * 
		 * DELETE: set : Maude!RecTerm 
		 */
		RecTerm set = factory.createRecTerm(); 
		set.setOp("Set`{_`}");
		List<behavior.Object> objects = nac.getEls().stream()
				.filter(x -> x instanceof behavior.Object)
				.map(o -> (behavior.Object) o)
				.collect(Collectors.toList());
		List<ActionExec> actions = nac.getEls().stream()
				.filter(x -> x instanceof ActionExec)
				.map(o -> (ActionExec) o)
				.collect(Collectors.toList());
		if(objects.isEmpty() && actions.isEmpty()) {
			set.getArgs().add(_maudeFact.getConstantEmpty());
		} else if(objects.isEmpty() && actions.size() == 1) {
			set.getArgs().add(getPatternElOid(actions.get(0)));
		} else if(objects.size() == 1 && actions.isEmpty()) {
			set.getArgs().add(getPatternElOid(objects.get(0)));
		} else {
			set.getArgs().add(getManyPatternElOid(objects, actions));
		}
		
		lhsTerm.getArgs().add(set);
		
		/*
		 * Second argument: the set of Variables in the NAC's LHS
		 */
		if(nac.getRule().getVbles().isEmpty()) {
			/* thisModule.VariableEmpty('') */
			set.getArgs().add(_maudeFact.getConstantNone());
		} else if(nac.getRule().getVbles().size() == 1) {
			/* thisModule.CreateVar(n."rule".vbles -> first(),1) */
			set.getArgs().add(new CreateVariable(nac.getRule().getVbles().get(0), 1).get());
		} else {
			/* thisModule.CreateSetVar(n."rule") */
			set.getArgs().add(new CreateSetVariables(nac.getRule().getVbles()).get());
		}
		
		/* 
		 * Third argument: NAC elements, both objects and action executions
		 */
		
		/*
		 * 	lazy rule NacElements{
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
		 */
		
		// I'm gonna create a model
		RecTerm model = _maudeFact.createRecTerm(MaudeOperators.MODEL);
		Maude.Variable mm = _maudeFact.getVariableMM();
		
		/* lhsTermArgs : Maude!RecTerm */
		
		
		for(behavior.Object obj : objects) {
			/* thisModule.Object2RecTerm(i,n) */
			
		}
		
		model.getArgs().add(mm);
		
		
		/* rhs */
		
		/* Equation with the owise attribute */
		//eq <nac>@<rule>(OIDSET@:Set, OBJSET@:Set{@Object}, MODEL@:@Model) = false [owise] .
		Equation equation2 = factory.createEquation();
		equation2.setLhs(factory.createRecTerm());
		((RecTerm) equation2.getLhs()).setOp(name);
		((RecTerm) equation2.getLhs()).getArgs().add(_maudeFact.getVariableOidSet());
		((RecTerm) equation2.getLhs()).getArgs().add(_maudeFact.getVariableObjectSet());
		((RecTerm) equation2.getLhs()).getArgs().add(_maudeFact.getVariableModel());
		
		equation2.setRhs(_maudeFact.getConstantFalse());
		equation2.getAtts().add("owise");
		
		results.add(op);
		results.add(equation1);
		results.add(equation2);
		return results;
	}


	/**
	 *  Creates an operator as the following for the NAC pattern:
	 *  
	 *  op <nac>@<rule> : Set Set{@Object} @Model -> Bool .
	 *  
	 * @param name of the NAC
	 * @return the Maude operation
	 */
	private Operation createNACoperator(String name) {
		EmotionsModule _emMod = EmotionsModule.getDefault();
		Operation res = factory.createOperation();
		res.setName(name);
		res.getArity().add(_emMod.getSortSet());
		res.getArity().add(_emMod.getSortSetObject());
		res.getArity().add(_emMod.getSortModel());
		res.setCoarity(_emMod.getSortBool());
		return res;
	}


	/**
	 * -- It creates variable for identifiers
	 * 
	 *	lazy rule PatternElOid{
	 *	from
	 *		o : Behavior!PatternEl	
	 *	to
	 *		d : Maude!Variable(
	 *			name <- o.id,
	 *			type <- thisModule.oclTypeSort
	 *		)
	 *	}
	 */
	private Maude.Variable getPatternElOid(ActionExec action) {
		Maude.Variable res = factory.createVariable();
		res.setName(action.getId());
		res.setType(EmotionsModule.getDefault().getSortOCLType());
		return res;
	}
	
	private Maude.Variable getPatternElOid(behavior.Object object) {
		Maude.Variable res = factory.createVariable();
		res.setName(object.getId());
		res.setType(EmotionsModule.getDefault().getSortOCLType());
		return res;
	}
	
	/**
	 * lazy rule ManyPatternElOid {
	 * 	from
	 *		r : Behavior!Rule
	 *	to
	 *		participantsRT : Maude!RecTerm(
	 *			op <- thisModule.mSetOperator, --thisModule.setOperator, -- '_`,_'
	 *			type <- thisModule.mSetSort, --thisModule.listSort, 
	 *			args <- r.objActExecLHSRule()->collect(e|thisModule.PatternElOid(e))
	 *		)
	 *  }
	 */
	private RecTerm getManyPatternElOid(List<behavior.Object> objects, List<ActionExec> actions) {
		RecTerm res = factory.createRecTerm();
		res.setOp("_`,_");
		for(behavior.Object o : objects) {
			res.getArgs().add(getPatternElOid(o));
		}
		for(ActionExec a : actions) {
			res.getArgs().add(getPatternElOid(a));
		}
		return res;
	}


	private String processSpecialChars(String str) {
		return str;
	}


	/**
	 * Returns a list of module importations to be added to the main system module
	 * @return
	 */
	private EList<ModImportation> getImportations() {
		/**
		 *  Fixed importations 
		 */
		/* Import e-Motions module */
		/* Create module and include it in the MaudeSpec */
		mSpec.getEls().add(EmotionsModule.getDefault().getModule());
		
		ModImportation emotionsImportation = factory.createModImportation();
		ModuleIdModExp emotionsModExp = factory.createModuleIdModExp();
		emotionsModExp.setModule(EmotionsModule.getDefault().getModule());
		emotionsImportation.setImports(emotionsModExp);
		
		/* Import dense or discrete time module */
		/* Create module and include it in the MaudeSpec */
		SModule timeModule = factory.createSModule();
		timeModule.setName(beh.isDenseTime()? "DENSE_TIME" : "DISCRETE_TIME");
		mSpec.getEls().add(timeModule);
		
		ModImportation timeImportation = factory.createModImportation();
		ModuleIdModExp timeModExp = factory.createModuleIdModExp();
		timeModExp.setModule(timeModule);
		timeImportation.setImports(timeModExp);
		
		EList<ModImportation> result = new BasicEList<>();
		result.add(emotionsImportation);
		result.add(timeImportation);
		
		/**
		 *  Metamodel importations
		 */
		for(EObject obj : beh.getMetamodelGD()) {
			MetamodelGD mmGD = (MetamodelGD) obj;
			result.add(createModImportation(mmGD));
		}
		
		return result;
	}

	/**
	 * Given a MetamodelGD, it creates a module importation.
	 * @param mmGD
	 */
	private ModImportation createModImportation(MetamodelGD mmGD) {
		
		/* Create the system module if it does not exist */
		SModule mmModule = factory.createSModule();
		mmModule.setName(mmGD.getName());
		mSpec.getEls().add(mmModule);
		
		ModImportation mmImportation = factory.createModImportation();
		ModuleIdModExp mmModExp = factory.createModuleIdModExp();
		mmModExp.setModule(mmModule);
		mmImportation.setImports(mmModExp);
		
		return mmImportation;
	}


	/**
	 * Initializes the transformation setting the class members (targeting the input model) 
	 * to its proper values.
	 */
	public void init() {
		/* Get the behavior object */
		beh = (Behavior) modelManager.getBehModel().getContents().get(0);
	}


	public MaudeSpec createMaudeSpec() {
		MaudeSpec mSpec = factory.createMaudeSpec();
		modelManager.getMaudeModel().getContents().add(mSpec);
		debug("Created Maude specification");
		return mSpec;
	}
	
	private SModule createSModule() {
		SModule smod = factory.createSModule();
		smod.setName(((MetamodelGD) beh.getMetamodelGD().get(0)).getName() + "BEHAVIOR@");
		
		modelManager.getMaudeModel().getContents().add(smod);
		return smod;
	}
	
	/**
	 * Prints to the PrintWriter given as parameter the string 'string'
	 * @param string
	 */
	private void debug(String string) {
		if (pw != null)
			pw.println("  - " + string);
	}

	/**
	 * Generates the output xmi model.
	 */
	public boolean saveOutput() {
		boolean res = true;
		try {
			modelManager.getMaudeModel().save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			res = false;
			e.printStackTrace();
		}
		return res;
	}
	
	public ModelManager getModelManager() {
		return modelManager;
	}
	
	public Behavior getBehavior() {
		return beh;
	}
}
