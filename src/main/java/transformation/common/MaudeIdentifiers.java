package main.java.transformation.common;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import gcs.ClassGD;

public final class MaudeIdentifiers {

	public MaudeIdentifiers() {
	}
	
	
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
	private static String getPackageName(EPackage epack) {
		if (epack.getESuperPackage() == null) {
			return epack.getName();
		} else {
			return epack.getName() + "@" + getPackageName(epack.getESuperPackage());
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
				: eclass.getName() + "@" + getPackageName(eclass.getEPackage());
	}

}
