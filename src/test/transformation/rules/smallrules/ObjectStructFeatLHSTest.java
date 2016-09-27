package test.transformation.rules.smallrules;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import Maude.Term;
import behavior.Pattern;
import main.java.transformation.rules.smallrules.ObjectStructFeatLHS;
import test.ParentTest;


public class ObjectStructFeatLHSTest extends ParentTest {
	
	@BeforeClass
	public static void setUp() {
		ParentTest.setUp();
	}
	
	@Test
	public void notStructuralFeatures() {
		/* TODO: preguntar a PacoG
		 * 
		Optional<behavior.Object> objA = trajectory.getBehavior().getRules().stream()
				.filter(r -> r.getName().equals("Snapshot")).findAny().get().getLhs().getEls().stream()
				.filter(el -> el instanceof behavior.Object && ((behavior.Object) el).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		*/
		Pattern lhsPattern = trajectory.getBehavior().getRules().stream()
				.filter(r -> r.getName().equals("Snapshot")).findAny().get().getLhs();
		Optional<behavior.Object> objA = lhsPattern.getEls().stream()
				.filter(el -> el instanceof behavior.Object && ((behavior.Object) el).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		if (objA.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, objA.get(), lhsPattern).get();
			assertTrue(res instanceof Maude.Variable);
			assertEquals(res.getType().getName(), "Set{@StructuralFeatureInstance}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void onlyOneSlot() {
		fail("not implemented yet");
	}
	
	@Test
	public void oneOutLink() {
		fail("not implemented yet");
	}
	
}
