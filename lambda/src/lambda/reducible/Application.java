package lambda.reducible;

import java.util.Map;

import lambda.Reducible;

public class Application implements Reducible {

	private final Reducible function;
	private final Reducible parameter;
		
	public String toString() {
		return function.toString()+"("+parameter.toString()+")";
	}
	
	public Application(Reducible function, Reducible parameter) {
		this.function = function;
		this.parameter = parameter;
		assert function.type() instanceof IAbstraction : "function is not of type Abstraction";
		final Reducible domain = ((IAbstraction)function.type()).domain();
		assert domain.containsAll(parameter.type()) : "Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type();
	}
	
	@Override
	public <X> Reducible replace(Variable variable, Reducible term) {
		return new Application(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible reduce() {
		return (function instanceof IAbstraction) 
				? ((IAbstraction)function).apply(parameter).reduce()
				: new Application(function.reduce(), parameter.reduce());
	}

	@Override
	public boolean isMapping(Reducible term, Map<Variable, Reducible> map) {
		if (term instanceof Application) {
			final Application that = (Application) term;
			return this.function.isMapping(that.function, map) && this.parameter.isMapping(that.parameter, map);
		}
		return false;
	}

	@Override
	public Reducible type() {
		return ((IAbstraction)function.type()).apply(parameter);
	}

	@Override
	public boolean contains(Variable variable) {
		return function.contains(variable) || parameter.contains(variable);
	}
	
}
