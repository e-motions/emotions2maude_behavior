package emotions2maude_behavior.transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import Maude.MaudeFactory;
import Maude.MaudeSpec;
import Maude.ModImportation;
import Maude.ModuleIdModExp;
import Maude.SModule;
import behavior.Behavior;
import behavior.Rule;
import gcs.MetamodelGD;

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
		this.pw = pw;
		
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
			debug("Rule: " + r.getName());
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
		/* Import e-Motions module */
		/* Create module and include it in the MaudeSpec */
		SModule emotionsMod = factory.createSModule();
		emotionsMod.setName("E-MOTIONS");
		mSpec.getEls().add(emotionsMod);
		
		ModImportation emotionsImportation = factory.createModImportation();
		ModuleIdModExp emotionsModExp = factory.createModuleIdModExp();
		emotionsModExp.setModule(emotionsMod);
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
	private void init() {
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
}
