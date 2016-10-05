package test.transformation.common;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import behavior.Rule;
import main.java.transformation.common.MaudeIdentifiers;
import test.ParentTest;

public class MaudeIdentifiersTest extends ParentTest {

	@BeforeClass
	public static void setUp() {
		ParentTest.setUp();
	}
	
	@Test
	public void testClassVariableName() {
		/* For trajectory objects */
		boolean foundTrajectoryObj = false;
		Optional<Rule> snapshot = trajectory.getBehavior().getRules().stream().filter(r -> r.getName().equals("Snapshot")).findFirst();
		if (snapshot.isPresent()) {
			Optional<behavior.Object> obj = snapshot.get().getLhs().getEls().stream()
				.filter(p -> p instanceof behavior.Object && ((behavior.Object) p).getId().equals("a"))
				.map(p -> (behavior.Object) p)
				.findFirst();
			if (obj.isPresent()) {
				foundTrajectoryObj = MaudeIdentifiers.classVariableName(obj.get()).equals("ARROW@DEFAULTNAME@a@CLASS");
			}
		}
		assertTrue(foundTrajectoryObj);
	}
}
