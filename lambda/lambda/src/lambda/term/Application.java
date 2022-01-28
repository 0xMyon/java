package lambda.term;

import java.util.Map;

import lambda.Reducible;
import lambda.Type;
import lambda.reducible.Variable;

public class Application<V,T> implements Reducible<T> {

	private final Reducible<T> function;
	private final Reducible<V> parameter;
	
	public String toString() {
		return "("+function.toString()+" "+parameter.toString()+")";
	}
	
	public Application(Reducible<T> function, Reducible<V> parameter) {
		this.function = function;
		this.parameter = parameter;
		assert function.type() instanceof Abstraction : "function is not of type Abstraction";
		@SuppressWarnings("unchecked")
		final Reducible<Type<Type<V>>> domain = ((Abstraction<Type<V>,Type<T>>)function.type()).domain();
		assert domain.isMapping(parameter.type()) : "Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type();
	}
	
	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return new Application<>(function.replace(variable, term), parameter.replace(variable, term));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Reducible<T> reduce() {
		return (function instanceof Abstraction) 
				? ((Abstraction<V,T>)function).apply(parameter).reduce()
				: new Application<>(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isMapping(Reducible<?> term, Map<Variable<?>, Reducible<?>> map) {
		if (term instanceof Application) {
			final Application<?,?> that = (Application<?,?>) term;
			return this.function.isMapping(that.function, map) && this.parameter.isMapping(that.parameter, map);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Reducible<Type<T>> type() {
		return ((Abstraction<Type<V>,Type<T>>)function.type()).apply(parameter.type());
	}

	@Override
	public boolean contains(Variable<?> variable) {
		return function.contains(variable) || parameter.contains(variable);
	}
	
}
