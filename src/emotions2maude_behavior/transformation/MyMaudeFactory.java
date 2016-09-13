package emotions2maude_behavior.transformation;

import Maude.Constant;
import Maude.MaudeFactory;
import Maude.Variable;

public class MyMaudeFactory {
	
	private static MyMaudeFactory self;
	private static MaudeFactory factory;
	
	private MyMaudeFactory() {}
	
	public static MyMaudeFactory getDefault() {
		if(self == null) {
			self = new MyMaudeFactory();
			factory = MaudeFactory.eINSTANCE;
		}
		return self;
	}
	
	public Constant getConstantFalse() {
		Constant res = factory.createConstant();
		res.setOp("false");
		
		return res;
	}
	
	/**
	 * It produces: OIDSET@:Set
	 */
	public Variable getVariableOidSet() {
		Variable res = factory.createVariable();
		res.setName("OIDSET@");
		res.setType(EmotionsModule.getDefault().getSortSet());
		return res;
	}
	
	/**
	 * It produces: OBJSET@:Set{@Object}
	 */
	public Variable getVariableObjectSet() {
		Variable res = factory.createVariable();
		res.setName("OBJSET@");
		res.setType(EmotionsModule.getDefault().getSortSetObject());
		return res;
	}
	
	/**
	 * It produces: MODEL@:@Model
	 */
	public Variable getVariableModel() {
		Variable res = factory.createVariable();
		res.setName("MODEL@");
		res.setType(EmotionsModule.getDefault().getSortModel());
		return res;
	}
	
	/**
	 * It produces constant: mt
	 */
	public Constant getConstantEmpty() {
		Constant res = factory.createConstant();
		res.setOp("mt");
		return res;
	}
}
