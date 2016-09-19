package main.java.transformation;

import java.util.List;

import Maude.Constant;
import Maude.MaudeFactory;
import Maude.RecTerm;
import Maude.Term;
import Maude.Variable;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.utils.MaudeOperators;

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
	 * It produces a variable: name:OCL-Type
	 */
	public Variable getVariableOCLType(String name) {
		Variable res = factory.createVariable();
		res.setName(name);
		res.setType(EmotionsModule.getDefault().getSortOCLType());
		return res;
	}
	
	/**
	 * It produces a variable: name:Oid
	 */
	public Variable getVariableOid(String name) {
		Variable res = factory.createVariable();
		res.setName(name);
		res.setType(EmotionsModule.getDefault().getSortOid());
		return res;
	}
	
	/**
	 * It produces a variable: MM@:@Metamodel
	 */
	public Variable getVariableMM() {
		Variable res = factory.createVariable();
		res.setName("MM@");
		res.setType(EmotionsModule.getDefault().getSortMetamodel());
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

	/**
	 * It produces constant: none
	 */
	public Constant getConstantNone() {
		return getConstant("none");
	}
	
	/**
	 * It produces a constant with op given by the string
	 */
	public Constant getConstant(String string) {
		Constant res = factory.createConstant();
		res.setOp(string);
		return res;
	}
	
	/**
	 * It produces a RecTerm with operator given by the argument
	 * @param op name
	 * @return the RecTerm
	 */
	public RecTerm createRecTerm(String op) {
		RecTerm res = factory.createRecTerm();
		res.setOp(op);
		return res;
	}
	
	/**
	 * It produces a term _:_ with arguments given by the parameter args,
	 * representing a structural feature
	 * @param args
	 * @return the RecTerm
	 */
	public RecTerm createStructuralFeature(List<Term> args) {
		RecTerm res = createRecTerm(MaudeOperators.SF);
		res.getArgs().addAll(args);
		return res;
	}
	
	/**
	 * It produces a term _`,_ with arguments given by the parameter args,
	 * representing a structural feature set.
	 * @param args
	 * @return the RecTerm
	 */
	public RecTerm createStructuralFeatureSet(List<Term> args) {
		RecTerm res = createRecTerm(MaudeOperators.SFS_SET);
		res.getArgs().addAll(args);
		return res;
	}
	/**
	 * Given its three arguments, it returns an object
	 * @param args
	 * @return the RecTerm
	 */
	public RecTerm createObject(Term id, Term cid, Term sfs) {
		RecTerm res = createRecTerm(MaudeOperators.OBJECT);
		res.getArgs().add(id); 
		res.getArgs().add(cid);
		res.getArgs().add(sfs);
		return res;
	}
	
	/**
	 * Given a behavior Object, it generates a Variable representing its class.
	 * A variable should be generated for inheritance purposes. The statements in ATL is
	 * <pre>
	 * objClass : Maude!Variable(
	 *		name <- obj.classGD.class.maudeName().toUpper()+'@'+obj.id+'@CLASS',
	 *		type <- thisModule.Class2Sort(obj.classGD.class)
	 *	),
	 * </pre>
	 * @param behObj Behavior Object
	 * @return the variable representing such class.
	 */
	public Variable getVariableObjectClass(behavior.Object behObj) {
		Variable res = factory.createVariable();
		res.setName(MaudeIdentifiers.classVariableName(behObj));
		res.setType(EmotionsModule.getDefault().getSort(MaudeIdentifiers.class2sort(behObj)));
		return res;
	}

	public Variable getVariableSFS(behavior.Object obj) {
		Variable res = factory.createVariable();
		res.setName(MaudeIdentifiers.sfs(obj));
		res.setType(EmotionsModule.getDefault().getSort("Set{@StructuralFeatureInstance}"));
		return res;
	}

}
