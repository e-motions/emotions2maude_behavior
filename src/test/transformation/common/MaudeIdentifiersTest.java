package test.transformation.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import behavior.PatternEl;
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
		List<PatternEl> patterns = new ArrayList<>(trajectory.getBehavior().getRules().get(2).getLhs().getEls());
		for (PatternEl p : patterns) {
			if (p instanceof behavior.Object && ((behavior.Object) p).getId().equals("a")) {
				assertEquals(MaudeIdentifiers.classVariableName((behavior.Object) p), "ARROW@DEFAULTNAME@a@CLASS");
				foundTrajectoryObj = true;
			}
		}
		assertTrue(foundTrajectoryObj);
	}

}
