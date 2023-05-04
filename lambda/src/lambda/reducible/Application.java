package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;
import lang.Container;

public class Application<T, TYPE extends Container<TYPE, T>> implements Reducible<T,TYPE> {

	private final Reducible<T, TYPE> function;
	private final Reducible<T, TYPE> parameter;

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable<T,TYPE>, String> names) {
		return function.toString(names)+"("+parameter.toString(names)+")";
	}

	public Application(final Reducible<T, TYPE> function, final Reducible<T, TYPE> parameter) throws TypeMismatch {
		this.function = function;
		this.parameter = parameter;
		if (!(function.type() instanceof IAbstraction)) throw new TypeMismatch(function+" is not a function");
		final Reducible<T, TYPE> domain = ((IAbstraction<T,TYPE>)function.type()).domain();
		if (!domain.isAssignable(parameter)) throw new TypeMismatch("Domain missmatch: "+domain+":"+domain.type()+" -> "+parameter.type()+":"+parameter.type().type());
	}

	@Override
	public Reducible<T, TYPE> replace(final Variable<T,TYPE> variable, final Reducible<T, TYPE> term) throws TypeMismatch {
		return new Application<>(function.replace(variable, term), parameter.replace(variable, term));
	}

	@Override
	public Reducible<T, TYPE> doReduction() {
		return (function instanceof IAbstraction)
				? ((IAbstraction<T,TYPE>)function).apply(parameter).reduce()
						: new Application<>(function.reduce(), parameter.reduce());

	}
	
	@Override
	public boolean isReducible() {
		return function.isReducible() || parameter.isReducible();
	}

	@Override
	public boolean isMapping(final Reducible<T, TYPE> term, final Map<Variable<T,TYPE>, Reducible<T, TYPE>> map) {
		if (term instanceof Application) {
			final Application<T,TYPE> that = (Application<T,TYPE>) term;
			return function.isMapping(that.function, map) && parameter.isMapping(that.parameter, map);
		}
		return false;
	}

	@Override
	public Reducible<T, TYPE> type() {
		return ((IAbstraction<T,TYPE>)function.type()).apply(parameter);
	}

	@Override
	public boolean isDepending(final Variable<T,TYPE> variable) {
		return function.isDepending(variable) || parameter.isDepending(variable);
	}

	@Override
	public int layer() {
		return function.layer();
	}

	@Override
	public Stream<Variable<T,TYPE>> freeVars() {
		return Stream.concat(function.freeVars(), parameter.freeVars());
	}

}
