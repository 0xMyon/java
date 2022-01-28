package lambda.reducible;

import java.util.Map;

import lambda.Reducible;
import lambda.Type;

public class Variable<T> implements Reducible<T> {

	private static int ID = 0;
	
	private final int id = ID++;
	private final Reducible<Type<T>> type;
	
	
	public Variable(Reducible<Type<T>> type) {
		this.type = type;
	}
	
	@Override
	public Reducible<Type<T>> type() {
		return type;
	}
	
	public String toString() {
		return Integer.toString(id);
	}

	@SuppressWarnings("unchecked") // if this == variable => T == X
	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return equals(variable) ? (Reducible<T>)term : this;
	}

	@Override
	public Reducible<T> reduce() {
		return this;
	}

	@Override
	public boolean isMapping(Reducible<?> term, Map<Variable<?>, Reducible<?>> map) {
		try {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().isMapping(term.type(), map)) {
				map.put(this, term);
				return true;
			}
		} catch (AssertionError e) {}
		return false;
		
		/*
		if (term instanceof Variable) {
			Variable<?> that = (Variable<?>) term;
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().isMapping(term.type(), map)) {
				map.put(this, that);
				return true;
			}
		}
		return false;
		*/
	}

	@Override
	public boolean contains(Variable<?> variable) {
		return equals(variable) || type.contains(variable);
	}
	

}