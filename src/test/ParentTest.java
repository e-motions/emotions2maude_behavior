package test;

import java.io.File;

import org.junit.BeforeClass;

import main.java.transformation.Emotions2Maude;
import main.java.transformation.EmotionsModule;
import main.java.transformation.MyMaudeFactory;

public class ParentTest {
	
	public static Emotions2Maude trajectory;
	
	protected static EmotionsModule emotionsMod;
	protected static MyMaudeFactory maudeFact;
	
	@BeforeClass
	public static void setUp() {
		/**
		 * Creating infrastructure
		 */
		emotionsMod = new EmotionsModule();
		maudeFact = new MyMaudeFactory(emotionsMod);
		
		/**
		 *  Loading some models 
		 */
		
		/* trajectory */
		String BEH_TRAJECTORY = "src/test/resources/trajectory/trajectory.behavior";
		String GCS_TRAJECTORY = "src/test/resources/trajectory/trajectory.gcs";
		String MAUDE_TRAJECTORY = "src/test/resources/trajectory/outs/outtrajectory.xmi";
		trajectory = new Emotions2Maude(new File(BEH_TRAJECTORY), new File(GCS_TRAJECTORY), new File(MAUDE_TRAJECTORY));
		trajectory.init();
		
	}

}
