package lambda.reducible;

import java.util.HashMap;
import java.util.Map;

import lambda.Reducible;
import lambda.TypeMismatch;

public class Variable implements Reducible {

	//private static int ID = 0;

	//private final int id = ID++;
	private final Reducible type;

	private static String[] typenames = {"α","β","γ","δ","κ","μ","ν","π","σ","τ","φ","ψ","ω"};
	private static String[] valuenames = {"a","b","c","d","e","f","g","h","i","j"};

	public Variable(final Reducible type) {
		this.type = type;
	}

	@Override
	public Reducible type() {
		return type;
	}

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable, String> names) {
		if (!names.containsKey(this)) {
			names.put(this, Integer.toString(names.size()));
		}
		return names.get(this);
	}


	@Override
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		if (!variable.type().containsAll(term.type())) throw new TypeMismatch();
		//assert variable.type().containsAll(term.type());
		return equals(variable) ? term : this;
	}

	@Override
	public Reducible reduce() {
		return this;
	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> map) {
		try {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (type().isMapping(term.type(), map)) {
				map.put(this, term);
				return true;
			} else {
				return false;
			}
		} catch (final AssertionError e) {
			return false;
		}
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return equals(variable) || type.isDepending(variable);
	}


}