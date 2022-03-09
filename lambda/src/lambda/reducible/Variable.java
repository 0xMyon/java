package lambda.reducible;

import java.util.HashMap;
import java.util.Map;

import lambda.Reducible;

public class Variable<T> implements Reducible<T> {

	//private static int ID = 0;
	
	//private final int id = ID++;
	private final Reducible<T> type;
	
	private static String[] typenames = {"α","β","γ","δ","κ","μ","ν","π","σ","τ","φ","ψ","ω"};
	private static String[] valuenames = {"a","b","c","d","e","f","g","h","i","j"};
	
	public Variable(Reducible<T> type) {
		this.type = type;
	}
	
	@Override
	public Reducible<T> type() {
		return type;
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}
	
	public String toString(Map<Variable<T>, String> names) {
		if (!names.containsKey(this)) {
			names.put(this, Integer.toString(names.size()));
		}
		return names.get(this);
	}
	

	@Override
	public Reducible<T> replace(Variable<T> variable, Reducible<T> term) {
		assert variable.type().containsAll(term.type());
		return equals(variable) ? term : this;
	}

	@Override
	public Reducible<T> reduce() {
		return this;
	}

	@Override
	public boolean isMapping(Reducible<T> term, Map<Variable<T>, Reducible<T>> map) {
		try {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().isMapping(term.type(), map)) {
				map.put(this, term);
				return true;
			} else {
				return false;
			}
		} catch (AssertionError e) {
			return false;
		}
	}

	@Override
	public boolean isDepending(Variable<T> variable) {
		return equals(variable) || type.isDepending(variable);
	}
	

}