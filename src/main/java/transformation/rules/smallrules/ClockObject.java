package main.java.transformation.rules.smallrules;

import Maude.Constant;
import Maude.RecTerm;
import main.java.transformation.MyMaudeFactory;

public class ClockObject extends Rule {

	public ClockObject(MyMaudeFactory maudeFact) {
		super(maudeFact);
	}

	@Override
	public void transform() {
		Maude.Variable id = maudeFact.getVariableOid("CLK@");
		Constant cid = maudeFact.getConstant("Clock@MGBehavior");
		
		Constant nameSF = maudeFact.getConstant("time@Clock@MGBehavior");
		Maude.Variable valueSF = maudeFact.createVariable("TIME@CLK@", "Time");
		RecTerm sfs = maudeFact.createStructuralFeature(nameSF, valueSF); 

		res = maudeFact.createObject(id, cid, sfs);
	}

}
