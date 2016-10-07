package main.java.transformation.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EReference;

import behavior.Link;

public final class ObjectOperations {
	
	/**
	 * Given a list of behavior links, it returns a Map association EReferences to Links.
	 * 
	 * The ATL code is:
	 * <pre>
	 * helper def : RefWithoutDuplicates( inCollection : Sequence(Behavior!Link) ) : Sequence(Behavior!EReference) =
	 *	inCollection -> iterate(e; outCollection : Sequence(Behavior!EReference) = Sequence{} |
	 *		if outCollection ->one(i|i=e.ref) then outCollection
	 *		else outCollection ->append(e.ref)
	 *		endif);
	 * </pre>
	 * 
	 * @param links
	 * @return map of EReferences
	 */
	public static Map<EReference, List<Link>> mapRef2Links(List<Link> links) {
		Set<EReference> refs = links.stream().map(r -> (EReference) r.getRef()).collect(Collectors.toSet());
		Map<EReference, List<Link>> res = new HashMap<EReference, List<Link>>();
		for (EReference r : refs) {
			res.put(r, links.stream().filter(ref -> (ref.getRef() == r)).collect(Collectors.toList()));
		}
		return res;
	}
}
