package emotions2maude_behavior.transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import Maude.MaudeFactory;
import Maude.MaudeSpec;
import Maude.SModule;
import behavior.Behavior;
import gcs.MetamodelGD;

public class Emotions2Maude {
	
	private PrintWriter pw;
	
	private ModelManager modelManager;
	private MaudeFactory factory;
	
	/* InputModel elements */
	private Behavior beh;
	
	
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
		MaudeSpec mSpec;
		SModule smod;
		
		init();
		
		mSpec = createMaudeSpec();
		smod = createSModule();
		mSpec.getEls().add(smod);
		
		return this;
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
		debug(beh.getMetamodelGD().toString());
		smod.setName(((MetamodelGD) beh.getMetamodelGD().get(0)).getName());
		
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
