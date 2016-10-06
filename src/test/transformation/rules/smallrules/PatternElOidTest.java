package test.transformation.rules.smallrules;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import Maude.Variable;
import behavior.ActionExec;
import behavior.Condition;
import behavior.Object;
import behavior.Pattern;
import behavior.PatternEl;
import behavior.Rule;
import main.java.exceptions.NotValidArgumentsE2MException;
import main.java.transformation.rules.smallrules.PatternElOid;
import test.ParentTest;

public class PatternElOidTest extends ParentTest {

	@BeforeClass
	public static void setUp() {
		ParentTest.setUp();
	}

	@Test
	public void transformObj1Test() {
		/* 
		 * Creating a behavior object.
		 * 
		 * Inspired in the object 'b' in the model `trajectory`, rule `initial`, NAC `noArrow`.
		 */
		Optional<Rule> optRule =  trajectory.getBehavior().getRules().stream().filter(r -> r.getName().equals("Initial")).findFirst();
		if (optRule.isPresent()) {
			Optional<Pattern> optNac = optRule.get().getNacs().stream().filter(n -> n.getName().equals("NoArrow")).findFirst();
			if (optNac.isPresent()) {
				try {
					behavior.Object obj1 = (Object) optNac.get().getEls().get(0);
					assertTrue(obj1.getId().equals("b"));
					Maude.Variable maudeVar = (Variable) new PatternElOid(maudeFact, obj1).get();
					assertTrue(maudeVar.getName().equals("b"));
					assertTrue(maudeVar.getType().getName().equals("OCL-Type"));
				} catch (Exception e) {
					fail();
				}
			} else {
				fail();
			}
		} else {
			fail();
		}
	}
	
	@Test
	public void transformAct1Test() {
		/* 
		 * Creating an ActionExec object.
		 * 
		 * Inspired in the condition in the model `MPN`, rule `SwitchOn`, pattern `LHS`.
		 */
		Optional<Rule> batteryOff =  mpn.getBehavior().getRules().stream()
				.filter(r -> r.getName().equals("BatteryOff")).findFirst();
		if (batteryOff.isPresent()) {
			Optional<Pattern> pisNotInACall = batteryOff.get().getNacs().stream().filter(n -> n.getName().equals("PisNotInACall")).findFirst();
			if (pisNotInACall.isPresent()) {
				try {
					ActionExec act2 = (ActionExec) pisNotInACall.get().getEls().get(0);
					assertTrue(act2.getId().equals("c"));
					Maude.Variable maudeVar = (Variable) new PatternElOid(maudeFact, act2).get();
					assertTrue(maudeVar.getName().equals("c"));
					assertTrue(maudeVar.getType().getName().equals("OCL-Type"));
				} catch (Exception e) {
					fail();
				}
			} else {
				fail();
			}
		} else {
			fail();
		}
	}
	
	@Test(expected = NotValidArgumentsE2MException.class)
	public void exception() {
		/* 
		 * throwing an Exception
		 * 
		 * Inspired in the aciton 'c' in the model `MPN`, rule `BatteryOff`, NAC `PisNotInACall`.
		 */
		Optional<Rule> switchOn =  mpn.getBehavior().getRules().stream()
				.filter(r -> r.getName().equals("SwitchOn")).findFirst();
		if (switchOn.isPresent()) {
			Optional<PatternEl> condition = switchOn.get().getLhs().getEls().stream().filter(n -> n instanceof Condition).findFirst();
			if (condition.isPresent()) {
				new PatternElOid(maudeFact, condition.get()).get();
			} else {
				fail();
			}
		} else {
			fail();
		}
	}

}
