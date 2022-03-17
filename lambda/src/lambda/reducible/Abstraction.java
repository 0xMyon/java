package lambda.reducible;

import static lambda.ThrowingFunction.unchecked;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
	public Reducible replace(final Variable variable, final Reducible term) throws TypeMismatch {
		return new Abstraction(domain().replace(variable, term), unchecked(x -> apply(x).replace(variable, term)));
	}

	@Override
	public Reducible apply(final Reducible parameter) throws TypeMismatch {
		return term.replace(variable, parameter);
	}

	@Override
	public Reducible reduce() {
		return new Abstraction(domain().reduce(), unchecked(x -> apply(x).reduce()));
	}

	@Override
	public boolean isMapping(final Reducible term, final Map<Variable, Reducible> map) {
		if (term instanceof Abstraction) {
			final Abstraction that = (Abstraction) term;
			return variable.isMapping(that.variable, map) && this.term.isMapping(that.term, map);
		}
		return false;
	}

	@Override
	public IAbstraction type() {
		return new Abstraction(domain(), unchecked(x -> apply(x).type()));
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


}