package test.transformation.rules.smallrules;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import Maude.Variable;
import behavior.Object;
import behavior.Pattern;
import behavior.Rule;
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

}
