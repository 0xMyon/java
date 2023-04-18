package lambda.reducible;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lambda.Reducible;
import lambda.TypeMismatch;

public class Abstraction implements IAbstraction {

	private final Variable variable;
	private final Reducible term;

	public Abstraction(final Reducible type, final Function<Variable, Reducible> lambda) {
		variable = new Variable(type);
		term = lambda.apply(variable);
	}

	@Override
	public Abstraction replace(final Variable variable, final Reducible term) {
		return new Abstraction(domain().replace(variable, term), x -> apply(x).replace(variable, term));
	}

	@Override
	public Reducible apply(final Reducible parameter) {
		if (domain().isAssignable(parameter))
			return term.replace(variable, parameter);
		else
			throw new TypeMismatch("'"+parameter.toString()+"' does not match type '"+domain().toString()+"'");
	}

	@Override
	public Abstraction reduce() {
		return new Abstraction(domain().reduce(), x -> apply(x).reduce());
	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> context) {
		if (term instanceof Abstraction) {
			final Abstraction that = (Abstraction) term;
			return variable.isMapping(that.variable, context) && this.term.isMapping(that.term, context);
		}
		return false;
	}

	@Override
	public Abstraction type() {
		return new Abstraction(domain(), x -> apply(x).type());
	}

	@Override
	public Reducible domain() {
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
	public String toString(final Map<Variable, String> names) {
		if (!isConstant())
			return "λ"+variable.toString(names)+":"+variable.type().toString(names)+"."+term.toString(names);
		else if (variable.type() instanceof Abstraction)
			return "("+variable.type().toString(names)+")->"+term.toString(names);
		else
			return variable.type().toString(names)+"->"+term.toString(names);
	}

	@Override
	public boolean isDepending(final Variable variable) {
		return isConstant() && this.variable.isDepending(variable) || term.isDepending(variable);
	}

	@Override
	public int layer() {
		return term.layer(); // TODO
	}


	@Override
	public boolean isAssignable(final Reducible that) {
		if (!isMapping(that.type())) {
			final Map<Variable, Reducible> map = new HashMap<>();
			return term.isMapping(that.type(), map) && map.containsKey(variable);
		}
		return false;
	}

	@Override
	public Stream<Variable> freeVars() {
		return Stream.concat(
				variable.type().freeVars(),
				term.freeVars()
				).filter(not(variable::equals));
	}

	private static <T> Predicate<T> not(final Predicate<T> that) {
		return that.negate();
	}


}