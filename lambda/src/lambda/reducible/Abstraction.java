package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;
import lang.Container;

public class Abstraction<T, TYPE extends Container<TYPE, T>> implements IAbstraction<T,TYPE> {

	private final Variable<T,TYPE> variable;
	private final Reducible<T,TYPE> term;

	public Abstraction(final Reducible<T,TYPE> type, final Function<Variable<T,TYPE>, Reducible<T,TYPE>> lambda) {
		variable = new Variable<>(type);
		term = lambda.apply(variable);
	}

	@Override
	public Abstraction<T,TYPE> replace(final Variable<T,TYPE> variable, final Reducible<T,TYPE> term) {
		return new Abstraction<>(domain().replace(variable, term), x -> apply(x).replace(variable, term));
	}

	@Override
	public Reducible<T,TYPE> apply(final Reducible<T,TYPE> parameter) {
		if (domain().isAssignable(parameter))
			return term.replace(variable, parameter);
		else
			throw new TypeMismatch("'"+parameter.toString()+"' does not match type '"+domain().toString()+"'");
	}

	@Override
	public Abstraction<T,TYPE> doReduction() {
		return new Abstraction<>(domain().reduce(), x -> apply(x).reduce());
	}
	
	@Override
	public boolean isReducible() {
		return variable.isReducible() || term.isReducible();
	}

	@Override
	public boolean isMapping(final Reducible<T,TYPE> term, final Map<Variable<T,TYPE>, Reducible<T,TYPE>> context) {
		if (term instanceof Abstraction) {
			final Abstraction<T,TYPE> that = (Abstraction<T,TYPE>) term;
			return variable.isMapping(that.variable, context) && this.term.isMapping(that.term, context);
		}
		return false;
	}

	@Override
	public Abstraction<T,TYPE> type() {
		return new Abstraction<>(domain(), x -> apply(x).type());
	}

	@Override
	public Reducible<T,TYPE> domain() {
		return variable.type();
	}

	@Override
	public boolean isConstant() {
		return !term.isDepending(variable);
	}

	@Override
	public String toString() {
		return toString(new HashMap<>());
	}

	@Override
	public String toString(final Map<Variable<T,TYPE>, String> names) {
		if (!isConstant())
			return "λ"+variable.toString(names)+":"+variable.type().toString(names)+"."+term.toString(names);
		else if (variable.type() instanceof Abstraction)
			return "("+variable.type().toString(names)+")->"+term.toString(names);
		else
			return variable.type().toString(names)+"->"+term.toString(names);
	}

	@Override
	public boolean isDepending(final Variable<T,TYPE> variable) {
		return isConstant() && this.variable.isDepending(variable) || term.isDepending(variable);
	}

	@Override
	public int layer() {
		return term.layer(); // TODO
	}


	@Override
	public boolean isAssignable(final Reducible<T,TYPE> that) {
		if (!isMapping(that.type())) {
			final Map<Variable<T,TYPE>, Reducible<T,TYPE>> map = new HashMap<>();
			return term.isMapping(that.type(), map) && map.containsKey(variable);
		}
		return false;
	}

	@Override
	public Stream<Variable<T,TYPE>> freeVars() {
		return Stream.concat(
				variable.type().freeVars(),
				term.freeVars()
				).filter(not(variable::equals));
	}

	private static <T> Predicate<T> not(final Predicate<T> that) {
		return that.negate();
	}

	


}