package main.java.transformation.common;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import Maude.Variable;
import behavior.Object;
import gcs.ClassGD;

public final class MaudeIdentifiers {

	/**
	 * The original call sentences could be something
	 * like the following: <code>obj.classGD.class.maudeName().toUpper()+'@'+obj.id+'@CLASS'</code>
	 *  
	 *  <pre>
	 * 	helper context Behavior!EPackage def : maudeName() : String =
	 *	    if (self.eSuperPackage.oclIsUndefined())then 
	 *			self.name
	 *	    else 
	 *	    	self.name + '@' + self.eSuperPackage.maudeName()  
	 *	    endif;
	 *	    
	 *	helper context Behavior!EClassifier def : maudeName() : String =
	 *	    if (self.ePackage.oclIsUndefined()) then 
	 *			self.name
	 *	    else 
	 *	    	self.name + '@' + self.ePackage.maudeName()  
	 *	    endif;
	 *  </pre>
	 * 
	 * @param Behavior object
	 * @return the string representing the name of the variable
	 */
	public static String classVariableName(behavior.Object obj) {
		String aux = getMaudeName(obj);
		return aux.toUpperCase() + "@" + obj.getId() + "@CLASS";
	}
	
	/**
	 * This rule is equal to the <code>unique lazy rule Class2Sort</code>.
	 * 
	 * @param behObj
	 * @return the maude name of the class of the <code>behObj</code> object.
	 */
	public static String class2sort(behavior.Object obj) {
		return getMaudeName(obj);
	}
	
	/**
	 * The original ATL code is:
	 * <pre>
	 * helper context Behavior!EPackage def : maudeName() : String =
	 *	    if (self.eSuperPackage.oclIsUndefined())then 
	 *			self.name
	 *	    else 
	 *	    	self.name + '@' + self.eSuperPackage.maudeName()  
	 *	    endif;
	 * </pre>
	 * @param epack
	 * @return
	 */
	private static String get(EPackage epack) {
		if (epack.getESuperPackage() == null) {
			return epack.getName();
		} else {
			return epack.getName() + "@" + get(epack.getESuperPackage());
		}
	}
	
	/**
	 * The original ATL code is:
	 * <pre>
	 * helper context Behavior!EClassifier def : maudeName() : String =
	 *	    if (self.ePackage.oclIsUndefined()) then 
	 *			self.name
	 *	    else 
	 *	    	self.name + '@' + self.ePackage.maudeName()  
	 *	    endif;
	 * </pre>
	 * @param epack
	 * @return
	 */
	private static String getMaudeName(behavior.Object obj) {
		ClassGD classGD = (ClassGD) obj.getClassGD();
		EClass eclass = (EClass) classGD.getClass_();
		return eclass.getEPackage() == null? eclass.getName() 
				: eclass.getName() + "@" + get(eclass.getEPackage());
	}

	public static String sfsVariableName(behavior.Object obj) {
		return obj.getId() + "@SFS";
	}

	public static String processSpecialChars(String name) {
		return name;
	}
	
	
	/**
	 * It is used for getting the name of the structural feature representing this 
	 * reference. The original ATL rule is:
	 * <pre>
	 * helper context Behavior!EStructuralFeature def : maudeName() : String =
     *   self.name + '@' + self.eContainingClass.maudeName();
	 * </pre>
	 * @param ref
	 * @return
	 */
	public static String get(EReference ref) {
		return ref.getName() + "@" + get(ref.getEContainingClass());
	}
	
	/**
	 * The original ATL code is:
	 * <pre>
	 * helper context Behavior!EClassifier def : maudeName() : String =
	 *	    if (self.ePackage.oclIsUndefined()) then 
	 *			self.name
	 *	    else 
	 *	    	self.name + '@' + self.ePackage.maudeName()  
	 *	    endif;
	 * </pre>
	 * @param epack
	 * @return
	 */
	public static String get(EClass _class) {
		String res = _class.getName();
		if (_class.getEPackage() != null) {
			res += "@" + get(_class.getEPackage());
		} 
		return res;
	}
	
	/**
	 * Should generate a variable identifier for a reference which cannot be set in the matching.
	 * The original code is: <code>linkRef.name.toUpper().processSpecOpChars()+'@'+objId+'@ATT'</code>
	 * @param obj source of the link
	 * @param ref name of the reference
	 * @return the identifier
	 */
	public static String getRefIdentifier(Object obj, EReference ref) {
		return ref.getName().toUpperCase() + "@" + obj.getId() + "@ATT";
	}
}
