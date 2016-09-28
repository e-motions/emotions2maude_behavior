package main.java.transformation;

import java.util.List;

import Maude.Constant;
import Maude.MaudeFactory;
import Maude.Operation;
import Maude.RecTerm;
import Maude.Sort;
import Maude.Term;
import Maude.Variable;
import main.java.exceptions.NotValidArgumentsE2MException;
import main.java.transformation.common.MaudeIdentifiers;
import main.java.transformation.utils.MaudeOperators;

public class MyMaudeFactory {
	
	private MaudeFactory factory;
	private EmotionsModule emotionsModule;
	
	public MyMaudeFactory(EmotionsModule emotionsModule) {
		factory = MaudeFactory.eINSTANCE;
		this.emotionsModule = emotionsModule;
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
		res.setType(emotionsModule.getSortSet());
		return res;
	}
	
	/**
	 * It produces: OBJSET@:Set{@Object}
	 */
	public Variable getVariableObjectSet() {
		Variable res = factory.createVariable();
		res.setName("OBJSET@");
		res.setType(emotionsModule.getSortSetObject());
		return res;
	}
	
	/**
	 * It produces: MODEL@:@Model
	 */
	public Variable getVariableModel() {
		Variable res = factory.createVariable();
		res.setName("MODEL@");
		res.setType(emotionsModule.getSortModel());
		return res;
	}
	
	/**
	 * It produces a variable: name:OCL-Type
	 */
	public Variable getVariableOCLType(String name) {
		Variable res = factory.createVariable();
		res.setName(name);
		res.setType(emotionsModule.getSortOCLType());
		return res;
	}
	
	/**
	 * It produces a variable: name:Oid
	 */
	public Variable getVariableOid(String name) {
		Variable res = factory.createVariable();
		res.setName(name);
		res.setType(emotionsModule.getSortOid());
		return res;
	}
	
	/**
	 * It produces a variable: MM@:@Metamodel
	 */
	public Variable getVariableMM() {
		Variable res = factory.createVariable();
		res.setName("MM@");
		res.setType(emotionsModule.getSortMetamodel());
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
		if (args.size() != 2) {
			throw new NotValidArgumentsE2MException("It must have exactly 2 arguments.");
		}
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
		res.setType(emotionsModule.getSort(MaudeIdentifiers.class2sort(behObj)));
		return res;
	}
	
	/**
	 * Given a behavior object, it creates the variable to match with the structural features not
	 * explicitly listed in the Maude object.
	 * @param obj
	 * @return the created Maude variable
	 */
	public Variable getVariableSFS(behavior.Object obj) {
		Variable res = factory.createVariable();
		res.setName(MaudeIdentifiers.sfsVariableName(obj));
		res.setType(emotionsModule.getSort("Set{@StructuralFeatureInstance}"));
		return res;
	}

	/**
	 * Given an id, it generates a variable <code>id:List{OCL-Exp}</code> used for references
	 * which are sequences or ordered sets.
	 * @param id of the variable
	 * @return the Maude variable
	 */
	public Variable getVariableOrderedLists(String id) {
		Variable res = factory.createVariable();
		res.setName(id);
		res.setType(emotionsModule.getSort("List{OCL-Exp}"));
		return res;
	}
	
	/**
	 * Given an id, it generates a variable <code>id:MSet{OCL-Exp}</code> used for references
	 * which are sets and bags.
	 * @param id of the variable
	 * @return the Maude variable
	 */
	public Variable getVariableMSet(String id) {
		Variable res = factory.createVariable();
		res.setName(id);
		res.setType(emotionsModule.getSort("MSet{OCL-Exp}"));
		return res;
	}

	public Operation createOperation(String name) {
		Operation res = factory.createOperation();
		res.setName(name);
		return res;
	}
	
	public Sort getSort(String name) {
		return emotionsModule.getSort(name);
	}

}
