package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lambda.Irreducible;
import lambda.Reducible;

public class Constant<T> implements Irreducible<T> {

	private final Constant<T> type;
	
	private final T name;
	
	public Constant(T name, Constant<T> type) {
		this.type = type;
		this.name = name;
	}
	
	public Constant(T name) {
		this(name, null);
	}

	@Override
	public boolean isMapping(Reducible<T> term, Map<Variable<T>, Reducible<T>> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Constant<T> type() {
		assert Objects.nonNull(type) : "can not get type of '"+name+"'";
		return type;
	}
	
	public String toString() {
		return toString(new HashMap<>());
	}
	
	@Override
	public String toString(Map<Variable<T>, String> names) {
		return name.toString();
	}

	@Override
	public boolean isDepending(Variable<T> variable) {
		return false;
	}

}
