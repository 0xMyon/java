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
		assert function.type() instanceof Abstraction : "function is not of ArrowType";
		assert ((Abstraction<Type<V>,Type<T>>)function.type()).domain().isEqual(parameter.type()) : "Domain missmatch";
	}
	
	@Override
	public <X> Reducible<T> replace(Variable<X> variable, Reducible<X> term) {
		return new Application<V,T>(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible<T> reduce() {
		return (function instanceof Abstraction) 
				? ((Abstraction<V,T>)function).apply(parameter).reduce()
				: new Application<V,T>(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isEqual(Reducible<?> term, Map<Variable<?>, Variable<?>> map) {
		if (term instanceof Application) {
			final Application<?,?> that = (Application<?,?>) term;
			return this.function.isEqual(that.function, map) && this.parameter.isEqual(that.parameter, map);
		}
		return false;
	}

	@Override
	public Reducible<Type<T>> type() {
		return ((Abstraction<Type<V>,Type<T>>)function.type()).apply(parameter.type());
	}

}
