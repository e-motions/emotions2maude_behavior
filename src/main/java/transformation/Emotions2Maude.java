package main.java.transformation;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import Maude.MaudeFactory;
import Maude.MaudeSpec;
import Maude.ModImportation;
import Maude.ModuleIdModExp;
import Maude.SModule;
import behavior.AtomicRule;
import behavior.Behavior;
import behavior.Rule;
import gcs.MetamodelGD;
import main.java.Debugger;
import main.java.transformation.rules.coarserules.AtomicRuleInstantaneousNotPeriodic;

public class Emotions2Maude {
	
	private ModelManager modelManager;
	private MaudeFactory factory;
	
	private EmotionsModule emotionsMod;
	private MyMaudeFactory maudeFact;
	
	/* InputModel elements */
	private Behavior beh;
	private MaudeSpec mSpec;
	
	/**
	 * Creates a Emotions2Maude transformation. It takes the inputModel path and the
	 * outputModel path as parameters.
	 * 
	 */
	public Emotions2Maude(File behModel, File gcsModel, File maudeModel) {
		modelManager = new ModelManager(behModel, gcsModel, maudeModel);
		factory = MaudeFactory.eINSTANCE;
		
		emotionsMod = new EmotionsModule();
		maudeFact = new MyMaudeFactory(emotionsMod);
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
		Debugger.debug("Added importations");
		
		for(Rule r : beh.getRules()) {
			/* is it Atomic or Ongoing? */
			if(r instanceof AtomicRule) {
				AtomicRule atomic = (AtomicRule) r;
				Debugger.debug("Rule: " + r.getName() + "  AtomicRule");
				Debugger.debug("   Min duration: " + atomic.getMinDuration());
				Debugger.debug("   Periodicity: " + atomic.getPeriodicity());
				/* is it periodic? */
				/* TODO: maybe this if statement should be modified */
 				if(atomic.getPeriodicity() == 0.0) {
 					/* periodic */
 					if(atomic.getMaxDuration().equals("") || atomic.getMaxDuration().equals("0")) {
 						/* instantaneous */
 						smod.getEls().addAll(new AtomicRuleInstantaneousNotPeriodic(maudeFact, atomic).get());
 					}
 				} else {
 					/* not periodic */
 					if(Double.valueOf(atomic.getMaxDuration()) == 0.0) {
 						/* instantaneous */
 					}
 				}
			} else {
				Debugger.debug("Rule: " + r.getName() + "  OngoingRule");
			}
			Debugger.debug("   Lower bound: " + r.getLowerBound());
			Debugger.debug("   Upper bound: " + r.getUpperBound());
			Debugger.debug("   Max duration: " + r.getMaxDuration());
		}
		
		return this;
	}

	
	
	/**
	 * Returns a list of module importations to be added to the main system module
	 * @return
	 */
	private EList<ModImportation> getImportations() {
		/**
		 *  Fixed importations 
		 */
		modelManager.getMaudeModel().getContents().add(emotionsMod.getModule());
		
		ModImportation emotionsImportation = factory.createModImportation();
		ModuleIdModExp emotionsModExp = factory.createModuleIdModExp();
		emotionsModExp.setModule(emotionsMod.getModule());
		emotionsImportation.setImports(emotionsModExp);
		
		/* Import dense or discrete time module */
		/* Create module and include it in the MaudeSpec */
		SModule timeModule = factory.createSModule();
		timeModule.setName(beh.isDenseTime()? "DENSE_TIME" : "DISCRETE_TIME");
		modelManager.getMaudeModel().getContents().add(timeModule);
		
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
		modelManager.getMaudeModel().getContents().add(mmModule);
		
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
		Debugger.debug("Created Maude specification");
		return mSpec;
	}
	
	private SModule createSModule() {
		SModule smod = factory.createSModule();
		smod.setName(((MetamodelGD) beh.getMetamodelGD().get(0)).getName() + "BEHAVIOR@");
		
		modelManager.getMaudeModel().getContents().add(smod);
		return smod;
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
