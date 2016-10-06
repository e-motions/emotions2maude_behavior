package test.transformation.rules.smallrules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import Maude.RecTerm;
import Maude.Term;
import behavior.OngoingRule;
import behavior.Pattern;
import behavior.Rule;
import main.java.transformation.rules.smallrules.ObjectStructFeatLHS;
import main.java.transformation.utils.MaudeOperators;
import test.ParentTest;


public class ObjectStructFeatLHSTest extends ParentTest {
	
	@BeforeClass
	public static void setUp() {
		ParentTest.setUp();
	}
	
	@Test
	public void noStructuralFeatures() {
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
			Term res = new ObjectStructFeatLHS(maudeFact, objA.get()).get();
			assertTrue(res instanceof Maude.Variable);
			assertEquals(res.getType().getName(), "Set{@StructuralFeatureInstance}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void onlyOneSlot() {
		/* querying model `Sequence`, rule `TestSlots`, pattern `LHS`, object `a1` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestSlots")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a1"))
				.map(ob -> (behavior.Object) ob).findAny();
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof RecTerm);
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			RecTerm slotSF = (RecTerm) ((RecTerm) res).getArgs().get(0);
			assertEquals(slotSF.getOp(), MaudeOperators.SF);
		} else {
			fail("Object not found.");
		}
	}
	
	@Test
	public void oneStructuralFeatureReferenceName() {
		/* querying rule 'Moving', pattern 'LHS', object 'mpn' */
		Pattern lhsPattern = mpn.getBehavior().getRules().stream()
				.filter(el -> el instanceof OngoingRule && el.getName().equals("Moving")).findFirst().get().getLhs();
		Optional<behavior.Object> objMpn = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("mpn"))
				.map(ob -> (behavior.Object) ob).findAny();
		if (objMpn.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, objMpn.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			assertEquals(reference.getOp(), MaudeOperators.SF);
			assertEquals(((Maude.Constant) reference.getArgs().get(0)).getOp(), "els@MPN@MPNs");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void oneOutLinkOrderedSet() {
		Pattern lhsPattern = mpn.getBehavior().getRules().stream()
				.filter(el -> el instanceof OngoingRule && el.getName().equals("Moving")).findFirst().get().getLhs();
		Optional<behavior.Object> objMpn = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("mpn"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   OrderedSet{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
		 */
		if (objMpn.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, objMpn.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a OrderedSet with just an argument, the one with the _#_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_ORDERED_SET);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have three arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 3);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "List{OCL-Exp}");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(2).getType().getName(), "List{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void oneOutLinkSequence() {
		/* we are using the input model `Sequence`, rule `MatchingSequence`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("MatchingSequence")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Sequence{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a Sequence with just an argument, the one with the _#_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_SEQUENCE);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have three arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 3);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "List{OCL-Exp}");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(2).getType().getName(), "List{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void oneOutLinkSet() {
		/* we are using the input model `Sequence`, rule `TestingSet1`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestingSet1")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Set{b1:OCL-Type ; remainings:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a Set with just an argument, the one with the _;_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_SET);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have two arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 2);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "MSet{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void threeOutLinkSet() {
		/* we are using the input model `Sequence`, rule `TestingSet2`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestingSet2")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Set{b1:OCL-Type ; remainings:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a Set with just an argument, the one with the _;_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_SET);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have two arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 4);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(2).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(3).getType().getName(), "MSet{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void oneOutLinkBag() {
		/* we are using the input model `Sequence`, rule `TestingBag1`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestingBag1")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Set{b1:OCL-Type ; remainings:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a Set with just an argument, the one with the _;_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_BAG);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have two arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 2);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "MSet{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	@Test
	public void twoOutLinkBag() {
		/* we are using the input model `Sequence`, rule `TestingBag1`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestingBag2")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Set{b1:OCL-Type ; remainings:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			RecTerm ordSet = (RecTerm) reference.getArgs().get(1);
			/* it should be a Set with just an argument, the one with the _;_ attribute */
			assertEquals(ordSet.getOp(), MaudeOperators.COLL_BAG);
			assertEquals(ordSet.getArgs().size(), 1);
			assertTrue(ordSet.getArgs().get(0) instanceof RecTerm);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getOp(), MaudeOperators.NOT_ORDERED_LIST_SEPARATOR);
			
			/* now it such list should have two arguments */
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().size(), 3);
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(0).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(1).getType().getName(), "OCL-Type");
			assertEquals(((RecTerm) ordSet.getArgs().get(0)).getArgs().get(2).getType().getName(), "MSet{OCL-Exp}");
		} else {
			fail("Input element is not valid");
		}
	}
	
	// TODO test for sth not supported by matching
	@Test
	public void twoOutLinkSequence() {
		/* we are using the input model `Sequence`, rule `TestSequence2`, pattern `LHS`, object `a` */
		Pattern lhsPattern = sequence.getBehavior().getRules().stream()
				.filter(el -> el instanceof Rule && el.getName().equals("TestSequence2")).findFirst().get().getLhs();
		Optional<behavior.Object> obj = lhsPattern.getEls().stream()
				.filter(ob -> ob instanceof behavior.Object && ((behavior.Object) ob).getId().equals("a"))
				.map(ob -> (behavior.Object) ob).findAny();
		/* an example of the resulting Maude term is:
		 *   Sequence{OtherTokens1:List{OCL-Exp} # sToken:OCL-Type # OtherTokens2:List{OCL-Exp}}
		 */
		if (obj.isPresent()) {
			Term res = new ObjectStructFeatLHS(maudeFact, obj.get()).get();
			assertTrue(res instanceof Maude.RecTerm);
			/* testing: structuralfeature1 # structuralfeature2 # REMAININGS:"Set{@StructuralFeatureInstance} */
			assertEquals(((RecTerm) res).getOp(), MaudeOperators.SFS_SET);
			/* testing size: just one reference plus the variable */
			assertEquals(((RecTerm) res).getArgs().size(), 2);
			/* getting the reference */
			RecTerm reference = (RecTerm) ((RecTerm) res).getArgs().get(0);
			/* it should be a just a variable */
			assertTrue(reference.getArgs().get(1) instanceof Maude.Variable);
		} else {
			fail("Input element is not valid");
		}
	}
}
