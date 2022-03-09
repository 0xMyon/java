package lambda.reducible;

import java.util.HashMap;
import java.util.Map;

import lambda.Reducible;

public class Application<T> implements Reducible<T> {

	private final Reducible<T> function;
	private final Reducible<T> parameter;
		
	public String toString() {
		return toString(new HashMap<>());
	}
	
	@Override
	public String toString(Map<Variable<T>, String> names) {
		return function.toString(names)+"("+parameter.toString(names)+")";
	}
	
	public Application(Reducible<T> function, Reducible<T> parameter) {
		this.function = function;
		this.parameter = parameter;
		assert function.type() instanceof IAbstraction : "function is not of type Abstraction";
		final Reducible<T> domain = ((IAbstraction<T>)function.type()).domain();
		assert domain.containsAll(parameter.type()) : "Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type();
	}
	
	@Override
	public Reducible<T> replace(Variable<T> variable, Reducible<T> term) {
		return new Application<>(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible<T> reduce() {
		return (function instanceof IAbstraction) 
				? ((IAbstraction<T>)function).apply(parameter).reduce()
				: new Application<>(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isMapping(Reducible<T> term, Map<Variable<T>, Reducible<T>> map) {
		if (term instanceof Application) {
			final Application<T> that = (Application<T>) term;
			return this.function.isMapping(that.function, map) && this.parameter.isMapping(that.parameter, map);
		}
		return false;
	}

	@Override
	public Reducible<T> type() {
		return ((IAbstraction<T>)function.type()).apply(parameter);
	}

	@Override
	public boolean isDepending(Variable<T> variable) {
		return function.isDepending(variable) || parameter.isDepending(variable);
	}
	
}
