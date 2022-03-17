package lambda.reducible;

import java.util.HashMap;
import java.util.Map;

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
		if (!domain.containsAll(parameter.type())) throw new TypeMismatch("Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type());
	}

	@Override
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		return new Application(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible reduce() {
		try {
			return (function instanceof IAbstraction)
					? ((IAbstraction)function).apply(parameter).reduce()
							: new Application(function.reduce(), parameter.reduce());
		} catch (final TypeMismatch e) {
			throw new RuntimeException(e);
		}
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
		try {
			return ((IAbstraction)function.type()).apply(parameter);
		} catch (final TypeMismatch e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return function.isDepending(variable) || parameter.isDepending(variable);
	}

}
