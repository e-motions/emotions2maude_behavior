package test.Main;

import java.io.File;
import java.text.DecimalFormat;

import main.java.transformation.Emotions2Maude;

public class Main {
	
	private static String BEH_TRAJECTORY = "src/test/resources/trajectory/trajectory.behavior";
	private static String GCS_TRAJECTORY = "src/test/resources/trajectory/trajectory.gcs";
	private static String MAUDE_TRAJECTORY = "src/test/resources/trajectory/outs/outtrajectory.xmi";

	public static void main(String[] args) {
		
		System.out.println("#1 Test: Trajectory\n------------------\n\n - Model2Model transformation");
		
		long startTime = System.currentTimeMillis();
		DecimalFormat df = new DecimalFormat("#0.000");
		
		File behModel = new File(BEH_TRAJECTORY);
		File gcsModel = new File(GCS_TRAJECTORY);
		File maudeModel = new File(MAUDE_TRAJECTORY);
		
		Emotions2Maude mt = new Emotions2Maude(behModel, gcsModel, maudeModel);
		
		mt.runTransformation().saveOutput();
		
		long endTime = System.currentTimeMillis();
		System.out.println("Transformation executed in " + df.format((endTime - startTime) / 1000.0) + "seconds");
	}

}
