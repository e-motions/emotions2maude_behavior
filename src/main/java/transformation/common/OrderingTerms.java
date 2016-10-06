package main.java.transformation.common;

import Maude.RecTerm;
import Maude.Variable;

public final class OrderingTerms {
	
	public static void objsetAtTheEnd(RecTerm parentTerm) {
		Maude.Variable objset = (Variable) parentTerm.getArgs().stream()
				.filter(e -> e instanceof Maude.Variable && ((Variable) e).getName().equals("OBJSET@")).findFirst().get();
		parentTerm.getArgs().remove(parentTerm.getArgs().indexOf(objset));
		parentTerm.getArgs().add(objset);
	}
}
