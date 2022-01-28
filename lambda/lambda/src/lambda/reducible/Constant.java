package lambda.reducible;

import java.util.Map;
import java.util.Objects;

import lambda.Reducible;
import lambda.Type;

public class Constant<T> implements Reducible<T> {

	private final Reducible<Type<T>> type;
	
	private final String name;
	
	Constant(Reducible<Type<T>> type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Constant(String name) {
		this(null, name);
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
	public boolean isEqual(Reducible<?> term, Map<Variable<?>, Variable<?>> map) {
		return Objects.equals(this, term);
	}

	@Override
	public Reducible<Type<T>> type() {
		if (null == type)
			throw new Error();
		return type;
	}
	
	public String toString() {
		return name;
	}

}
