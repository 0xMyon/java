package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;

public class Application implements Reducible {

	private final Reducible function;
	private final Reducible parameter;

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable, String> names) {
		return function.toString(names)+"("+parameter.toString(names)+")";
	}

	public Application(final Reducible function, final Reducible parameter) throws TypeMismatch {
		this.function = function;
		this.parameter = parameter;
		if (!(function.type() instanceof IAbstraction)) throw new TypeMismatch(function+" is not a function");
		final Reducible domain = ((IAbstraction)function.type()).domain();
		if (!domain.isAssignable(parameter)) throw new TypeMismatch("Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type());
	}

	@Override
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		return new Application(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible reduce() {
		return (function instanceof IAbstraction)
				? ((IAbstraction)function).apply(parameter).reduce()
						: new Application(function.reduce(), parameter.reduce());

	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> map) {
		if (term instanceof Application) {
			final Application that = (Application) term;
			return function.isMapping(that.function, map) && parameter.isMapping(that.parameter, map);
		}
		return false;
	}

	@Override
	public Reducible type() {
		return ((IAbstraction)function.type()).apply(parameter.type());
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return function.isDepending(variable) || parameter.isDepending(variable);
	}

	@Override
	public int layer() {
		return function.layer();
	}

	@Override
	public Stream<Variable> freeVars() {
		return Stream.concat(function.freeVars(), parameter.freeVars());
	}

}
