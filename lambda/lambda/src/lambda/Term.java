package lambda;

import java.util.HashMap;
import java.util.Map;

public interface Term {

	Term replace(Variable variable, Term term);
	
	Term reduce();
	
	default boolean isEqual(Term that) {
		return isEqual(that, new HashMap<>()) && that.isEqual(this, new HashMap<>());
	}
	
	boolean isEqual(Term term, Map<Variable, Variable> map);
	
	default boolean isBetaEqual(Term that) {
		return this.reduce().isEqual(that.reduce());
	}
	
}
