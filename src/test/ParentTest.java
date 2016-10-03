package test;

import java.io.File;

import org.junit.BeforeClass;

import main.java.transformation.Emotions2Maude;
import main.java.transformation.EmotionsModule;
import main.java.transformation.MyMaudeFactory;

public class ParentTest {
	
	public static Emotions2Maude trajectory;
	public static Emotions2Maude mpn;
	public static Emotions2Maude sequence;
	
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
		
		/* MPNs {@link http://atenea.lcc.uma.es/index.php/Main_Page/Resources/E-motions/MPNsExample} */
		String BEH_MPN = "src/test/resources/mpns/MPNs.behavior";
		String GCS_MPN = "src/test/resources/mpns/MPNs.gcs";
		String MAUDE_MPN = "src/test/resources/mpns/outs/output_mpn.xmi";
		mpn = new Emotions2Maude(new File(BEH_MPN), new File(GCS_MPN), new File(MAUDE_MPN));
		mpn.init();
		
		/* testsequence */
		String BEH_SEQ = "src/test/resources/testsequence/test.behavior";
		String GCS_SEQ = "src/test/resources/testsequence/test.gcs";
		String MAUDE_SEQ = "src/test/resources/testsequence/outs/out_sequence.xmi";
		sequence = new Emotions2Maude(new File(BEH_SEQ), new File(GCS_SEQ), new File(MAUDE_SEQ));
		sequence.init();
	}

}
