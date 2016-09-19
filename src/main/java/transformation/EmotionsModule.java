package main.java.transformation;

import java.util.HashMap;
import java.util.Map;

import Maude.MaudeFactory;
import Maude.SModule;
import Maude.Sort;

public class EmotionsModule {

	private static EmotionsModule self;
	private static MaudeFactory factory;

	private SModule mod;

	private static Map<String, Sort> mappings;

	private EmotionsModule() {
	}

	public static EmotionsModule getDefault() {
		if (self == null) {
			self = new EmotionsModule();
			factory = MaudeFactory.eINSTANCE;
			mappings = new HashMap<>();
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
	
	/**
	 * Tries to get a sort. If it has not been created, it creates the new sort.
	 * Else, it returns the sort.
	 * @param name of the new sort
	 * @return such sort.
	 */
	public Sort getSort(String name) {
		Sort res;
		if((res = mappings.get(name)) == null) {
			res = factory.createSort();
			res.setName(name);
			getModule().getEls().add(res);
			mappings.put(name, res);
		}
		return res;
	}

	public Sort getSortSet() {
		return getSort("Set");
	}

	public Sort getSortSetObject() {
		return getSort("Set{@Object}");
	}

	public Sort getSortModel() {
		return getSort("@Model");
	}

	public Sort getSortBool() {
		return getSort("Bool");
	}

	public Sort getSortOCLType() {
		return getSort("OCL-Type");
	}

	public Sort getSortOid() {
		return getSort("Oid");
	}

	public Sort getSortMetamodel() {
		return getSort("@Metamodel");
	}

}
