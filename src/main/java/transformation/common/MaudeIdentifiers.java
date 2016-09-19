package main.java.transformation.common;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import gcs.ClassGD;

public final class MaudeIdentifiers {

	public MaudeIdentifiers() {
	}
	
	
	/**
	 * The original call sentences is: <code>obj.classGD.class.maudeName().toUpper()+'@'+obj.id+'@CLASS'</code>
	 *  
	 *  <code>
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
	 *  </code>
	 * 
	 * @param Behavior object
	 * @return the string representing the name of the variable
	 */
	public static String classVariableName(behavior.Object obj) {
		ClassGD classGD = (ClassGD) obj.getClassGD();
		EClass eclass = (EClass) classGD.getClass_();
		String aux = eclass.getEPackage() == null? eclass.getName() 
				: eclass.getName() + "@" + getPackageName(eclass.getEPackage());
		return aux.toUpperCase() + "@" + obj.getId() + "@CLASS";
	}

	private static String getPackageName(EPackage epack) {
		if (epack.getESuperPackage() == null) {
			return epack.getName();
		} else {
			return epack.getName() + "@" + getPackageName(epack.getESuperPackage());
		}
	}

}
