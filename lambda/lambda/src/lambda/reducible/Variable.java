package lambda.reducible;

import java.util.Map;

import lambda.Reducible;
import lambda.Type;

public class Variable<T> implements Reducible<T> {

	private static int ID = 0;
	private final int id = ID++;
	
	public String toString() {
		return ""+id;
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
	public boolean isEqual(Reducible<?> term, Map<Variable<?>, Variable<?>> map) {
		if (term instanceof Variable) {
			if (map.containsKey(this)) {
				return map.get(this).equals(term);
			} else if (this.type().equals(term.type())) {
				map.put(this, (Variable<?>)term);
				return true;
			}
		}
		return false;
	}
	
	private final Reducible<Type<T>> type;
	
	public Variable(Reducible<Type<T>> type) {
		this.type = type;
	}
	
	@Override
	public Reducible<Type<T>> type() {
		return type;
	}

}