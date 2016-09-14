package emotions2maude_behavior.transformation;

import Maude.MaudeFactory;
import Maude.SModule;
import Maude.Sort;

public class EmotionsModule {

	private static EmotionsModule self;
	private static MaudeFactory factory;

	private SModule mod;

	/* Sorts */
	private Sort sortSet;
	private Sort sortSetObject;
	private Sort sortModel;
	private Sort sortBool;
	private Sort sortOCLType;
	private Sort sortOid;
	private Sort sortMetamodel;

	private EmotionsModule() {
	}

	public static EmotionsModule getDefault() {
		if (self == null) {
			self = new EmotionsModule();
			factory = MaudeFactory.eINSTANCE;
		}
		return self;
	}

	public SModule getModule() {
		if (mod == null) {
			mod = factory.createSModule();
			mod.setName("E-MOTIONS");
		}
		return mod;
	}

	public Sort getSortSet() {
		if (sortSet == null) {
			sortSet = factory.createSort();
			sortSet.setName("Set");
			mod.getEls().add(sortSet);
		}
		return sortSet;
	}

	public Sort getSortSetObject() {
		if (sortSetObject == null) {
			sortSetObject = factory.createSort();
			sortSetObject.setName("Set{@Object}");
			mod.getEls().add(sortSetObject);
		}
		return sortSetObject;
	}

	public Sort getSortModel() {
		if (sortModel == null) {
			sortModel = factory.createSort();
			sortModel.setName("@Model");
			mod.getEls().add(sortModel);
		}
		return sortModel;
	}

	public Sort getSortBool() {
		if (sortBool == null) {
			sortBool = factory.createSort();
			sortBool.setName("Bool");
			mod.getEls().add(sortBool);
		}
		return sortBool;
	}

	public Sort getSortOCLType() {
		if (sortOCLType == null) {
			sortOCLType = factory.createSort();
			sortOCLType.setName("OCL-Type");
			mod.getEls().add(sortOCLType);
		}
		return sortOCLType;
	}

	public Sort getSortOid() {
		if (sortOid == null) {
			sortOid = factory.createSort();
			sortOid.setName("Oid");
			mod.getEls().add(sortOid);
		}
		return sortOid;
	}

	public Sort getSortMetamodel() {
		if (sortMetamodel == null) {
			sortMetamodel = factory.createSort();
			sortMetamodel.setName("@Metamodel");
			mod.getEls().add(sortMetamodel);
		}
		return sortMetamodel;
	}

}
