package lambda.reducible;

import java.util.Map;
import java.util.Objects;

import lambda.Reducible;
import lambda.Type;

public class Constant<T> implements Reducible<T> {

	private final Constant<Type<T>> type;
	
	private final String name;
	
	public Constant(String name, Constant<Type<T>> type) {
		this.type = type;
		this.name = name;
	}
	
	public Constant(String name) {
		this(name, null);
	}
	
	
	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return this;
	}

	@Override
	public Reducible<T> reduce() {
		return this;
	}

	@Override
	public boolean isMapping(Reducible<?> term, Map<Variable<?>, Reducible<?>> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Constant<Type<T>> type() {
		assert Objects.nonNull(type) : "can not get type of '"+name+"'";
		return type;
	}
	
	public String toString() {
		return name;
	}

	@Override
	public boolean contains(Variable<?> variable) {
		return false;
	}

}
