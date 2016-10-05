package test.transformation.rules.smallrules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import Maude.RecTerm;
import Maude.Term;
import Maude.Variable;
import behavior.ActionExec;
import behavior.Pattern;
import main.java.transformation.rules.smallrules.PatternNAC;
import main.java.transformation.utils.MaudeOperators;
import test.ParentTest;

public class PatternNACTest extends ParentTest {
	
	private static Pattern trajectoryNoArrow;
	private static Term outModel;
	
	@BeforeClass
	public static void setUp() {
		ParentTest.setUp();
		trajectory.getBehavior().getRules().stream()
				.filter(r -> r.getName().equals("Initial")).findFirst()
				.ifPresent(x -> x.getNacs().stream()
						.filter(n -> n.getName().equals("NoArrow")).findFirst()
						.ifPresent(na -> trajectoryNoArrow = na));
		if (trajectoryNoArrow != null) {
			outModel = new PatternNAC(maudeFact, trajectoryNoArrow).get();
		} else {
			fail();
		}
	}
	
	@Test
	public void trajectoryNoArrowTestIsARecTerm() {
		assertTrue(outModel instanceof RecTerm);
	}
	
	@Test
	public void trajectoryNoArrowTestIsAModel() {
		if (outModel instanceof RecTerm) {
			RecTerm model = (RecTerm) outModel;
			assertEquals(model.getOp(), MaudeOperators.MODEL);
			assertTrue(model.getArgs().get(0) instanceof Variable);
			assertEquals(model.getArgs().get(0).getType(), maudeFact.getSort("@Metamodel"));
		} else {
			fail();
		}
	}
	
	@Test
	public void trajectoryNoArrowAsManyObjectsAsObjsAndActions() {
		RecTerm model = (RecTerm) outModel;
		RecTerm args = (RecTerm) model.getArgs().get(1);
		long numberOfObjsActions = trajectoryNoArrow.getEls().stream()
				.filter(elem -> (elem instanceof behavior.Object) || (elem instanceof ActionExec))
				.count();
		/* we include also a variable */
		assertEquals(args.getArgs().size(), numberOfObjsActions + 1); 
	}
	
	@Test
	public void trajectoryNoArrowOBJECTSETVariable() {
		RecTerm model = (RecTerm) outModel;
		RecTerm objects = (RecTerm) model.getArgs().get(1);
		Optional<Maude.Variable> var = objects.getArgs().stream()
										.filter(a -> a instanceof Maude.Variable)
										.map(v -> (Maude.Variable) v)
										.findFirst();
		assertTrue(var.isPresent());
		assertEquals(var.get().getType().getName(), "Set{@Object}");
	}

}
