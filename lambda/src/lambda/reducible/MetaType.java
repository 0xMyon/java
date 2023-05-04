package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lambda.Reducible;
import lang.Container;

public class MetaType<T, TYPE extends Container<TYPE, T>> implements Reducible<T, TYPE> {

	MetaType(Reducible<T, TYPE> type) {
		this.type = type;
	}
	
	private final Reducible<T, TYPE> type;
	
	@Override
	public Reducible<T, TYPE> replace(Variable<T, TYPE> variable, Reducible<T, TYPE> term) {
		return new MetaType<>(type.replace(variable, term));
	}

	@Override
	public Reducible<T, TYPE> doReduction() {
		return type.reduce().type();
	}
	
	@Override
	public boolean isReducible() {
		return type.isReducible();
	}

	@Override
	public boolean isMapping(Reducible<T, TYPE> term, Map<Variable<T, TYPE>, Reducible<T, TYPE>> context) {
		if (term instanceof MetaType) {
			MetaType<T,TYPE> that = (MetaType<T,TYPE>) term;
			return type.isMapping(that.type, context);
		}
		return false;
	}

	@Override
	public Reducible<T, TYPE> type() {
		return new MetaType<>(this);
	}

	@Override
	public int layer() {
		return type.layer()+1;
	}

	@Override
	public boolean isDepending(Variable<T, TYPE> variable) {
		return type.isDepending(variable);
	}

	@Override
	public Stream<Variable<T, TYPE>> freeVars() {
		return type.freeVars();
	}

	@Override
	public String toString(Map<Variable<T, TYPE>, String> names) {
		return "$["+type.toString(names)+"]";
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}

}
