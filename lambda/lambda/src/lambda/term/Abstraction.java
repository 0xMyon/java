package lambda.term;

import java.util.Map;
import java.util.function.Function;

import lambda.Term;
import lambda.Type;
import lambda.type.ArrowType;

public class Abstraction implements Term {

	private final Variable variable;
	private final Term term;
	
	public Abstraction(Type type, Function<Variable, Term> lambda) {
		this.variable = new Variable(type);
		this.term = lambda.apply(variable);
	}

	@Override
	public Term replace(Variable variable, Term term) {
		return new Abstraction(variable.type(), x -> this.term.replace(this.variable, x).replace(variable, term));
	}

	public Term apply(Term parameter) {
		return term.replace(variable, parameter);
	}

	@Override
	public Term reduce() {
		return new Abstraction(variable.type(), x -> this.term.replace(this.variable, x).reduce());
	}

	@Override
	public boolean isEqual(Term term, Map<Variable, Variable> map) {
		if (term instanceof Abstraction) {
			Abstraction that = (Abstraction) term;
			return this.variable.isEqual(that.variable, map) && this.term.isEqual(that.term, map);
		}
		return false;
	}

	@Override
	public Type type() {
		return new ArrowType(variable.type(), term.type());
	}
	
	
	
}
