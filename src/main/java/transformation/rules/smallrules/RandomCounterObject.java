package main.java.transformation.rules.smallrules;

import Maude.Constant;
import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;

public class RandomCounterObject extends Rule {

	public RandomCounterObject(MyMaudeFactory maudeFact) {
		super(maudeFact);
	}

	@Override
	public void transform() {
		Maude.Variable id = maudeFact.getVariableOid("oid('rdm@)");
		Constant cid = maudeFact.getConstant("Counter@MGBehavior");
		
		Constant nameSF = maudeFact.getConstant("value@Counter@MGBehavior");
		Maude.Variable valueSF = maudeFact.createVariable("VALUE@rdm@", "Int");
		RecTerm sfs = maudeFact.createStructuralFeature(nameSF, valueSF); 

		res = maudeFact.createObject(id, cid, sfs);
	}

}
