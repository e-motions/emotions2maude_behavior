package main.java.transformation.rules.smallrules;

import Maude.Constant;
import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;

public class IdCounterObject extends Rule {

	public IdCounterObject(MyMaudeFactory maudeFact) {
		super(maudeFact);
	}

	@Override
	public void transform() {
		Maude.Variable id = maudeFact.getVariableOid("oid('ids@)");
		Constant cid = maudeFact.getConstant("Counter@MGBehavior");
		
		Constant nameSF = maudeFact.getConstant("value@Counter@MGBehavior");
		Maude.Variable valueSF = maudeFact.createVariable("VALUE@ids@", "Int");
		RecTerm sfs = maudeFact.createStructuralFeature(nameSF, valueSF); 

		res = maudeFact.createObject(id, cid, sfs);
	}

}
